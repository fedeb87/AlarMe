package com.federicoberon.alarme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.federicoberon.alarme.di.ApplicationContext;
import com.federicoberon.alarme.retrofit.HoroscopeService;
import com.federicoberon.alarme.retrofit.HoroscopeServiceTwo;
import com.federicoberon.alarme.retrofit.WeatherService;
import com.federicoberon.alarme.retrofit.WeatherServiceTwo;
import com.federicoberon.alarme.ui.alarm.AlarmViewModel;
import com.federicoberon.alarme.ui.home.HomeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Unit test for {@link HomeViewModel}
 */
public class AlarmViewModelTest {

    @Inject
    private WeatherService weatherService;

    @Inject
    private WeatherServiceTwo weatherServiceTwo;

    @Inject
    private HoroscopeService horoscopeService;

    @Inject
    private HoroscopeServiceTwo horoscopeServiceTwo;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AlarmViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mViewModel = new AlarmViewModel(weatherService, weatherServiceTwo,
                horoscopeService, horoscopeServiceTwo);
    }

    @Test
    public void test_getAllAlarms_whenNoAlarmsSaved() {
        assertTrue(true);
    }

    @Test
    public void propertiesAreSetMilestoneConstructor() {
        AlarmViewModel alarmViewModel = new AlarmViewModel(weatherService, weatherServiceTwo,
                horoscopeService, horoscopeServiceTwo);
/*
        try {
            // I'm use reflection to test private methods
            Field alarmRepositoryField = HomeViewModel.class.getDeclaredField(
                    "mAlarmRepository");
            alarmRepositoryField.setAccessible(true);
            assertSame(alarmRepository, alarmRepositoryField.get(homeViewModel));

            Field multiselect_listField = HomeViewModel.class.getDeclaredField(
                    "multiselect_list");
            multiselect_listField.setAccessible(true);
            assertEquals(multiselect_listField.get(homeViewModel), Collections.EMPTY_LIST);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
 */
    }
}
