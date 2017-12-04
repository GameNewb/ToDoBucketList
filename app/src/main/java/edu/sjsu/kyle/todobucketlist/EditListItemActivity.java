package edu.sjsu.kyle.todobucketlist;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

public class EditListItemActivity extends AppCompatActivity {
    String inputText;
    int position;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list_item);

        SpannableString s = new SpannableString("Edit Things To Do");
        s.setSpan(new TypefaceSpan(this, "ConcursoItalian_BTN.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        // Get intent data for editing
        Intent intent = getIntent();
        inputText = intent.getStringExtra(IntentConstants.INTENT_LIST_DATA);
        position = intent.getIntExtra(IntentConstants.INTENT_ITEM_POSITION, -1);

        // Set the input field to the actual item and change the font
        EditText inputData = (EditText) findViewById(R.id.toEditItem);
        AssetManager am = getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ConcursoItalian_BTN.ttf"));
        inputData.setTypeface(typeface);
        inputData.setText(inputText);
    }

    public void saveButtonClicked(View v)
    {
        String editedItem = ((EditText) findViewById(R.id.toEditItem)).getText().toString();

        // If user edits the item into empty string, set it back to what it was before
        if(editedItem.equals(""))
        {
            editedItem = inputText;
        }

        // Put the intent data back and terminate activity
        Intent intent = new Intent();
        intent.putExtra(IntentConstants.INTENT_EDIT_ITEM, editedItem);
        intent.putExtra(IntentConstants.INTENT_ITEM_POSITION, position);
        setResult(IntentConstants.INTENT_RESULT_CODE_TWO, intent);
        finish();
    }
}
