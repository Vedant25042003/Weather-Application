package com.example.weatherapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Models.WeatherDaysForecast;
import com.example.weatherapplication.Models.WeatherDetails;
import com.example.weatherapplication.R;

import java.util.ArrayList;
import java.util.List;

public class WeatherDayItemAdapter extends RecyclerView.Adapter<WeatherDayItemAdapter.ViewHolder > {

    Context context;
    ArrayList<WeatherDaysForecast> list;
    ArrayList<WeatherDaysForecast.Forecast.ForecastDay> forecastDay;

    public WeatherDayItemAdapter(Context context, ArrayList<WeatherDaysForecast> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        WeatherDaysForecast forecast = list.get(position);
//        WeatherDaysForecast.Forecast.ForecastDay forecastDay = forecastDay.get(position);
//        holder.weather_temp.setText(String.valueOf(forecastDay.getDay().getMaxtemp()));
//        holder.weather_temp.setText(String.valueOf(list));
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView weather_temp;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            weather_temp = itemView.findViewById(R.id.WeekDay);
        }

    }
}
