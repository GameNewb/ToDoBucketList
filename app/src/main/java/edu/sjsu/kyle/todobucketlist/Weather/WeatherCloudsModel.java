package edu.sjsu.kyle.todobucketlist.Weather;

/**
 * Created by Kiyeon on 12/2/2017.
 *
 * Credits go to EDMT Dev for an awesome tutorial on Weather components
 * https://www.youtube.com/watch?v=wAV2Uqv9KLo
 *
 */

public class WeatherCloudsModel {

    private int all;

    public WeatherCloudsModel(int all) {
        this.all = all;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }
}
