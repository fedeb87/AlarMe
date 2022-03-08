package com.federicoberon.newapp.repositories;

import android.util.Log;

import com.federicoberon.newapp.datasource.dao.AlarmDao;
import com.federicoberon.newapp.datasource.dao.MelodyDao;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.model.MelodyEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Using the Room database as a data source.
 */
@Singleton
public class AlarmRepository implements AlarmDataSource {

    private final AlarmDao mAlarmDao;
    private final MelodyDao mMelodyDao;

    // @Inject lets Dagger know how to create instances of this object
    @Inject
    public AlarmRepository(AlarmDao alarmDao, MelodyDao melodyDao) {
        mAlarmDao = alarmDao;
        mMelodyDao = melodyDao;
    }

    @Override
    public Flowable<List<AlarmEntity>> getAllAlarms() {
        return mAlarmDao.getAllAlarm();
    }

    @Override
    public Flowable<List<AlarmEntity>> getFirstAlarmStarted() {
        return mAlarmDao.getFirstAlarmStarted();
    }

    @Override
    public Flowable<AlarmEntity> getAlarmById(long id) {
        return mAlarmDao.getAlarmById(id);
    }

    @Override
    public Flowable<List<AlarmEntity>> getAlarmByIds(List<Long> ids) {
        return mAlarmDao.getAlarmByIds(ids);
    }

    @Override
    public Flowable<List<AlarmEntity>> getAlarms(String filter) {
        String finalFilter = "%" +
                filter +
                "%";
        return mAlarmDao.getAlarm(finalFilter);
    }

    @Override
    public Maybe<Long> insertOrUpdateAlarm(AlarmEntity alarmEntity) {
       return mAlarmDao.insert(alarmEntity);
    }

    @Override
    public Completable deleteAlarm(long currentMilestoneId) {
        return mAlarmDao.deleteAlarm(currentMilestoneId);
    }

    @Override
    public Completable deleteAlarms(List<Long> ids) {
        return mAlarmDao.deleteAlarms(ids);
    }

    @Override
    public Flowable<List<MelodyEntity>> getAllMelodies() {
        return mMelodyDao.getAllMelodies();
    }

    public Flowable<MelodyEntity> getMelodyId(long id) {
        return mMelodyDao.getMelody(id);
    }

    public Flowable<MelodyEntity> getMelodyName(String name) {
        return mMelodyDao.getMelodyByTitle(name);
    }

}
