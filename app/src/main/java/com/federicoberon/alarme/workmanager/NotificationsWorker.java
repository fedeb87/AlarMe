package com.federicoberon.alarme.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationsWorker extends Worker {

    public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("title");
        String message = getInputData().getString("message");
        long id = getInputData().getLong("id", 0L);

        WorkerUtils.makeStatusNotification(id, title, message, getApplicationContext());

        return Result.success();
    }

}