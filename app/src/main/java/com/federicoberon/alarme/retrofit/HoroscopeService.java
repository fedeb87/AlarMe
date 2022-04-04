package com.federicoberon.alarme.retrofit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HoroscopeService {

    /*
    @POST("/")
    Call<HoroscopeClass> horoscope(@Query("sign") String sign, @Query("day") String day);*/

    /*@POST("?sign={sign}&day=today")
    Call<Horoscope> getHoroscope(@Path("sign") String sign);*/


    @POST("/")
    Call<Horoscope> getHoroscope(@Query("sign") String sign, @Query("day") String day);

    class Factory {
        public static HoroscopeService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://aztro.sameerkumar.website/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(HoroscopeService.class);
        }
    }
}
