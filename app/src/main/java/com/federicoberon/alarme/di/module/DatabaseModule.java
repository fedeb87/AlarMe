package com.federicoberon.alarme.di.module;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

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

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private static AppDatabase INSTANCE;
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

    @SuppressLint("CheckResult")
    @Singleton
    @Provides
    AppDatabase provideDatabase () {
        if(INSTANCE != null && INSTANCE.melodyDao().getCantMelodies() > 0){
            return INSTANCE;
        }
        INSTANCE = Room.databaseBuilder(
                mContext,
                AppDatabase.class,
                mDBName)
                .addCallback(getRDC())
                .fallbackToDestructiveMigration()
                .build();

        return INSTANCE;
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
                    db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);
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
