package com.federicoberon.alarme.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.federicoberon.alarme.AlarMe.CHANNEL_ID;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ACTION_SNOOZE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LATITUDE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LONGITUDE;

import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.broadcastreceiver.ActionReceiver;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.ui.alarm.AlarmActivity;
import com.federicoberon.alarme.utils.AlarmManager;
import com.federicoberon.alarme.utils.VibrationManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;

public class AlarmService extends Service {

    private static final String LOG_TAG = "AlarmService";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {

        ((AlarMe) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate();
        latitude = -1;
        longitude = -1;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);

        if(intent.hasExtra(ACTION_SNOOZE)){
            AlarmManager.schedule(this, AlarmManager.getSnoozedAlarm(alarmEntity, alarmEntity.getPostponeTime()));
            stopSelf();
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // todo la cosa esta en hacerlo aca, y pasar la latitud y long a la activity comno extra
        if(alarmEntity.isWeatherOn())
            getCurrentLocation(alarmEntity);
        else
            displayAlarm(alarmEntity);


        return START_NOT_STICKY;
    }

    private void displayAlarm(AlarmEntity alarmEntity) {
        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        notificationIntent.putExtra(ALARM_ENTITY,alarmEntity);
        if(latitude!=-1){
            notificationIntent.putExtra(LATITUDE,latitude);
            notificationIntent.putExtra(LONGITUDE,longitude);
        }


        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            snoozePendingIntent = PendingIntent.getService(this, 1, snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        }else{
            snoozePendingIntent = PendingIntent.getService(this, 1, snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // discard action //
        Intent intentDiscard = new Intent(this, ActionReceiver.class);
        intentDiscard.putExtra(ALARM_ENTITY, alarmEntity);
        intentDiscard.setAction(ALARM_ENTITY);
        PendingIntent discardPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            discardPendingIntent = PendingIntent.getBroadcast(this, 2,
                    intentDiscard, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        }else{
            discardPendingIntent = PendingIntent.getBroadcast(this, 2,
                    intentDiscard, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // full screen intent //
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(latitude!=-1){
            fullScreenIntent.putExtra(LATITUDE,latitude);
            fullScreenIntent.putExtra(LONGITUDE,longitude);
        }

        fullScreenIntent.putExtra(ALARM_ENTITY, alarmEntity);

        PendingIntent fullScreenPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            fullScreenPendingIntent = PendingIntent.getActivity(this, 3,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
        }else{
            fullScreenPendingIntent = PendingIntent.getActivity(this, 3,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(alarmEntity.getMelodyUri()));
            mediaPlayer.setLooping(true);
            mediaPlayer.setWakeMode(this, AudioManager.MODE_RINGTONE);
            mediaPlayer.setVolume(alarmEntity.getVolume(), alarmEntity.getVolume());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(VibrationManager.getVibrationByName(alarmEntity.getVibrationPatter()), 0);
        startForeground(101, notification);

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(AlarmEntity alarmEntity) {

        if (checkLocationPermissions())
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {

                            Log.w("MIO", "<<< Ya teno una ubicacion >>>");
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            displayAlarm(alarmEntity);

                        } else {
                            Log.w("MIO", "<<< Pido una nueva ubicacion >>>");
                            requestNewLocation(alarmEntity);
                        }
                    });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation(AlarmEntity alarmEntity) {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w("MIO", "<<< locationResult es NULL >>>");
                    return;
                }
                Log.w("MIO", "<<< locationResult NO es  NULL >>>");
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                fusedLocationClient.removeLocationUpdates(this);
                displayAlarm(alarmEntity);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkLocationPermissions())
            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper());
    }

    private boolean checkLocationPermissions() {
        // check locaiton permissions
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.e("MIO" ,"<<< NO HAY PERMISOS >>>");
            return false;
        }

        // check google service installed
        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                != ConnectionResult.SUCCESS) {
            Log.e("MIO", "<<< NO TIENE LOS SERVICIOS INSTALADOS >>>");
            return false;
        }
        return true;
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
