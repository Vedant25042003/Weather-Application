package com.example.weatherapplication.Api;

import com.example.weatherapplication.Models.WeatherDetails;

import java.util.List;

import okhttp3.Cache;
import retrofit2.Call;

public class ApiRepository {
    public RetrofitAPI retrofitAPI;

    public ApiRepository(){
        retrofitAPI = RetrofitClient.getApi();
    }

    public Call<WeatherDetails> getCurrentTemp(String City, String aqi){
        return retrofitAPI.getCurrentWeather(City, aqi);
    }
}
