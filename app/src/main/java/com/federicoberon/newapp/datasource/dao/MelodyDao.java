package com.federicoberon.newapp.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.model.MelodyEntity;

import java.util.List;
import io.reactivex.Completable;
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
