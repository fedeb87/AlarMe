package com.federicoberon.alarme;

import com.federicoberon.alarme.model.AlarmEntity;
import java.util.Calendar;
import java.util.Date;

public class TestDataHelper {
    public static final AlarmEntity ALARM_1 = new AlarmEntity(1, " ", getDate1(), 20,
            0, 20*60, getDaysOfWeek(), true, " ", " ",
            5, true, " ", false, 5, true,
            5, true, false, true, false);

    public static final AlarmEntity ALARM_2 = new AlarmEntity(2, " ", getDate2(), 15,
            10, 910, getDaysOfWeek2(), false, " ", " ",
            5, true, " ", true, 5, true,
            5, true, true, true, true);


    private static boolean[] getDaysOfWeek(){
        return new boolean[7];
    }

    private static boolean[] getDaysOfWeek2(){
        return new boolean[]{true, true, true, true, true, true, true};
    }

    public static Date getDate1(){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2022);
        calendar1.set(Calendar.MONTH, 9);
        calendar1.set(Calendar.DAY_OF_MONTH, 10);
        calendar1.set(Calendar.HOUR_OF_DAY, 20);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        return calendar1.getTime();
    }

    public static Date getDate2(){
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2022);
        calendar2.set(Calendar.MONTH, 6);
        calendar2.set(Calendar.DAY_OF_MONTH, 7);
        calendar2.set(Calendar.HOUR_OF_DAY, 15);
        calendar2.set(Calendar.MINUTE, 10);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return calendar2.getTime();
    }
}
