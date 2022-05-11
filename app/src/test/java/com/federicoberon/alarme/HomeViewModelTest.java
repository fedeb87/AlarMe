package com.federicoberon.alarme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.ui.home.HomeViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Unit test for {@link HomeViewModel}
 */
public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AlarmRepository alarmRepository;

    private HomeViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mViewModel = new HomeViewModel(alarmRepository);
    }

    @Test
    public void test_getAllAlarms_whenNoAlarmsSaved() {
        // UserDataSource returns an empty list of alarms
        when(alarmRepository.getAllAlarms()).thenReturn(Flowable.empty());

        //When getting the milestone
        mViewModel.getAllAlarms()
                .test()
                .assertComplete();
    }

    @Test
    public void test_getMilestone_whenMilestoneSaved() {
        // Repository returns a milestone
        when(alarmRepository.getAllAlarms()).thenReturn(Flowable.just(Collections.singletonList(TestDataHelper.ALARM_1)));

        //When getting the milestone
        mViewModel.getAllAlarms()
                .test()
                .assertValue(Collections.singletonList(TestDataHelper.ALARM_1));

    }

    @Test
    public void test_getFirstAlarmStarted() {
        // Given that the Repo returns a milestone
        when(alarmRepository.getFirstAlarmStarted()).thenReturn(Flowable.just(Arrays.asList(
                TestDataHelper.ALARM_2, TestDataHelper.ALARM_1)));

        //When getting the milestone
        mViewModel.getFirstAlarmStarted()
                .test()
                // The correct milestone is emitted
                .assertValue(alarm -> {
                    // The emitted milestone is the expected one
                    return alarm.get(0).equals(TestDataHelper.ALARM_2);
                });

    }

    @Test
    public void test_deleteAlarm(){
        // Given that the Repo returns a Completable1
        when(alarmRepository.deleteAlarm(TestDataHelper.ALARM_2)).thenReturn(Maybe.just(1));

        mViewModel.deleteAlarm(TestDataHelper.ALARM_2)
            .test()
            // The correct milestone is emitted
            .assertValue(cant -> {
                // The emitted milestone is the expected one
                return cant.equals(1);
        });

        verify(alarmRepository).deleteAlarm(TestDataHelper.ALARM_2);
    }

    @Test
    public void test_deleteAlarms(){

        List<Long> ids = Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId());

        // Given that the Repo returns a Completable
        when(alarmRepository.deleteAlarms(ids)).thenReturn(Completable.complete());
        mViewModel.deleteAlarms(ids);

        verify(alarmRepository).deleteAlarms(ids);
    }

    @Test
    public void test_inactiveAlarms(){

        List<Long> ids = Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId());

        // Given that the Repo returns a Completable
        when(alarmRepository.updateAlarms(ids, false)).thenReturn(Completable.complete());
        mViewModel.inactiveAlarms(ids);

        verify(alarmRepository).updateAlarms(ids, false);
    }

    @Test
    public void test_activeAlarms(){

        List<Long> ids = Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId());

        // Given that the Repo returns a Completable
        when(alarmRepository.updateAlarms(ids, true)).thenReturn(Completable.complete());
        mViewModel.activeAlarms(ids);

        verify(alarmRepository).updateAlarms(ids, true);
    }

    @Test
    public void test_disableAlarm(){
        AlarmEntity alarmEntity = TestDataHelper.ALARM_1;
        mViewModel.disableAlarm(alarmEntity);
        verify(alarmRepository).insertOrUpdateAlarm(alarmEntity);
    }

    @Test
    public void test_setAndGetAlarms(){
        List<AlarmEntity> alarmList = Arrays.asList(TestDataHelper.ALARM_1, TestDataHelper.ALARM_2);
        mViewModel.setAlarms(alarmList);
        assertSame(mViewModel.getAlarms(), alarmList);
    }

    @Test
    public void propertiesAreSetMilestoneConstructor() {
        HomeViewModel homeViewModel = new HomeViewModel(alarmRepository);

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
    }
}
