package com.dyszlewskiR.edu.scientling.service.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Preferences {

    public enum AnswerConnection {
        LACK(1),
        LESSON(2),
        CATEGORY(3);
        private int id;

        AnswerConnection(int id) {
            this.id = id;
        }

        int getValue() {
            return id;
        }

        String getStringValue() {
            return String.valueOf(id);
        }
    }

    public static final String WORDS_IN_LEARNING_PREF = "prefWordsInLearning";
    public static final String WORDS_IN_REPETITION_PREF = "prefWordsInRepetitions";
    public static final String NUMBER_ANSWER_PREF = "prefNumberAnswers";
    public static final String ANSWER_CONNECTION_PREF = "prefAnswerConnection";
    public static final String DEFAULT_EXERCISE_PREF = "prefDefaultExercise";
    public static final String DEFAULT_DIRECTION_PREF = "prefDefaultDirection";
    public static final String NUMBER_FLASHCARD_PREF = "prefNumberFlashcard";
    public static final String ORDER_LEARNING_PREF = "prefOrderLearning";
    public static final String SHOW_SPEECH_BUTTON_PREF = "prefSpeechButton";
    public static final String REMINDER_PREF = "prefReminder";
    public static final String REMINDER_TIME_PREF = "prefReminderTime";
    public static final String REMINDER_SOUND = "reminder_sound";
    public static final String REMINDER_VIBRATION = "reminder_vibration";


    public static int getNumberWordsInLearning(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(WORDS_IN_LEARNING_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static int getNumberWordsInRepetitions(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(WORDS_IN_REPETITION_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static int getNumberAnswers(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(NUMBER_ANSWER_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static int getNumberFlashcards(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(NUMBER_FLASHCARD_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static int getDefaultExercise(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(DEFAULT_EXERCISE_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static AnswerConnection getAnswerConnection(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(ANSWER_CONNECTION_PREF, null);
        int position = Integer.valueOf(valueString);
        return AnswerConnection.values()[position];
    }

    public static int getExerciseDirection(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(DEFAULT_DIRECTION_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static int getOrderLearning(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String valueString = prefs.getString(ORDER_LEARNING_PREF, null);
        return Integer.valueOf(valueString);
    }

    public static boolean getReminderEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(REMINDER_PREF, false);
    }

    public static String getReminderTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(REMINDER_TIME_PREF, "16:00");
    }

    public static boolean getReminderSound(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(REMINDER_SOUND, false);
    }

    public static boolean getReminderVibration(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(REMINDER_VIBRATION, false);
    }

    public static boolean isShowSpeechButton(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SHOW_SPEECH_BUTTON_PREF, false);
    }
}
