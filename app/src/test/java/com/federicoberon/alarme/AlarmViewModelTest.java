package com.federicoberon.alarme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.federicoberon.alarme.api.WeatherResponse;
import com.federicoberon.alarme.api.WeatherResponseTwo;
import com.federicoberon.alarme.api.WeatherService;
import com.federicoberon.alarme.api.WeatherServiceTwo;
import com.federicoberon.alarme.ui.alarm.AlarmViewModel;
import com.federicoberon.alarme.ui.home.HomeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;
import io.reactivex.Observable;

import static org.mockito.Mockito.when;

/**
 * Unit test for {@link HomeViewModel}
 */
@RunWith(JUnit4.class)
public class AlarmViewModelTest {

    private AlarmViewModel mViewModel;

    @Mock
    private WeatherService weatherService;

    @Mock
    private WeatherServiceTwo weatherServiceTwo;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    double latitude = 1.5;
    double longitude = 1.6;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mViewModel = new AlarmViewModel(weatherService, weatherServiceTwo);
        mViewModel.init("sign");
    }

    @Test
    public void test_init() {
        String test = "test";

        mViewModel.init(test);

        try {
            // I'm use reflection to test private methods
            Field sign_field = AlarmViewModel.class.getDeclaredField("sign");

            sign_field.setAccessible(true);

            assertEquals(test, sign_field.get(mViewModel));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_callWeatherAPI() {

        Observable<WeatherResponse> response = Observable.just(new WeatherResponse());

        when(weatherService.getWeather(
                "576d14184a3e42cc8cd10015222203", new double[]{latitude, longitude}, 1))
                .thenReturn(response);

        Observable<WeatherResponse> result = mViewModel.callWeatherAPI(latitude, longitude);

        assertSame(result, response);
    }

    @Test
    public void test_callWeatherAPI_wrong_param() {

        when(weatherService.getWeather(
                "576d14184a3e42cc8cd10015222203", new double[]{0, 0}, 1))
                .thenReturn(Observable.empty());

        Observable<WeatherResponse> result = mViewModel.callWeatherAPI(0, 0);
        assertSame(result, Observable.empty());


        when(weatherService.getWeather(
                "576d14184a3e42cc8cd10015222203", new double[]{latitude, longitude}, 1))
                .thenThrow(new RuntimeException());

        result = mViewModel.callWeatherAPI(latitude, longitude);
        assertSame(result, Observable.empty());
    }

    @Test
    public void test_callWeatherAPITwo() {

        Observable<WeatherResponseTwo> response = Observable.just(new WeatherResponseTwo());

        when(weatherServiceTwo.getWeather(latitude, longitude,"minutely,hourly,alerts"
                , "imperial", "33b26b2199a99f5ddb67b87ce114460a"))
                .thenReturn(response);

        Observable<WeatherResponseTwo> result = mViewModel.callWeatherAPITwo(latitude, longitude);

        assertSame(result, response);
    }

    @Test
    public void test_callWeatherAPITwo_wrong_param() {

        when(weatherServiceTwo.getWeather(0, 0, "minutely,hourly,alerts"
                , "imperial", "33b26b2199a99f5ddb67b87ce114460a"))
                .thenReturn(Observable.empty());

        Observable<WeatherResponseTwo> result = mViewModel.callWeatherAPITwo(0, 0);
        assertSame(result, Observable.empty());


        when(weatherServiceTwo.getWeather(latitude, longitude,"minutely,hourly,alerts"
                , "imperial", "33b26b2199a99f5ddb67b87ce114460a"))
                .thenThrow(new RuntimeException());

        result = mViewModel.callWeatherAPITwo(latitude, longitude);
        assertSame(result, Observable.empty());
    }

    @Test
    public void test_coordsCached() {
        assertFalse(mViewModel.cordsCached());
        mViewModel.latitude = 1;
        assertTrue(mViewModel.cordsCached());
    }

    @Test
    public void propertiesAreSetAlarmConstructor() {
        AlarmViewModel alarmViewModel = new AlarmViewModel(weatherService, weatherServiceTwo);

        try {
            // I'm use reflection to test private methods
            Field weatherService_field = AlarmViewModel.class.getDeclaredField(
                    "weatherService");
            Field weatherServiceTwo_field = AlarmViewModel.class.getDeclaredField(
                    "weatherServiceTwo");
            Field isPreview_field = AlarmViewModel.class.getDeclaredField("isPreview");
            Field inCelsius_field = AlarmViewModel.class.getDeclaredField("inCelsius");

            weatherService_field.setAccessible(true);
            weatherServiceTwo_field.setAccessible(true);
            isPreview_field.setAccessible(true);
            inCelsius_field.setAccessible(true);

            assertEquals(weatherService, weatherService_field.get(alarmViewModel));
            assertEquals(weatherServiceTwo, weatherServiceTwo_field.get(alarmViewModel));
            assertEquals(false, isPreview_field.get(alarmViewModel));
            assertEquals(true, inCelsius_field.get(alarmViewModel));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
