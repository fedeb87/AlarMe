package com.federicoberon.alarme.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.MelodyEntity;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Data Access Object for the Alarm table.
 */
@Dao
public interface MelodyDao extends BaseDao<AlarmEntity>{

    @Query("SELECT * FROM melodies")
    Flowable<List<MelodyEntity>> getAllMelodies();


    @Query("SELECT * FROM melodies where id = :id")
    Flowable<MelodyEntity> getMelody(long id);

    @Query("SELECT * FROM melodies where title = :name")
    Flowable<MelodyEntity> getMelodyByTitle(String name);
}
