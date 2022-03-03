package com.federicoberon.newapp.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.federicoberon.newapp.model.AlarmEntity;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Data Access Object for the Alarm table.
 */
@Dao
public interface AlarmDao extends BaseDao<AlarmEntity>{

    @Query("SELECT * FROM alarms ORDER BY alarm_date DESC")
    Flowable<List<AlarmEntity>> getAllAlarm();

    @Query("SELECT m.* FROM alarms m WHERE m.title LIKE :filter ORDER BY m.alarm_date DESC")
    Flowable<List<AlarmEntity>> getAlarm(String filter);

    @Query("SELECT m.* FROM alarms m WHERE m.id = :id")
    Flowable<AlarmEntity> getAlarmById(long id);

    @Query("DELETE FROM alarms WHERE id = :currentAlarmId")
    Completable deleteAlarm(long currentAlarmId);

    /* 0 (false) and 1 (true). */
    @Query("UPDATE alarms SET started = :status WHERE id = :id")
    Maybe<Integer> disableAlarm(long id, boolean status);

    @Query("UPDATE alarms SET started = :status WHERE id = :id")
    Maybe<Integer> enableAlarm(long id, boolean status);
}
