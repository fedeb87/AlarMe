package com.federicoberon.newapp.repositories;

import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.model.MelodyEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Access point for managing user data.
 */
public interface AlarmDataSource {

    /**
     * Gets the milestone from the data source.
     *
     * @return the milestone from the data source.
     */
    Flowable<List<AlarmEntity>> getAllAlarms();

    Flowable<List<AlarmEntity>> getAlarmByIds(List<Long> ids);

    Flowable<List<AlarmEntity>> getAlarms(String filter);

    Flowable<List<AlarmEntity>> getFirstAlarmStarted();

    Flowable<AlarmEntity> getAlarmById(long id);


    /**
     * Inserts the milestone into the data source, or, if this is an existing user, updates it.
     *
     * @param alarmEntity the user to be inserted or updated.
     */
    Maybe<Long> insertOrUpdateAlarm(AlarmEntity alarmEntity);

    Completable deleteAlarm(long currentMilestoneId);

    Completable deleteAlarms(List<Long> ids);

    Flowable<List<MelodyEntity>> getAllMelodies();

}
