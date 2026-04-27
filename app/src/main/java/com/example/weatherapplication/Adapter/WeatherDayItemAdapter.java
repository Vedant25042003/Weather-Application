package com.example.weatherapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.weatherapplication.Activity.MainActivity;
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
    private int textColor = Color.BLACK;
    private OnItemClickListener listener;

//    we add the interface here so when we set adapter we can add what to do when RV item is clicked
    public WeatherDayItemAdapter(Context context, List<WeatherForecast.Forecast.ForecastDay> list, OnItemClickListener listener) {
        this.context = context;
        this.forecastDays = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_day_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherForecast.Forecast.ForecastDay forecastDay = forecastDays.get(position);

//      gets date from the api
        LocalDate date = LocalDate.parse(forecastDay.getDate());

//      loads the days of week
        DayOfWeek day = date.getDayOfWeek();

//      sets day of week
        holder.DayofWeek.setText(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()));

//      sets day condition image and temp
        holder.forecastDaycondition.setText(forecastDay.getDay().getDayCondition().getDayConText());
        holder.dayTemp.setText(String.valueOf(forecastDay.getDay().getMaxtemp()+"°"+ "/ " +forecastDay.getDay().getMinDayTemp_c()+"°"));
        Picasso.get()
                .load("https:" + forecastDay.getDay().getDayCondition().getImage())
                .into(holder.forecastDayImage);

//      when user clicks day item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, forecastDay.getDate(), Toast.LENGTH_SHORT).show();
//              to get the index of the item clicked
                for (int i=0; i< forecastDays.size();i++){
                    if (forecastDays.get(i).getDate().equals( forecastDay.getDate())) {
//                        Toast.makeText(context, String.valueOf(i), Toast.LENGTH_SHORT).show();
//                      this acts as put extra of intent to get values in activity
                        listener.onItemClick(i, String.valueOf(day));
                    }
                }
            }
        });
//      sets color of text
        holder.DayofWeek.setTextColor(textColor);
        holder.dayTemp.setTextColor(textColor);
        holder.forecastDaycondition.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return forecastDays != null ? forecastDays.size(): 0;
    }

    public void setTextColor(int color) {
        this.textColor = color;
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


//    created interface for click of recycler view items
    public interface OnItemClickListener {
//        when item is clicked you will store these values
        void onItemClick(int item, String day);
    }
}
