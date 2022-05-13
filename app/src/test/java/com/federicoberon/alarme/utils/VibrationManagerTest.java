package com.federicoberon.alarme.utils;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.federicoberon.alarme.TestDataHelper;

public class VibrationManagerTest {
    final String key = "Long";

    @Test
    public void test_getVibrations(){
        assertTrue(VibrationManager.getVibrations().containsKey(key));
        assertEquals(13, VibrationManager.getVibrations().size());
    }

    @Test
    public void test_getVibrationByName(){
        assertArrayEquals(VibrationManager.getVibrationByName(key)
                , TestDataHelper.VIBRATION_PATTERN_1.timestamps);
    }
}
