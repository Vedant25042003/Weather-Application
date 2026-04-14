package com.example.weatherapplication.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.R;
import com.squareup.picasso.Picasso;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherForecast.Forecast.ForecastDay forecastDay = forecastDays.get(position);

        LocalDate date = LocalDate.parse(forecastDay.getDate());
        DayOfWeek day = date.getDayOfWeek();
        holder.DayofWeek.setText(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()));

        holder.forecastDaycondition.setText(forecastDay.getDay().getDayCondition().getDayConText());
        holder.dayTemp.setText(String.valueOf(forecastDay.getDay().getMaxtemp()+"°"+ "/ " +forecastDay.getDay().getMinDayTemp_c()+"°"));
        Picasso.get()
                .load("https:" + forecastDay.getDay().getDayCondition().getImage())
                .into(holder.forecastDayImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, forecastDay.getDate(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastDays != null ? forecastDays.size(): 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView forecastDaycondition, DayofWeek, dayTemp;
        ImageView forecastDayImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            forecastDaycondition = itemView.findViewById(R.id.WeekDay);
            forecastDayImage = itemView.findViewById(R.id.weatherImageDay);
            DayofWeek = itemView.findViewById(R.id.dayofWeek);
            dayTemp = itemView.findViewById(R.id.tempOfDay);

        }
    }
}
