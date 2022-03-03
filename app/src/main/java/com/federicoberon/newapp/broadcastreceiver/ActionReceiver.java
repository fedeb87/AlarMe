package com.federicoberon.newapp.broadcastreceiver;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.service.ServiceUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActionReceiver extends BroadcastReceiver {

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmEntity alarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        ServiceUtil serviceUtil = new ServiceUtil(context);

        // disable alarm
        alarmEntity.setStarted(false);
        serviceUtil.disableAlarm(alarmEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                            // stop alarm service
                            Intent intentService = new Intent(context, AlarmService.class);
                            context.stopService(intentService);

                            },
                        throwable -> Log.e("MIO", "Unable to get milestones: ", throwable));
    }

}
