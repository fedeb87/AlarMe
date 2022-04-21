package com.federicoberon.alarme.utils;

import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.federicoberon.alarme.R;
import com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver;
import com.federicoberon.alarme.model.AlarmEntity;

import java.util.Calendar;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class AlarmManager {
    @SuppressLint("UnspecifiedImmutableFlag")
    public static void schedule(Context context, AlarmEntity alarmEntity) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        intent.putExtra(ALARM_ENTITY, alarmEntity);

        PendingIntent alarmPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, 0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmEntity.getAlarmDate());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String toastText = String.format(context.getString(R.string.scheduled_alarm_msg),
                alarmEntity.getTitle(),
                RelativeTime.getTimeAgo(calendar.getTimeInMillis(), context));

        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent);
        else
            alarmManager.setExact(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );

    }

    public static void dismissAlarm(Context context, List<AlarmEntity> alarmsToDelete){
        for(AlarmEntity ae: alarmsToDelete){
            dismissAlarm(context, ae);
        }
    }

    public static void dismissAlarm(Context context, AlarmEntity alarmEntity){

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(ALARM_ENTITY, alarmEntity);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, (int)alarmEntity.getId(),
                    intent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            pendingIntent = PendingIntent.getBroadcast(context, (int)alarmEntity.getId(),
                    intent, 0);
        }
        alarmManager.cancel(pendingIntent); //Remove any alarms with a matching Inten

    }

    public static String getRecurringDaysText(AlarmEntity alarmEntity) {
        if (!recurring(alarmEntity)) {
            return null;
        }

        String days = "";
        if (alarmEntity.isMonday()) {
            days += "Mo ";
        }
        if (alarmEntity.isTuesday()) {
            days += "Tu ";
        }
        if (alarmEntity.isWednesday()) {
            days += "We ";
        }
        if (alarmEntity.isThursday()) {
            days += "Th ";
        }
        if (alarmEntity.isFriday()) {
            days += "Fr ";
        }
        if (alarmEntity.isSaturday()) {
            days += "Sa ";
        }
        if (alarmEntity.isSunday()) {
            days += "Su ";
        }

        return days;
    }

    public static boolean recurring(AlarmEntity alarmEntity) {
        return alarmEntity.isMonday() || alarmEntity.isFriday() || alarmEntity.isSaturday() ||
                alarmEntity.isWednesday() || alarmEntity.isSunday() || alarmEntity.isThursday() ||
                alarmEntity.isTuesday();
    }

    public static AlarmEntity getSnoozedAlarm(AlarmEntity alarmEntity, int repeat_time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, repeat_time);

        alarmEntity.setAlarmDate(calendar.getTime());
        alarmEntity.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        alarmEntity.setMinute(Calendar.MINUTE);
        return alarmEntity;
    }
}
