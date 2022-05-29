package com.federicoberon.alarme.datasource.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.TestUtils;
import com.federicoberon.alarme.datasource.AppDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MelodyDaoTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;

    private MelodyDao melodyDao;

    @Before
    public void initDb() {
        mDatabase = TestUtils.initDb();
        melodyDao = mDatabase.melodyDao();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void test_getAllMelodies_when_no_alarm_inserted() {
        // The emitted type is the expected one
        melodyDao.getAllMelodies().
                test().
                assertValue(List::isEmpty);
    }

    @Test
    public void test_getAllMelodies() {
        melodyDao.insert(TestDataHelper.MELODY_1).
                test().
                assertComplete();
        melodyDao.insert(TestDataHelper.MELODY_2).
                test().
                assertComplete();

        // The emitted type is the expected one
        melodyDao.getAllMelodies().
                test().
                assertValue(melodyEntities -> melodyEntities.size() == 2);
    }

    @Test
    public void test_getMelody() {
        melodyDao.insert(TestDataHelper.MELODY_1).
                test().
                assertComplete();

        melodyDao.getMelody(TestDataHelper.MELODY_1.getId()).
                test().
                assertValue(melody -> {
                        // The emitted user is the expected one
                        return melody.equals(TestDataHelper.MELODY_1);
                    }
                );
    }

    @Test
    public void test_getMelodyByTitle() {
        melodyDao.insert(TestDataHelper.MELODY_1).
                test().
                assertComplete();

        melodyDao.insert(TestDataHelper.MELODY_2).
                test().
                assertComplete();

        // correct ids
        melodyDao.getMelodyByTitle(TestDataHelper.MELODY_1.getTitle()).
                test().
                assertValue(alarm -> {
                    // The emitted user is the expected one
                    return alarm.getId() == TestDataHelper.MELODY_1.getId();
                });
    }
}
