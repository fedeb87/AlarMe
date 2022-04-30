package com.federicoberon.alarme.di;

import android.content.ContentValues;
import android.content.Context;

import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.datasource.AppDatabase;
import com.federicoberon.alarme.datasource.dao.AlarmDao;
import com.federicoberon.alarme.datasource.dao.MelodyDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestDatabaseModule {

    @ApplicationContext
    private final Context mContext;

    public TestDatabaseModule (@ApplicationContext Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    AppDatabase provideDatabase () {
        return Room.inMemoryDatabaseBuilder(
                mContext,
                AppDatabase.class)
                .addCallback(getRDC())
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    private RoomDatabase.Callback getRDC() {
        return new RoomDatabase.Callback(){
            public void onCreate (SupportSQLiteDatabase db){
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", "Family");
                contentValues.put("color", "#F06423");
                db.insert("milestone_types", OnConflictStrategy.IGNORE, contentValues);
                contentValues = new ContentValues();
                contentValues.put("title", "Visit uncle Bill");
                contentValues.put("description", "Visit uncle Bill and her cats :)");
                contentValues.put("milestone_date", TestDataHelper.getDate1().getTime());
                contentValues.put("type", 1);
                db.insert("milestones", OnConflictStrategy.IGNORE, contentValues);
            }
        };
    }

    @Singleton
    @Provides
    AlarmDao provideMilestoneDao(AppDatabase db) { return db.alarmDao(); }

    @Singleton
    @Provides
    MelodyDao provideMelodyDao(AppDatabase db) { return db.melodyDao(); }

}
