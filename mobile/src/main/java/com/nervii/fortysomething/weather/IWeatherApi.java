package com.nervii.fortysomething.weather;

public interface IWeatherApi {
    WeatherInfo getCurrentWeatherInfo(double lon, double lat);
}
