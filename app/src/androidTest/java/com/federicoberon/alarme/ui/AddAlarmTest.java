package com.federicoberon.alarme.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import android.app.Instrumentation;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.TimePicker;

import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.TestDataHelper;
import com.federicoberon.alarme.di.component.TestApplicationComponent;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddAlarmTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public final CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();
    private AlarmEntity alarm_to_insert;

    @Inject
    AddAlarmViewModel viewmodel;
    private Context mContext;

    @Before
    public void setupRecyclerView(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AlarMeApplication app
                = (AlarMeApplication) instrumentation.getTargetContext().getApplicationContext();
        TestApplicationComponent component = (TestApplicationComponent) app.appComponent;
        mContext = app.appComponent.getContext();
        component.inject(this);
        alarm_to_insert = TestDataHelper.ALARM_3;

        when(viewmodel.getMelodyByName(any())).thenReturn(Single.just(TestDataHelper.MELODY_1));
        when(viewmodel.getNextAlarm()).thenReturn(new  MutableLiveData<>());
        when(viewmodel.saveAlarm(alarm_to_insert.getTitle())).thenReturn(Maybe.just(alarm_to_insert.getId()));

    }

    @Test
    public void test_createAlarm() {

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

    @Test
    public void addAlarmWithAllFeatures(){
        onView(withId(R.id.fab))
                .perform(click());

        // Then the second screen with the milestone details should appear.
        onView(withId(R.id.editTextAlarmName))
                .check(matches(isDisplayed()));

        onView(withId(R.id.repeatSwitch)).perform(setChecked(true));
        onView(withId(R.id.repeatSwitch)).check(matches(isChecked()));


        for(int i=0;i<=10;i++){
            onView(withId(R.id.repeatLinearLayout)).perform(swipeUp());
        }

        onView(withId(R.id.phrasesSwitch)).perform(setChecked(false));
        onView(withId(R.id.phrasesValue)).check(matches(withText(
                mContext.getString(R.string.no_phrases_string))));

        onView(withId(R.id.ringtoneLinearLayout)).perform(click());
        onView(withId(R.id.melodyLayout)).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.vibrationSwitch)).perform(setChecked(true));
        onView(withId(R.id.vibrationLinearLayout)).perform(click());
        onView(withId(R.id.vibrations_list)).check(matches(isDisplayed()));
    }

    public static void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    public static ViewAction setChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public BaseMatcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {}

                    @Override
                    public void describeTo(Description description) {}
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }
}