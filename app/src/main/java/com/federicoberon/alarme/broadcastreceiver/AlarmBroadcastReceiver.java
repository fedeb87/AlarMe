package com.federicoberon.alarme.broadcastreceiver;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.service.RescheduleAlarmsService;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SNOOZE = "Snooze";
    public static final String ALARM_ENTITY = "AlarmEntity";
    public static final String IS_PREVIEW = "Come from preview";
    public static final String STOP_SERVICE = "Stop service";
    public static final String LOCKED_SCREEN = "Locked screen";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    Intent intentService;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = "Alarm Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }  else if(Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())){
                // Handle key press.
                if(intentService != null){
                    context.stopService(intentService);
                    intentService = null;
                }
        } else{
            String toastText = "Alarm Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if (alarmIsToday(alarmEntity)) {
                startAlarmService(context, alarmEntity);
            } else if (containsTrueAnyDay(alarmEntity)) {
                startAlarmService(context, alarmEntity);
            }
        }
    }

    private boolean alarmIsToday(AlarmEntity alarmEntity) {

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTime(alarmEntity.getAlarmDate());
        Calendar currentCalendar = Calendar.getInstance();

        return alarmCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private boolean containsTrueAnyDay(AlarmEntity alarmEntity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today) {
            case Calendar.MONDAY:
                return alarmEntity.isMonday();
            case Calendar.TUESDAY:
                return alarmEntity.isTuesday();
            case Calendar.WEDNESDAY:
                return alarmEntity.isWednesday();
            case Calendar.THURSDAY:
                return alarmEntity.isThursday();
            case Calendar.FRIDAY:
                return alarmEntity.isFriday();
            case Calendar.SATURDAY:
                return alarmEntity.isSaturday();
            case Calendar.SUNDAY:
                return alarmEntity.isSunday();
        }
        return false;
    }

    /**
     * Runs when creating new alarm create or on editing one
     * @param context
     * @param alarmEntity
     */
    private void startAlarmService(Context context, AlarmEntity alarmEntity) {

        intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(ALARM_ENTITY, alarmEntity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    /**
     * Runs when restart device. Execute RescheduleAlarmsService
     * @param context Application context
     */
    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
