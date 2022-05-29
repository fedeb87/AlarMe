package com.federicoberon.alarme;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import com.federicoberon.alarme.datasource.AppDatabase;
import com.federicoberon.alarme.di.component.ApplicationComponent;
import com.federicoberon.alarme.di.component.DaggerApplicationComponent;
import com.federicoberon.alarme.di.module.ApplicationModule;
import com.federicoberon.alarme.di.module.AudioManagerModule;
import com.federicoberon.alarme.di.module.DatabaseModule;
import com.federicoberon.alarme.api.WeatherService;
import com.federicoberon.alarme.api.WeatherServiceTwo;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Android Application class. Used for accessing singletons.
 */
public class AlarMeApplication extends Application {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    private WeatherService weatherService;
    private WeatherServiceTwo weatherServiceTwo;
    private Scheduler defaultSubscribeScheduler;

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appComponent = initializeComponent();


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannnel();
    }

    public ApplicationComponent initializeComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .audioManagerModule(new AudioManagerModule())
                .build();
    }

    public static AlarMeApplication get(Context context) {
        return (AlarMeApplication) context.getApplicationContext();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(true);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }

    public void setWeatherService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public void setWeatherServiceTwo(WeatherServiceTwo weatherService) {
        this.weatherServiceTwo = weatherService;
    }

    public WeatherService getWeatherService() {
        if (this.weatherService == null) {
            this.weatherService = WeatherService.Factory.create();
        }
        return weatherService;
    }

    public WeatherServiceTwo getWeatherServiceTwo() {
        if (this.weatherServiceTwo == null) {
            this.weatherServiceTwo = WeatherServiceTwo.Factory.create();
        }
        return weatherServiceTwo;
    }
}
