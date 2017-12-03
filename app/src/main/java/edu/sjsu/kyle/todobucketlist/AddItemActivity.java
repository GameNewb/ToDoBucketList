package edu.sjsu.kyle.todobucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setTitle("Add Things To Do");
    }

    public void saveButtonClicked(View v)
    {
        String inputText = ((EditText) findViewById(R.id.toAddItem)).getText().toString();
        if(inputText.equals(""))
        {

        }
        else
        {
            Intent intent = new Intent();
            intent.putExtra(IntentConstants.INTENT_ADD_ITEM, inputText);
            setResult(IntentConstants.INTENT_RESULT_CODE, intent);
            finish();
        }
    }
}
