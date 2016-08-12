package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SharedPrefsHelper {
    private final static String PREF_FILE = "PREF";

    /**
     * Set a string shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    static void setSharedPreferenceString(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    static void setSharedPreferenceInt(Context context, String key, int value){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Set an object shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    static void setSharedPreferenceObject(Context context, String key, Object value){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(value));
        editor.apply();
    }

    /**
     * Set a Boolean shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    static void setSharedPreferenceBoolean(Context context, String key, boolean value){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a string shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    static String getSharedPreferenceString(Context context, String key, String defValue){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        String value = settings.getString(key, defValue);

        return value;
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    static int getSharedPreferenceInt(Context context, String key, int defValue){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        int value = settings.getInt(key, defValue);

        return value;
    }

    /**
     * Get an object shared preference
     * @param key - Key to set shared preference
     */
    static Object getSharedPreferenceObject(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        Gson gson = new Gson();
        String json = settings.getString(key, null);

        return gson.fromJson(json, Object.class);
    }

    /**
     * Get a boolean shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        boolean value = settings.getBoolean(key, defValue);

        return value;
    }

    /**
     * Remove an object shared preference
     * @param key - Key to set shared preference
     */
    static void removeSharedPreferenceObject(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
    }

    static Map<String, ?> getAll(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getAll();
    }

    static List<PreferredJourney> getAllAsObject(Context context) {
        Gson gson = new Gson();
        List<PreferredJourney> list = new LinkedList<>();
        for (String el : (Collection<String>)getAll(context).values()) {
            list.add(gson.fromJson(el, PreferredJourney.class));
        }
        return list;
    }

}