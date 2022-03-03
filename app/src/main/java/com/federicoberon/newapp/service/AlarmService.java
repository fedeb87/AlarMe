package com.federicoberon.newapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import static com.federicoberon.newapp.SimpleRemindMeApplication.CHANNEL_ID;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ACTION_DISCARD;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ACTION_SNOOZE;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.broadcastreceiver.ActionReceiver;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.ui.alarm.AlarmActivity;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.VibrationManager;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class AlarmService extends Service {

    private static final String LOG_TAG = "AlarmService";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {

        ((SimpleRemindMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate();
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);

        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(ALARM_ENTITY,alarmEntity);

        if(intent.hasExtra(ACTION_SNOOZE)){
            //AlarmManager.getSnoozedAlarm(alarmEntity, alarmEntity.getPostponeTime());
            AlarmManager.schedule(this, AlarmManager.getSnoozedAlarm(alarmEntity, alarmEntity.getPostponeTime()));
            stopSelf();
        }

        // else, is a new alarm
        // wrap the AlarmActivity in a PendingIntent
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }

        // create snooze intent //
        Intent snoozeIntent = new Intent(this, AlarmService.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(ACTION_SNOOZE, 0);
        snoozeIntent.putExtra(ALARM_ENTITY, alarmEntity);
        PendingIntent snoozePendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            snoozePendingIntent = PendingIntent.getService(this, 1, snoozeIntent,
                    PendingIntent.FLAG_IMMUTABLE);
        }else{
            snoozePendingIntent = PendingIntent.getService(this, 1, snoozeIntent,
                    0);
        }

        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.putExtra(ALARM_ENTITY, alarmEntity);
        intentAction.setAction(ALARM_ENTITY);
        PendingIntent discardPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            discardPendingIntent = PendingIntent.getBroadcast(this, 2,
                    intentAction, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        }else{
            discardPendingIntent = PendingIntent.getBroadcast(this, 2,
                    intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // full screen intent //
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        fullScreenIntent.putExtra(ALARM_ENTITY, alarmEntity);

        PendingIntent fullScreenPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                        fullScreenIntent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                    fullScreenIntent, 0);
        }

        String alarmTitle = String.format(getString(R.string.notification_title), alarmEntity.getTitle());
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(VibrationManager.getVibrationByName(alarmEntity.getVibrationPatter()))
                .setContentText("Ring Ring .. Ring Ring")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                        snoozePendingIntent)
                .addAction(R.drawable.ic_discard, getString(R.string.discard),
                        discardPendingIntent)
                .build();

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(alarmEntity.getMelodyUri()));
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(VibrationManager.getVibrationByName(alarmEntity.getVibrationPatter()), 0);
        startForeground(101, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        mediaPlayer.stop();
        mDisposable.clear();
        vibrator.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
