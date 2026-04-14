package com.example.weatherapplication.Api;

import com.example.weatherapplication.Models.SearchModel;
import com.example.weatherapplication.Models.WeatherForecast;
import com.example.weatherapplication.Models.WeatherDetails;

import java.util.List;

import retrofit2.Call;

public class ApiRepository {
    public RetrofitAPI retrofitAPI;

    public ApiRepository(){
        retrofitAPI = RetrofitClient.getApi();
    }

    public Call<WeatherDetails> getCurrentTemp(String City, String aqi){
        return retrofitAPI.getCurrentWeather(City, aqi);
    }

    public Call<WeatherForecast> getDaysForecast(String City, int days, String aqi, String alerts){
        return retrofitAPI.getWeatherForecast(City, days, aqi, alerts);
    }

    public Call<List<SearchModel>> getSearchCityName(String City){
        return retrofitAPI.getCityNames(City);
    }
}
