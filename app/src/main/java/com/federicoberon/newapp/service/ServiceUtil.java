package com.federicoberon.newapp.service;

import android.content.Context;

import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.di.module.DatabaseModule;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.repositories.AlarmRepository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public class ServiceUtil {

    private AlarmRepository mAlarmRepository;

    public ServiceUtil(Context context) {
        mAlarmRepository = new AlarmRepository(((SimpleRemindMeApplication)context.getApplicationContext()).appComponent.getAlarmDao(),
                ((SimpleRemindMeApplication)context.getApplicationContext()).appComponent.getMelodyDao());
    }

    public Maybe<Long> disableAlarm(AlarmEntity alarmEntity){
        return mAlarmRepository.insertOrUpdateAlarm(alarmEntity);
    }

    public Flowable<List<AlarmEntity>> getAllAlarms() {
        return mAlarmRepository.getAllAlarms();
    }
}
