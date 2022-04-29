package com.federicoberon.alarme.service;

import android.content.Context;

import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public class ServiceUtil {

    private AlarmRepository mAlarmRepository;

    public ServiceUtil(Context context) {
        mAlarmRepository = new AlarmRepository(((AlarMeApplication)context.getApplicationContext()).appComponent.getAlarmDao(),
                ((AlarMeApplication)context.getApplicationContext()).appComponent.getMelodyDao());
    }

    public Maybe<Long> updateAlarm(AlarmEntity alarmEntity){
        return mAlarmRepository.insertOrUpdateAlarm(alarmEntity);
    }

    public Flowable<List<AlarmEntity>> getAllAlarms() {
        return mAlarmRepository.getAllAlarms();
    }
}
