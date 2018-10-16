package com.danertu.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class SPTool {
    public static final String SP_MESSAGE = "message";
    public static final String SP_APP_CONFIG = "appConfig";
    public static final String SP_MESSAGE_CLEAR = "isCleared";
    public static final String SP_MESSAGE_CLEAR_TIME = "clearTime";

    public static void updateBoolean(Context context, String spName, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static boolean getBoolean(Context context, String spName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void updateString(Context context, String spName, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getString(Context context, String spName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
}
