package edu.sjsu.kyle.todobucketlist;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.widget.Switch;
import android.widget.Toast;

import edu.sjsu.kyle.todobucketlist.Database.AlarmReminderContract;
import edu.sjsu.kyle.todobucketlist.Database.AlarmReminderDbHelper;

public class ToDoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // Variables for the list item and its adapters
    // arrayList and arrayAdapters are deprecated/unused due to new CursorAdapter
    ListView listView;
    ArrayList<String> arrayList;
    ArraySwipeAdapter<String> arrayAdapter;
    Button deleteItem;

    // Variables for the customized txt file
    String email;
    String toDoTxt;

    // SwipeLayout
    SwipeLayout swipeLayout;

    // Variable for adapter and alarms
    AlarmCursorAdapter mCursorAdapter;
    AlarmReminderDbHelper alarmReminderDbHelper = new AlarmReminderDbHelper(this);
    private static final int TASK_LOADER = 0;

    // Variable for Task FAB
    private FloatingActionButton mAddTaskButton;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int exp;

    // Variables for quickly adding tasks
    Calendar mCalendar;
    int mYear, mMonth, mHour, mMinute, mDay;
    long mRepeatTime;
    Switch mRepeatSwitch;
    String mTitle;
    String mTime;
    String mDate;
    String mRepeat;
    String mRepeatNo;
    String mRepeatType;
    String mActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Initialize the SharedPreferences and get the exp
        preferences = getApplicationContext().getSharedPreferences(IntentConstants.PREFERENCES_EXP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        exp = preferences.getInt(IntentConstants.PREFERENCES_EXP, 0);

        // Customize the ActionBar font
        SpannableString s = new SpannableString("To Do List");
        s.setSpan(new TypefaceSpan(this, "ConcursoItalian_BTN.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        // Set SwipeLayout
        setSwipeLayout();

        // Initialize the list and its adapter
        listView = (ListView) findViewById(R.id.toDoList);
        View emptyView = (View) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        arrayList = new ArrayList<>();
        mCursorAdapter = new AlarmCursorAdapter(getApplicationContext(), null);
        listView.setAdapter(mCursorAdapter);

        // Set appropriate onItem click listeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ToDoListActivity.this, AddTaskActivity.class);

                Uri currentTaskUri = ContentUris.withAppendedId(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentTaskUri);

                startActivity(intent);

            }
        });

        mAddTaskButton = (FloatingActionButton) findViewById(R.id.fab);

        mAddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == IntentConstants.INTENT_REQUEST_CODE)
        {
            // Save to array and database after
            /*
             * DEPRECATED - Using CursorAdapter now instead of ArrayAdapter

            inputText = data.getStringExtra(IntentConstants.INTENT_ADD_ITEM);
            arrayList.add(inputText);
            dbHelper.insertNewTask(inputText);
            arrayAdapter.notifyDataSetChanged();
            setClicks();
            */
        }
        else if(resultCode == IntentConstants.INTENT_RESULT_CODE_TWO)
        {
            // Save/update the array and database after EDITING
            /*
             * DEPRECATED - Using CursorAdapter now instead of ArrayAdapter

            inputText = data.getStringExtra(IntentConstants.INTENT_EDIT_ITEM);
            position = data.getIntExtra(IntentConstants.INTENT_ITEM_POSITION, -1);
            dbHelper.deleteTask(arrayList.get(position));
            arrayList.remove(position);
            arrayList.add(position, inputText);
            dbHelper.insertNewTask(inputText);
            arrayAdapter.notifyDataSetChanged();
            */
        }
    }

    @Override
    public void onBackPressed() {

        preferences = getApplicationContext().getSharedPreferences(IntentConstants.PREFERENCES_EXP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        exp = preferences.getInt(IntentConstants.PREFERENCES_EXP, 0);

        // Set the result back to obtain the preference data
        Intent intent = new Intent();
        intent.putExtra(IntentConstants.PREFERENCES_EXP, exp);
        setResult(IntentConstants.PREFERENCES_RESULT_CODE, intent);
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(Color.parseColor("#21c08a"), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            // When the + Context menu item is clicked, quickly add a task and populate the values wit the given text
            case R.id.addTask:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Quick Add Task")
                        .setMessage("Ugh. Another task? Really?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());

                                // Initialize default values
                                mActive = "false";
                                mRepeat = "false";
                                mRepeatNo = Integer.toString(1);
                                mRepeatType = "Hour";

                                mCalendar = Calendar.getInstance();
                                mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                                mMinute = mCalendar.get(Calendar.MINUTE);
                                mYear = mCalendar.get(Calendar.YEAR);
                                mMonth = mCalendar.get(Calendar.MONTH) + 1;
                                mDay = mCalendar.get(Calendar.DATE);

                                mDate = mDay + "/" + mMonth + "/" + mYear;
                                mTime = mHour + ":" + mMinute;

                                ContentValues values = new ContentValues();

                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE, task);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_DATE, mDate);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TIME, mTime);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT, mRepeat);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO, mRepeatNo);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE, mRepeatType);
                                values.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE, mActive);

                                // This is a NEW reminder, so insert a new reminder into the provider,
                                // returning the content URI for the new reminder.
                                Uri newUri = getContentResolver().insert(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, values);
                                //Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_task_successful),
                                //        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set the swipe listeners for the delete button
    private void setSwipeLayout()
    {
        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.to_do_items, null);
        swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);

        // Set show mode
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        deleteItem = (Button) view.findViewById(R.id.delete);
    }

    // Loader functions for Cursor
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmReminderContract.AlarmReminderEntry._ID,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
                AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
                AlarmReminderContract.AlarmReminderEntry.KEY_TIME,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE

        };

        return new CursorLoader(this,   // Parent activity context
                AlarmReminderContract.AlarmReminderEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    /* --- DEPRECATED FUNCTIONS ---
       These functions were initially used during the first round of app building
       However, I found multiple sources/tutorials that contained better implementation for what I'm trying to do (e.g. Cursor Adapter)
       These new implementations also satisfied quite a lot of the requirements, and so I had opted to not use these old code
     */

    /*
     * DEPRECATED - Not using txt file saving anymore
     * Keeping it here for functional requirement
     * Function to retrieve the user email, and append it to the txt file
     * This ensures that the txt file being opened is for the specific logged in user
     */
    private void setTxtFileName()
    {
        Intent intent = getIntent();
        email = intent.getStringExtra(IntentConstants.INTENT_TO_DO_LIST);
        if(email != null)
        {
            toDoTxt = "ToDoList-" + email + ".txt";
        }

    }

    /*
     * DEPRECATED - Not using txt file saving anymore
     * Keeping it here for functional requirement
     * Function to read the array/list data from a txt file
     */
    private void readFromFile()
    {
        // Open the file again when app is closed/uninstalled
        try {
            Scanner scanner = new Scanner(openFileInput(toDoTxt));
            while(scanner.hasNextLine())
            {
                String textData = scanner.nextLine();
                arrayAdapter.add(textData);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * DEPRECATED - Not using txt file saving anymore
     * Keeping it here for functional requirement
     * Function to save the data to a txtfile
     */
    private void saveToFile()
    {
        try {
            PrintWriter pw = new PrintWriter(openFileOutput(toDoTxt, Context.MODE_PRIVATE));

            // Write data onto the file
            for(String data : arrayList)
            {
                pw.println(data);
            }

            // Close the file
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * DEPRECATED - Not using txt file saving anymore
     * Keeping it here for functional requirement
     * Function that deletes the list item from the file
     */
    private void removeItemFromFile(String itemToRemove)
    {
        try {
            // Create a tempfile to store the output
            File inputFile = new File(toDoTxt);
            File tempFile = new File("tempFile.txt");

            // Initialize the reader/writer
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            // Write to the file while there is a line
            while((currentLine = reader.readLine()) != null)
            {
                String trimmedLine = currentLine.trim();

                if(trimmedLine.equals(itemToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            // Close reader/writer and rename the file to be opened later
            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * DEPRECATED - Not using ArrayAdapter anymore, so this class is redundant
     * Keeping it here for functional requirement
     * Function to set the onClickListeners for each list item
     */
    private void setClicks()
    {
        /*
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ToDoListActivity.this, AddTaskActivity.class);
                intent.putExtra(IntentConstants.INTENT_LIST_DATA, arrayList.get(position).toString());
                intent.putExtra(IntentConstants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, IntentConstants.INTENT_REQUEST_CODE_TWO);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/
    }
}
