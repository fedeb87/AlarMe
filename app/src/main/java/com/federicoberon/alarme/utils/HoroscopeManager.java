package com.federicoberon.alarme.utils;

import android.content.Context;

import com.federicoberon.alarme.R;
import com.federicoberon.alarme.model.HoroscopeNames;
import java.util.LinkedHashMap;

public class HoroscopeManager {
    private static LinkedHashMap<String, HoroscopeNames> horoscopes;

    public static void initHoroscopes(Context context){
        // tauro -> {Tauro, Taurus, taurus, daily-taurus-horoscope}
        horoscopes = new LinkedHashMap<String, HoroscopeNames>(){{
            put("aries",        new HoroscopeNames(context.getString(R.string.aries),
                    "aries", "daily-aries-horoscope", R.drawable.aries));
            put("taurus",       new HoroscopeNames(context.getString(R.string.taurus),
                    "taurus", "daily-taurus-horoscope", R.drawable.taurus));
            put("gemini",       new HoroscopeNames(context.getString(R.string.gemini),
                    "gemini", "daily-gemini-horoscope", R.drawable.gemini));
            put("cancer",       new HoroscopeNames(context.getString(R.string.cancer),
                    "cancer", "daily-cancer-horoscope", R.drawable.cancer));
            put("leo",          new HoroscopeNames(context.getString(R.string.leo),
                    "leo", "daily-leo-horoscope", R.drawable.leo));
            put("virgo",        new HoroscopeNames(context.getString(R.string.virgo),
                    "virgo", "daily-virgo-horoscope", R.drawable.virgo));
            put("libra",        new HoroscopeNames(context.getString(R.string.libra),
                    "libra", "daily-libra-horoscope", R.drawable.libra));
            put("scorpio",      new HoroscopeNames(context.getString(R.string.scorpio),
                    "scorpio", "daily-scorpio-horoscope", R.drawable.scorpio));
            put("sagittarius",  new HoroscopeNames(context.getString(R.string.sagittarius),
                    "sagittarius", "daily-sagittarius-horoscope", R.drawable.sagittarius));
            put("capricorn",    new HoroscopeNames(context.getString(R.string.capricorn),
                    "capricorn", "daily-capricorn-horoscope", R.drawable.capricorn));
            put("aquarius",     new HoroscopeNames(context.getString(R.string.aquarius),
                    "aquarius", "daily-aquarius-horoscope", R.drawable.aquarius));
            put("pisces",       new HoroscopeNames(context.getString(R.string.pisces),
                    "pisces", "daily-pisces-horoscope", R.drawable.pisces));
        }};
    }

    public static String getName(Context context, String id){
        if (horoscopes == null)
            initHoroscopes(context);
        return horoscopes.get(id).getName();
    }

    public static String getNameURL(Context context, String id){
        if (horoscopes == null)
            initHoroscopes(context);
        return horoscopes.get(id).getName_in_url();
    }

    public static String getNameURLTwo(Context context, String id){
        if (horoscopes == null)
            initHoroscopes(context);
        return horoscopes.get(id).getName_in_url_two();
    }

    public static int getIconId(Context context, String id){
        if (horoscopes == null)
            initHoroscopes(context);
        return horoscopes.get(id).getIconId();
    }
}
