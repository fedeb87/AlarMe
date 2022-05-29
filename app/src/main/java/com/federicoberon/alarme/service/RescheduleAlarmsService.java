package com.federicoberon.alarme.service;

import static com.federicoberon.alarme.MainActivity.ENABLE_LOGS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.utils.AlarmManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Simply call AlarmService for each alarm in the database
 */
public class RescheduleAlarmsService extends LifecycleService {

    private static final String LOG_TAG = "RescheduleAlarmsService";

    @Inject
    SharedPreferences sharedPref;

    @Inject
    AlarmRepository alarmRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {

        ((AlarMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mDisposable.add(alarmRepository.getAllAlarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarms -> {
                            for (AlarmEntity a : alarms) {
                                if (a.isStarted()) {
                                    AlarmManager.schedule(getApplicationContext(), a);
                                }
                            }
                        },
                        throwable -> {
                            if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                Log.e(LOG_TAG, "Unable to get milestones: ", throwable);
                        }));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }
}
