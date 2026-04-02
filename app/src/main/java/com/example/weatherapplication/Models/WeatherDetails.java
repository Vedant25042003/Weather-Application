package com.example.weatherapplication.Models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class WeatherDetails {

    private Current current;

    public Current getCurrent(){
        return current;
    }

    public static class Current{
        private Condition condition;

        @SerializedName("temp_c")
        private float currentTemperature;

        public float getCurrentTemperature() {
            return currentTemperature;
        }

        public Condition getCondition(){
            return condition;
        }

        public static class Condition{
            @SerializedName("text")
            private String weatherText;

            @SerializedName("icon")
            private String weatherIcon;

            public String getWeatherText() {
                return weatherText;
            }

            public String getWeatherIcon(){
                return weatherIcon;
            }
        }
    }
}
