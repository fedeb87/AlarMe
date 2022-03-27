package com.federicoberon.newapp.retrofit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {

    // todo mediio cagada que las key esten aca, la app tendria que leerlas de algun lugar de internet por si necesito actualizarla

    @GET("v1/forecast.xml")
    Call<WeatherResponse> getWeather(@Query("key") String key, @Query("q") double[] query, @Query("day") int day);

    class Factory {
        public static WeatherService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.weatherapi.com/")
                    .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(WeatherService.class);
        }
    }
}
