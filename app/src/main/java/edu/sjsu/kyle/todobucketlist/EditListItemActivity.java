package edu.sjsu.kyle.todobucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditListItemActivity extends AppCompatActivity {
    String inputText;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_item);
        getSupportActionBar().setTitle("Edit Things To Do");

        Intent intent = getIntent();
        inputText = intent.getStringExtra(IntentConstants.INTENT_LIST_DATA);
        position = intent.getIntExtra(IntentConstants.INTENT_ITEM_POSITION, -1);
        EditText inputData = (EditText) findViewById(R.id.toEditItem);
        inputData.setText(inputText);
    }

    public void saveButtonClicked(View v)
    {
        String editedItem = ((EditText) findViewById(R.id.toEditItem)).getText().toString();
        Intent intent = new Intent();
        intent.putExtra(IntentConstants.INTENT_EDIT_ITEM, editedItem);
        intent.putExtra(IntentConstants.INTENT_ITEM_POSITION, position);
        setResult(IntentConstants.INTENT_RESULT_CODE_TWO, intent);
        finish();
    }
}
