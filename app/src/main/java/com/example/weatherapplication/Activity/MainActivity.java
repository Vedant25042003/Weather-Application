package com.example.weatherapplication.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Adapter.WeatherDayItemAdapter;
import com.example.weatherapplication.Api.ApiRepository;
import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.Models.WeatherDetails;
import com.example.weatherapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView temp, weather;
    ImageView weatherImage;
    ApiRepository apiRepository = new ApiRepository();
    WeatherDetails weatherDetails;
    RecyclerView DayWeatherForecast;
    WeatherDayItemAdapter weatherDayItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        temp = findViewById(R.id.Temp_c);
        weather = findViewById(R.id.weatherText);
        weatherImage = findViewById(R.id.weatherImage);
        DayWeatherForecast = findViewById(R.id.day_items);

        String City = "London";
        String aqi = "no";
        int days = 3;
        String alerts = "no";

        getDaysForecast(City, days, aqi, alerts);
        getCurrentWeather(City, aqi);
    }

    private void getCurrentWeather(String city, String aqi) {
        apiRepository.getCurrentTemp(city, aqi).enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(@NonNull Call<WeatherDetails> call, @NonNull Response<WeatherDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherDetails = response.body();

                    String imageUrl = "https://" + weatherDetails.getCurrent().getCondition().getWeatherIcon();
                    temp.setText(String.valueOf(weatherDetails.getCurrent().getCurrentTemperature()));
                    weather.setText(String.valueOf(weatherDetails.getCurrent().getCondition().getWeatherText()));
                    Picasso.get()
                            .load(imageUrl)
                            .into(weatherImage);

                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherDetails> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDaysForecast(String City, int days, String aqi, String alerts) {
        apiRepository.getDaysForecast(City, days, aqi, alerts).enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecast> call, @NonNull Response<WeatherForecast> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecast forecastResponse = response.body();

                    if (forecastResponse.getForecast() != null && forecastResponse.getForecast().getForecastDay() != null) {
                        weatherDayItemAdapter = new WeatherDayItemAdapter(MainActivity.this, forecastResponse.getForecast().getForecastDay());
                        DayWeatherForecast.setAdapter(weatherDayItemAdapter);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to get forecast", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherForecast> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Forecast Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
