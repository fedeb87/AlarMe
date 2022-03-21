package com.federicoberon.newapp.retrofit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public interface HoroscopeServiceTwo {

    //daily-aries-horoscope
    @POST("{sign}")
    Call<HoroscopeTwo> getHoroscope(@Path("sign") String sign);

    class Factory {
        public static HoroscopeServiceTwo create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://feeds.feedburner.com/clickastro/")
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(HoroscopeServiceTwo.class);
        }
    }
}
