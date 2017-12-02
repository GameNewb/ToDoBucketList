package edu.sjsu.kyle.todobucketlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDoListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    String inputText;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        listView = (ListView) findViewById(R.id.toDoList);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

    }

    public void onClick(View v)
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
            inputText = data.getStringExtra(IntentConstants.INTENT_ADD_ITEM);
            arrayList.add(inputText);
            arrayAdapter.notifyDataSetChanged();
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

            // Open the file again when app is closed/uninstalled
            try {
                Scanner scanner = new Scanner(openFileInput("ToDoList.txt"));
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
        else if(resultCode == IntentConstants.INTENT_RESULT_CODE_TWO)
        {
            inputText = data.getStringExtra(IntentConstants.INTENT_EDIT_ITEM);
            position = data.getIntExtra(IntentConstants.INTENT_ITEM_POSITION, -1);
            arrayList.remove(position);
            arrayList.add(position, inputText);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Saving to a text file
        try {
            PrintWriter pw = new PrintWriter(openFileOutput("ToDoList.txt", Context.MODE_PRIVATE));

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
        // Terminate program on back button pressed
        finish();
    }
}
