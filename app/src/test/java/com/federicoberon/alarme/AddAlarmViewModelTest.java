package com.federicoberon.alarme;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import android.media.AudioManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.ui.home.HomeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Unit test for {@link HomeViewModel}
 */
public class AddAlarmViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AudioManager audioManager;

    @Mock
    private AlarmRepository alarmRepository;

    private AddAlarmViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mViewModel = new AddAlarmViewModel(audioManager, alarmRepository);
    }

    @Test
    public void test_setDaysOfWeek() {
        // UserDataSource returns an empty list of users
        mViewModel.setDaysOfWeek(TestDataHelper.getDaysOfWeek_mon_wed());
        assertArrayEquals(mViewModel.getDaysOfWeek(), TestDataHelper.getDaysOfWeek_mon_wed());
    }

    @Test
    public void test_setDaysOfWeek_position_all_unchecked() {
        // Repository returns a milestone
        mViewModel.setDaysOfWeek(TestDataHelper.getDaysOfWeek_wed());
        mViewModel.setDaysOfWeek(4);

        assertNull(mViewModel.getScheduledDay());
        assertArrayEquals(mViewModel.getDaysOfWeek(), TestDataHelper.getDaysOfWeek_all_unchecked());
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.DAY_OF_YEAR), TestDataHelper.getTomorrow());
    }

    @Test
    public void test_setDaysOfWeek_position_one_checked() {
        // Repository returns a milestone
        mViewModel.setDaysOfWeek(TestDataHelper.getDaysOfWeek_mon_wed());
        mViewModel.setDaysOfWeek(2);

        assertArrayEquals(mViewModel.getDaysOfWeek(), TestDataHelper.getDaysOfWeek_wed());
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.DAY_OF_WEEK), 3);
    }

    @Test
    public void test_restart(){
        mViewModel.restart();
        assertEquals(mViewModel.getmHour(), -1);
        assertEquals(mViewModel.getmMinutes(), -1);
        assertArrayEquals(mViewModel.getDaysOfWeek(), TestDataHelper.getDaysOfWeek_all_unchecked());
    }

    @Test
    public void test_containsTrue(){
        mViewModel.setDaysOfWeek(TestDataHelper.getDaysOfWeek_mon_wed());
        assertTrue(mViewModel.containsTrue());

        mViewModel.setDaysOfWeek(TestDataHelper.getDaysOfWeek_all_unchecked());
        assertFalse(mViewModel.containsTrue());
    }

    @Test
    public void test_setInsertedAlarm(){
        AlarmEntity alarmEntity = new AlarmEntity();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        alarmEntity.setAlarmDate(cal.getTime());

        mViewModel.setInsertedAlarm(alarmEntity);

        assertEquals(mViewModel.getNextAlarmValue(), cal);
        assertEquals(mViewModel.getInsertedAlarm(), alarmEntity);
    }

    @Test
    public void test_setNextAlarm(){
        mViewModel.setNextAlarm();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        assertEquals(mViewModel.getNextAlarmValue(), cal);
    }

    @Test
    public void test_setTime(){
        int hour = 20;
        int minutes = 0;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);

        mViewModel.setTime(hour, minutes);
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.HOUR_OF_DAY), cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.MINUTE), cal.get(Calendar.MINUTE));

        cal.add(Calendar.DAY_OF_YEAR, -1);
        mViewModel.setDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        mViewModel.setTime(hour, minutes);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.HOUR_OF_DAY), cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(mViewModel.getNextAlarmValue().get(Calendar.MINUTE), cal.get(Calendar.MINUTE));
    }

    @Test
    public void test_getDate(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        mViewModel.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        //mViewModel.setDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

        Calendar cal_result = Calendar.getInstance();
        cal_result.setTime(mViewModel.getDate());
        assertEquals(cal_result.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(cal_result.get(Calendar.MINUTE), cal.get(Calendar.MINUTE));
        assertEquals(cal_result.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_MONTH)+1);
    }

    @Test
    public void test_saveAlarm(){
        when(alarmRepository.insertOrUpdateAlarm(any())).thenReturn(Maybe.just(1L));
        mViewModel.setSelectedMelody(TestDataHelper.MELODY_1);

        mViewModel.saveAlarm(TestDataHelper.ALARM_1.getTitle()).test().assertValue(1L);
    }

    @Test
    public void test_saveAlarm_with_inserted_alarm(){
        when(alarmRepository.insertOrUpdateAlarm(any())).thenReturn(Maybe.just(1L));
        mViewModel.setInsertedAlarm(TestDataHelper.ALARM_1);
        mViewModel.setSelectedMelody(TestDataHelper.MELODY_1);

        mViewModel.saveAlarm(TestDataHelper.ALARM_1.getTitle()).test().assertValue(1L);

    }

    @Test
    public void test_getAlarmById(){
        when(alarmRepository.getAlarmById(TestDataHelper.ALARM_1.getId()))
                .thenReturn(Flowable.just(TestDataHelper.ALARM_1));

        mViewModel.getAlarmById(TestDataHelper.ALARM_1.getId())
                .test()
                // The correct milestone is emitted
                .assertValue(alarm -> {
                    // The emitted milestone is the expected one
                    return alarm.getId() == TestDataHelper.ALARM_1.getId();
                });
    }

    @Test
    public void test_getMelodies(){

        when(alarmRepository.getAllMelodies()).thenReturn(Flowable.just(Arrays.asList(
                TestDataHelper.MELODY_1, TestDataHelper.MELODY_2)));

        mViewModel.getMelodies().test()
                .assertValue(melodies -> {
                    // The emitted milestone is the expected one
                    return melodies.size() == TestDataHelper.MELODY_2.getId();
                });
    }

    @Test
    public void test_getMelodyById(){
        when(alarmRepository.getMelodyId(TestDataHelper.MELODY_1.getId()))
                .thenReturn(Single.just(TestDataHelper.MELODY_1));

        mViewModel.getMelodyById(TestDataHelper.MELODY_1.getId()).test()
                .assertValue(melody -> {
                    // The emitted milestone is the expected one
                    return melody.equals(TestDataHelper.MELODY_1);
                });
    }

    @Test
    public void test_getMelodyByName(){
        String name = TestDataHelper.MELODY_1.getTitle();
        when(alarmRepository.getMelodyName(name))
                .thenReturn(Single.just(TestDataHelper.MELODY_1));

        mViewModel.getMelodyByName(name).test()
                .assertValue(melody -> {
                    // The emitted milestone is the expected one
                    return melody.equals(TestDataHelper.MELODY_1);
                });
    }

    @Test
    public void test_setSelectedMelody_with_inserted_alarm(){
        mViewModel.setInsertedAlarm(TestDataHelper.ALARM_1);
        mViewModel.setSelectedMelody(TestDataHelper.MELODY_1);
        assertEquals(mViewModel.getInsertedAlarm().getMelodyUri(), TestDataHelper.MELODY_1.getUri());
        assertEquals(mViewModel.getSelectedMelody(), TestDataHelper.MELODY_1);
    }

    @Test
    public void test_setSelectedVibration(){
        String vibrationName = "vibration";
        mViewModel.setInsertedAlarm(TestDataHelper.ALARM_1);
        mViewModel.setSelectedVibration(vibrationName);

        assertEquals(vibrationName, mViewModel.getInsertedAlarm().getVibrationPatter());
        assertEquals(vibrationName, mViewModel.getSelectedVibration());
    }

    @Test
    public void test_setSelectedPostpone(){
        int postpone = 8;
        mViewModel.setInsertedAlarm(TestDataHelper.ALARM_1);
        mViewModel.setSelectedPostpone(postpone);

        assertEquals(postpone, mViewModel.getInsertedAlarm().getPostponeTime());
        assertEquals(postpone, mViewModel.getSelectedPostpone());
    }

    @Test
    public void test_setSelectedRepeat(){
        int repeat = 8;
        mViewModel.setInsertedAlarm(TestDataHelper.ALARM_1);
        mViewModel.setSelectedRepeat(repeat);

        assertEquals(repeat, mViewModel.getInsertedAlarm().getRepeatTime());
        assertEquals(repeat, mViewModel.getSelectedRepeat());
    }

}
