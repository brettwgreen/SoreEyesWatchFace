package com.nervii.fortysomething;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.nervii.fortysomething.weather.IWeatherApi;
import com.nervii.fortysomething.weather.openweather.OpenWeatherApi;
import com.nervii.fortysomething.weather.WeatherInfo;

public class WeatherApiTest extends ApplicationTestCase<Application> {
    public WeatherApiTest() {
        super(Application.class);
    }

    public void testGetWeatherData() {

        IWeatherApi api = new OpenWeatherApi();
        //api.setContext(this.getContext());
        WeatherInfo info = api.getCurrentWeatherInfo(40.71, -74.01);

        assertNotNull(info);
    }
}