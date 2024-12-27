package com.indosoft.mediBridge.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppSession {
    private static AppSession instance;

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor prefsEditor;

    // Singleton to get the instance
    public static synchronized AppSession getInstance(Context context) {
        if (instance == null) {
            instance = new AppSession(context);
        }
        return instance;
    }

    // Private constructor
    private AppSession(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
    }

    // Clear all stored data
    public void clear() {
        prefsEditor.clear().apply();
    }

    // Save a string value
    public void putString(String key, String value) {
        validateKey(key);
        validateValue(value);
        prefsEditor.putString(key, value).apply();
    }

    // Retrieve a string value
    public String getString(String key) {
        validateKey(key);
        return sharedPreferences.getString(key, "");
    }

    // Save a boolean value
    public void putBoolean(String key, boolean value) {
        validateKey(key);
        prefsEditor.putBoolean(key, value).apply();
    }

    // Retrieve a boolean value
    public boolean getBoolean(String key, boolean defaultValue) {
        validateKey(key);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Save an object as a JSON string
    public void putObject(String key, Object obj) {
        validateKey(key);
        validateValue(obj);
        String json = new Gson().toJson(obj);
        putString(key, json);
    }

    // Retrieve an object from JSON string
    public <T> T getObject(String key, Class<T> classOfT) {
        validateKey(key);
        String json = getString(key);
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            return null; // Return null if JSON parsing fails
        }
    }

    // Validate key
    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    // Validate value
    private void validateValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
    }
}
