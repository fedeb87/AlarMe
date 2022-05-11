package com.federicoberon.alarme.utils;

import android.os.Vibrator;
import java.util.LinkedHashMap;

public class VibrationManager {

    /** Name of default vibration */
    public static final String DEFAULT_VIBRATION = "Default";

    /** Delay after which to start playing all vibrations */
    private static final int DELAY = 100;

    private static final LinkedHashMap<String, VibrationPattern> vibrations = new LinkedHashMap<String, VibrationPattern>(){{
        put(DEFAULT_VIBRATION,      new VibrationPattern(new long[]{DELAY, 250, 250, 250}));
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

    public static class VibrationPattern {
        final long[] timestamps;
        public VibrationPattern(long[] _timestamps) {
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
     * uses deprecated vibrate method otherwise.
     * @param vibrateName The name of the vibration Pattern to be played
     */
    public static void vibrateByName(Vibrator vibrator, String vibrateName) {
        vibrator.vibrate(VibrationManager.getVibrationByName(vibrateName), 0);
    }
}
