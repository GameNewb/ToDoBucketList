package edu.sjsu.kyle.todobucketlist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.lang.reflect.Type;
import java.util.Locale;

import edu.sjsu.kyle.todobucketlist.Canvas.CanvasActivity;
import edu.sjsu.kyle.todobucketlist.Weather.OpenWeatherMap;
import edu.sjsu.kyle.todobucketlist.Weather.WeatherCommons;
import edu.sjsu.kyle.todobucketlist.Weather.WeatherHelper;

public class LandingPageActivity extends AppCompatActivity implements LocationListener {

    // Profile components
    TextView nameField;
    TextView emailField;
    Button logOut;
    TextView userName;
    TextView userEmail;
    ImageView userPic;
    String name;
    String email;
    String imageUrl;

    // Weather components
    TextView txtCity;
    TextView txtLastUpdate;
    TextView txtDescription;
    TextView txtHumidity;
    TextView txtTime;
    TextView txtCelsius;
    ImageView weatherIcon;

    // Activity components
    Button toDoList;
    Button drawingCanvas;

    // Location and weather components
    LocationManager locationManager;
    String provider;
    static double lat;
    static double lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int PERMISSION = 0;

    private Typeface typeface;

    // Level and EXP components for user
    private Handler progressHandler;
    private TextView levelText, currentExpText, nextLevelText;
    private ProgressBar expProgress;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int exp;
    private int level;
    private int toNextLevel;

