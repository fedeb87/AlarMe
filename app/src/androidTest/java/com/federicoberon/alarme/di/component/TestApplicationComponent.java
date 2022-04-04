package com.federicoberon.alarme.di.component;

import com.federicoberon.alarme.di.TestDatabaseModule;
import com.federicoberon.alarme.di.module.ApplicationModule;
import com.federicoberon.alarme.di.module.AlarmManagerModule;
import com.federicoberon.alarme.ui.AddAlarmActivityTest;
import com.federicoberon.alarme.ui.MainActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        TestDatabaseModule.class,
        AlarmManagerModule.class
})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivityTest);
    void inject(AddAlarmActivityTest addMilestoneActivityTest);
}