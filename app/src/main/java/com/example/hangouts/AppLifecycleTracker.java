package com.example.hangouts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private final String TAG = getClass().getName();
    private boolean isInBackground;
    private String formattedTime;
    private Context context;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        context = activity.getApplicationContext();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(isInBackground){
            Log.d(TAG, "app went to foreground");
            isInBackground = false;

            Toast.makeText(context, formattedTime, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if(i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            Log.d(TAG, "app went to background");
            isInBackground = true;

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            formattedTime = context.getResources().getString(R.string.last_time_used) + " " + formatter.format(cal.getTime());
        }
    }
}
