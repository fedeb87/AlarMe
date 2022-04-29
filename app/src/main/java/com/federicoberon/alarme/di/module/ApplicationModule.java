package com.federicoberon.alarme.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.federicoberon.alarme.di.ApplicationContext;
import com.federicoberon.alarme.retrofit.HoroscopeService;
import com.federicoberon.alarme.retrofit.HoroscopeServiceTwo;
import com.federicoberon.alarme.retrofit.WeatherService;
import com.federicoberon.alarme.retrofit.WeatherServiceTwo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application mApplication;
    private HoroscopeService horoscopeService;
    private HoroscopeServiceTwo horoscopeServiceTwo;
    private WeatherService weatherService;
    private WeatherServiceTwo weatherServiceTwo;

    public ApplicationModule(Application app) {
        mApplication = app;
    }

    @Singleton
    @Provides
    @ApplicationContext
    public Context provideContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }


    @Provides
    @Singleton
    public WeatherService getWeatherService() {
        if (this.weatherService == null) {
            this.weatherService = WeatherService.Factory.create();
        }
        return weatherService;
    }

    @Provides
    @Singleton
    public WeatherServiceTwo getWeatherServiceTwo() {
        if (this.weatherServiceTwo == null) {
            this.weatherServiceTwo = WeatherServiceTwo.Factory.create();
        }
        return weatherServiceTwo;
    }

    @Provides
    @Singleton
    public HoroscopeService getHoroscopeService() {
        if (this.horoscopeService == null) {
            this.horoscopeService = HoroscopeService.Factory.create();
        }
        return horoscopeService;
    }

    @Provides
    @Singleton
    public HoroscopeServiceTwo getHoroscopeServiceTwo() {
        if (this.horoscopeServiceTwo == null) {
            this.horoscopeServiceTwo = HoroscopeServiceTwo.Factory.create();
        }
        return horoscopeServiceTwo;
    }
}

