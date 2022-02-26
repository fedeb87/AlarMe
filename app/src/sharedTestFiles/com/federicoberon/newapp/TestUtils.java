package com.federicoberon.newapp;

import android.content.ContentValues;

import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import com.federicoberon.newapp.datasource.AppDatabase;

public class TestUtils {

    public static AppDatabase initDb(){
        RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
            public void onCreate (SupportSQLiteDatabase db){
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", "Family");
                contentValues.put("color", "#F06423");
                db.insert("milestone_types", OnConflictStrategy.IGNORE, contentValues);
            }
        };

        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .addCallback(rdc)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }
}
