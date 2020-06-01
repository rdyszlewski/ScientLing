package com.dyszlewskiR.edu.scientling.controlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.SpeechPlayer;

import java.util.List;

public class LearningControls {
    public static final String ITEMS = "items";
    public static final String LEARNING_MODE = "learning";
    public static final String SET = "set";

    private List<Word> mWords;
    private int mCurrentFragment;
    private Uri mRecordUri;
    private SpeechPlayer mSpeechPlayer;
    private VocabularySet mSet;
    private int mCurrentPosition;
    private boolean mLearningMode;

    public LearningControls(Intent intent){
        mWords = intent.getParcelableArrayListExtra(ITEMS);
        mLearningMode = intent.getBooleanExtra(LEARNING_MODE, true);
        mSet = intent.getParcelableExtra(SET);
        mCurrentPosition = 0;
        mCurrentFragment = -1;
    }

    public List<Word> getWords() {return mWords;}
    public void setWords(List<Word> words){mWords = words;}

    public boolean isLearningMode(){return mLearningMode;}
    public void setLearningMode(boolean learningMode) {mLearningMode = learningMode;}

    public VocabularySet getSet(){return mSet;}
    public void setSet(VocabularySet set){mSet = set;}

    public int getCurrentFragment(){return mCurrentFragment;}
    public void setCurrentFragment(int fragmentNumber){
        mCurrentFragment = fragmentNumber;
    }

    public void initSpeechPlayer(Context context) {
        mSpeechPlayer = new SpeechPlayer(context);
        mSpeechPlayer.setMediaCatalog(mSet.getCatalog());
        mSpeechPlayer.setLanguageCode(mSet.getLanguageL2().getCode());
        //mSpeechPlayer.setCallback(this);
        Word word = mWords.get(mCurrentPosition);
        mSpeechPlayer.setValues(word.getContent(), word.getRecordName());
    }
}
