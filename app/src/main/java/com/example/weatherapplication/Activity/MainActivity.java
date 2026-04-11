package com.example.weatherapplication.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Adapter.WeatherDayItemAdapter;
import com.example.weatherapplication.Adapter.WeatherHourItemAdapter;
import com.example.weatherapplication.Api.ApiRepository;
import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.Models.WeatherDetails;
import com.example.weatherapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView temp, weather, cityName;
    ImageView weatherImage;
    ApiRepository apiRepository = new ApiRepository();
    WeatherDetails weatherDetails;
    RecyclerView DayWeatherForecast, DayHourForecast;
    WeatherDayItemAdapter weatherDayItemAdapter;
    WeatherHourItemAdapter weatherHourItemAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;

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

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        temp = findViewById(R.id.Temp_c);
        weather = findViewById(R.id.weatherText);
        weatherImage = findViewById(R.id.weatherImage);
        DayWeatherForecast = findViewById(R.id.day_items);
        DayHourForecast = findViewById(R.id.hourForecast);
        cityName = findViewById(R.id.cityName);

        String aqi = "no";
        int days = 3;
        String alerts = "yes";


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses != null && !addresses.isEmpty()){
                                        String CityName = addresses.get(0).getLocality();
                                        SharedPreferences sharedLocationPref = getSharedPreferences("LocationCache", MODE_PRIVATE);
                                        sharedLocationPref.edit()
                                                .putString("Lat",String.valueOf(latitude))
                                                .putString("Lon", String.valueOf(longitude))
                                                .putString("CityName",CityName)
                                                .apply();
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                String City = latitude +","+ longitude;
                                String CityName = getSharedPreferences("LocationCache",MODE_PRIVATE).getString("CityName","");
//                                String City = "mandals";
                                cityName.setText(CityName);

                                getDaysForecast(City, days + 1, aqi, alerts);
                                getCurrentWeather(City, aqi);

                            } else{
                                SharedPreferences sharedPrefs = getSharedPreferences("LocationCache",MODE_PRIVATE);
                                String CityName = sharedPrefs.getString("CityName","");
                                String Lat = sharedPrefs.getString("Lat","0.0");
                                String Lon = sharedPrefs.getString("Lon","0.0");

                                String City = Lat + "," +Lon;
//                                String City = "Aurangabad";

                                cityName.setText(CityName);

                                getDaysForecast(City, days + 1, aqi, alerts);
                                getCurrentWeather(City, aqi);
                            }
                        }
                    });
        }
    }

    private void getCurrentWeather(String city, String aqi) {
        apiRepository.getCurrentTemp(city, aqi).enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(@NonNull Call<WeatherDetails> call, @NonNull Response<WeatherDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherDetails = response.body();

                    String imageUrl = "https://" + weatherDetails.getCurrent().getCondition().getWeatherIcon();
                    temp.setText(String.valueOf(weatherDetails.getCurrent().getCurrentTemperature()+ "°c"));
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

                        weatherHourItemAdapter = new WeatherHourItemAdapter(MainActivity.this, forecastResponse.getForecast().getForecastDay().get(0).getHour());
                        DayHourForecast.setAdapter(weatherHourItemAdapter);

//                      check weather there are any alerts
                        if (!forecastResponse.getAlerts().getAlertList().isEmpty()){
//                          if yes, we create a alert dialog
//                          First create a builder,
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

//                          set content of the Alert Dialog
                            alertDialog.setTitle(forecastResponse.getAlerts().getAlertList().get(0).getMsgType())
                                    .setMessage(forecastResponse.getAlerts().getAlertList().get(0).getEvent()+"\n"
                                            + forecastResponse.getAlerts().getAlertList().get(0).getHeadline())

//                                    create a negative button to dismiss the dialog
                                    .setNegativeButton("cancel",(dialog,id)->dialog.dismiss());
//                          Add dialog.show to show the dialog
                            alertDialog.show();
                        }
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
