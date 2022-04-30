package com.federicoberon.alarme;

import android.content.Context;

import com.federicoberon.alarme.di.TestDatabaseModule;
import com.federicoberon.alarme.di.component.ApplicationComponent;
import com.federicoberon.alarme.di.module.ApplicationModule;
import com.federicoberon.alarme.di.module.AudioManagerModule;
import com.federicoberon.alarme.di.component.DaggerTestApplicationComponent;
import com.federicoberon.alarme.di.module.RingtoneManagerModule;

public class AlarMeTestApplication extends AlarMeApplication{

    @Override
    public ApplicationComponent initializeComponent() {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .testDatabaseModule(new TestDatabaseModule(this))
                .ringtoneManagerModule(new RingtoneManagerModule())
                .audioManagerModule(new AudioManagerModule())
                .build();
    }

    public static AlarMeTestApplication get(Context context) {
        return (AlarMeTestApplication) context.getApplicationContext();
    }

}
