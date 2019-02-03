package com.codility.pedometer.utils;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * ****************************************************************************
 * * Copyright © 2018 W3 Engineers Ltd., All rights reserved.
 * *
 * * Created by:
 * * Name : SUDIPTA KUMAR PAIK
 * * Date : 2/1/18
 * * Email : sudipta@w3engineers.com
 * *
 * * Purpose : SharedPref for all type of SharedPreferences functionality
 * *
 * * Last Edited by :  Md. Nomanur Rashid on 5 May 2018.
 * *
 * * Last Reviewed by :
 * ****************************************************************************
 */

public class SharedPref {

    private final static String PREFERENCE_NAME = "mesh_rnd";
    private static SharedPreferences preferences;
    private static SharedPref sharedPref;

    private SharedPref(Context context) {

    }

    public static SharedPref on(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
        if (sharedPref == null) {
            sharedPref = new SharedPref(context);
        }
        return sharedPref;
    }

    public boolean write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);

        return editor.commit();
    }

    public boolean write(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);

        return editor.commit();
    }

    public boolean write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public boolean write(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public boolean write(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }


    public String read(String key) {
        return preferences.getString(key, "");
    }

    public String read(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public long readLong(String key) {
        return preferences.getLong(key, 0);
    }

    public float readFloat(String key) {
        return preferences.getFloat(key, 0.0f);
    }

    public int readInt(String key) {
        return preferences.getInt(key, 0);
    }

    public boolean readBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean readSettingsBoolean(String key) {
        return preferences.getBoolean(key, true);
    }

    public boolean readBooleanDefaultTrue(String key) {
        return preferences.getBoolean(key, true);
    }

    public boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * Remove all saved shared Preference data of app.
     */
    public void removeAllPreferenceData() {
        preferences.edit().clear().apply();
    }

    public void removeSpecificItem(String key) {
        preferences.edit().remove(key).apply();
    }

}
