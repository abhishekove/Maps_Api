package com.dev.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST=9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MainActivity";

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    Button button,addPlace;
    SeekBar seekBar;
    RecyclerView recyclerView;
    CollectionReference collectionReference;
    List<parking> parkings=new ArrayList<>();
    List<Double> distance=new ArrayList<>();
    List<Pair<Double,parking>> smp=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.stMap);
        addPlace=findViewById(R.id.signup);
        seekBar=findViewById(R.id.seek);
        recyclerView=findViewById(R.id.places);
        collectionReference=FirebaseFirestore.getInstance().collection("parking");

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Double mProgress=0.0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mProgress=Double.parseDouble(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                parkings.clear();
                distance.clear();
                for (Pair<Double,parking> pair:smp){
//                    Log.d(TAG, "onDataChange: "+pair.first);
                    if (pair.first>mProgress)break;
                    distance.add(pair.first);
                    parkings.add(pair.second);
                }
//                for (Map.Entry<Double,parking> entry:smp.entrySet()){
//                    if (entry.getKey()>=mProgress){
//                        break;
//                    }
//                    distance.add(entry.getKey());
//                    parkings.add(entry.getValue());
//                }
                parkingAdapter adapter=new parkingAdapter(parkings,distance);
                recyclerView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(),"seekbar touch stopped!"+mProgress, Toast.LENGTH_SHORT).show();

            }
        });
//        getLocationPermission();
        if (isServicesOK()){
            getLocationPermission();
        }
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServicesOK()){
                    Intent intent=new Intent(MainActivity.this,MapActivity.class);
                    intent.putExtra("places", (Serializable) parkings);
                    startActivity(intent);
                }
            }
        });
    }
    public boolean isServicesOK(){
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available== ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(MainActivity.this,"Nope",Toast.LENGTH_LONG).show();
        }
        return false;
    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
//                initMap();
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    getDeviceLocation();
//                    initMap();
                }
            }
        }
    }
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM);
                            final LatLng initial=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            FirebaseDatabase.getInstance().getReference().child("parking").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    smp.clear();
                                    distance.clear();
                                    parkings.clear();
                                    for (DataSnapshot data:snapshot.getChildren()){
                                        parking park=data.getValue(parking.class);

//                                        parkings.add(park);
                                        LatLng destination=new LatLng(Double.parseDouble(park.getLat()),Double.parseDouble(park.getLng()));
//                                        distance.add(getDistance(initial,destination));
//                                        Log.d(TAG, "onDataChange: "+park.getName()+" "+getDistance(initial,destination));
//                                        smp.put(getDistance(initial,destination),park);
                                        smp.add(new Pair<Double, parking>(getDistance(initial,destination),park));
                                    }
                                    Log.d(TAG, "onDataChange: "+smp.size());
//                                    smp.sort(Sortbyroll);
                                    Collections.sort(smp, new Sortbyroll());
//                                    for (Map.Entry<Double,parking> entry:smp.entrySet()){
//                                        distance.add(entry.getKey());
//                                        parkings.add(entry.getValue());
//                                    }
                                    for (Pair<Double,parking> pair:smp){
                                        Log.d(TAG, "onDataChange: "+pair.first);
                                        distance.add(pair.first);
                                        parkings.add(pair.second);
                                    }
                                    parkingAdapter adapter=new parkingAdapter(parkings,distance);
                                    recyclerView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                    if (error!=null)return;
//                                    for (QueryDocumentSnapshot documentSnapshot:value){
//                                        parking park=documentSnapshot.toObject(parking.class);
////                                        parkings.add(park);
//                                        LatLng destination=new LatLng(Double.parseDouble(park.getLat()),Double.parseDouble(park.getLng()));
////                                        distance.add(getDistance(initial,destination));
//                                        smp.put(getDistance(initial,destination),park);
//                                    }
//                                    for (Map.Entry<Double,parking> entry:smp.entrySet()){
//                                        distance.add(entry.getKey());
//                                        parkings.add(entry.getValue());
//                                    }
//                                    parkingAdapter adapter=new parkingAdapter(parkings,distance);
//                                    recyclerView.setAdapter(adapter);
//                                }
//                            });


                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    Double getDistance(LatLng initial,LatLng destination){
        double distance=0;
        distance=Math.sqrt(Math.pow(destination.latitude-initial.latitude,2)+Math.pow(destination.longitude-initial.longitude,2));
        return distance;
    }

    class Sortbyroll implements Comparator<Pair<Double,parking>>
    {
        @Override
        public int compare(Pair<Double, parking> o1, Pair<Double, parking> o2) {
            return Double.compare(o1.first,o2.first);
        }
        // Used for sorting in ascending order of
        // roll number
//        public int compare(Student a, Student b)
//        {
//            return a.rollno - b.rollno;
//        }
    }
}