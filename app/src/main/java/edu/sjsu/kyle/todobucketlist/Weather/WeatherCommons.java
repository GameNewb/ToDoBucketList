package edu.sjsu.kyle.todobucketlist.Weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kiyeon on 12/2/2017.
 *
 * Credits go to EDMT Dev for an awesome tutorial on Weather components
 * https://www.youtube.com/watch?v=wAV2Uqv9KLo
 *
 */

public class WeatherCommons {
    public static String API_KEY = "07d3c527cb596240d913af91a7d2a8fb";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/weather";
    public static int WEATHER_RESPONSE_CODE = 200;

    public static String apiRequest(String lat, String lng)
    {
        StringBuilder sb = new StringBuilder(API_LINK);
        //sb.append(String.format("?lat=%s&lon=%s&APPID-%s&units=metric", lat, lng, API_KEY));
        sb.append(String.format("?lat=%s&lon=%s&appid=%s&units", lat, lng, API_KEY));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) unixTimeStamp*1000);

        return dateFormat.format(date);
    }

    public static String getImage(String icon) {
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }

    public static String getDateNow() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();

        return dateFormat.format(date);
    }
}
