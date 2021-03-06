package com.federicoberon.alarme.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.federicoberon.alarme.model.AlarmEntity;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Data Access Object for the Alarm table.
 */
@Dao
public interface AlarmDao extends BaseDao<AlarmEntity>{

    @Query("SELECT * FROM alarms ORDER BY hourInMinutes DESC")
    Flowable<List<AlarmEntity>> getAllAlarm();

    @Query("SELECT * FROM alarms ORDER BY started DESC, alarm_date ASC")
    Flowable<List<AlarmEntity>> getFirstAlarmStarted();

    @Query("SELECT m.* FROM alarms m WHERE m.id = :id")
    Flowable<AlarmEntity> getAlarmById(long id);

    @Query("SELECT m.* FROM alarms m WHERE m.id IN (:ids)")
    Flowable<List<AlarmEntity>> getAlarmByIds(List<Long> ids);

    @Query("DELETE FROM alarms WHERE id IN (:ids)")
    Completable deleteAlarms(List<Long> ids);

    /* 0 (false) and 1 (true). */
    @Query("UPDATE alarms SET started = 1 WHERE id IN (:ids)")
    Completable activateAlarms(List<Long> ids);

    @Query("UPDATE alarms SET started = 0 WHERE id IN (:ids)")
    Completable inactivateAlarms(List<Long> ids);


}
