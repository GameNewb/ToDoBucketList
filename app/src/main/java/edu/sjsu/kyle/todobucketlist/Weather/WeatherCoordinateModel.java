package edu.sjsu.kyle.todobucketlist.Weather;

/**
 * Created by Kiyeon on 12/2/2017.
 *
 * Credits go to EDMT Dev for an awesome tutorial on Weather components
 * https://www.youtube.com/watch?v=wAV2Uqv9KLo
 *
 */


public class WeatherCoordinateModel {

    private double lat;
    private double lon;

    public WeatherCoordinateModel(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
