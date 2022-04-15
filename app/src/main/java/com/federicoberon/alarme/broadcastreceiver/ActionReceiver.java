package com.federicoberon.alarme.broadcastreceiver;

import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.IS_PREVIEW;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.service.ServiceUtil;
import com.federicoberon.alarme.utils.AlarmManager;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActionReceiver extends BroadcastReceiver {

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        ServiceUtil serviceUtil = new ServiceUtil(context);

        // disable alarm
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);

        boolean isPreview = false;
        if(intent.hasExtra(IS_PREVIEW)) {
            isPreview = intent.getBooleanExtra(IS_PREVIEW, false);
        }

        if(AlarmManager.recurring(alarmEntity)) {
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
            // repetir activado
            Calendar alarmCal = Calendar.getInstance();
            // a este despues le sumo cuando sepa cuanto
            alarmCal.setTime(alarmEntity.getAlarmDate());

            alarmCal.add(Calendar.HOUR_OF_DAY, alarmEntity.getRepeatTime());

            alarmEntity.setAlarmDate(alarmCal.getTime());
            AlarmManager.schedule(context, alarmEntity);
        } else{
            alarmEntity.setStarted(false);
        }

        // todo do this only when not come from preview alarm
        if(!isPreview) {
            serviceUtil.updateAlarm(alarmEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                                // stop alarm service
                                Log.w("MIO", "Alarm disable: " + id);
                            },
                            throwable -> Log.e("MIO", "Unable to get milestones: ", throwable));
        }
    }
}
