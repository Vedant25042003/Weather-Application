package com.example.weatherapplication.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecast {

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
                private float maxDaytemp_c;

                @SerializedName("condition")
                private DayCondition condition;

                public DayCondition getDayCondition() {
                    return condition;
                }

                public float getMaxtemp() {
                    return maxDaytemp_c;
                }

                public static class DayCondition{

                    @SerializedName("text")
                    private String dayText;

                    @SerializedName("icon")
                    private String image;

                    public String getDayText() {
                        return dayText;
                    }

                    public String getImage() {
                        return image;
                    }

                }

            }
        }

    }
}
