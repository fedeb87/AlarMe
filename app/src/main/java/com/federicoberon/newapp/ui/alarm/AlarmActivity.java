package com.federicoberon.newapp.ui.alarm;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.newapp.utils.AlarmManager;

import javax.inject.Inject;

public class AlarmActivity extends AppCompatActivity {
    private FragmentAlarmBinding binding;
    private AlarmEntity mAlarmEntity;


    @Inject
    AddAlarmViewModel addAlarmViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ((SimpleRemindMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        binding = FragmentAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ----- If extra alarmEntity not found then exit activity
        Intent intent = getIntent();
        if(intent.hasExtra(ALARM_ENTITY))
            mAlarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        else {
            Toast.makeText(this, getString(R.string.no_alarm_entity), Toast.LENGTH_SHORT).show();
            finish();
        }

        // ----- Config dismiss alarm action
        binding.activityRingDismiss.setOnClickListener(v -> {
            mAlarmEntity.setStarted(false);
            addAlarmViewModel.disableAlarm(mAlarmEntity);
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
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
            AlarmManager.schedule(getApplicationContext(), AlarmManager.getSnoozedAlarm(mAlarmEntity, postpone_time));
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        animateClock();
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(binding.activityRingClock,
                "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }
}
