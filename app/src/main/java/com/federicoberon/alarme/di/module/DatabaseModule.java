package com.federicoberon.alarme.di.module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.federicoberon.alarme.datasource.AppDatabase;
import com.federicoberon.alarme.datasource.dao.AlarmDao;
import com.federicoberon.alarme.datasource.dao.MelodyDao;
import com.federicoberon.alarme.di.ApplicationContext;
import com.federicoberon.alarme.di.DatabaseInfo;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @ApplicationContext
    private final Context mContext;

    @DatabaseInfo
    private final String mDBName = "AlarMe.db";

    private ArrayList<Uri> toneUriList;
    private ArrayList<String> toneNameList;
    private ArrayList<Integer> toneIdList;

    public DatabaseModule (@ApplicationContext Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    AppDatabase provideDatabase () {
        return Room.databaseBuilder(
                mContext,
                AppDatabase.class,
                mDBName)
                .addCallback(getRDC())
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * load all system ringtone info into the itnernal database
     * @return
     */
    private RoomDatabase.Callback getRDC() {

        loadSystemRingtone();

        return new RoomDatabase.Callback() {
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                ContentValues contentValues = new ContentValues();
                int position = 0;
                while (position < toneIdList.size()){
                    contentValues.put("id",toneIdList.get(position));
                    contentValues.put("title",toneNameList.get(position));
                    contentValues.put("uri", toneUriList.get(position).toString());
                    db.insert("melodies", OnConflictStrategy.IGNORE, contentValues);
                    position++;
                }
            }
        };
    }

    private void loadSystemRingtone(){
        toneUriList = new ArrayList<>();
        toneNameList = new ArrayList<>();
        toneIdList = new ArrayList<>();

        RingtoneManager manager = new RingtoneManager(mContext);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        String title;
        String uri;
        int id;

        while (cursor.moveToNext()) {
            title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            id = cursor.getInt(RingtoneManager.ID_COLUMN_INDEX);
            //id = cursor.getPosition();


            toneIdList.add(id);
            toneNameList.add(title);
            toneUriList.add(Uri.parse(uri + "/" + id));
        }
    }

    private RoomDatabase.Callback getRDC2() {
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
        calendar2.set(Calendar.HOUR, 4);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return new RoomDatabase.Callback(){
            public void onCreate (@NonNull SupportSQLiteDatabase db){
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", "Visit uncle Bill");
                contentValues.put("repeat_time", 0);
                contentValues.put("postpone_time", 0);
                contentValues.put("monday", false);
                contentValues.put("tuesday", false);
                contentValues.put("wednesday", false);
                contentValues.put("thursday", false);
                contentValues.put("friday", false);
                contentValues.put("saturday", false);
                contentValues.put("sunday", false);
                contentValues.put("started", true);
                contentValues.put("hour", 20);
                contentValues.put("minute", 0);
                contentValues.put("alarm_date", calendar1.getTimeInMillis());
                db.insert("alarms", OnConflictStrategy.IGNORE, contentValues);
                contentValues = new ContentValues();
                contentValues.put("title", "Stephan birthday");
                contentValues.put("repeat_time", 0);
                contentValues.put("postpone_time", 0);
                contentValues.put("monday", false);
                contentValues.put("tuesday", false);
                contentValues.put("wednesday", false);
                contentValues.put("thursday", false);
                contentValues.put("friday", false);
                contentValues.put("saturday", false);
                contentValues.put("sunday", false);
                contentValues.put("started", true);
                contentValues.put("hour", 12);
                contentValues.put("minute", 0);
                contentValues.put("alarm_date", calendar2.getTimeInMillis());
                db.insert("alarms", OnConflictStrategy.IGNORE, contentValues);
            }
        };
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() { return mDBName; }

    @Singleton
    @Provides
    AlarmDao provideMilestoneDao(AppDatabase db) { return db.alarmDao(); }

    @Singleton
    @Provides
    MelodyDao provideMelodyDao(AppDatabase db) { return db.melodyDao(); }
}
