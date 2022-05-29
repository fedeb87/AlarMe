package com.federicoberon.alarme.datasource.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.TestUtils;
import com.federicoberon.alarme.datasource.AppDatabase;
import com.federicoberon.alarme.model.AlarmEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AlarmDaoTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;

    private AlarmDao mAlarmDao;

    @Before
    public void initDb() {
        mDatabase = TestUtils.initDb();
        mAlarmDao = mDatabase.alarmDao();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void test_getAllAlarms_when_no_alarm_inserted() {
        // The emitted type is the expected one
        mAlarmDao.getAllAlarm().
                test().
                assertValue(List::isEmpty);
    }

    @Test
    public void test_getFirstAlarmStarted(){
        mAlarmDao.getFirstAlarmStarted().
                test().
                assertValue(List::isEmpty);

        mAlarmDao.insert(TestDataHelper.ALARM_1)
                .test()
                .assertComplete();

        mAlarmDao.insert(TestDataHelper.ALARM_2)
                .test()
                .assertComplete();

        mAlarmDao.getFirstAlarmStarted().
                test().
                assertValue(alarms -> {
                    if (alarms.size()==2)
                        return alarms.get(0).getId() == 2;
                    return false;
                });
    }

    @Test
    public void test_getAlarmById() {
        mAlarmDao.insert(TestDataHelper.ALARM_1).
                test().
                assertComplete();

        mAlarmDao.getAlarmById(TestDataHelper.ALARM_1.getId()).
                test().
                assertValue(milestone1 -> {
                    // The emitted user is the expected one
                    return milestone1.equals(TestDataHelper.ALARM_1);
                });
    }

    @Test
    public void test_getAlarmByIds() {
        mAlarmDao.insert(TestDataHelper.ALARM_1).
                test().
                assertComplete();
        mAlarmDao.insert(TestDataHelper.ALARM_2).
                test().
                assertComplete();

        // correct ids
        mAlarmDao.getAlarmByIds(
                Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId())).
                test().
                assertValue(alarms -> {
                    // The emitted user is the expected one
                    return alarms.size() == 2;
                });

        // wrong ids
        mAlarmDao.getAlarmByIds(
                Arrays.asList(3L, 4L)).
                test().
                assertValue(List::isEmpty);
    }

    @Test
    public void test_activateAlarms() {

        mAlarmDao.insert(TestDataHelper.ALARM_1).
                test().
                assertComplete();
        mAlarmDao.insert(TestDataHelper.ALARM_2).
                test().
                assertComplete();

        // correct ids
        mAlarmDao.activateAlarms(
                Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId())).
                test().
                assertComplete();

        // check all alarms are activated
        mAlarmDao.getAllAlarm().
                test().
                assertValue(alarms -> {
                    // The emitted user is the expected one
                    for (AlarmEntity alarm : alarms){
                        if(!alarm.isStarted())
                            return false;
                    }
                    return true;
                });
    }

    @Test
    public void test_inactivateAlarms() {

        mAlarmDao.insert(TestDataHelper.ALARM_1).
                test().
                assertComplete();
        mAlarmDao.insert(TestDataHelper.ALARM_2).
                test().
                assertComplete();

        // correct ids
        mAlarmDao.inactivateAlarms(
                Arrays.asList(TestDataHelper.ALARM_1.getId(), TestDataHelper.ALARM_2.getId())).
                test().
                assertComplete();

        // check all alarms are activated
        mAlarmDao.getAllAlarm().
                test().
                assertValue(alarms -> {
                    // The emitted user is the expected one
                    for (AlarmEntity alarm : alarms){
                        if(alarm.isStarted())
                            return false;
                    }
                    return true;
                });
    }

    @Test
    public void test_deleteAlarms() {
        // insert one element
        mAlarmDao.insert(TestDataHelper.ALARM_1).
                test().assertComplete();

        // check that element are inserted
        mAlarmDao.getAllAlarm().
                test().
                assertValue(alarms -> {
                    // The emitted user is the expected one
                    return alarms.size()==1;
                });

        // delete inserted element
        mAlarmDao.deleteAlarms(Collections.singletonList(TestDataHelper.ALARM_1.getId())).
                test().assertComplete();

        // check the database are empty
        // The emitted user is the expected one
        mAlarmDao.getAllAlarm().
                test().
                assertValue(List::isEmpty);
    }
}
