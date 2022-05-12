package com.federicoberon.alarme.di.module;

import android.content.Context;
import android.media.AudioManager;

import com.federicoberon.alarme.di.ApplicationContext;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class AudioManagerModule {
    @Provides
    AudioManager provideAudioManager(@ApplicationContext Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
}
