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
        @SerializedName("is_day")
        private int isDay;
        @SerializedName("wind_kph")
        private float windSpeedKmph;
        @SerializedName("humidity")
        private float humidity;
        @SerializedName("feelslike_c")
        private float feelsLike_c;
        @SerializedName("dewpoint_c")
        private float dewpoint_c;

        public int getIsDay() {
            return isDay;
        }
        public float getWindSpeedKmph() {
            return windSpeedKmph;
        }
        public float getHumidity() {
            return humidity;
        }
        public float getFeelsLike_c() {
            return feelsLike_c;
        }
        public float getDewpoint_c() {
            return dewpoint_c;
        }

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
