package edu.sjsu.kyle.todobucketlist.Weather;

import java.util.List;

/**
 * Created by Kiyeon on 12/2/2017.
 */

public class OpenWeatherMap {
    private WeatherCoordinateModel coord;
    private List<WeatherDetailsModel> weather;
    private String base;
    private WeatherDataModel main;
    private WeatherWindModel wind;
    private WeatherRainModel rain;
    private WeatherCloudsModel clouds;
    private WeatherSysModel sys;
    private int dt;
    private int id;
    private String name;
    private int cod;

    public OpenWeatherMap()
    {

    }

    public OpenWeatherMap(WeatherCoordinateModel coord, List<WeatherDetailsModel> weather, String base, WeatherDataModel main, WeatherWindModel wind, WeatherRainModel rain, WeatherCloudsModel clouds, WeatherSysModel sys, int dt, int id, String name, int cod) {
        this.coord = coord;
        this.weather = weather;
        this.base = base;
        this.main = main;
        this.wind = wind;
        this.rain = rain;
        this.clouds = clouds;
        this.sys = sys;
        this.dt = dt;
        this.id = id;
        this.name = name;
        this.cod = cod;
    }

    public WeatherCoordinateModel getCoord() {
        return coord;
    }

    public void setCoord(WeatherCoordinateModel coord) {
        this.coord = coord;
    }

    public List<WeatherDetailsModel> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherDetailsModel> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public WeatherDataModel getMain() {
        return main;
    }

    public void setMain(WeatherDataModel main) {
        this.main = main;
    }

    public WeatherWindModel getWind() {
        return wind;
    }

    public void setWind(WeatherWindModel wind) {
        this.wind = wind;
    }

    public WeatherRainModel getRain() {
        return rain;
    }

    public void setRain(WeatherRainModel rain) {
        this.rain = rain;
    }

    public WeatherCloudsModel getClouds() {
        return clouds;
    }

    public void setClouds(WeatherCloudsModel clouds) {
        this.clouds = clouds;
    }

    public WeatherSysModel getSys() {
        return sys;
    }

    public void setSys(WeatherSysModel sys) {
        this.sys = sys;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
