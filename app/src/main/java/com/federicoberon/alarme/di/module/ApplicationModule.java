package com.federicoberon.alarme.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.federicoberon.alarme.di.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application mApplication;

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
}

