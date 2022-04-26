package com.federicoberon.alarme;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.work.WorkManager;

import com.federicoberon.simpleremindme.repositories.MilestoneRepository;
import com.federicoberon.simpleremindme.ui.home.HomeViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Unit test for {@link HomeViewModel}
 */
public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private WorkManager mWorkManager;

    @Mock
    private MilestoneRepository milestoneRepository;

    private HomeViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mViewModel = new HomeViewModel(milestoneRepository);
    }

    @Test
    public void getAllMilestones_whenNoMilestonesSaved() {
        // Given that the UserDataSource returns an empty list of users
        when(milestoneRepository.getAllMilestone()).thenReturn(Flowable.empty());

        //When getting the Milestone
        mViewModel.getMilestones("")
                .test()
                // That is empty
                .assertNoValues();
    }

    @Test
    public void getMilestone_whenMilestoneSaved() {
        // Given that the Repo returns a milestone
        when(milestoneRepository.getAllMilestone()).thenReturn(Flowable.just(Collections.singletonList(TestDataHelper.MILESTONE_1)));

        //When getting the milestone
        mViewModel.getMilestones("")
                .test()
                .assertValue(Collections.singletonList(TestDataHelper.MILESTONE_1));

    }

    @Test
    public void getMilestoneById() {
        // Given that the Repo returns a milestone
        long id = TestDataHelper.MILESTONE_2.getId();
        when(milestoneRepository.getMilestoneById(id)).thenReturn(Flowable.just(TestDataHelper.MILESTONE_2));

        //When getting the milestone
        mViewModel.getMilestoneById(id)
                .test()
                // The correct milestone is emitted
                .assertValue(milestoneXType -> {
                    // The emitted milestone is the expected one
                    return milestoneXType.equals(TestDataHelper.MILESTONE_2) &&
                            mViewModel.getCurrentMilestoneId() == TestDataHelper.MILESTONE_2.getId();
                });

    }

    @Test
    public void deleteMilestone(){

        long testId = 1;
        // Given that the Repo returns a Completable
        when(milestoneRepository.deleteMilestone(testId)).thenReturn(Completable.complete());

        mViewModel.deleteMilestone();

        verify(milestoneRepository).deleteMilestone(-1);
    }

    @Test
    public void propertiesAreSetMilestoneConstructor() {
        HomeViewModel homeViewModel = new HomeViewModel(milestoneRepository,
                mWorkManager);

        assertSame(mWorkManager, homeViewModel.getWorkManager());
        assertSame(milestoneRepository, homeViewModel.getMilestoneRepository());
    }
}
