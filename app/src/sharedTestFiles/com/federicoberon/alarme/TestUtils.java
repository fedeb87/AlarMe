package com.federicoberon.alarme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import com.federicoberon.alarme.datasource.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class TestUtils {

    private static ArrayList<Uri> toneUriList;
    private static ArrayList<String> toneNameList;
    private static ArrayList<Integer> toneIdList;

    public static AppDatabase initDb(){

        RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
            public void onCreate (SupportSQLiteDatabase db){
            }
        };

        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .addCallback(rdc)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    public static AppDatabase initDb(Context context){

        final Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2022);
        calendar1.set(Calendar.MONTH, 9);
        calendar1.set(Calendar.DAY_OF_MONTH, 10);
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        final Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2023);
        calendar2.set(Calendar.MONTH, 6);
        calendar2.set(Calendar.DAY_OF_MONTH, 7);
        calendar2.set(Calendar.HOUR, 4);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
            public void onCreate (SupportSQLiteDatabase db){
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1");
                contentValues.put("title", "First title");
                contentValues.put("alarm_date", calendar1.getTimeInMillis());
                contentValues.put("hour", 20);
                contentValues.put("minute", 0);
                contentValues.put("hourInMinutes", 1800);
                contentValues.put("melodyOn", true);
                contentValues.put("melodyUri", "melody Uri");
                contentValues.put("melodyName", "melody Name");
                contentValues.put("volume", 10);
                contentValues.put("vibrationOn", true);
                contentValues.put("vibrationPatter", "vibration Patter");
                contentValues.put("postponeOn", true);
                contentValues.put("postpone_time", 0);
                contentValues.put("repeatOn", true);
                contentValues.put("repeat_time", 0);
                contentValues.put("horoscopeOn", false);
                contentValues.put("weatherOn", false);
                contentValues.put("isPhrasesOn", true);
                contentValues.put("started", true);
                contentValues.put("monday", false);
                contentValues.put("tuesday", false);
                contentValues.put("wednesday", false);
                contentValues.put("thursday", false);
                contentValues.put("friday", false);
                contentValues.put("saturday", false);
                contentValues.put("sunday", false);
                db.insert("alarms", OnConflictStrategy.IGNORE, contentValues);
                contentValues = new ContentValues();
                contentValues.put("id", "2");
                contentValues.put("title", "Stephan birthday");
                contentValues.put("alarm_date", calendar2.getTimeInMillis());
                contentValues.put("hour", 12);
                contentValues.put("minute", 0);
                contentValues.put("hourInMinutes", 1200);
                contentValues.put("melodyOn", true);
                contentValues.put("melodyUri", "melody Uri 2 ");
                contentValues.put("melodyName", "melody Name 2");
                contentValues.put("volume", 10);
                contentValues.put("vibrationOn", false);
                contentValues.put("vibrationPatter", "vibration Patter");
                contentValues.put("postponeOn", false);
                contentValues.put("postpone_time", 0);
                contentValues.put("repeatOn", false);
                contentValues.put("repeat_time", 0);
                contentValues.put("horoscopeOn", true);
                contentValues.put("weatherOn", false);
                contentValues.put("isPhrasesOn", false);
                contentValues.put("started", true);
                contentValues.put("monday", false);
                contentValues.put("tuesday", false);
                contentValues.put("wednesday", true);
                contentValues.put("thursday", false);
                contentValues.put("friday", false);
                contentValues.put("saturday", false);
                contentValues.put("sunday", false);
                db.insert("alarms", OnConflictStrategy.IGNORE, contentValues);
                contentValues.put("id",9);
                contentValues.put("title","Flutey Phone");
                contentValues.put("uri", "content://media/internal/audio/media/9");
                db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
                contentValues.put("id",41);
                contentValues.put("title","Ether Shake");
                contentValues.put("uri", "content://media/internal/audio/media/41");
                db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
                contentValues.put("id",42);
                contentValues.put("title","Aquila");
                contentValues.put("uri", "content://media/internal/audio/media/42");
                db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
                contentValues.put("id",43);
                contentValues.put("title","Mildly Alarming");
                contentValues.put("uri", "content://media/internal/audio/media/43");
                db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
                contentValues.put("id",44);
                contentValues.put("title","No Limits");
                contentValues.put("uri", "content://media/internal/audio/media/44");
                db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
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
