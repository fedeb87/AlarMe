package com.federicoberon.newapp.retrofit;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WeatherResponseTwo {
    @SerializedName("current")
    @Expose
    public Current current;
    @SerializedName("daily")
    @Expose
    public List<Daily> daily = null;


    public static class Current {
        @SerializedName("temp")
        @Expose
        public Double temp;
        @SerializedName("weather")
        @Expose
        public List<Weather> weather = null;

    }

    public static class Daily {
        @SerializedName("temp")
        @Expose
        public Temp temp;

        @SerializedName("weather")
        @Expose
        public List<DailyWeather> dailyWeather = null;

    }

    public static class Temp {
        @SerializedName("min")
        @Expose
        public Double min;
        @SerializedName("max")
        @Expose
        public Double max;
    }

    public static class Weather {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("main")
        @Expose
        public String main;
        @SerializedName("description")
        @Expose
        public String description;

    }

    public static class DailyWeather {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("main")
        @Expose
        public String main;
        @SerializedName("description")
        @Expose
        public String description;

    }
}