package com.blogspot.jimzhou001.experimentmanager.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.blogspot.jimzhou001.experimentmanager.Activity.MainActivity;
import com.blogspot.jimzhou001.experimentmanager.AlarmService;
import com.blogspot.jimzhou001.experimentmanager.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        int requestCode = (int)System.currentTimeMillis();
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("id", requestCode);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);
        builder.setContentTitle("又要做实验啦！");
        builder.setContentText("勤奋的你，离成功又近了一步哦！");
        builder.setSmallIcon(R.drawable.icon);
        builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sound));
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notification.flags = Notification.FLAG_INSISTENT;
        notificationManager.notify(requestCode, notification);
        AlarmService.addIndex();
    }

}
