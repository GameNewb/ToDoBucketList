package edu.sjsu.kyle.todobucketlist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import edu.sjsu.kyle.todobucketlist.Weather.OpenWeatherMap;
import edu.sjsu.kyle.todobucketlist.Weather.WeatherCommons;
import edu.sjsu.kyle.todobucketlist.Weather.WeatherHelper;

public class LandingPageActivity extends AppCompatActivity implements LocationListener {

    // Profile components
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

    LocationManager locationManager;
    String provider;
    static double lat;
    static double lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Set Profile UI Components
        setProfileUIComponents();

        // Set Location data such as coordinates, city, etc.
        setLocationComponents();

    }

    // Function to set the UI components after Activity creation
    private void setProfileUIComponents() {
        userName = (TextView) findViewById(R.id.profileName);
        userEmail = (TextView) findViewById(R.id.profileEmail);
        userPic = (ImageView) findViewById(R.id.profilePic);
        logOut = (Button) findViewById(R.id.logOut);

        // Get the user data from the passed intent
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
                intent.putExtra("signOut", IntentConstants.INTENT_GOOGLE_REQUEST_CODE_SIGNOUT);
                startActivity(intent);
                finish();
            }
        });

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

        // Set location service
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

        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.e("LOCATION", "No Location!");
            Toast.makeText(getApplicationContext(), "No Location Service", Toast.LENGTH_SHORT).show();
        }
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

    private class GetWeather extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog = new ProgressDialog(LandingPageActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
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

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s, mType);
            progressDialog.dismiss();

            txtCity.setText(String.format("%s, %s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", WeatherCommons.getDateNow()));
            txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d%%", openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s/%s",
                    WeatherCommons.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),
                    WeatherCommons.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));

            txtCelsius.setText(String.format("%.2f °F", convertTemperature(openWeatherMap.getMain().getTemp())));
            Picasso.with(LandingPageActivity.this)
                    .load(WeatherCommons.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(weatherIcon);
        }
    }

    private double convertTemperature(double kelvinTemp)
    {
        double fahrenheit;
        double celsius;

        celsius = kelvinTemp - 273.15;
        fahrenheit = ((celsius * 9.0)/5.0) + 32.0;

        return fahrenheit;
    }
}
