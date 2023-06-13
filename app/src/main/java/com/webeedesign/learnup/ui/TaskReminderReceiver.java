package com.webeedesign.learnup.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.dashboard.AddTask;

public class TaskReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "task_reminder_channel";

    @SuppressLint({"LaunchActivityFromNotification", "UnspecifiedImmutableFlag"})
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the required permission is granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Vibration permission not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskName = intent.getStringExtra("TASK_NAME");

        // Create an intent for the notification
        Intent notificationIntent = new Intent(context, AddTask.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent for the notification
        long notificationId = System.currentTimeMillis(); // Unique ID for the notification
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use FLAG_IMMUTABLE for Android S and above
            pendingIntent = PendingIntent.getBroadcast(context, (int) notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            // Use FLAG_UPDATE_CURRENT for older Android versions
            pendingIntent = PendingIntent.getBroadcast(context, (int) notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.raw.smallicon)
                .setContentTitle("Reminder!")
                .setContentText("You have " + taskName + " in one hour")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) notificationId, builder.build());

    }
}
