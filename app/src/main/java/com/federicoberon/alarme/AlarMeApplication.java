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
import com.federicoberon.alarme.retrofit.HoroscopeService;
import com.federicoberon.alarme.retrofit.HoroscopeServiceTwo;
import com.federicoberon.alarme.retrofit.WeatherService;
import com.federicoberon.alarme.retrofit.WeatherServiceTwo;

import dagger.Provides;

/**
 * Android Application class. Used for accessing singletons.
 */
public class AlarMeApplication extends Application {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";

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
}
