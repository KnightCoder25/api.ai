package com.romeo.apiai;

import android.app.Application;

import com.romeo.agent.Agent;
import com.romeo.agent.Keys;

/**
 * Created by romeo on 15/03/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Agent.init(Keys.ACCESS_TOKEN,Keys.SUBSCRIPTION_KEY,getApplicationContext());
    }
}
