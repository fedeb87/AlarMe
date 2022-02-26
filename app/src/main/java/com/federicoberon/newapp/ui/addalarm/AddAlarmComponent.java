package com.federicoberon.newapp.ui.addalarm;

import com.federicoberon.newapp.di.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
// Definition of a Dagger subcomponent
@Subcomponent
public interface AddAlarmComponent {

    // Factory to create instances of RegistrationComponent
    @Subcomponent.Factory
    interface Factory {
        AddAlarmComponent create();
    }
/*
    void inject (AddAlarmFragment addAlarmFragment);
    void inject (RingtoneListFragment ringtoneListFragment);
    void inject (RingtonePickerFragment ringtonePickerFragment);

 */
}
