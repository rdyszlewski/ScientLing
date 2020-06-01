package com.dyszlewskiR.edu.scientling.service.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceInitializer {
    private static final String INITIALIZE_PREF = "prefInitialize";

    private static final int DEFAULT_WORDS_IN_LEARNING = 5;
    private static final int DEFAULT_WORDS_IN_REPETITION = 5;
    private static final int DEFAULT_NUMBER_ANSWERS = 6;
    private static final int DEFAULT_NUMBER_FLASHCARD = 7;
    private static final int DEFAULT_ANSWER_CONNECTION = Preferences.AnswerConnection.LACK.getValue();
    private static final int DEFAULT_EXERCISE = 1;
    private static final int DEFAULT_DIRECTION = 0;
    private static final int DEFAULT_ORDER_LEARNING = 2;
    private static final boolean DEFAULT_SHOW_SPEECH_BUTTON = false;
    private static final boolean DEFAULT_REMINDER = true;
    private static final String DEFAULT_REMINDER_TIME = "16:00";
    private static final boolean DEFAULT_REMINDER_SOUND = false;
    private static final boolean DEFAULT_REMINDER_VIBRATION = true;


    public static boolean isInitialize(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(INITIALIZE_PREF, false);
    }

    public static void initialize(Context context) {
        Log.d("PreferenceInitialize", "initialize");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Preferences.WORDS_IN_LEARNING_PREF, String.valueOf(DEFAULT_WORDS_IN_LEARNING));
        editor.putString(Preferences.WORDS_IN_REPETITION_PREF, String.valueOf(DEFAULT_WORDS_IN_REPETITION));
        editor.putString(Preferences.NUMBER_ANSWER_PREF, String.valueOf(DEFAULT_NUMBER_ANSWERS));
        editor.putString(Preferences.ANSWER_CONNECTION_PREF, String.valueOf(DEFAULT_ANSWER_CONNECTION));
        editor.putString(Preferences.DEFAULT_EXERCISE_PREF, String.valueOf(DEFAULT_EXERCISE));
        editor.putString(Preferences.DEFAULT_DIRECTION_PREF, String.valueOf(DEFAULT_DIRECTION));
        editor.putString(Preferences.NUMBER_FLASHCARD_PREF, String.valueOf(DEFAULT_NUMBER_FLASHCARD));
        editor.putString(Preferences.ORDER_LEARNING_PREF, String.valueOf(DEFAULT_ORDER_LEARNING));
        editor.putBoolean(Preferences.SHOW_SPEECH_BUTTON_PREF, DEFAULT_SHOW_SPEECH_BUTTON);
        editor.putBoolean(Preferences.REMINDER_PREF, DEFAULT_REMINDER);
        editor.putString(Preferences.REMINDER_TIME_PREF, DEFAULT_REMINDER_TIME);
        editor.putBoolean(Preferences.REMINDER_SOUND, DEFAULT_REMINDER_SOUND);
        editor.putBoolean(Preferences.REMINDER_VIBRATION, DEFAULT_REMINDER_VIBRATION);
        editor.putBoolean(INITIALIZE_PREF, true);

        editor.apply();
    }

}
