package com.federicoberon.newapp.utils;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.MONDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.REPEAT_TIME;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.POSTPONE_TIME;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.TUESDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.WEDNESDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.THURSDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.FRIDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.SATURDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.SUNDAY;
import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.TITLE;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver;
import com.federicoberon.newapp.model.AlarmEntity;

import java.util.Calendar;

import javax.inject.Singleton;

@Singleton
public class AlarmManager {
    //todo aca iria el metodo para apagar o borrar una alarma ya creada
    @SuppressLint("UnspecifiedImmutableFlag")
    public static void schedule(Context context, AlarmEntity alarmEntity) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(MONDAY, alarmEntity.isMonday());
        intent.putExtra(TUESDAY, alarmEntity.isTuesday());
        intent.putExtra(WEDNESDAY, alarmEntity.isWednesday());
        intent.putExtra(THURSDAY, alarmEntity.isThursday());
        intent.putExtra(FRIDAY, alarmEntity.isFriday());
        intent.putExtra(SATURDAY, alarmEntity.isSaturday());
        intent.putExtra(SUNDAY, alarmEntity.isSunday());
        intent.putExtra(REPEAT_TIME, alarmEntity.getRepeatTime());
        intent.putExtra(POSTPONE_TIME, alarmEntity.getPostponeTime());

        intent.putExtra(TITLE, alarmEntity.getTitle());

        PendingIntent alarmPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, PendingIntent.FLAG_IMMUTABLE);
        }else{
            alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, 0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmEntity.getAlarmDate());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (!recurring(alarmEntity)) {
            String toastText = String.format("One Time Alarm %s scheduled for %s at %s",
                        alarmEntity.getTitle(),
                        StringHelper.toDay(calendar.get(Calendar.DAY_OF_WEEK)),
                        DateFormat.getTimeFormat(context).format(calendar.getTime()));
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        } else {
            String toastText = String.format("Recurring Alarm %s scheduled for %s at %s",
                    alarmEntity.getTitle(), getRecurringDaysText(alarmEntity),
                    DateFormat.getTimeFormat(context).format(calendar.getTime()));
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

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

    private static boolean recurring(AlarmEntity alarmEntity) {
        return alarmEntity.isMonday() && alarmEntity.isFriday() && alarmEntity.isSaturday() &&
                alarmEntity.isWednesday() && alarmEntity.isSunday() && alarmEntity.isThursday() &&
                alarmEntity.isTuesday();
    }

    public static AlarmEntity getSnoozedAlarm(int repeat_time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, repeat_time);
        // todo
        /*return new AlarmEntity(String title, Date alarmDate, int hour, int minute,
                       boolean[] daysOfWeek, boolean melodyOn, long melodyId, String melodyName,
                       boolean vibrationOn, String vibrationPatter, boolean postponeOn,
                       int postponeTime, boolean repeatOn, int repeatTime, boolean started);

         */
        return null;
    }
}
