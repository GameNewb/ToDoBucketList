package edu.sjsu.kyle.todobucketlist;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.adapters.CursorSwipeAdapter;

import java.util.Locale;
import java.util.Random;

import edu.sjsu.kyle.todobucketlist.Database.AlarmReminderContract;

public class AlarmCursorAdapter extends CursorSwipeAdapter {

    private TextView mTitleText, mDateAndTimeText, mRepeatInfoText, mDeleteReminderText;
    private ImageView mActiveImage , mThumbnailImage;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;
    private Button mDelete;
    private Typeface typeface;
    private String generatedString;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int level;

    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.to_do_items, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Initialize the SharedPreferences and get the level
        preferences = context.getSharedPreferences(IntentConstants.PREFERENCES_LEVELS, Context.MODE_PRIVATE);
        editor = preferences.edit();
        level = preferences.getInt(IntentConstants.PREFERENCES_LEVELS, 0);

        // Set views for fields
        mTitleText = (TextView) view.findViewById(R.id.recycle_title);
        mDateAndTimeText = (TextView) view.findViewById(R.id.recycle_date_time);
        mRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
        mDeleteReminderText = (TextView) view.findViewById(R.id.delete_reminder_text);
        mActiveImage = (ImageView) view.findViewById(R.id.active_image);
        mThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);

        // Change font for the edittext
        AssetManager am = context.getAssets();
        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ConcursoItalian_BTN.ttf"));
        mTitleText.setTypeface(typeface);
        mDateAndTimeText.setTypeface(typeface);
        mRepeatInfoText.setTypeface(typeface);
        mDeleteReminderText.setText(randomTextGenerator());

        mDelete = (Button) view.findViewById(R.id.delete);

        // Set columns indexes
        int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO);
        int repeatTypeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE);
        int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the item from the list
                context.getContentResolver().delete(ContentUris.withAppendedId(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, cursor.getLong(0)), null,null);

                // Add to levels when item/task is completed
                level++;
                Toast.makeText(context, "Exp is " + level, Toast.LENGTH_SHORT).show();
                editor.putInt(IntentConstants.PREFERENCES_LEVELS, level);
                editor.apply();

                // Show toast message
                deleteTask(v);
            }
        });

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String active = cursor.getString(activeColumnIndex);

        String dateTime = date + " " + time;

        // Set appropriate fields
        setReminderTitle(title);
        setReminderDateTime(dateTime);
        setReminderRepeatInfo(repeat, repeatNo, repeatType);
        setActiveImage(active);
    }

    // Set reminder title view
    public void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    // Set date and time views
    public void setReminderDateTime(String datetime) {
        mDateAndTimeText.setText(datetime);
    }

    // Set repeat views
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
    }

    // Set active image as on or off
    public void setActiveImage(String active){
        if(active.equals("true")){
            mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        }else if (active.equals("false")) {
            mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }
    }

    // Display a Toast Message when task is deleted
    public void deleteTask(View view)
    {
        Toast.makeText(super.mContext, "Task Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipeLayout;
    }

    @Override
    public void closeAllItems() {

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
                generatedString = "Did you really finish it? REALLY?";
                break;
            case 2:
                generatedString = "Please tell me you completed it.";
                break;
            case 3:
                generatedString = "NANI?! OWARIMASHITA KA?";
                break;
            case 4:
                generatedString = "Oh finally.. YOU finished it.";
                break;
            case 5:
                generatedString = "Did it take you 10 years to finish???";
                break;
            case 6:
                generatedString = "You better not be lying about completing this.";
                break;
            case 7:
                generatedString = "YOU completed a task? Seriously?";
                break;
            case 8:
                generatedString = "Oh you completed a task? You must be VERY productive.";
                break;
            case 9:
                generatedString = "( ͡° ͜ʖ ͡°) Delete the task?";
                break;
            case 10:
                generatedString = "Time to play?";
                break;
        }

        return generatedString;
    }
}
