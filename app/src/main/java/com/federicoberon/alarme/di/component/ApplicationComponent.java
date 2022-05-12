package com.federicoberon.alarme.di.component;

import android.app.Application;
import android.content.Context;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.broadcastreceiver.ActionReceiver;
import com.federicoberon.alarme.datasource.dao.AlarmDao;
import com.federicoberon.alarme.datasource.dao.MelodyDao;
import com.federicoberon.alarme.di.ApplicationContext;
import com.federicoberon.alarme.di.module.ApplicationModule;
import com.federicoberon.alarme.di.module.AudioManagerModule;
import com.federicoberon.alarme.di.module.DatabaseModule;
import com.federicoberon.alarme.di.module.RingtoneManagerModule;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.service.RescheduleAlarmsService;
import com.federicoberon.alarme.ui.about.AboutFragment;
import com.federicoberon.alarme.ui.addalarm.AddAlarmFragment;
import com.federicoberon.alarme.ui.addalarm.postpone.PostponePickerFragment;
import com.federicoberon.alarme.ui.addalarm.melody.RingtoneListFragment;
import com.federicoberon.alarme.ui.addalarm.melody.RingtonePickerFragment;
import com.federicoberon.alarme.ui.addalarm.repeat.RepeatPickerFragment;
import com.federicoberon.alarme.ui.addalarm.vibration.VibratorListFragment;
import com.federicoberon.alarme.ui.alarm.AlarmActivity;
import com.federicoberon.alarme.ui.home.HomeFragment;
import com.federicoberon.alarme.ui.themes.ThemesFragment;

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
    void inject (AlarmService alarmService);
    void inject(ThemesFragment themesFragment);

    void inject(ActionReceiver actionReceiver);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    AlarmDao getAlarmDao();

    MelodyDao getMelodyDao();
}
