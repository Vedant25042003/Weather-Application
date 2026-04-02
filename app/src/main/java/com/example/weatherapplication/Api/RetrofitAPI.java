package com.example.weatherapplication.Api;

import com.example.weatherapplication.Models.WeatherDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("current.json")
    Call<WeatherDetails> getCurrentWeather(@Query("q") String city, @Query("aqi") String aqi);

}
