package com.example.weatherapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeatherDayItemAdapter extends RecyclerView.Adapter<WeatherDayItemAdapter.ViewHolder> {

    private Context context;
    private List<WeatherForecast.Forecast.ForecastDay> forecastDays;

    public WeatherDayItemAdapter(Context context, List<WeatherForecast.Forecast.ForecastDay> list) {
        this.context = context;
        this.forecastDays = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherForecast.Forecast.ForecastDay forecastDay = forecastDays.get(position);
        holder.forecastDaytemp.setText(forecastDay.getDay().getDayCondition().getDayText());
        Picasso.get()
                .load("https:" + forecastDay.getDay().getDayCondition().getImage())
                .into(holder.forecastDayImage);
    }

    @Override
    public int getItemCount() {
        return forecastDays != null ? forecastDays.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView forecastDaytemp;
        ImageView forecastDayImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            forecastDaytemp = itemView.findViewById(R.id.WeekDay);
            forecastDayImage = itemView.findViewById(R.id.weatherImageDay);

        }
    }
}
