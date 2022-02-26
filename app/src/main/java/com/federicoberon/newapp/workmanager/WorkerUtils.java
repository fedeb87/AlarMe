package com.federicoberon.newapp.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.federicoberon.newapp.R;

public class WorkerUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannels(Context context, CharSequence name, String description, boolean vibrationActive) {
        NotificationChannel notificationChannel =
                new NotificationChannel(context.getResources().getString(R.string.CHANNEL_ID), name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(description);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(vibrationActive);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        // Add the channel
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     *
     * @param id Local Id
     * @param message Message shown on the notification
     * @param context Context needed to create Toast
     */
    public static void makeStatusNotification(long id, String title, String message, Context context) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibracionActiva = sharedPreferences.getBoolean(context.getResources().getString(
                R.string.vibration_key), false);

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = context.getResources().getString(R.string.VERBOSE_NOTIFICATION_CHANNEL_NAME);
            String description = context.getResources().getString(R.string.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION);

            createChannels(context, name, description, vibracionActiva);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setAutoCancel(true);

        if (vibracionActiva) builder.setVibrate(new long[0]);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Show the notification
        NotificationManagerCompat.from(context).notify(Integer.parseInt(context.getResources().getString(R.string.NOTIFICATION_ID)), builder.build());
    }
}
