package com.example.hangouts;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        AppLifecycleTracker handler = new AppLifecycleTracker();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);
    }
}
