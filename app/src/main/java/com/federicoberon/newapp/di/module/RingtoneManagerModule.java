package com.federicoberon.newapp.di.module;

import android.content.Context;
import android.media.RingtoneManager;

import com.federicoberon.newapp.di.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module
public class RingtoneManagerModule {
    @Provides
    RingtoneManager provideRingtoneManager(@ApplicationContext Context context) {
        RingtoneManager rm = new RingtoneManager(context);
        rm.setType(RingtoneManager.TYPE_RINGTONE);
        return rm;
    }
}
