package com.example.weatherapplication.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Adapter.SearchItemAdapter;
import com.example.weatherapplication.Adapter.WeatherDayItemAdapter;
import com.example.weatherapplication.Adapter.WeatherHourItemAdapter;
import com.example.weatherapplication.Api.ApiRepository;
import com.example.weatherapplication.Models.SearchModel;
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

    TextView temp, weather, cityName, sunrise, feelsLike, sunset, winds, humidity, dew, sunrise_moonrise,
            sunset_moonset;
    ImageView weatherImage, sunriseMoonriseImage, sunsetMoonsetImage, backgroundImage;
    SearchView citySearchView;
    SearchBar citySearchBar;
    ApiRepository apiRepository = new ApiRepository();
    WeatherDetails weatherDetails;
    RecyclerView DayWeatherForecast, DayHourForecast, SearchCityName;
    WeatherDayItemAdapter weatherDayItemAdapter;
    WeatherHourItemAdapter weatherHourItemAdapter;
    SearchItemAdapter searchItemAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;

    String aqi = "no";
    int days = 3;
    String alerts = "yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        citySearchBar = findViewById(R.id.searchCityBar);
        citySearchView = findViewById(R.id.searchView);
        DayWeatherForecast = findViewById(R.id.day_items);
        DayHourForecast = findViewById(R.id.hourForecast);
        cityName = findViewById(R.id.cityName);
        SearchCityName = findViewById(R.id.cityNameRV);
        feelsLike = findViewById(R.id.feels_like_temp);
        winds = findViewById(R.id.WindSpeedKMph);
        humidity = findViewById(R.id.humidity);
        dew = findViewById(R.id.dew_point);
        sunrise = findViewById(R.id.sunrise_time);
        sunset = findViewById(R.id.sunset_moonset_time);
        sunrise_moonrise = findViewById(R.id.sunrise_moonrise_text);
        sunset_moonset = findViewById(R.id.sunset_moonset_text);
        sunriseMoonriseImage = findViewById(R.id.sunrise_moonrise_image);
        sunsetMoonsetImage = findViewById(R.id.sunset_moonset_image);
        backgroundImage = findViewById(R.id.backgroundImage);

        citySearchView.setupWithSearchBar(citySearchBar);
        SearchCity();
        String City = getIntent().getStringExtra("City");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (isInternetAvailable(this)) {

            if (City != null) {
                getCurrentWeather(City, aqi);
                getDaysForecast(City, days, aqi, alerts);
                cityName.setText(City);

            } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    String CityName = addresses.get(0).getLocality();
                                    SharedPreferences sharedLocationPref = getSharedPreferences("LocationCache", MODE_PRIVATE);
                                    sharedLocationPref.edit()
                                            .putString("Lat", String.valueOf(latitude))
                                            .putString("Lon", String.valueOf(longitude))
                                            .putString("CityName", CityName)
                                            .apply();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            String City = latitude + "," + longitude;
                            String CityName = getSharedPreferences("LocationCache", MODE_PRIVATE).getString("CityName", "");
                            cityName.setText(CityName);

                            getDaysForecast(City, days, aqi, alerts);
                            getCurrentWeather(City, aqi);

                        } else {
                            SharedPreferences sharedPrefs = getSharedPreferences("LocationCache", MODE_PRIVATE);
                            String CityName = sharedPrefs.getString("CityName", "");
                            String Lat = sharedPrefs.getString("Lat", "0.0");
                            String Lon = sharedPrefs.getString("Lon", "0.0");

                            String City = Lat + "," + Lon;
                            cityName.setText(CityName);

                            getDaysForecast(City, days, aqi, alerts);
                            getCurrentWeather(City, aqi);
                        }
                    }
                });

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }

        } else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("No Internet Connection")
                    .setMessage("Please turn on the internet Connection to get latest weather updates")
                    .setPositiveButton("Turn On",(dialog, which)->{
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Ok", (dialog,id)->dialog.dismiss());
            alertDialog.show();
        }
    }

    protected void onResume(){
        super.onResume();
        if (isInternetAvailable(this)){
            SharedPreferences sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE);
            String CityName = sharedPreferences.getString("CityName", "");
            String Lat = sharedPreferences.getString("Lat", "0.0");
            String Lon = sharedPreferences.getString("Lon", "0.0");
            String City = Lat +"," +Lon;

            String CitySearch = getIntent().getStringExtra("City");
            if (CitySearch != null){
                getCurrentWeather(CitySearch,aqi);
                getDaysForecast(CitySearch, days, aqi, alerts);
                cityName.setText(CitySearch);

            }else{
                getCurrentWeather(City,aqi);
                getDaysForecast(City, days, aqi, alerts);
                cityName.setText(CityName);
            }
        }
    }


