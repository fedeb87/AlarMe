package com.federicoberon.newapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.federicoberon.newapp.R;

import java.util.LinkedHashMap;

public class VibrationManager {

    /** Name of default vibration */
    public static final String DEFAULT_VIBRATION = "Default";

    /** Delay after which to start playing all vibrations */
    private static final int DELAY = 100;

    /** Name of None vibration, i.e. no vibration set */
    public static final String NONE_VIBRATION = "None";

    private static final LinkedHashMap<String, VibrationPattern> vibrations = new LinkedHashMap<String, VibrationPattern>(){{
        put(DEFAULT_VIBRATION,      new VibrationPattern(new long[]{DELAY, 250, 250, 250}));
        //put(NONE_VIBRATION,         new VibrationPattern(new long[]{}));
        put("Short",                new VibrationPattern(new long[]{DELAY, 300}));
        put("Medium",               new VibrationPattern(new long[]{DELAY, 500}));
        put("Long",                 new VibrationPattern(new long[]{DELAY, 1200}));
        put("Short Double Skip",    new VibrationPattern(new long[]{DELAY, 150, 150, 150}));
        put("Double Skip",          new VibrationPattern(new long[]{DELAY, 300, 300, 300}));
        put("Short Multi Skip",     new VibrationPattern(new long[]{DELAY, 200, 50, 200, 100, 200, 150, 200}));
        put("Multi Skip",           new VibrationPattern(new long[]{DELAY, 300, 75, 300, 150, 300, 175, 300}));
        put("Skippidy Skip",        new VibrationPattern(new long[]{DELAY, 300, 150, 200, 200, 500, 50, 100}));
        put("Short Short Long",     new VibrationPattern(new long[]{DELAY, 70, 70, 70, 55, 70, 55, 625}));
        put("Long Short Short",     new VibrationPattern(new long[]{DELAY, 220, 90, 60, 75, 70, 75, 60}));
        put("Staccato",             new VibrationPattern(new long[]{DELAY, 70, 70, 70, 70, 70, 70, 70, 70}));
        put("Double Staccato",      new VibrationPattern(new long[]{DELAY, 75, 85, 60, 70, 70, 50, 50, 430, 70, 90, 70, 70, 60, 50, 50}));
    }};

    private static final LinkedHashMap<VibrationPattern, String> vibrations_reverse = new LinkedHashMap<VibrationPattern, String>(){{
        put(new VibrationPattern(new long[]{DELAY, 250, 250, 250}), DEFAULT_VIBRATION);
        put(new VibrationPattern(new long[]{DELAY, 300}),   "Short");
        put(new VibrationPattern(new long[]{DELAY, 500}), "Medium");
        put(new VibrationPattern(new long[]{DELAY, 1200}), "Long");
        put(new VibrationPattern(new long[]{DELAY, 150, 150, 150}),
                "Short Double Skip");
        put(new VibrationPattern(new long[]{DELAY, 300, 300, 300}),
                "Double Skip");
        put(new VibrationPattern(new long[]{DELAY, 200, 50, 200, 100, 200, 150, 200}),
                "Short Multi Skip");
        put(new VibrationPattern(new long[]{DELAY, 300, 75, 300, 150, 300, 175, 300}),
                "Multi Skip");
        put(new VibrationPattern(new long[]{DELAY, 300, 150, 200, 200, 500, 50, 100}),
                "Skippidy Skip");
        put(new VibrationPattern(new long[]{DELAY, 70, 70, 70, 55, 70, 55, 625}),
                "Short Short Long");
        put(new VibrationPattern(new long[]{DELAY, 220, 90, 60, 75, 70, 75, 60}),
                "Long Short Short");
        put(new VibrationPattern(new long[]{DELAY, 70, 70, 70, 70, 70, 70, 70, 70}),
                "Staccato");
        put(new VibrationPattern(new long[]{DELAY, 75, 85, 60, 70, 70, 50, 50, 430, 70, 90, 70, 70, 60, 50, 50}),
                "Double Staccato");
    }};

    public static String getVibrationNameByPattern(String vibrationPatter) {
        return vibrations_reverse.get(vibrationPatter);
    }

    private static class VibrationPattern {
        final long[] timestamps;
        VibrationPattern(long[] _timestamps) {
            timestamps = _timestamps;
        }
    }

    public static LinkedHashMap<String, VibrationPattern> getVibrations(){
        return vibrations;
    }

    public static long[] getVibrationByName(String name){
        return vibrations.get(name).timestamps;
    }

    /**
     * Plays the vibration given a vibratePattern object. Uses notification channels if SDK >= 26,
     * uses deprecatd vibrate method otherwise.
     * @param vibrateName The name of the vibration Pattern to be played
     */
    public static void vibrateByName(Context context, String vibrateName) {
        if (!vibrateName.equals(NONE_VIBRATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, vibrateName)
                        .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                        .setTimeoutAfter(3000)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1, builder.build());
            }
            else {
                VibrationPattern pattern = getVibrations().get(vibrateName);
                if (pattern != null)
                    vibrate(context, pattern.timestamps);
            }
        }
    }

    /**
     * Deprecated. Plays the vibration given a long pattern. Used for SDK < 26 where notification
     * channels aren't available yet. Uses Vibrator service.
     * @param pattern Pattern to play. Array of longs.
     */
    public static void vibrate(Context context, long[] pattern) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
                );
            } else {
                vibrator.vibrate(pattern, -1);
            }
        }
        else{
            Log.e("MIO", "ERROR::: No vibrator found!");
        }
    }
}
