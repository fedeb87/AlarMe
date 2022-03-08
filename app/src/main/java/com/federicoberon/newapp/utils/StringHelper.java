package com.federicoberon.newapp.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.model.AlarmEntity;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StringHelper {

    public static String getFormatedAlarmDate(Context context, AlarmEntity alarmEntity) {

        boolean[] daysOfWeek = new boolean[7];
        daysOfWeek[0] = alarmEntity.isSunday();
        daysOfWeek[1] = alarmEntity.isMonday();
        daysOfWeek[2] = alarmEntity.isThursday();
        daysOfWeek[3] = alarmEntity.isWednesday();
        daysOfWeek[4] = alarmEntity.isTuesday();
        daysOfWeek[5] = alarmEntity.isFriday();
        daysOfWeek[6] = alarmEntity.isSaturday();

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
            String days = "";
            int i=0;
            while(i<7){
                if(daysOfWeek[i]){
                    days += DateFormatSymbols.getInstance().getWeekdays()[i+1].substring(0,3);
                    days+=". ";
                }
                i++;
            }
            return String.format(context.getString(R.string.alarms_every), days,
                    DateFormat.getTimeFormat(context).format(nextAlarm.getTime()));
        }else if(iToday(nextAlarm)){
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
        cal.add(Calendar.DAY_OF_MONTH, 1);
        if(cal.get(Calendar.DAY_OF_MONTH) == nextAlarm.get(Calendar.DAY_OF_MONTH)){
            return true;
        }
        return false;
    }

    private static boolean iToday(Calendar nextAlarm) {
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.DAY_OF_MONTH) == nextAlarm.get(Calendar.DAY_OF_MONTH)){
            return true;
        }
        return false;
    }


    private static boolean containsTrue(boolean[] daysOfWeek) {
        for(boolean b : daysOfWeek) if(b) return true;
        return false;
    }

    public static String toDay(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Wrong Day";
        }
    }
}
