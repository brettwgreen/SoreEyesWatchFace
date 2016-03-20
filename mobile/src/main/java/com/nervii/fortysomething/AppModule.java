package com.swarmnyc.fortysomething;

import com.google.inject.AbstractModule;
import com.nervii.fortysomething.weather.IWeatherApi;
import com.nervii.fortysomething.weather.openweather.OpenWeatherApi;

public class AppModule extends AbstractModule {
    //private Context mContext;

    /*public AppModule(Context context) {
        this.mContext = context;
    }*/

    @Override
    protected void configure() {
        bind(IWeatherApi.class).toInstance(new OpenWeatherApi());
    }
}
