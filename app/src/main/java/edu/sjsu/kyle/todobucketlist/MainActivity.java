package edu.sjsu.kyle.todobucketlist;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    int signOutCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = (SignInButton) findViewById(R.id.googleLogin);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        // For signout, retrieve the code from previous activity
        Intent intents = getIntent();
        signOutCode = intents.getIntExtra("signOut",0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.googleLogin:
                signIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Function to signIn user
    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        // If user was already signed in earlier, log them out first
        if(signOutCode != 0)
        {
            signOut();
        }
        startActivityForResult(intent, IntentConstants.INTENT_GOOGLE_REQUEST_CODE);

    }

    // Function to signOut user
    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //Intent intent = new Intent(LandingPageActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
            }
        });
    }

    private void handleResult(GoogleSignInResult result)
    {
        // If login success
        if(result.isSuccess())
        {
            // Obtain user properties from sign in
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String imageUrl = account.getPhotoUrl().toString();

            // Pass the values to the landing page
            Intent intent = new Intent(MainActivity.this, LandingPageActivity.class);
            intent.putExtra(IntentConstants.INTENT_SIGNIN_NAME, name);
            intent.putExtra(IntentConstants.INTENT_SIGNIN_EMAIL, email);
            intent.putExtra(IntentConstants.INTENT_SIGNIN_PHOTO, imageUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Unable to sign in. Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Sign in request
        if(requestCode == IntentConstants.INTENT_GOOGLE_REQUEST_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

}
