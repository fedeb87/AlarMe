package com.federicoberon.alarme.api;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HoroscopeService {

    @POST("/")
    Observable<Horoscope> getHoroscope(@Query("sign") String sign, @Query("day") String day);

    class Factory {
        public static HoroscopeService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://aztro.sameerkumar.website/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(HoroscopeService.class);
        }
    }
}
