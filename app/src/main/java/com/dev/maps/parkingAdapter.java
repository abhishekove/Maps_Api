package com.dev.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class parkingAdapter extends RecyclerView.Adapter<parkingAdapter.parkingHolder> {

    private List<parking> parkings=new ArrayList<>();
    private List<Double> distance=new ArrayList<>();

    public parkingAdapter(List<parking> parkings, List<Double> distance) {
        this.parkings = parkings;
        this.distance = distance;
    }

    public parkingAdapter(List<parking> parkings) {
        this.parkings = parkings;
    }

    @NonNull
    @Override
    public parkingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card,parent,false);
        return new parkingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull parkingHolder holder, int position) {
        parking park=parkings.get(position);
        holder.placename.setText(park.getName());
        holder.distance.setText(String.valueOf(distance.get(position)));
    }

    @Override
    public int getItemCount() {
        return parkings.size();
    }
    public void setParkings(List<parking> parkings){
        this.parkings=parkings;
        notifyDataSetChanged();
    }

    class  parkingHolder extends RecyclerView.ViewHolder{
        private TextView placename,distance;

        public parkingHolder(@NonNull View itemView) {
            super(itemView);
            placename=itemView.findViewById(R.id.placename);
            distance=itemView.findViewById(R.id.distance);
        }
    }
}
