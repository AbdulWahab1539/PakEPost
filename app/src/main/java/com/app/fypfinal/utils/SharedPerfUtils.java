package com.app.fypfinal.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.fypfinal.Info.Info;


public class SharedPerfUtils implements Info {

    static String NAME = "SharedPrefs";

    public static void putBooleanSharedPrefs(Activity context, Boolean value, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences(NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanSharedPrefs(Activity context, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences(NAME, 0);
        boolean id = mPrefs.getBoolean(key, false);
        Log.i(TAG, "getBooleanSharedPrefs: " + id);
        return id;
    }


    public static void putStringSharedPrefs(Activity context, String value, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences(NAME, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringSharedPrefs(Activity context, String key) {
        SharedPreferences mPrefs = context.getSharedPreferences(NAME, 0);
        String id = mPrefs.getString(key, "");
        Log.i(TAG, "getStringSharedPrefs: " + id);
        return id;
    }

}
