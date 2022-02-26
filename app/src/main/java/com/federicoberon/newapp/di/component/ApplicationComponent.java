package com.federicoberon.newapp.di.component;

import android.app.Application;
import android.content.Context;

import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.di.ApplicationContext;
import com.federicoberon.newapp.di.module.ApplicationModule;
import com.federicoberon.newapp.di.module.AudioManagerModule;
import com.federicoberon.newapp.di.module.DatabaseModule;
import com.federicoberon.newapp.di.module.RingtoneManagerModule;
import com.federicoberon.newapp.service.RescheduleAlarmsService;
import com.federicoberon.newapp.ui.about.AboutFragment;
import com.federicoberon.newapp.ui.addalarm.AddAlarmFragment;
import com.federicoberon.newapp.ui.addalarm.postpone.PostponePickerFragment;
import com.federicoberon.newapp.ui.addalarm.melody.RingtoneListFragment;
import com.federicoberon.newapp.ui.addalarm.melody.RingtonePickerFragment;
import com.federicoberon.newapp.ui.addalarm.repeat.RepeatPickerFragment;
import com.federicoberon.newapp.ui.addalarm.vibration.VibratorListFragment;
import com.federicoberon.newapp.ui.alarm.AlarmActivity;
import com.federicoberon.newapp.ui.home.HomeFragment;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DatabaseModule.class,
        RingtoneManagerModule.class,
        AudioManagerModule.class
})
public interface ApplicationComponent {
    // This tells Dagger that LoginActivity requests injection so the graph needs to
    // satisfy all the dependencies of the fields that LoginActivity is injecting.
    void inject (HomeFragment homeFragment);
    void inject (AboutFragment aboutFragment);
    void inject (MainActivity mainActivity);
    void inject (AlarmActivity alarmActivity);
    void inject (RescheduleAlarmsService rescheduleAlarmsService);
    void inject (AddAlarmFragment addAlarmFragment);
    void inject (RingtoneListFragment ringtoneListFragment);
    void inject (VibratorListFragment ringtoneListFragment);
    void inject (RingtonePickerFragment ringtonePickerFragment);
    void inject (PostponePickerFragment postponePickerFragment);
    void inject (RepeatPickerFragment repeatPickerFragment);

    //AddAlarmComponent.Factory addAlarmComponent();

    @ApplicationContext
    Context getContext();

    Application getApplication();
}
