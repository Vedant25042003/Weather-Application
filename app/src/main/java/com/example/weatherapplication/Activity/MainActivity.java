package com.example.weatherapplication.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.location.Priority;
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
    ProgressBar progressBar;
    ApiRepository apiRepository = new ApiRepository();
    WeatherDetails weatherDetails;
    WeatherForecast weatherForecast;
    RecyclerView DayWeatherForecast, DayHourForecast, SearchCityName;
    WeatherDayItemAdapter weatherDayItemAdapter;
    WeatherHourItemAdapter weatherHourItemAdapter;
    SearchItemAdapter searchItemAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean alertShown = false;//    set a boolean for alert dialog shown
    boolean locationDialogShown = false;
    int i=0;

    private ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                // Retrieve individual grant status
                Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineGranted != null && fineGranted && coarseGranted != null && coarseGranted) {
                    if (isLocation(this)) {
                        currentLocation();
                    }else if (!locationDialogShown){
                        locationDialogShown=true;
                        locationDialog();
                    }
                } else {
                    // DENIED: User selected "Don't allow"
                    Toast.makeText(this, "please give location permission", Toast.LENGTH_SHORT).show();
                }
            });

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
//      declare geocoder and initialize it

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
        progressBar = findViewById(R.id.loader);

//      set up search view with searchbar
        citySearchView.setupWithSearchBar(citySearchBar);
        SearchCity();
        String City = getIntent().getStringExtra("City");

//      gets you your location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//      check if internet is available
        if (isInternetAvailable(this)) {

//          check if the city from search is there or not, if yes then call api
            if (City != null) {
                SharedPreferences sharedLocationPref = getSharedPreferences("LocationCache", MODE_PRIVATE);
                sharedLocationPref.edit()
                        .remove("Lat")
                        .remove("Lon")
                        .remove("CityName")
                        .putString("CitySearchName",City)
                        .apply();

                getCurrentWeather(City, aqi);
                getDaysForecast(City, days, aqi, alerts);
                cityName.setText(City);

//          checks if location is granted or not
            } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                currentLocation();

//          if no location permission get one
            } else {
                locationPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }

//      if no internet get a pop up dialog
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

    //  create ion resume so when the activity pauses you start from where you left
    protected void onResume(){
        super.onResume();
        String CitySearch = getIntent().getStringExtra("City");
        if (isInternetAvailable(this) && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (isLocation(this) && CitySearch==null) {
                progressBar.setVisibility(View.VISIBLE);
                currentLocation();

            } else if (!isLocation(this) && CitySearch==null && !locationDialogShown) {
                locationDialogShown = true;
                locationDialog();

            } else if (CitySearch != null){
                SharedPreferences sharedLocationPref = getSharedPreferences("LocationCache", MODE_PRIVATE);
                sharedLocationPref.edit()
                        .remove("Lat")
                        .remove("Lon")
                        .remove("CityName")
                        .putString("CitySearchName",CitySearch)
                        .apply();
                loadData();
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

//                      set recycler views Adapter to get and display data as we get it from API
                        weatherDayItemAdapter = new WeatherDayItemAdapter(MainActivity.this, forecastResponse.getForecast().getForecastDay(), item -> {
                            i = item;

                            if (weatherDetails.getCurrent().getIsDay() == 1) {
                                sunrise.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getMoonriseTime());
                                sunrise_moonrise.setText("Moonrise");
                                sunriseMoonriseImage.setImageResource(R.drawable.mountain_moon_svgrepo_com);
                                sunset.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getSunsetTime());
                                sunset_moonset.setText("Sunset");
                                sunsetMoonsetImage.setImageResource(R.drawable.sunset_svgrepo_com);
                            } else{
                                sunrise.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getSunriseTime());
                                sunrise_moonrise.setText("Sunrise");
                                sunriseMoonriseImage.setImageResource(R.drawable.sunrise_over_mountains_svgrepo_com);
                                sunset.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getMoonsetTime());
                                sunset_moonset.setText("Moonset");
                                sunsetMoonsetImage.setImageResource(R.drawable.moon_svgrepo_com);
                            }

                            String imageUrl = "https://" +forecastResponse.getForecast().getForecastDay().get(i).getDay().getDayCondition().getImage();
                            temp.setText(String.valueOf(forecastResponse.getForecast().getForecastDay().get(i).getDay().getMinDayTemp_c()+ "°c"));
                            weather.setText(String.valueOf(forecastResponse.getForecast().getForecastDay().get(i).getDay().getDayCondition().getDayConText()));
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(weatherImage);

//                    set the last cardview items
                            feelsLike.setText(String.valueOf(weatherDetails.getCurrent().getFeelsLike_c()+"°c"));
                            winds.setText(String.valueOf(weatherDetails.getCurrent().getWindSpeedKmph()+" Km/h"));
                            humidity.setText(String.valueOf(weatherDetails.getCurrent().getHumidity()));
                            dew.setText(String.valueOf(weatherDetails.getCurrent().getDewpoint_c()+ "°c"));


                            weatherHourItemAdapter = new WeatherHourItemAdapter(MainActivity.this, forecastResponse.getForecast().getForecastDay().get(i).getHour());
                            DayHourForecast.setAdapter(weatherHourItemAdapter);

                            if (weatherDetails.getCurrent() != null) {
//                          checks weather day or night
                                boolean isDay = weatherDetails.getCurrent().getIsDay() == 1;
//                          apply color changes according to day or night
                                applyDayNightUI(isDay);
                            }

                        });
                        DayWeatherForecast.setAdapter(weatherDayItemAdapter);

                        weatherHourItemAdapter = new WeatherHourItemAdapter(MainActivity.this, forecastResponse.getForecast().getForecastDay().get(i).getHour());
                        DayHourForecast.setAdapter(weatherHourItemAdapter);

