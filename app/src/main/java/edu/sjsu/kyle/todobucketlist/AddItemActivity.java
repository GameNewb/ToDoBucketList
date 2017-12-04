package edu.sjsu.kyle.todobucketlist;

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

public class AddItemActivity extends AppCompatActivity {

    private EditText addItem;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        SpannableString s = new SpannableString("Add Things To Do");
        s.setSpan(new TypefaceSpan(this, "ConcursoItalian_BTN.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        // Change font for the edittext
        addItem = (EditText) findViewById(R.id.toAddItem);
        AssetManager am = getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ConcursoItalian_BTN.ttf"));
        addItem.setTypeface(typeface);
    }

    public void saveButtonClicked(View v)
    {
        String inputText = addItem.getText().toString();
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
