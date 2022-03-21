package com.federicoberon.newapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import com.federicoberon.newapp.datasource.AppDatabase;
import com.federicoberon.newapp.di.component.ApplicationComponent;
import com.federicoberon.newapp.di.component.DaggerApplicationComponent;
import com.federicoberon.newapp.di.module.ApplicationModule;
import com.federicoberon.newapp.di.module.AudioManagerModule;
import com.federicoberon.newapp.di.module.DatabaseModule;
import com.federicoberon.newapp.di.module.RingtoneManagerModule;
import com.federicoberon.newapp.retrofit.HoroscopeService;
import com.federicoberon.newapp.retrofit.HoroscopeServiceTwo;
import com.federicoberon.newapp.ui.addalarm.AddAlarmComponent;

/**
 * Android Application class. Used for accessing singletons.
 */
public class SimpleRemindMeApplication extends Application {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appComponent = initializeComponent();
    private HoroscopeService horoscopeService;
    private HoroscopeServiceTwo horoscopeServiceTwo;


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

    public static SimpleRemindMeApplication get(Context context) {
        return (SimpleRemindMeApplication) context.getApplicationContext();
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

    public HoroscopeService getHoroscopeService() {
        if (this.horoscopeService == null) {
            this.horoscopeService = HoroscopeService.Factory.create();
        }
        return horoscopeService;
    }

    public HoroscopeServiceTwo getHoroscopeServiceTwo() {
        if (this.horoscopeServiceTwo == null) {
            this.horoscopeServiceTwo = HoroscopeServiceTwo.Factory.create();
        }
        return horoscopeServiceTwo;
    }
}
