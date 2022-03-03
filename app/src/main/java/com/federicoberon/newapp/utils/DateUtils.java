package com.federicoberon.newapp.utils;

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
        if (alarmHour <= currentHour) // es mañana
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        return currentCalendar.get(Calendar.DAY_OF_MONTH);
    }
}
