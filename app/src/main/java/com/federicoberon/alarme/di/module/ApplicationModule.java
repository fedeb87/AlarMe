package com.federicoberon.alarme.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.api.HoroscopeService;
import com.federicoberon.alarme.api.HoroscopeServiceTwo;
import com.federicoberon.alarme.api.WeatherService;
import com.federicoberon.alarme.api.WeatherServiceTwo;
import com.federicoberon.alarme.di.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final AlarMeApplication mApplication;

    public ApplicationModule(Application app) {
        mApplication = (AlarMeApplication)app;
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
    public WeatherService provideWeatherService() {
        return mApplication.getWeatherService();
    }

    @Provides
    @Singleton
    public WeatherServiceTwo provideWeatherServiceTwo() {
        return mApplication.getWeatherServiceTwo();
    }

    @Provides
    @Singleton
    public HoroscopeService provideHoroscopeService() {
        return mApplication.getHoroscopeService();
    }

    @Provides
    @Singleton
    public HoroscopeServiceTwo provideHoroscopeServiceTwo() {
        return mApplication.getHoroscopeServiceTwo();
    }

}