//    Api Call function to get current weather
    private void getCurrentWeather(String city, String aqi) {
        apiRepository.getCurrentTemp(city, aqi).enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(@NonNull Call<WeatherDetails> call, @NonNull Response<WeatherDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherDetails = response.body();

                    if (weatherDetails.getCurrent().getIsDay()==1){
//                        we create a style in values night(themes) where we set text color to white
//                        we set night mode to no
                        backgroundImage.setImageResource(R.drawable.full_image);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
                    }else {
//                        we create a style in values Theme where we set text color to Black
//                        we set night mode to yes
                        backgroundImage.setImageResource(R.drawable.background);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

//                        tried to implement text color change using this only in theme doesn't work properly
//                        use night mode easy to use
//                        setTheme(R.style.Dark);
                    }

                    String imageUrl = "https://" + weatherDetails.getCurrent().getCondition().getWeatherIcon();
                    temp.setText(String.valueOf(weatherDetails.getCurrent().getCurrentTemperature()+ "°c"));
                    weather.setText(String.valueOf(weatherDetails.getCurrent().getCondition().getWeatherText()));
                    Picasso.get()
                            .load(imageUrl)
                            .into(weatherImage);

//                    set the last cardview items
                    feelsLike.setText(String.valueOf(weatherDetails.getCurrent().getFeelsLike_c()+"°c"));
                    winds.setText(String.valueOf(weatherDetails.getCurrent().getWindSpeedKmph()+" Km/h"));
                    humidity.setText(String.valueOf(weatherDetails.getCurrent().getHumidity()));
                    dew.setText(String.valueOf(weatherDetails.getCurrent().getDewpoint_c()+ "°c"));

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

//    API call function to get forecast weather
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

                        if (weatherDetails.getCurrent().getIsDay() == 1) {
                            sunrise.setText(forecastResponse.getForecast().getForecastDay().get(0).getAstro().getMoonriseTime());
                            sunrise_moonrise.setText("Moonrise");
                            sunriseMoonriseImage.setImageResource(R.drawable.mountain_moon_svgrepo_com);
                            sunset.setText(forecastResponse.getForecast().getForecastDay().get(0).getAstro().getSunsetTime());
                            sunset_moonset.setText("Sunset");
                            sunsetMoonsetImage.setImageResource(R.drawable.sunset_svgrepo_com);
                        } else{
                            sunrise.setText(forecastResponse.getForecast().getForecastDay().get(0).getAstro().getSunriseTime());
                            sunrise_moonrise.setText("Sunrise");
                            sunriseMoonriseImage.setImageResource(R.drawable.sunrise_over_mountains_svgrepo_com);
                            sunset.setText(forecastResponse.getForecast().getForecastDay().get(0).getAstro().getMoonsetTime());
                            sunset_moonset.setText("Moonset");
                            sunsetMoonsetImage.setImageResource(R.drawable.moon_svgrepo_com);
                        }

//                      check weather there are any alerts
                        if (!forecastResponse.getAlerts().getAlertList().isEmpty()){
//                          if yes, we create a alert dialog
//                          First create a builder,
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.alerts_dialog, null);

                            alertDialog.setView(dialogView);

                            TextView alertTitle, alertmsg;
                            Button okBtn;

                            alertTitle = dialogView.findViewById(R.id.alertType);
                            alertmsg = dialogView.findViewById(R.id.alertDescription);
                            okBtn = dialogView.findViewById(R.id.okButton);
                            AlertDialog alert = alertDialog.create();

                            alertTitle.setText(forecastResponse.getAlerts().getAlertList().get(0).getMsgType());
                            alertmsg.setText(forecastResponse.getAlerts().getAlertList().get(0).getEvent()+"\n"
                                          + forecastResponse.getAlerts().getAlertList().get(0).getHeadline());

                            okBtn.setOnClickListener(view -> {
                                alert.dismiss();
                            });
                            alert.show();
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

//    API call to fetch city
    private void fetchCity(String City){
        apiRepository.getSearchCityName(City).enqueue(new Callback<List<SearchModel>>() {
            @Override
            public void onResponse(Call<List<SearchModel>> call, Response<List<SearchModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchItemAdapter = new SearchItemAdapter(MainActivity.this, response.body());
                    SearchCityName.setAdapter(searchItemAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SearchModel>> call, Throwable t) {}
        });
    }

//    search functionality
    private void SearchCity(){
        citySearchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString();

                if (query.length() > 2) {
                    fetchCity(query);
                }
            }
        });
    }

    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }
}
