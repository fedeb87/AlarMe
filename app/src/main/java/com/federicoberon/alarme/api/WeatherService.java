package com.federicoberon.alarme.api;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("v1/forecast.xml")
    Observable<WeatherResponse> getWeather(@Query("key") String key, @Query("q") double[] query, @Query("day") int day);

    class Factory {
        public static WeatherService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.weatherapi.com/")
                    .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherService.class);
        }
    }
}
