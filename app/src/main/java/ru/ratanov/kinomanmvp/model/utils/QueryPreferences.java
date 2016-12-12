package ru.ratanov.kinomanmvp.model.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    public static final String PREF_SERVER = "server";
    public static final String PREF_PORT = "port";
    public static final String PREF_LOGIN = "login";
    public static final String PREF_PASSWORD = "password";

    public static String getStoredQuery(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, null);
    }

    public static void setStoredQuery(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }
}
