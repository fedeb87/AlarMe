package com.federicoberon.alarme.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.federicoberon.alarme.AlarMeApplication.CHANNEL_ID;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ACTION_SNOOZE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.IS_PREVIEW;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LATITUDE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LONGITUDE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.STOP_SERVICE;

import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.broadcastreceiver.ActionReceiver;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.ui.alarm.AlarmActivity;
import com.federicoberon.alarme.utils.AlarmManager;
import com.federicoberon.alarme.utils.CustomMediaPlayer;
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
    private Vibrator vibrator;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;
    private boolean comeFromPreview = false;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {

        ((AlarMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate();
        latitude = -1;
        longitude = -1;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);

        if(intent.hasExtra(STOP_SERVICE)){
            finishService();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else {

            if (intent.hasExtra(IS_PREVIEW)) {
                comeFromPreview = true;
            }

            if (intent.hasExtra(ACTION_SNOOZE)) {
                AlarmManager.schedule(this, AlarmManager.getSnoozedAlarm(alarmEntity, alarmEntity.getPostponeTime()));
                stopSelf();
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // get current location and request weather
            if (alarmEntity.isWeatherOn())
                getCurrentLocation(alarmEntity);

            displayAlarm(alarmEntity);
        }
        return START_NOT_STICKY;
    }

    private void displayAlarm(AlarmEntity alarmEntity) {
        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        notificationIntent.putExtra(ALARM_ENTITY, alarmEntity);
        notificationIntent.putExtra(IS_PREVIEW, comeFromPreview);
        if (latitude != -1) {
            notificationIntent.putExtra(LATITUDE, latitude);
            notificationIntent.putExtra(LONGITUDE, longitude);
        }

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // create snooze intent //
        Intent snoozeIntent = new Intent(this, AlarmService.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(ACTION_SNOOZE, 0);
        snoozeIntent.putExtra(ALARM_ENTITY, alarmEntity);
        PendingIntent snoozePendingIntent;
        snoozePendingIntent = PendingIntent.getService(this, 1, snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // discard action //
        Intent intentDismiss = new Intent(this, ActionReceiver.class);
        intentDismiss.putExtra(ALARM_ENTITY, alarmEntity);
        intentDismiss.putExtra(IS_PREVIEW, comeFromPreview);
        intentDismiss.setAction(ALARM_ENTITY);
        PendingIntent dismissPendingIntent;
        dismissPendingIntent = PendingIntent.getBroadcast(this, 2,
                intentDismiss, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // full screen intent //
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (latitude != -1) {
            fullScreenIntent.putExtra(LATITUDE, latitude);
            fullScreenIntent.putExtra(LONGITUDE, longitude);
        }

        fullScreenIntent.putExtra(ALARM_ENTITY, alarmEntity);
        fullScreenIntent.putExtra(IS_PREVIEW, comeFromPreview);

        PendingIntent fullScreenPendingIntent;
        fullScreenPendingIntent = PendingIntent.getActivity(this, 3,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String contentText = "Ring Ring .. Ring Ring";
        if (!alarmEntity.getTitle().isEmpty())
            contentText = alarmEntity.getTitle();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(alarmEntity.getTitle())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(VibrationManager.getVibrationByName(alarmEntity.getVibrationPatter()))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setOngoing(true)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                        snoozePendingIntent)
                .addAction(R.drawable.ic_discard, getString(R.string.discard),
                        dismissPendingIntent)
                .build();

        if (alarmEntity.isMelodyOn()) {
            try {
                playRingtone(alarmEntity.getMelodyUri(), alarmEntity.getVolume());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (alarmEntity.isVibrationOn())
            vibrator.vibrate(VibrationManager.getVibrationByName(alarmEntity.getVibrationPatter()), 0);

        startForeground(101, notification);
    }

    /**
     * Play selected melody, default in case of error
     * @param uri Selected melody uri
     * @param volume Selected volume
     * @throws IOException
     */
    private void playRingtone(String uri, int volume) throws IOException {
        if(uri!=null)
            CustomMediaPlayer.getMediaPlayerInstance().playAudioFile(this, uri, volume);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(AlarmEntity alarmEntity) {

        if (locationEnabled(this)) {
            if (checkLocationPermissions()) {
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
            }else
                displayAlarm(alarmEntity);
        }else
            displayAlarm(alarmEntity);
    }

    public static boolean locationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled;
        boolean network_enabled;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ignored) {
            gps_enabled = false;
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ignored) {
            network_enabled = false;
        }
        return gps_enabled || network_enabled;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation(AlarmEntity alarmEntity) {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
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
        // check location permissions
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.e("MIO" ,"<<< NO PERMISSIONS >>>");
            return false;
        }

        // check google service installed
        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
                != ConnectionResult.SUCCESS) {
            Log.e("MIO", "<<< GOOGLE SERVICE ARE NOT INSTALLED >>>");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        finishService();
        super.onDestroy();
    }

    private void finishService() {
        stopForeground(true);
        CustomMediaPlayer.getMediaPlayerInstance().stopAudioFile();
        mDisposable.clear();
        vibrator.cancel();
        stopSelf();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