//                      checks weather details isnt empty
                        if (weatherDetails.getCurrent() != null) {
//                          checks weather day or night
                            boolean isDay = weatherDetails.getCurrent().getIsDay() == 1;
//                          apply color changes according to day or night
                            applyDayNightUI(isDay);
                        }

//                      checks if its day or night and set the item text
                        if (weatherDetails.getCurrent().getIsDay() == 1) {
                            sunrise.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getMoonriseTime());
                            sunrise_moonrise.setText("Moonrise");
                            sunriseMoonriseImage.setImageResource(R.drawable.mountain_moon_svgrepo_com);
                            sunset.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getSunsetTime());
                            sunset_moonset.setText("Sunset");
                            sunsetMoonsetImage.setImageResource(R.drawable.sunset_svgrepo_com);
                        } else{
                            sunrise.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getSunriseTime());
                            sunrise_moonrise.setText("Sunrise");
                            sunriseMoonriseImage.setImageResource(R.drawable.sunrise_over_mountains_svgrepo_com);
                            sunset.setText(forecastResponse.getForecast().getForecastDay().get(i).getAstro().getMoonsetTime());
                            sunset_moonset.setText("Moonset");
                            sunsetMoonsetImage.setImageResource(R.drawable.moon_svgrepo_com);
                        }

