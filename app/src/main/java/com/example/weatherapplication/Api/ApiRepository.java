package com.example.weatherapplication.Api;

import com.example.weatherapplication.Models.WeatherDaysForecast;
import com.example.weatherapplication.Models.WeatherDetails;

import java.util.List;

import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.http.Query;

public class ApiRepository {
    public RetrofitAPI retrofitAPI;

    public ApiRepository(){
        retrofitAPI = RetrofitClient.getApi();
    }

    public Call<WeatherDetails> getCurrentTemp(String City, String aqi){
        return retrofitAPI.getCurrentWeather(City, aqi);
    }

    public Call<WeatherDaysForecast> getDaysForecast(String City, int days, String aqi, String alerts){
        return retrofitAPI.getWeatherForecast(City, days, aqi, alerts);
    }
}
