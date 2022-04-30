package com.federicoberon.alarme.di.component;

import com.federicoberon.alarme.di.TestDatabaseModule;
import com.federicoberon.alarme.di.module.ApplicationModule;
import com.federicoberon.alarme.di.module.AudioManagerModule;
import com.federicoberon.alarme.di.module.RingtoneManagerModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        TestDatabaseModule.class,
        RingtoneManagerModule.class,
        AudioManagerModule.class
})
public interface TestApplicationComponent extends ApplicationComponent {
}