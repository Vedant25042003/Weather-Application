package com.example.weatherapplication.Api;

import com.example.weatherapplication.Models.WeatherDaysForecast;
import com.example.weatherapplication.Models.WeatherDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("current.json")
    Call<WeatherDetails> getCurrentWeather(@Query("q") String city, @Query("aqi") String aqi);

    @GET("/forecast.json")
    Call<WeatherDaysForecast>getWeatherForecast(@Query("q") String city,
                                                @Query("days") int days,
                                                @Query("aqi") String aqi,
                                                @Query("alerts") String alerts);

}