//                      check weather there are any alerts
                        if (!forecastResponse.getAlerts().getAlertList().isEmpty() && !alertShown){

//                          sets alert shown true as boolean
                            alertShown = true;

                            String title = forecastResponse.getAlerts().getAlertList().get(0).getMsgType();
                            String alertMsg = forecastResponse.getAlerts().getAlertList().get(0).getEvent()
                                    +"\n"+ forecastResponse.getAlerts().getAlertList().get(0).getHeadline();

                            WeatherAlertDialog(title,alertMsg);
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
//                  set search adapter
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

//      get text from search view
//      add text change listener text watcher
        citySearchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            // set on text changes listener
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//              checks if weather data is there and set theme
                if (weatherDetails.getCurrent() != null) {
                    boolean isDay = weatherDetails.getCurrent().getIsDay() == 1;
                    applyDayNightUI(isDay);
                }
//              store char sequence in variable
                String query = charSequence.toString();

//              set the number of charecter after which we search
                if (query.length() > 2) {

//                  call our fetch city api call function
                    fetchCity(query);
                }
            }
        });
    }

    //  function checks if internet is available
    public boolean isInternetAvailable(Context context) {
//      connectivity manager gets system service info like connectivity etc
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//      checks if connectivity manager is empty
        if (cm != null) {
//          checks network active or not
            Network network = cm.getActiveNetwork();
//           if null return
            if (network == null) return false;

//            checks if network is working
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    public boolean isLocation(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return lm.isLocationEnabled();
            }
        }
        return false;
    }


    //   functions helps us set all text color as per our theme
    private void setAllTextColors(ViewGroup root, int color) {
//      we use for loop to get every root item, child
        for (int i = 0; i < root.getChildCount(); i++) {
//          we say that view named as child has data of child at position i
            View child = root.getChildAt(i);

//          if child id is search view then nothing happen
            if (child.getId() == R.id.searchView) {
                continue;

//          gets all text views and set there text color to color
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);

//          for a view group like linear layout etc, set all text color of view group child as color
            } else if (child instanceof ViewGroup) {
                setAllTextColors((ViewGroup) child, color);
            }
        }
    }


    //    helps to apply Day night ui color and background
    private void applyDayNightUI(boolean isDay) {

//      isDay checks weather its day or night
//      Text color when day and when night
        int textColor = isDay ? Color.BLACK : Color.WHITE;

//      sets  Background on if day or night accordingly
        backgroundImage.setImageResource(isDay ? R.drawable.full_image : R.drawable.background);

//      gets the root layout i.e main
        ViewGroup rootLayout = findViewById(R.id.main);
//      set all text color in root layout according to the day night seen above
        setAllTextColors(rootLayout, textColor);


//      set text color for RecyclerView adapter items
        if (weatherDayItemAdapter != null && weatherHourItemAdapter != null) {
//          in adapter we create a function called text color
//          and then call set color individually then add it here so it change when its needed
//          and notify data changed
            weatherDayItemAdapter.setTextColor(textColor);
            weatherHourItemAdapter.setTextColor(textColor);
            weatherDayItemAdapter.notifyDataSetChanged();
            weatherHourItemAdapter.notifyDataSetChanged();
        }
    }

    private void loadData(){

        if (!isLocation(this)){
            SharedPreferences sharedPrefs = getSharedPreferences("LocationCache", MODE_PRIVATE);

            if (sharedPrefs.contains("Lat")) {
                String CityName = sharedPrefs.getString("CityName", "");
                String Lat = sharedPrefs.getString("Lat", "0.0");
                String Lon = sharedPrefs.getString("Lon", "0.0");
                String City = Lat + "," + Lon;
                cityName.setText(CityName);

                getDaysForecast(City, days, aqi, alerts);
                getCurrentWeather(City, aqi);

            } else if (sharedPrefs.contains("CitySearchName")) {
                String CityName = sharedPrefs.getString("CitySearchName", "");
                cityName.setText(CityName);

                getDaysForecast(CityName, days, aqi, alerts);
                getCurrentWeather(CityName, aqi);
            }
        }

    }

    private void currentLocation(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    progressBar.setVisibility(View.GONE);

                    if (location != null) {
//                  if location is there get lat and lon of it
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

//                  Use try catch for geocoder getting your locations name
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

//                      create string city, get city name from shared prefs and then run api
                        String City = latitude + "," + longitude;
                        String CityName = getSharedPreferences("LocationCache", MODE_PRIVATE).getString("CityName", "");
                        cityName.setText(CityName);

                        getDaysForecast(City, days, aqi, alerts);
                        getCurrentWeather(City, aqi);

                    }
                }
            });
        }
    }

    private void locationDialog(){
        AlertDialog.Builder locationDialog = new AlertDialog.Builder(this);
        locationDialog.setTitle("Permission Required")
                .setMessage("Turn On Location it is required to get data of your current location")
                .setPositiveButton("Turn On", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        locationDialog.show();
    }


    private void WeatherAlertDialog(String title, String alertMsg){

        boolean isDay = weatherDetails.getCurrent().getIsDay() == 1;
        int backgroundColor = isDay ? Color.WHITE : Color.parseColor("#292941");
        int textColor = isDay ? Color.BLACK : Color.WHITE;


//      build Alert dialog using builder
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

//      inflate custom layout using layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alerts_dialog, null);

//      sets background for dialog as per day/night
        dialogView.setBackgroundColor(backgroundColor);

//      sets text color for dialog as per day/ night
        setAllTextColors((ViewGroup) dialogView,textColor);

//      set alert dialog view
        alertDialog.setView(dialogView);

//      gets the items in alert dialog
        TextView alertTitle, alertmsg;
        Button okBtn;

//      initialize item in alert dialog
        alertTitle = dialogView.findViewById(R.id.alertType);
        alertmsg = dialogView.findViewById(R.id.alertDescription);
        okBtn = dialogView.findViewById(R.id.okButton);

//      creates alert dialog
        AlertDialog alert = alertDialog.create();

//      set title and msg in alert dialog
        alertTitle.setText(title);
        alertmsg.setText(alertMsg);

//      sets button functionality
        okBtn.setOnClickListener(view -> {
            alert.dismiss();
        });

//      shows alert
        alert.show();
    }

}