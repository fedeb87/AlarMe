package com.federicoberon.alarme.repositories;

import com.federicoberon.alarme.datasource.dao.AlarmDao;
import com.federicoberon.alarme.datasource.dao.MelodyDao;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.MelodyEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

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
    public Maybe<Long> insertOrUpdateAlarm(AlarmEntity alarmEntity) {
       return mAlarmDao.insert(alarmEntity);
    }

    public Maybe<Integer> deleteAlarm(AlarmEntity alarmEntity) {
        return mAlarmDao.delete(alarmEntity);
    }

    @Override
    public Completable deleteAlarms(List<Long> ids) {
        return mAlarmDao.deleteAlarms(ids);
    }

    public Completable updateAlarms(List<Long> ids, boolean active) {
        if(active)
            return mAlarmDao.activateAlarms(ids);
        else
            return mAlarmDao.inactivateAlarms(ids);
    }


    @Override
    public Flowable<List<MelodyEntity>> getAllMelodies() {
        return mMelodyDao.getAllMelodies();
    }

    public Single<MelodyEntity> getMelodyId(long id) {
        return mMelodyDao.getMelody(id);
    }

    public Single<MelodyEntity> getMelodyName(String name) {
        return mMelodyDao.getMelodyByTitle(name);
    }

}
