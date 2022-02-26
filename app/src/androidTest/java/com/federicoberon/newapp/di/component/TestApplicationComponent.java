package com.federicoberon.newapp.di.component;

import com.federicoberon.newapp.di.TestDatabaseModule;
import com.federicoberon.newapp.di.module.ApplicationModule;
import com.federicoberon.newapp.di.module.AlarmManagerModule;
import com.federicoberon.newapp.ui.AddAlarmActivityTest;
import com.federicoberon.newapp.ui.MainActivityTest;

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