package com.example.hangouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Utils
{
    public final static int THEME_BLUE = 0;
    public final static int THEME_RED = 1;
    public final static int THEME_DARK = 2;

    private final static String SHARED_PREFS = "sharedPrefs";
    private final static String THEME = "theme";

    /** Saves the theme locally */
    private static void storeThemeLocally(Activity activity, int theme) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME, theme);
        editor.apply();
    }

    /** Loads the theme from local */
    private static int loadTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        // Default value will be Blue theme
        return sharedPreferences.getInt(THEME, THEME_BLUE);
    }

    /** Set the theme of the Activity, and restart it by creating a new Activity of the same type. */
    public static void changeToTheme(Activity activity, int theme)
    {
        /* By default, the theme will be blue */
        storeThemeLocally(activity, theme);
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        int sTheme = loadTheme(activity);
        boolean isMainActivity = activity.getClass().getName().equals(MainActivity.class.getName());
        switch (sTheme)
        {
            default:
            case THEME_BLUE:
                if (isMainActivity)
                    activity.setTheme(R.style.Theme_Blue_NoActionBar);
                else
                    activity.setTheme(R.style.Theme_Blue);
                break;
            case THEME_RED:
                if (isMainActivity)
                    activity.setTheme(R.style.Theme_Red_NoActionBar);
                else
                    activity.setTheme(R.style.Theme_Red);
                break;
            case THEME_DARK:
                if (isMainActivity)
                    activity.setTheme(R.style.Theme_Dark_NoActionBar);
                else
                    activity.setTheme(R.style.Theme_Dark);
                break;
        }
    }
}