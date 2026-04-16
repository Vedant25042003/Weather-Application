package com.example.weatherapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.R;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherHourItemAdapter extends RecyclerView.Adapter<WeatherHourItemAdapter.ViewHolder> {

    private Context context;
    private List<WeatherForecast.Forecast.ForecastDay.Hour> weatherHourForecasts;
//    initialize text color white
    private int textColor = Color.WHITE;

    public WeatherHourItemAdapter(Context context, List<WeatherForecast.Forecast.ForecastDay.Hour> weatherForecastHourList){
        this.context = context;
        this.weatherHourForecasts = weatherForecastHourList;
    }

    @NonNull
    @Override
    public WeatherHourItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_hour_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull WeatherHourItemAdapter.ViewHolder holder, int position) {
        WeatherForecast.Forecast.ForecastDay.Hour forecastDay = weatherHourForecasts.get(position);

        String apiTime = forecastDay.getTime();
        DateTimeFormatter inputTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(apiTime,inputTime);
        String Time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        holder.time.setText(Time);
        holder.hour_temp_c.setText(String.valueOf(forecastDay.getTemp_c()) + "°c");
        Picasso.get()
                .load("http:"+forecastDay.getHourCondition().getHourIcon())
                .into(holder.hourImage);

//      use function to change color according to the theme
        holder.time.setTextColor(textColor);
        holder.hour_temp_c.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return weatherHourForecasts.size();
    }

//  add a set text color function
    public void setTextColor(int color) {
        this.textColor = color;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView time, hour_temp_c;
        ImageView hourImage;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            time = itemView.findViewById(R.id.hourForecastTime);
            hour_temp_c = itemView.findViewById(R.id.forecastHourTemp);
            hourImage = itemView.findViewById(R.id.forecastHourImage);

        }
    }
}
