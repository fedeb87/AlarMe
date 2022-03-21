package com.federicoberon.newapp.ui.alarm;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.retrofit.Horoscope;
import com.federicoberon.newapp.retrofit.HoroscopeTwo;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.HoroscopeManager;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AlarmActivity extends AppCompatActivity implements AlarmViewModel.OnResponseUpdateListener {
    private static final String LOG_TAG = "AlarmActivity";
    private FragmentAlarmBinding binding;
    private AlarmEntity mAlarmEntity;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    SharedPreferences sharedPref;

    @Inject
    AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ((SimpleRemindMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        binding = FragmentAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alarmViewModel.init(this,
                sharedPref.getString(getString(R.string.sign_name), "aries"));

        // ----- If extra alarmEntity not found then exit activity
        Intent intent = getIntent();
        if(intent.hasExtra(ALARM_ENTITY)) {
            mAlarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        }else {
            Toast.makeText(this,
                    getString(R.string.no_alarm_entity),
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // ----- Config dismiss alarm action
        binding.activityRingDismiss.setOnClickListener(v -> {
            if(!AlarmManager.recurring(mAlarmEntity)) {
                mAlarmEntity.setStarted(false);
                mDisposable.add(alarmViewModel.updateAlarm(mAlarmEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> Log.w(LOG_TAG, ""),
                        throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }
        });

        // ----- Hide snooze button if the alarm aren't configured it for that
        int postpone_time;
        if(mAlarmEntity.isPostponeOn())
            postpone_time = mAlarmEntity.getPostponeTime();
        else {
            postpone_time = 0;
            binding.activityRingSnooze.setVisibility(View.GONE);
        }

        binding.activityRingSnooze.setOnClickListener(v -> {
            AlarmManager.schedule(getApplicationContext(),
                    AlarmManager.getSnoozedAlarm(mAlarmEntity, postpone_time));
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        Objects.requireNonNull(getSupportActionBar()).setTitle(" ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        animateClock();

        if(mAlarmEntity.isHoroscopeOn())
            alarmViewModel.loadHoroscope();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();

        binding = null;
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(binding.activityRingClock,
                "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onHoroscopeChanged(Horoscope horoscope) {
        if(horoscope!=null){
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            alarmViewModel.loadHoroscopeTwo();
        }
    }

    private void loadHoroscopeInfo(String description) {
        binding.signImage.setBackground(
                getDrawable(HoroscopeManager.getIconId(this, alarmViewModel.getSign())));

        binding.signTitle.setText(HoroscopeManager.getName(
                this, alarmViewModel.getSign()));

        binding.signDesc.animate().alpha(0f).setDuration(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.signDesc.setText(description);
                        binding.signDesc.animate().alpha(1f).setDuration(800)
                                .start();
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
    }

    @Override
    public void onHoroscopeChangedTwo(HoroscopeTwo horoscope) {
        if(horoscope!=null){
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            binding.horoscopeCardView.setVisibility(View.GONE);
        }
    }
}
