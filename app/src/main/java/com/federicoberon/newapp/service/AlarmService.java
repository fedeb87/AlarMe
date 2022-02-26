package com.federicoberon.newapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import static com.federicoberon.newapp.SimpleRemindMeApplication.CHANNEL_ID;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ACTION_DISCARD;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ACTION_SNOOZE;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.POSTPONE_TIME;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.TITLE;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.ui.alarm.AlarmActivity;
import com.federicoberon.newapp.utils.AlarmManager;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        //Cursor cursor = manager.getCursor();
        //manager.getRingtoneUri(0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


        mediaPlayer = MediaPlayer.create(this, notification);
        mediaPlayer.setLooping(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        int repeat_time = 0;
        // todo aca llamar al alarmManager para que actualice la base de datos de las alarams (isStarted)y vea lo de repetir
        if(intent.hasExtra(ACTION_DISCARD)){
            stopSelf();
        }else if(intent.hasExtra(ACTION_SNOOZE)){
            if(intent.hasExtra(POSTPONE_TIME)){
                repeat_time = intent.getIntExtra(POSTPONE_TIME, 10);

                notificationIntent.putExtra(POSTPONE_TIME,repeat_time);
            }
            AlarmManager.getSnoozedAlarm(repeat_time);
            stopSelf();
        }


        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }

        long[] pattern = { 0, 100, 1000 };
        long[] default_pattern = {0, 250, 250, 250};
        // todo estos 2 probar de lanzar de nuevo el AlarmService?
        // snooze intent //
        Intent snoozeIntent = new Intent(this, AlarmService.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(ACTION_SNOOZE, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getService(this, 1, snoozeIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        // discard intent //
        Intent discardIntent = new Intent(this, AlarmService.class);
        discardIntent.setAction(ACTION_DISCARD);
        discardIntent.putExtra(ACTION_DISCARD,0);
        PendingIntent discardPendingIntent =
                PendingIntent.getService(this, 2, discardIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        // full screen intent //
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_IMMUTABLE);

        // todo los drawables de los botones? sino borrarlos
        String alarmTitle = String.format("%s Alarm", intent.getStringExtra(TITLE));
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(alarmTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(pattern)
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
        mediaPlayer.start();

        vibrator.vibrate(pattern, 0);

        startForeground(101, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.w("MIO", "ONDESTROY del AlarmService");
        stopForeground(true);
        stopSelf();
        mediaPlayer.stop();
        vibrator.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
