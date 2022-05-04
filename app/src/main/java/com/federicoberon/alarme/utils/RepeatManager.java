package com.federicoberon.alarme.utils;

import android.content.Context;

import com.federicoberon.alarme.R;

import java.util.LinkedHashMap;
import java.util.Set;

public class RepeatManager {
    public static final Integer DEFAULT_REPEAT = 8;

    private static LinkedHashMap<Integer, String> repeatOptions;


    private static void startRepeatOptions(Context context){
        repeatOptions = new LinkedHashMap<Integer, String>(){{
            put(1,                String.format(context.getString(R.string.hours_string_single), 1));
            put(2,               String.format(context.getString(R.string.hours_string), 2));
            put(4,                 String.format(context.getString(R.string.hours_string), 4));
            put(DEFAULT_REPEAT,      String.format(context.getString(R.string.hours_string), DEFAULT_REPEAT));
            put(12,    String.format(context.getString(R.string.hours_string), 12));
        }};
    }


    public static LinkedHashMap<Integer, String> getRepeatOptions(Context context){
        if (repeatOptions == null)
            startRepeatOptions(context);

        return repeatOptions;
    }

    public static String getRepeat(int selectedRepeat) {
        return repeatOptions.get(selectedRepeat);
    }

    public static Integer getRepeatTime(int indexOf) {
        // get the key set
        Set<Integer> keySet = repeatOptions.keySet();

        Integer[] keyArray
                = keySet.toArray(new Integer[0]);

        return keyArray[indexOf];
    }
}