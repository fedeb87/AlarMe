package com.federicoberon.newapp.ui.alarm;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.POSTPONE_TIME;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.federicoberon.newapp.databinding.FragmentAlarmBinding;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.utils.AlarmManager;

public class AlarmActivity extends AppCompatActivity {
    private FragmentAlarmBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.activityRingDismiss.setOnClickListener(v -> {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        binding.activityRingSnooze.setOnClickListener(v -> {
            Intent intent = getIntent();
            int repeat_time = 0;
            if(intent.hasExtra(POSTPONE_TIME)) {
                repeat_time = intent.getIntExtra(POSTPONE_TIME, 10);
            }

            // todo deberia tener su viewmodel?
            AlarmManager.schedule(getApplicationContext(), AlarmManager.getSnoozedAlarm(repeat_time));

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
