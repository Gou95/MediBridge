package com.indosoft.MediBridges.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppSession {
    private static AppSession instance;

    private final SharedPreferences sharedPreferences;

    private AppSession(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppSession getInstance(Context context) {
        if (instance == null) {
            instance = new AppSession(context.getApplicationContext());
        }
        return instance;
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void setValue(String key, String value) {
        if (key != null && value != null) {
            sharedPreferences.edit().putString(key, value).apply();
        }
    }

    public String getValue(String key) {
        return key != null ? sharedPreferences.getString(key, "") : "";
    }

    public void setBoolean(String key, boolean value) {
        if (key != null) {
            sharedPreferences.edit().putBoolean(key, value).apply();
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return key != null && sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putObject(String key, Object obj) {
        if (key != null && obj != null) {
            Gson gson = new Gson();
            setValue(key, gson.toJson(obj));
        }
    }

    public <T> T getObject(String key, Class<T> classOfT) {
        if (key == null) {
            return null;
        }
        String json = getValue(key);
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
