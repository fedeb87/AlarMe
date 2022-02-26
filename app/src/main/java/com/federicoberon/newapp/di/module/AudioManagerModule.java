package com.federicoberon.newapp.di.module;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;

import com.federicoberon.newapp.di.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module
public class AudioManagerModule {
    @Provides
    AudioManager provideAudioManager(@ApplicationContext Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
}
