package com.federicoberon.alarme.di.module;

import android.media.AudioManager;

import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AddAlarmViewModelModule {

    @Provides
    @Singleton
    AddAlarmViewModel provideAddAlarmViewModel(AudioManager audioManager
            , AlarmRepository alarmRepository) {
        return new AddAlarmViewModel(audioManager, alarmRepository);
    }
}
