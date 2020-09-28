package com.dev.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class InfoPage extends AppCompatActivity {

    TextView plName,Capacity,plTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        plName=findViewById(R.id.plName);
        plTime=findViewById(R.id.plTime);
        Capacity=findViewById(R.id.capacity);
        parking park= (parking) getIntent().getSerializableExtra("places");
        plName.setText("name "+park.getName());
        plTime.setText("time "+park.getTime());
        Capacity.setText("Capacity "+park.gettCapacity()+park.getfCapacity());
    }
}