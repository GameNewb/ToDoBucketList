package edu.sjsu.kyle.todobucketlist.Reminder;

import android.app.AlarmManager;
import android.content.Context;


/*
 * Credits to delaroystudios for providing an excellent tutorial regarding Alarm Managers
 * https://www.youtube.com/watch?v=P46LTiPlvUA&t=894s
 *
 */

public class AlarmManagerProvider {
    private static final String TAG = AlarmManagerProvider.class.getSimpleName();
    private static AlarmManager sAlarmManager;
    public static synchronized void injectAlarmManager(AlarmManager alarmManager) {
        if (sAlarmManager != null) {
            throw new IllegalStateException("Alarm Manager Already Set");
        }
        sAlarmManager = alarmManager;
    }
    /*package*/ static synchronized AlarmManager getAlarmManager(Context context) {
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return sAlarmManager;
    }
}
