package com.federicoberon.alarme.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.federicoberon.alarme.model.MelodyEntity;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Data Access Object for the Melody table.
 */
@Dao
public interface MelodyDao extends BaseDao<MelodyEntity>{

    @Query("SELECT * FROM melodies")
    Flowable<List<MelodyEntity>> getAllMelodies();

    @Query("SELECT * FROM melodies where id = :id")
    Single<MelodyEntity> getMelody(long id);

    @Query("SELECT * FROM melodies where title = :name")
    Single<MelodyEntity> getMelodyByTitle(String name);
}
