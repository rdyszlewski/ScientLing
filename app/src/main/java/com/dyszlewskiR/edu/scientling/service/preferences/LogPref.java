package com.dyszlewskiR.edu.scientling.service.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Razjelll on 19.04.2017.
 */

public class LogPref {
    private static final String LOGIN = "prefLogin";
    private static final String PASSWORD = "prefPassword";
    private static final String IS_LOGGED = "prefLogged";

    public static String getLogin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LOGIN, null);
    }

    public static void setLogin(String login, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(LOGIN, login);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getString(PASSWORD, null);
    }

    public static void setPassword(String password, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public static boolean isLogged(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(IS_LOGGED, false);
    }

    public static void setLogged(boolean logged, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGGED, logged);
        editor.apply();
    }

}
