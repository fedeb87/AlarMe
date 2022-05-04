package com.federicoberon.alarme.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import io.reactivex.Observable;

public interface WeatherServiceTwo {

    @GET("data/2.5/onecall")
    Observable<WeatherResponseTwo> getWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("exclude") String exclude, @Query("units") String units, @Query("appid") String key);

    class Factory {
        public static WeatherServiceTwo create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherServiceTwo.class);
        }
    }
}
