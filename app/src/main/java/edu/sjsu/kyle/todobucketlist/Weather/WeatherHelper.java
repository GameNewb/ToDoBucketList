package edu.sjsu.kyle.todobucketlist.Weather;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kiyeon on 12/2/2017.
 */

public class WeatherHelper {
    static String stream = null;

    public WeatherHelper() {

    }

    // Function to make a request to OpenWeatherMap's API and get the result
    public String getHTTPData(String urlString)
    {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if(httpURLConnection.getResponseCode() == WeatherCommons.WEATHER_RESPONSE_CODE)
            {
                BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = r.readLine()) != null)
                {
                    sb.append(line);
                }

                stream = sb.toString();
                httpURLConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("STREAMDATA", "STREAM DATA  " + stream);
        return stream;
    }
}
