package com.federicoberon.alarme.retrofit;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "root", strict = false)
public class WeatherResponse {

    @Element(name = "current")
    private CurrentWeather currentWeather;

    @Element(name = "forecast")
    private Forecast dayForecast;

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public Forecast getDayForecast() {
        return dayForecast;
    }

    public void setDayForecast(Forecast dayForecast) {
        this.dayForecast = dayForecast;
    }

    @Root
    public static class CurrentWeather {

        @Element(name = "temp_c")
        private float temp_c;

        @Element(name = "temp_f")
        private float temp_f;

        @Element(name = "condition")
        private Condition condition;

        public float getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(float temp_c) {
            this.temp_c = temp_c;
        }

        public float getTemp_f() {
            return temp_f;
        }

        public void setTemp_f(float temp_f) {
            this.temp_f = temp_f;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    @Root
    public static class Condition {

        @Element(name = "code")
        private int id;

        @Element(name = "text")
        private String condition;

        @Element(name = "icon")
        private String icon_url;

        public String getCondition() {
            return condition;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }
    }

    @Root
    public static class Forecast {

        @Element(name = "forecastday")
        private Forecastday forecastday;

        public Forecastday getForecastday() {
            return forecastday;
        }

        public void setForecastday(Forecastday forecastday) {
            this.forecastday = forecastday;
        }
    }

    @Root
    public static class Forecastday {

        @Element(name = "day")
        Day day;

        public Day getDay() {
            return day;
        }

        public void setDay(Day day) {
            this.day = day;
        }
    }

    @Root
    public static class Day {

        @Element(name = "maxtemp_c")
        private float maxtemp_c;

        @Element(name = "maxtemp_f")
        private float maxtemp_f;

        @Element(name = "mintemp_c")
        private float mintemp_c;

        @Element(name = "mintemp_f")
        private float mintemp_f;

        @Element(name = "daily_chance_of_rain")
        private String daily_chance_of_rain;

        @Element(name = "daily_chance_of_snow")
        private float daily_chance_of_snow;

        @Element(name = "condition")
        private Condition condition;

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public float getMaxtemp_c() {
            return maxtemp_c;
        }

        public void setMaxtemp_c(float maxtemp_c) {
            this.maxtemp_c = maxtemp_c;
        }

        public float getMaxtemp_f() {
            return maxtemp_f;
        }

        public void setMaxtemp_f(float maxtemp_f) {
            this.maxtemp_f = maxtemp_f;
        }

        public float getMintemp_c() {
            return mintemp_c;
        }

        public void setMintemp_c(float mintemp_c) {
            this.mintemp_c = mintemp_c;
        }

        public float getMintemp_f() {
            return mintemp_f;
        }

        public void setMintemp_f(float mintemp_f) {
            this.mintemp_f = mintemp_f;
        }

        public String getDaily_chance_of_rain() {
            return daily_chance_of_rain;
        }

        public void setDaily_chance_of_rain(String daily_chance_of_rain) {
            this.daily_chance_of_rain = daily_chance_of_rain;
        }

        public float getDaily_chance_of_snow() {
            return daily_chance_of_snow;
        }

        public void setDaily_chance_of_snow(float daily_chance_of_snow) {
            this.daily_chance_of_snow = daily_chance_of_snow;
        }
    }
}