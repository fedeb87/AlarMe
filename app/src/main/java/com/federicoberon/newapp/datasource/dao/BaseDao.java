package com.federicoberon.newapp.datasource.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import io.reactivex.Maybe;

@Dao
public interface BaseDao<T>  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insert(T t);

    @Update
    Maybe<Integer> update(T t);

    @Delete
    Maybe<Integer> delete(T t);
}