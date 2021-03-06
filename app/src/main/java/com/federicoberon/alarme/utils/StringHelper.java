package com.federicoberon.alarme.utils;

import android.content.Context;
import android.text.format.DateFormat;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.model.AlarmEntity;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class StringHelper {

    public static String getFormatedAlarmDate(Context context, AlarmEntity alarmEntity) {
        boolean[] daysOfWeek = DateUtils.getArrayBooleanDays(alarmEntity);
        Calendar nextAlarm = Calendar.getInstance();
        nextAlarm.setTime(alarmEntity.getAlarmDate());

        return getFormatedAlarmDate(context, nextAlarm, daysOfWeek);
    }

    public static String getFormatedAlarmDate(Context context, Calendar nextAlarm, boolean[] daysOfWeek) {
        // Hoy sab. 12 de feb
        // Mañana dom. 13 de feb
        // Cada Mar
        // Cada Mie
        // Vie. 15 de abr
        if(containsTrue(daysOfWeek)){

            if(allTrue(daysOfWeek))
                return String.format(context.getString(R.string.alarms_everyDay),
                        DateFormat.getTimeFormat(context).format(nextAlarm.getTime()));

            StringBuilder days = new StringBuilder();
            int i=0;
            while(i<7){
                if(daysOfWeek[i]){
                    days.append(DateFormatSymbols.getInstance().getWeekdays()[i + 1].substring(0, 3));
                    days.append(". ");
                }
                i++;
            }
            return String.format(context.getString(R.string.alarms_every), days.toString(),
                    DateFormat.getTimeFormat(context).format(nextAlarm.getTime()));
        }else if(isToday(nextAlarm)){
            return String.format(context.getString(R.string.alarm_today),
                    nextAlarm.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,Locale.getDefault()),
                    nextAlarm.get(Calendar.DAY_OF_MONTH),
                    nextAlarm.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault()),
                    DateFormat.format("kk:mm", nextAlarm.getTime()).toString());
        }else if(isTomorrow(nextAlarm)){
            return String.format(context.getString(R.string.alarm_tomorrow),
                    nextAlarm.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,Locale.getDefault()),
                    nextAlarm.get(Calendar.DAY_OF_MONTH),
                    nextAlarm.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault()),
                    DateFormat.format("kk:mm", nextAlarm.getTime()).toString());
        }else
            return String.format("%s %s - %s", nextAlarm.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                Locale.getDefault()),
                DateFormat.getDateFormat(context.getApplicationContext()).format(nextAlarm.getTime()),
                DateFormat.format("kk:mm", nextAlarm.getTime()).toString());
    }

    private static boolean isTomorrow(Calendar nextAlarm) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.get(Calendar.DAY_OF_YEAR) == nextAlarm.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isToday(Calendar nextAlarm) {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_YEAR) == nextAlarm.get(Calendar.DAY_OF_YEAR);
    }


    public static boolean containsTrue(boolean[] daysOfWeek) {
        for(boolean b : daysOfWeek) if(b) return true;
        return false;
    }

    public static boolean allTrue(boolean[] daysOfWeek) {
        for(boolean b : daysOfWeek) if(!b) return false;
        return true;
    }
}
