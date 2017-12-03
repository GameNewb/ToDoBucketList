package edu.sjsu.kyle.todobucketlist.Weather;

/**
 * Created by Kiyeon on 12/2/2017.
 */

public class WeatherWindModel {

    private double speed;
    private double deg;

    public WeatherWindModel(double speed, double deg) {
        this.speed = speed;
        this.deg = deg;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
