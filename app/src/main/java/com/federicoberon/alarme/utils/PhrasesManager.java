package com.federicoberon.alarme.utils;

import com.federicoberon.alarme.R;
import java.util.ArrayList;
import java.util.Arrays;

public class PhrasesManager {
    private static ArrayList<Integer> phrases;

    private static void startPhrasesOptions(){

        phrases = new ArrayList<>(
            Arrays.asList(R.string.phrase_enero_1,R.string.phrase_enero_2,
                R.string.phrase_enero_3,R.string.phrase_enero_4,R.string.phrase_enero_5,
                R.string.phrase_enero_6,R.string.phrase_enero_7,R.string.phrase_enero_8,
                R.string.phrase_enero_9,R.string.phrase_enero_10,R.string.phrase_enero_11,
                R.string.phrase_enero_12,R.string.phrase_enero_13,R.string.phrase_enero_14,
                R.string.phrase_enero_15,R.string.phrase_enero_16,R.string.phrase_enero_17,
                R.string.phrase_enero_18,R.string.phrase_enero_19,R.string.phrase_enero_20,
                R.string.phrase_enero_21,R.string.phrase_enero_22,R.string.phrase_enero_23,
                R.string.phrase_enero_24,R.string.phrase_enero_25,R.string.phrase_enero_26,
                R.string.phrase_enero_27,R.string.phrase_enero_28,R.string.phrase_enero_29,
                R.string.phrase_enero_30,R.string.phrase_febrero_1,R.string.phrase_febrero_2,
                R.string.phrase_febrero_3,R.string.phrase_febrero_4,R.string.phrase_febrero_5,
                R.string.phrase_febrero_6,R.string.phrase_febrero_7,R.string.phrase_febrero_8,
                R.string.phrase_febrero_9,R.string.phrase_febrero_10,R.string.phrase_febrero_11,
                R.string.phrase_febrero_12,R.string.phrase_febrero_13,R.string.phrase_febrero_14,
                R.string.phrase_febrero_15,R.string.phrase_febrero_16,R.string.phrase_febrero_17,
                R.string.phrase_febrero_18,R.string.phrase_febrero_19,R.string.phrase_febrero_20,
                R.string.phrase_febrero_21,R.string.phrase_febrero_22,R.string.phrase_febrero_23,
                R.string.phrase_febrero_24,R.string.phrase_febrero_25,R.string.phrase_febrero_26,
                R.string.phrase_febrero_27,R.string.phrase_febrero_28,R.string.phrase_febrero_29,
                R.string.phrase_marzo_1,R.string.phrase_marzo_2,R.string.phrase_marzo_3,
                R.string.phrase_marzo_4,R.string.phrase_marzo_5,R.string.phrase_marzo_6,
                R.string.phrase_marzo_7,R.string.phrase_marzo_8,R.string.phrase_marzo_9,
                R.string.phrase_marzo_10,R.string.phrase_marzo_11,R.string.phrase_marzo_12,
                R.string.phrase_marzo_13,R.string.phrase_marzo_14,R.string.phrase_marzo_15,
                R.string.phrase_marzo_16,R.string.phrase_marzo_17,R.string.phrase_marzo_18,
                R.string.phrase_marzo_19,R.string.phrase_marzo_20,R.string.phrase_marzo_21,
                R.string.phrase_marzo_22,R.string.phrase_marzo_23,R.string.phrase_marzo_24,
                R.string.phrase_marzo_25,R.string.phrase_marzo_26,R.string.phrase_marzo_27,
                R.string.phrase_marzo_28,R.string.phrase_marzo_29,R.string.phrase_marzo_30,
                R.string.phrase_marzo_31,R.string.phrase_abril_1,R.string.phrase_abril_2,
                R.string.phrase_abril_3,R.string.phrase_abril_4,R.string.phrase_abril_5,
                R.string.phrase_abril_6,R.string.phrase_abril_7,R.string.phrase_abril_8,
                R.string.phrase_abril_9,R.string.phrase_abril_10,R.string.phrase_abril_11,
                R.string.phrase_abril_12,R.string.phrase_abril_13,R.string.phrase_abril_14,
                R.string.phrase_abril_15,R.string.phrase_abril_16,R.string.phrase_abril_17,
                R.string.phrase_abril_18,R.string.phrase_abril_19,R.string.phrase_abril_20,
                R.string.phrase_abril_21,R.string.phrase_abril_22,R.string.phrase_abril_23,
                R.string.phrase_abril_24,R.string.phrase_abril_25,R.string.phrase_abril_26,
                R.string.phrase_abril_27,R.string.phrase_abril_28,R.string.phrase_abril_29,
                R.string.phrase_abril_30,R.string.phrase_abril_31,R.string.phrase_mayo_1,
                R.string.phrase_mayo_2,R.string.phrase_mayo_3,R.string.phrase_mayo_4,
                R.string.phrase_mayo_5,R.string.phrase_mayo_6,R.string.phrase_mayo_7,
                R.string.phrase_mayo_8,R.string.phrase_mayo_9,R.string.phrase_mayo_10,
                R.string.phrase_mayo_11,R.string.phrase_mayo_12,R.string.phrase_mayo_13,
                R.string.phrase_mayo_14,R.string.phrase_mayo_15,R.string.phrase_mayo_16,
                R.string.phrase_mayo_17,R.string.phrase_mayo_18,R.string.phrase_mayo_19,
                R.string.phrase_mayo_20,R.string.phrase_mayo_21,R.string.phrase_mayo_22,
                R.string.phrase_mayo_23,R.string.phrase_mayo_24,R.string.phrase_mayo_25,
                R.string.phrase_mayo_26,R.string.phrase_mayo_27,R.string.phrase_mayo_28,
                R.string.phrase_mayo_29,R.string.phrase_mayo_30,R.string.phrase_mayo_31,
                R.string.phrase_junio_1,R.string.phrase_junio_2,R.string.phrase_junio_3,
                R.string.phrase_junio_4,R.string.phrase_junio_5,R.string.phrase_junio_6,
                R.string.phrase_junio_7,R.string.phrase_junio_8,R.string.phrase_junio_9,
                R.string.phrase_junio_10,R.string.phrase_junio_11,R.string.phrase_junio_12,
                R.string.phrase_junio_13,R.string.phrase_junio_14,R.string.phrase_junio_15,
                R.string.phrase_junio_16,R.string.phrase_junio_17,R.string.phrase_junio_18,
                R.string.phrase_junio_19,R.string.phrase_junio_20,R.string.phrase_junio_21,
                R.string.phrase_junio_22,R.string.phrase_junio_23,R.string.phrase_junio_24,
                R.string.phrase_junio_25,R.string.phrase_junio_26,R.string.phrase_junio_27,
                R.string.phrase_junio_28,R.string.phrase_junio_29,R.string.phrase_junio_30,
                R.string.phrase_junio_31,R.string.phrase_julio_1));

    }

    public static Integer getPhraseId(int position) {
        if(phrases == null || phrases.size() < 1)
            startPhrasesOptions();

        return phrases.get(position);
    }

    public static Integer getPhrasesSize() {
        if(phrases == null || phrases.size() < 1)
            startPhrasesOptions();

        return phrases.size();
    }
}