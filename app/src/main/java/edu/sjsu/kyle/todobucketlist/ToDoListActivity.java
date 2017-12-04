package edu.sjsu.kyle.todobucketlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Scanner;

import edu.sjsu.kyle.todobucketlist.Database.DBHelper;

public class ToDoListActivity extends AppCompatActivity{

    // Variables for the list item and its adapters
    ListView listView;
    ArrayList<String> arrayList;
    ArrayList<String> taskList;
    ArraySwipeAdapter<String> arrayAdapter;
    String inputText;
    TextView listItems;
    Button deleteItem;
    int position;

    // Variables for the customized txt file
    String email;
    String toDoTxt;

    // Database variable
    DBHelper dbHelper;

    SwipeLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        getSupportActionBar().setTitle("To Do List");

        dbHelper = new DBHelper(this);

        // Set SwipeLayout
        setSwipeLayout();

        // Set txt file name
        setTxtFileName();

        // Initialize the list and its adapter
        //loadTaskList();
        listView = (ListView) findViewById(R.id.toDoList);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArraySwipeAdapter<String>(this, R.layout.listview_item, R.id.position, arrayList);
        listView.setAdapter(arrayAdapter);

        // Read from the txt file
        readFromFile();

        // Set the appropriate touch gestures
        setClicks();
    }

    public void onItemClick(View v)
    {
        Intent intent = new Intent();
        intent.setClass(ToDoListActivity.this, AddItemActivity.class);
        startActivityForResult(intent, IntentConstants.INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == IntentConstants.INTENT_REQUEST_CODE)
        {
            // Save to array and database after ADDING
            inputText = data.getStringExtra(IntentConstants.INTENT_ADD_ITEM);
            arrayList.add(inputText);
            dbHelper.insertNewTask(inputText);
            arrayAdapter.notifyDataSetChanged();
            setClicks();
        }
        else if(resultCode == IntentConstants.INTENT_RESULT_CODE_TWO)
        {
            // Save/update the array and database after EDITING
            inputText = data.getStringExtra(IntentConstants.INTENT_EDIT_ITEM);
            position = data.getIntExtra(IntentConstants.INTENT_ITEM_POSITION, -1);
            dbHelper.deleteTask(arrayList.get(position));
            arrayList.remove(position);
            arrayList.add(position, inputText);
            dbHelper.insertNewTask(inputText);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Saving to a text file when back button is pressed
        saveFromFile();

        // Terminate program on back button pressed
        //finish();
    }

    // Function to retrieve the user email, and append it to the txt file
    // This ensures that the txt file being opened is for the specific logged in user
    private void setTxtFileName()
    {
        Intent intent = getIntent();
        email = intent.getStringExtra(IntentConstants.INTENT_TO_DO_LIST);
        if(email != null)
        {
            toDoTxt = "ToDoList-" + email + ".txt";
        }

    }

    // Function to read the array/list data from a txt file
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

    // Function to save the array/list data to a txt file
    private void saveFromFile()
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

    // Function that deletes the list item from the file
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

    // Function to set the onClickListeners for each list item
    private void setClicks()
    {
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ToDoListActivity.this, EditListItemActivity.class);
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
        });
    }

    // Function to initialize the list and adapter
    // Adds data to array from database
    private void loadTaskList()
    {
        taskList = dbHelper.getTaskList();
        listView = (ListView) findViewById(R.id.toDoList);
        arrayList = new ArrayList<>();

        if(arrayAdapter == null)
        {
            arrayAdapter = new ArraySwipeAdapter<String>(this, R.layout.listview_item, R.id.position, taskList);
        }
        else
        {
            arrayAdapter.clear();
            arrayAdapter.addAll(taskList);
            arrayAdapter.notifyDataSetChanged();
        }

        listView.setAdapter(arrayAdapter);
    }

    // Function that deletes the task and updates the UI component and txt file
    public void deleteTask(View view)
    {
        int index = listView.getPositionForView(view);

        removeItemFromFile(arrayList.get(index));
        arrayList.remove(index);
        arrayAdapter.notifyDataSetChanged();

        Toast.makeText(ToDoListActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
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

                                // Add to array when user clicks the quick add button
                                arrayList.add(task);
                                dbHelper.insertNewTask(task);
                                arrayAdapter.notifyDataSetChanged();
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
        View view = factory.inflate(R.layout.listview_item, null);
        swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);

        // Set show mode
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        deleteItem = (Button) view.findViewById(R.id.delete);

    }

}
