package com.example.weatherapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.R;

public class Weather_DayItem_Adapter extends RecyclerView.Adapter<Weather_DayItem_Adapter.ViewHolder> {


    @NonNull
    @Override
    public Weather_DayItem_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Weather_DayItem_Adapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView WeekDay;
        ImageView weatherImage;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            WeekDay = itemView.findViewById(R.id.WeekDay);
            weatherImage = itemView.findViewById(R.id.weatherImageDay);

        }
    }
}
