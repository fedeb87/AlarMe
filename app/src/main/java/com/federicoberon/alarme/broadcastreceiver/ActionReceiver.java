package com.federicoberon.alarme.broadcastreceiver;

import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.IS_PREVIEW;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.STOP_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.service.RescheduleAlarmsService;
import com.federicoberon.alarme.service.ServiceUtil;
import com.federicoberon.alarme.utils.AlarmManager;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActionReceiver extends BroadcastReceiver {

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("MIO", "<<< RECIBIDO >>> ");
        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        ServiceUtil serviceUtil = new ServiceUtil(context);

        boolean isPreview = false;
        if(intent.hasExtra(IS_PREVIEW)) {
            isPreview = intent.getBooleanExtra(IS_PREVIEW, false);
        }

        // disable alarm
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(ALARM_ENTITY, alarmEntity);
        intentService.putExtra(STOP_SERVICE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }

        //context.stopService(intentService);

        // in any case discard it
        AlarmManager.dismissAlarm(context, alarmEntity);

        if(AlarmManager.recurring(alarmEntity)) {
            Log.w("MIO", "<<< ES RECURRENTE >>> ");
            // tiene dias fijos activos
            int days_to_add = 0;
            // lunes es 2, domingo sera 1
            // y sabado sera 7
            Calendar alarmCal = Calendar.getInstance();
            // a este despues le sumo cuando sepa cuanto
            alarmCal.setTime(alarmEntity.getAlarmDate());

            // empieza en mañana
            int day_of_week = alarmCal.get(Calendar.DAY_OF_WEEK);
            boolean find = false;

            while(!find){
                days_to_add += 1;
                if(alarmEntity.getDaysOfWeek()[day_of_week])
                    find = true;
                else
                    day_of_week = day_of_week+1==7? 0 : day_of_week+1;
            }

            alarmCal.add(Calendar.DAY_OF_MONTH, days_to_add);
            alarmEntity.setAlarmDate(alarmCal.getTime());

            AlarmManager.schedule(context, alarmEntity);
        }else if(alarmEntity.isRepeatOn()){
            Log.w("MIO", "<<< ES REPETITIVA >>> ");
            // repetir activado
            Calendar alarmCal = Calendar.getInstance();
            // a este despues le sumo cuando sepa cuanto
            alarmCal.setTime(alarmEntity.getAlarmDate());

            alarmCal.add(Calendar.HOUR_OF_DAY, alarmEntity.getRepeatTime());

            alarmEntity.setAlarmDate(alarmCal.getTime());
            AlarmManager.schedule(context, alarmEntity);
        } else{
            Log.w("MIO", "<<< ES SIMPLE >>> ");
            alarmEntity.setStarted(false);
        }

        if(!isPreview) {
            serviceUtil.updateAlarm(alarmEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        // stop alarm service
                        Log.w("MIO", "Alarm updated: " + id);
                    },
                    throwable -> Log.e("MIO", "Unable to get milestones: ", throwable));
        }
    }
}