    // Twitter integration
    TwitterLoginButton loginButton;
    RelativeLayout twitterLayout;
    ListView twitterTimeline;
    boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_landing_page);

        // Get the appropriate levels and exp
        preferences = getApplicationContext().getSharedPreferences(IntentConstants.PREFERENCES_EXP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        exp = preferences.getInt(IntentConstants.PREFERENCES_EXP, 0);
        level = preferences.getInt(IntentConstants.PREFERENCES_CURRENT_LEVEL, 0);
        toNextLevel = preferences.getInt(IntentConstants.PREFERENCES_NEXT_EXP_LEVEL, 10);
        loggedIn = preferences.getBoolean(IntentConstants.PREFERENCES_TWITTER_LOGIN, false);

        // Customize the ActionBar title font
        SpannableString s = new SpannableString("The Procrastinator");
        s.setSpan(new TypefaceSpan(this, "ConcursoItalian_BTN.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        // Set Profile UI Components
        setProfileUIComponents();

        // Set Location data such as coordinates, city, etc.
        setLocationComponents();

        // Set the buttons for corresponding Activities
        setActivityComponents();

        // Set twitter button
        setTwitterComponents();
    }

    // Function to set the UI components after Activity creation
    private void setProfileUIComponents() {
        AssetManager am = getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ConcursoItalian_BTN.ttf"));

        // Get appropriate view ID
        nameField = (TextView) findViewById(R.id.profileNameTag);
        emailField = (TextView) findViewById(R.id.profileEmailTag);
        userName = (TextView) findViewById(R.id.profileName);
        userEmail = (TextView) findViewById(R.id.profileEmail);
        userPic = (ImageView) findViewById(R.id.profilePic);
        logOut = (Button) findViewById(R.id.logOut);

        // Get the user data from the passed intent
        Intent intent = getIntent();
        name = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_NAME);
        email = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_EMAIL);
        imageUrl = intent.getStringExtra(IntentConstants.INTENT_SIGNIN_PHOTO);

        // Set the font style of the texts
        nameField.setTypeface(typeface);
        emailField.setTypeface(typeface);
        userName.setTypeface(typeface);
        userEmail.setTypeface(typeface);
        logOut.setTypeface(typeface);

        // Set appropriate user profile fields
        userName.setText(name);
        userEmail.setText(email);
        Glide.with(this).load(imageUrl).into(userPic);

        // Set logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPageActivity.this, MainActivity.class);
                intent.putExtra("signOut", IntentConstants.INTENT_GOOGLE_REQUEST_CODE_SIGNOUT);
                startActivity(intent);
                finish();
            }
        });

        // Set fields for level progress
        levelText = (TextView) findViewById(R.id.levelText);
        currentExpText = (TextView) findViewById(R.id.currentExpText);
        nextLevelText = (TextView) findViewById(R.id.nextLevelText);
        levelText.setTypeface(typeface);
        currentExpText.setTypeface(typeface);
        nextLevelText.setTypeface(typeface);
        expProgress = (ProgressBar) findViewById(R.id.levelProgress);

        // Handle the UI changes for progress bar when view is created
        updateProgressBar();
    }

    // Function to set the UI and Location components
    private void setLocationComponents() {

        // Set Control
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        // Set font of the text fields
        txtCity.setTypeface(typeface);
        txtLastUpdate.setTypeface(typeface);
        txtDescription.setTypeface(typeface);
        txtHumidity.setTypeface(typeface);
        txtTime.setTypeface(typeface);
        txtCelsius.setTypeface(typeface);

        // Set location service and provider
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        // Runtime permission for locations and network
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LandingPageActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, PERMISSION);
        }

        // Get the location
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.e("LOCATION", "No Location!");
            Toast.makeText(getApplicationContext(), "No Location Service", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to set the Activity UI components
    private void setActivityComponents() {
        toDoList = (Button) findViewById(R.id.goToList);
        toDoList.setTypeface(typeface);
        toDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPageActivity.this, ToDoListActivity.class);
                intent.putExtra(IntentConstants.INTENT_TO_DO_LIST, email);
                startActivityForResult(intent, IntentConstants.PREFERENCES_REQUEST_CODE);
                //finish();
            }
        });

        drawingCanvas = (Button) findViewById(R.id.goToCanvas);
        drawingCanvas.setTypeface(typeface);
        drawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPageActivity.this, CanvasActivity.class);
                startActivity(intent);
            }
        });
    }

    // Function to set the Twitter components
    private void setTwitterComponents() {
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login);
        twitterTimeline = (ListView) findViewById(R.id.twitter_timeline);
        twitterLayout = (RelativeLayout) findViewById(R.id.twitterLayout);

        // If user has logged in already, show the user timeline
        // Else, show the Twitter Login Button and then display the timeline
        if(loggedIn)
        {
            loginButton.setVisibility(View.GONE);
            final UserTimeline userTimeline = new UserTimeline.Builder()
                    .screenName("Kiyeonshi")
                    .build();
            final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getApplicationContext())
                    .setTimeline(userTimeline)
                    .build();
            twitterTimeline.setAdapter(adapter);
        }
        else
        {
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    TwitterAuthToken authToken = session.getAuthToken();
                    String token = authToken.token;
                    String secret = authToken.secret;

                    loginButton.setVisibility(View.GONE);
                    loggedIn = true;
                    editor.putBoolean(IntentConstants.PREFERENCES_TWITTER_LOGIN, loggedIn);
                    editor.apply();

                    final UserTimeline userTimeline = new UserTimeline.Builder()
                            .screenName("Kiyeonshi")
                            .build();
                    final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getApplicationContext())
                            .setTimeline(userTimeline)
                            .build();
                    twitterTimeline.setAdapter(adapter);
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    loggedIn = false;
                    Toast.makeText(getApplicationContext(), "Unable to sign in. Try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

        if(resultCode == IntentConstants.PREFERENCES_RESULT_CODE)
        {
            updateProgressBar();
        }
    }

    // Function that updates the progress bar through UI threads and handlers
    private void updateProgressBar()
    {
        // Update the progress bar when we're back in the landing page
        progressHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // Get the appropriate exp and set the progress bar value
                preferences = getApplicationContext().getSharedPreferences(IntentConstants.PREFERENCES_EXP, Context.MODE_PRIVATE);
                editor = preferences.edit();
                exp = preferences.getInt(IntentConstants.PREFERENCES_EXP, 0);
                level = preferences.getInt(IntentConstants.PREFERENCES_CURRENT_LEVEL, 0);
                toNextLevel = preferences.getInt(IntentConstants.PREFERENCES_NEXT_EXP_LEVEL, 10);

                // Update the UI components based on the current user exp
                levelText.setText("Level " + String.valueOf(level) + ":");
                currentExpText.setText(String.valueOf(exp));

                // Set text field for next level
                nextLevelText.setText(String.valueOf(toNextLevel));

                // If user reaches the next level exp requirement, increase max for progress bar
                if(exp >= toNextLevel)
                {
                    toNextLevel = (int) (toNextLevel * 1.5);
                    expProgress.setMax(toNextLevel);

                    nextLevelText.setText(String.valueOf(toNextLevel));

                    // Increase user level
                    level++;
                    levelText.setText("Level " + String.valueOf(level) + ":");

                    // Put to sharedpreferences
                    editor.putInt(IntentConstants.PREFERENCES_CURRENT_LEVEL, level);
                    editor.putInt(IntentConstants.PREFERENCES_NEXT_EXP_LEVEL, toNextLevel);
                    editor.apply();
                }
                else
                {
                    expProgress.setMax(toNextLevel);
                }

                expProgress.setProgress(exp);
            }
        };

        // Run a thread to process the message
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressHandler.sendEmptyMessageAtTime(0,0);
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Runtime permission for locations and network
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LandingPageActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, PERMISSION);
        }

        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Runtime permission for locations and network
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LandingPageActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(WeatherCommons.apiRequest(String.valueOf(lat), String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // Function that processes the weather variables and posts on the UI thread
    private class GetWeather extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog = new ProgressDialog(LandingPageActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Please wait...");
            //progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            WeatherHelper http = new WeatherHelper();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.contains("Error: Not found city")) {
                progressDialog.dismiss();
                return;
            }

            // Parse JSON data
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s, mType);
            //progressDialog.dismiss();

            // Set the text of the fields
            txtCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", WeatherCommons.getDateNow()));
            txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText("Humidity: " + String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("Sunrise: %s\nSunset: %s",
                    WeatherCommons.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),
                    WeatherCommons.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));



            txtCelsius.setText(String.format("%.0f °F", convertTemperature(openWeatherMap.getMain().getTemp())));
            Picasso.with(LandingPageActivity.this)
                    .load(WeatherCommons.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(weatherIcon);
        }
    }

    // Function that converts the temperature obtain from the weather API into Fahrenheit
    private double convertTemperature(double kelvinTemp)
    {
        double fahrenheit;
        double celsius;

        celsius = kelvinTemp - 273.15;
        fahrenheit = ((celsius * 9.0)/5.0) + 32.0;

        return fahrenheit;
    }
}
