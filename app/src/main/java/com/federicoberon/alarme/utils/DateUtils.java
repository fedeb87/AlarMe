package com.federicoberon.alarme.utils;

import com.federicoberon.alarme.model.AlarmEntity;

import java.util.Calendar;

public class DateUtils {

    /**
     *
     * @param mHour Hour of the alarm
     * @param mMinutes Minutes of the alarm
     * @return (int) Day of month for the time passed
     */
    public static int isTomorrow(int mHour, int mMinutes) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)*60 + currentCalendar.get(Calendar.MINUTE);
        int alarmHour = mHour*60 + mMinutes;
        if (alarmHour <= currentHour) // is tomorrow
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        return currentCalendar.get(Calendar.DAY_OF_MONTH);
    }


    public static boolean[] getArrayBooleanDays(AlarmEntity alarmEntity){

        boolean[] daysOfWeek = new boolean[7];
        daysOfWeek[0] = alarmEntity.isSunday();
        daysOfWeek[1] = alarmEntity.isMonday();
        daysOfWeek[2] = alarmEntity.isThursday();
        daysOfWeek[3] = alarmEntity.isWednesday();
        daysOfWeek[4] = alarmEntity.isTuesday();
        daysOfWeek[5] = alarmEntity.isFriday();
        daysOfWeek[6] = alarmEntity.isSaturday();

        return daysOfWeek;
    }
}
