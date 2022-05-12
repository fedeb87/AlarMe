package com.federicoberon.alarme.di;

import android.media.AudioManager;

import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockAddAlarmViewModelModule {

    @Provides
    @Singleton
    AddAlarmViewModel provideAddAlarmViewModel(AudioManager audioManager
            , AlarmRepository alarmRepository) {
        return Mockito.mock(AddAlarmViewModel.class, Mockito.withSettings().useConstructor(audioManager, alarmRepository));
    }
}
