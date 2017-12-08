package edu.sjsu.kyle.todobucketlist.Reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Random;

import edu.sjsu.kyle.todobucketlist.R;
import edu.sjsu.kyle.todobucketlist.AddTaskActivity;
import edu.sjsu.kyle.todobucketlist.Database.AlarmReminderContract;

public class ReminderAlarmService extends IntentService {
    private static final String TAG = ReminderAlarmService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;
    private String generatedString;

    Cursor cursor;

    // This is a deep link intent, and needs the task stack
    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, ReminderAlarmService.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderAlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = intent.getData();

        // Display a notification to view the task details
        Intent action = new Intent(this, AddTaskActivity.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Grab the task description
        if(uri != null){
            cursor = getContentResolver().query(uri, null, null, null, null);
        }

        // Set the description for the notification
        String description = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                if(AlarmReminderContract.getColumnString(cursor, AlarmReminderContract.AlarmReminderEntry.KEY_TITLE) == null)
                {
                    description = " - You have a task to complete. DO IT! NOW!";
                }
                else {
                    description = AlarmReminderContract.getColumnString(cursor, AlarmReminderContract.AlarmReminderEntry.KEY_TITLE) + randomTextGenerator();
                }
            }
        } finally {
            if (cursor != null) {

                // Set a "motivational" text description if description is empty - aka task has been deleted
                if(AlarmReminderContract.getColumnString(cursor, AlarmReminderContract.AlarmReminderEntry.KEY_TITLE) == null && description.isEmpty())
                {
                    description = "You deleted a task that had an alarm. Did you actually complete it??";
                }

                cursor.close();
            }

            if(description.isEmpty())
            {
                description = "I doubt you'll complete this task.";
            }

        }

        // Create and Set up the notification to receive
        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.task_title))
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }

    // Function that generates 10 random text strings to append to the notification
    // It helps motivate people... I think :)
    private String randomTextGenerator()
    {
        Random rand = new Random();

        int n = rand.nextInt(10) + 1;

        switch (n)
        {
            case 1:
                generatedString = " - Don't make me slap you. MOVE IT!";
                break;
            case 2:
                generatedString = " - Come on, man. Stop being lazy.";
                break;
            case 3:
                generatedString = " - For the love of god, just move your ass.";
                break;
            case 4:
                generatedString = " - I swear to Goku if you don't do this task...";
                break;
            case 5:
                generatedString = " - Don't make me call your mom.";
                break;
            case 6:
                generatedString = " - Hey lazy butt, finish this task.";
                break;
            case 7:
                generatedString = " - Stop being a couch potato. Get rid of this task.";
                break;
            case 8:
                generatedString = " - The faster you finish this, the faster you can play.";
                break;
            case 9:
                generatedString = " - JUST DO IT! DON'T LET YOUR DREAMS BE DREAMS!";
                break;
            case 10:
                generatedString = " - Dude. Please. Finish this task.";
                break;
        }

        return generatedString;
    }
}