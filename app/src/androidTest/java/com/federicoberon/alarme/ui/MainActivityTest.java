package com.federicoberon.alarme.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.view.Gravity;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.TestUtils;
import com.federicoberon.alarme.ui.home.AlarmAdapter;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Objects;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    // todo test long click pressed
    // todo test swipe to dismiss

    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();

    @Before
    public void setupRecyclerView(){
        TestUtils.initDb();
        // Set dummy data to recyclerview adapter
        activityScenarioRule.getScenario().onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.milestonesRecyclerView);
            ((AlarmAdapter) Objects.requireNonNull(recyclerView.getAdapter()))
                    .setAlarms(Arrays.asList(TestDataHelper.ALARM_1, TestDataHelper.ALARM_2));
        });
    }

    @Test
    public void clickOnFirstItem() {

        // When clicking on the first alarm
        onView(withId(R.id.milestonesRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Then the second screen with the milestone details should appear.
        onView(withId(R.id.editTextAlarmName))
                .check(matches(isDisplayed()));

        onView(withId(R.id.editTextAlarmName)).check(matches(withText(TestDataHelper.ALARM_1.getTitle())));
    }

    @Test
    public void clickOnAboutMenu(){

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about));

        onView(withId(R.id.textViewFooter)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnNewMilestone(){
        onView(withId(R.id.fab))
                .perform(click());

        onView(withId(R.id.editTextAlarmName))
                .check(matches(isDisplayed()));
    }

    // todo test long click pressed
    // todo test swipe to dismiss
}