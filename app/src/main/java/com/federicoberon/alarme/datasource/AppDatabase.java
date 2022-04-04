package com.federicoberon.alarme.datasource;

import android.content.ContentValues;
import android.content.Context;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.federicoberon.alarme.datasource.dao.AlarmDao;
import com.federicoberon.alarme.datasource.dao.MelodyDao;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.MelodyEntity;

import java.util.Calendar;

/**
 * The Room database that contains the Users table
 */
@Database(entities = {AlarmEntity.class, MelodyEntity.class}, version = 1)
@TypeConverters({DateConverterUtils.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "SimpleRemindMe.db";

    public abstract AlarmDao alarmDao();
    public abstract MelodyDao melodyDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    final Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.YEAR, 2021);
                    calendar1.set(Calendar.MONTH, 9);
                    calendar1.set(Calendar.DAY_OF_MONTH, 10);
                    calendar1.set(Calendar.HOUR, 0);
                    calendar1.set(Calendar.HOUR_OF_DAY, 0);
                    calendar1.set(Calendar.MINUTE, 0);
                    calendar1.set(Calendar.SECOND, 0);
                    calendar1.set(Calendar.MILLISECOND, 0);
                    calendar1.set(Calendar.MILLISECOND, 0);


                    final Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(Calendar.YEAR, 2022);
                    calendar2.set(Calendar.MONTH, 6);
                    calendar2.set(Calendar.DAY_OF_MONTH, 7);
                    calendar2.set(Calendar.HOUR, 0);
                    calendar2.set(Calendar.HOUR_OF_DAY, 0);
                    calendar2.set(Calendar.MINUTE, 0);
                    calendar2.set(Calendar.SECOND, 0);
                    calendar2.set(Calendar.MILLISECOND, 0);
                    calendar2.set(Calendar.MILLISECOND, 0);

                    RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
                        public void onCreate (SupportSQLiteDatabase db){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("title", "Family");
                            contentValues.put("color", "#F06423");
                            db.insert("milestone_types", OnConflictStrategy.IGNORE, contentValues);
                            contentValues = new ContentValues();
                            contentValues.put("title", "Work");
                            contentValues.put("color", "#741873");
                            db.insert("milestone_types", OnConflictStrategy.IGNORE, contentValues);
                            contentValues = new ContentValues();
                            contentValues.put("title", "Birthday");
                            contentValues.put("color", "#A20C0C");
                            db.insert("milestone_types", OnConflictStrategy.IGNORE, contentValues);
                            contentValues = new ContentValues();
                            contentValues.put("title", "Visit uncle Bill");
                            contentValues.put("description", "Visit uncle Bill and her cats :)");
                            contentValues.put("milestone_date", calendar1.getTimeInMillis());
                            contentValues.put("type", 1);
                            db.insert("milestones", OnConflictStrategy.IGNORE, contentValues);
                            contentValues = new ContentValues();
                            contentValues.put("title", "Stephan birthday");
                            contentValues.put("description", "Stephan celebrate his 22 years old in a beach");
                            contentValues.put("milestone_date", calendar2.getTimeInMillis());
                            contentValues.put("type", 2);
                            db.insert("milestones", OnConflictStrategy.IGNORE, contentValues);
                        }
                    };

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addCallback(rdc)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
