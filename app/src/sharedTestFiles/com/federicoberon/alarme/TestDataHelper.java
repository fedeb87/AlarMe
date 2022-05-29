package com.federicoberon.alarme;

import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.MelodyEntity;
import com.federicoberon.alarme.utils.VibrationManager;

import java.util.Calendar;
import java.util.Date;

public class TestDataHelper {
    public static final AlarmEntity ALARM_1 = new AlarmEntity(1, "First title", getDate1(), 20,
            0, 20*60, getDaysOfWeek_all_unchecked(), true, " ", " ",
            5, true, " ", false, 5, true,
            5, true, false, true, false, false);

    public static final AlarmEntity ALARM_2 = new AlarmEntity(2, " ", getDate2(), 15,
            10, 910, getDaysOfWeek_all_checked(), false, " ", " ",
            5, true, " ", true, 5, true,
            5, true, true, true, false, true);

    public static final AlarmEntity ALARM_3 = new AlarmEntity(3, "Third alarm", getDate3(), 14,
            30, 1010, getDaysOfWeek_all_checked(), true, "content://media/internal/audio/media/9", "Flutey Phone",
            5, false, " ", true, 5, false,
            0, true, false, true, false, true);

    public static final MelodyEntity MELODY_1 = new MelodyEntity(1, "Test Melody", "Melody URI");
    public static final MelodyEntity MELODY_2 = new MelodyEntity(2, "Test Melody 2 ", "Melody 2 URI");

    public static final VibrationManager.VibrationPattern VIBRATION_PATTERN_1 =
            new VibrationManager.VibrationPattern(new long[]{100, 1200});

    public static boolean[] getDaysOfWeek_all_unchecked(){
        return new boolean[7];
    }

    private static boolean[] getDaysOfWeek_all_checked(){
        return new boolean[]{true, true, true, true, true, true, true};
    }

    public static boolean[] getDaysOfWeek_mon_wed(){
        return new boolean[]{false, true, false, true, false, false, false};
    }

    public static boolean[] getDaysOfWeek_wed(){
        return new boolean[]{false, false, false, true, false, false, false};
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

    public static Date getDate3(){
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2028);
        calendar2.set(Calendar.MONTH, 6);
        calendar2.set(Calendar.DAY_OF_MONTH, 7);
        calendar2.set(Calendar.HOUR_OF_DAY, 15);
        calendar2.set(Calendar.MINUTE, 10);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return calendar2.getTime();
    }

    public static int getTomorrow() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1;
    }
}
