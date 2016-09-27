package com.blogspot.jimzhou001.experimentmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blogspot.jimzhou001.experimentmanager.Activity.MainActivity;
import com.blogspot.jimzhou001.experimentmanager.Receiver.AlarmReceiver;
import com.coolerfall.daemon.Daemon;

import java.util.LinkedList;

public class AlarmService extends Service {

    private LinkedList<Long> alarmList = new LinkedList<Long>();
    private static int index = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(AlarmService.this, AlarmService.class, Daemon.INTERVAL_ONE_MINUTE/60);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Cursor cursor = MainActivity.noteDatabase.query("Notes", null, "millis>=" + System.currentTimeMillis(), null, null, null, "millis");
            if (cursor.moveToFirst()) {
                do {
                    long millis = cursor.getLong(cursor.getColumnIndex("millis"));
                    alarmList.add(millis);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            long triggerAtTime = alarmList.get(index);
            Intent i = new Intent(this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
            }
        } catch (Exception e) {
            index = 0;
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void addIndex() {
        ++index;
    }

}
