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
            @SerializedName("hour")
            private List<Hour> hour;

            public List<Hour> getHour() {
                return hour;
            }

            public String getDate() {
                return date;
            }

            public Day getDay() {
                return day;
            }

            public static class Day {

                @SerializedName("maxtemp_c")
                private float maxDaytemp_c;

                @SerializedName("mintemp_c")
                private float minDayTemp_c;

                @SerializedName("condition")
                private DayCondition condition;

                public DayCondition getDayCondition() {
                    return condition;
                }

                public float getMaxtemp() {
                    return maxDaytemp_c;
                }
                public float getMinDayTemp_c() {
                    return minDayTemp_c;
                }

                public static class DayCondition{

                    @SerializedName("text")
                    private String dayText;

                    @SerializedName("icon")
                    private String image;

                    public String getDayConText() {
                        return dayText;
                    }

                    public String getImage() {
                        return image;
                    }

                }

            }

            public static class Hour{
                @SerializedName("time")
                private String time;

                @SerializedName("temp_c")
                private String temp_c;

                @SerializedName("condition")
                private HourCondition hourCondition;

                public String getTime() {
                    return time;
                }

                public String getTemp_c() {
                    return temp_c;
                }


                public HourCondition getHourCondition() {
                    return hourCondition;
                }

                public static class HourCondition{
                    @SerializedName("text")
                    private String HourText;

                    @SerializedName("icon")
                    private String HourIcon;

                    public String getHourIcon() {
                        return HourIcon;
                    }

                    public String getHourText() {
                        return HourText;
                    }
                }
            }
        }

    }

    @SerializedName("alerts")
    private Alerts alerts;

    public Alerts getAlerts() {
        return alerts;
    }
    public static class Alerts{

        @SerializedName("alert")
        private List<Alert> alertList;

        public List<Alert> getAlertList() {
            return alertList;
        }
        public static class Alert{
            @SerializedName("event")
            private String Event;

            @SerializedName("msgtype")
            private String msgType;

            @SerializedName("headline")
            private String Headline;

            public String getHeadline() {
                return Headline;
            }

            public String getMsgType() {
                return msgType;
            }

            public String getEvent() {
                return Event;
            }
        }
    }
}
