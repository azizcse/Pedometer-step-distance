package com.codility.pedometer;

import android.app.Application;

import com.codility.pedometer.utils.SharedPref;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPref.on(getApplicationContext());
    }
}
