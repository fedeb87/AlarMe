package com.federicoberon.alarme.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.view.Gravity;
import android.widget.TimePicker;

import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.model.AlarmEntity;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddAlarmTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();
    private AlarmEntity alarm_to_insert;

    @Before
    public void setupRecyclerView(){

        alarm_to_insert = TestDataHelper.ALARM_3;
    }

    @Test
    public void test_createAlarm() {
        //activityScenarioRule.getScenario().onActivity(TestUtils::initDb);

        onView(withId(R.id.fab))
                .perform(click());

        // Then the second screen with the milestone details should appear.
        onView(withId(R.id.editTextAlarmName))
                .check(matches(isDisplayed()));

        // set milestone title
        onView(withId(R.id.editTextAlarmName))
                .perform(typeText(alarm_to_insert.getTitle()), closeSoftKeyboard());

        // set milestone time
        Calendar cal = Calendar.getInstance();
        cal.setTime(alarm_to_insert.getAlarmDate());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(cal.get(Calendar.HOUR)
                        , cal.get(Calendar.MINUTE)));

        for(int i=0;i<=10;i++){
            onView(withId(R.id.repeatLinearLayout)).perform(swipeUp());
        }

        onView(withId(R.id.okButton))
                .perform(click());

        try {
            drain();
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drain() throws TimeoutException, InterruptedException {
        mCountingTaskExecutorRule.drainTasks(1, TimeUnit.MINUTES);
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