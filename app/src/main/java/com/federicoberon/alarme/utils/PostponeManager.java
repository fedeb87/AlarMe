package com.federicoberon.alarme.utils;

import android.content.Context;

import com.federicoberon.alarme.R;

import java.util.LinkedHashMap;
import java.util.Set;

public class PostponeManager {
    private static final Integer DEFAULT_POSTPONE = 5;
    private static LinkedHashMap<Integer, String> postponeOptions;

    private static void startPostponeOptions(Context context){
        postponeOptions = new LinkedHashMap<Integer, String>(){{
            put(3,                String.format(context.getString(R.string.minutes_string), 3));
            put(DEFAULT_POSTPONE,      String.format(context.getString(R.string.minutes_string), DEFAULT_POSTPONE));
            put(10,               String.format(context.getString(R.string.minutes_string), 10));
            put(20,                 String.format(context.getString(R.string.minutes_string), 20));
            put(30,    String.format(context.getString(R.string.minutes_string), 30));
        }};
    }

    public static LinkedHashMap<Integer, String> getPostponeOptions(Context context){
        if (postponeOptions == null)
            startPostponeOptions(context);

        return postponeOptions;
    }

    public static String getPostpone(int selectedPostpone) {
        return postponeOptions.get(selectedPostpone);
    }

    public static Integer getPostponeTime(int indexOf) {
        // get the key set
        Set<Integer> keySet = postponeOptions.keySet();

        Integer[] keyArray
                = keySet.toArray(new Integer[keySet.size()]);

        return keyArray[indexOf];
    }
}