package com.federicoberon.alarme.utils;

import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
        alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmEntity.getAlarmDate());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String toastText = String.format(context.getString(R.string.scheduled_alarm_msg),
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

        pendingIntent = PendingIntent.getBroadcast(context, (int) alarmEntity.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent); //Remove any alarms with a matching Inten

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
