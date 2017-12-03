package edu.sjsu.kyle.todobucketlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.w3c.dom.Text;

public class LandingPageActivity extends AppCompatActivity {

    Button logOut;
    TextView userName;
    TextView userEmail;
    ImageView userPic;

    String name;
    String email;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        setUIComponents();
    }

    private void setUIComponents(){
        userName = (TextView) findViewById(R.id.profileName);
        userEmail = (TextView) findViewById(R.id.profileEmail);
        userPic = (ImageView) findViewById(R.id.profilePic);
        logOut = (Button) findViewById(R.id.logOut);

        Intent intent = getIntent();
        name = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_NAME);
        email = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_EMAIL);
        imageUrl = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_PHOTO);

        // Set appropriate user profile fields
        userName.setText(name);
        userEmail.setText(email);
        Glide.with(this).load(imageUrl).into(userPic);

        // Set logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPageActivity.this, MainActivity.class);
                intent.putExtra("signOut",IntentConstants.INTENT_GOOGLE_REQUEST_CODE_SIGNOUT);
                startActivity(intent);
                finish();
            }
        });

    }
}
