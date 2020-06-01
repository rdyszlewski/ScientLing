package com.dyszlewskiR.edu.scientling.presentation.activity;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;
import com.dyszlewskiR.edu.scientling.utils.resources.Colors;
import com.dyszlewskiR.edu.scientling.presentation.widgets.NumberPreference;

public class PreferenceActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setupActionBar();
        initValues();

    }

    private void initValues() {
        NumberPreference wordsInLearning = (NumberPreference) findPreference(Preferences.WORDS_IN_LEARNING_PREF);
        wordsInLearning.setMaxValue(20);
        wordsInLearning.setMinValue(2);
        NumberPreference wordsInRepetition = (NumberPreference) findPreference(Preferences.WORDS_IN_REPETITION_PREF);
        wordsInRepetition.setMaxValue(20);
        wordsInRepetition.setMinValue(2);
        NumberPreference wordsInFlashcard = (NumberPreference) findPreference(Preferences.NUMBER_FLASHCARD_PREF);
        wordsInFlashcard.setMaxValue(20);
        wordsInFlashcard.setMinValue(2);
        NumberPreference numberAnswers = (NumberPreference) findPreference(Preferences.NUMBER_ANSWER_PREF);
        numberAnswers.setMaxValue(6);
        numberAnswers.setMinValue(2);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Colors.getColor(R.color.colorMain, getBaseContext())));
        }
    }

}
