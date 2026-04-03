package com.example.weatherapplication.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDaysForecast {

    @SerializedName("forecast")
    private Forecast forecast;

    public Forecast getForecast(){
        return forecast;
    }

    public static class Forecast{
        @SerializedName("forecastday")
        private List<ForecastDay> forecastDay;

        public List<ForecastDay> getForecastDay() {
            return forecastDay;
        }

        public static class ForecastDay{
            @SerializedName("date")
            private String date;
            @SerializedName("day")
            private Day day;

            public String getDate() {
                return date;
            }

            public Day getDay() {
                return day;
            }
            public static class Day {
                @SerializedName("maxtemp_c")
                private double maxtemp_c;

                public double getMaxtemp() {
                    return maxtemp_c;
                }
            }
        }
    }

}
