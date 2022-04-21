package com.federicoberon.alarme.utils;

import android.app.Application;
import android.content.Context;

import com.federicoberon.alarme.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 12 * MONTH_MILLIS;

    /**
     * Get relative time from now
     * @param time timestamp
     * @param ctx Context
     * @return
     */
    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        long diff = time - now;

        int years, months, days, hours, minutes;
        String results = "";

        if(diff >= YEAR_MILLIS){
            years = (int) (diff / YEAR_MILLIS);
            results = results.concat(String.format(ctx.getString(R.string.years), years));
            diff = diff - (years * YEAR_MILLIS);
        }

        if(diff >= MONTH_MILLIS){
            months = (int) (diff / MONTH_MILLIS);
            results = results.concat(String.format(ctx.getString(R.string.months), months));
            diff = diff - (months * MONTH_MILLIS);
        }

        if(diff >= DAY_MILLIS){
            days = (int) (diff / DAY_MILLIS);
            results = results.concat(String.format(ctx.getString(R.string.days), days));
            diff = diff - (days * DAY_MILLIS);
        }

        if(diff >= HOUR_MILLIS){
            hours = (int) (diff / HOUR_MILLIS);
            results = results.concat(String.format(ctx.getString(R.string.hours), hours));
            diff = diff - (hours * HOUR_MILLIS);
        }

        if(diff >= MINUTE_MILLIS){
            minutes = (int) (diff / MINUTE_MILLIS);
            results = results.concat(String.format(ctx.getString(R.string.minutes), minutes));
        }

        return results;
    }

}

