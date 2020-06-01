package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseDirection;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseManager;
import com.dyszlewskiR.edu.scientling.service.exercises.IExerciseCallback;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.ISpeechCallback;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.SpeechPlayer;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SpeechButton;

import java.io.IOException;

public abstract class ExerciseFragment extends Fragment implements ISpeechCallback{

    protected ExerciseManager mExerciseManager;
    protected IExerciseCallback mActivityCallback;
    protected ExerciseDirection mExerciseDirection;

    protected SpeechButton mSpeechButton;

    //W każdym ćwiczeniu istnieje możliwość odtwarzania dźwięku, więc można napisać część współną
    private SpeechPlayer mSpeechPlayer;

    public abstract void toAnswer(String answer);
    protected abstract void showQuestion();

    public void setExerciseManager(ExerciseManager exerciseManager){
        mExerciseManager = exerciseManager;
    }

    public void setExerciseDirection(ExerciseDirection direction){
       mExerciseDirection = direction;
   }

    public void setExerciseCallback(IExerciseCallback callback){
        mActivityCallback = callback;
    }

    public void setSpeechPlayer(SpeechPlayer speechPlayer){
        mSpeechPlayer = speechPlayer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mSpeechPlayer != null){
            mSpeechPlayer.setCallback(this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        showQuestion();
        if(mExerciseDirection == ExerciseDirection.L1_TO_L2){
            mSpeechButton.setVisibility(View.INVISIBLE);
        } else {
            setSpeechButtonListener();
        }
    }

    private void setSpeechButtonListener(){
        mSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSpeechPlayer.isInit()){
                    mSpeechButton.setState(SpeechButton.LOADING);
                }
                try {
                    mSpeechPlayer.speak();
                } catch (IOException e) {
                    Toast.makeText(getContext(), getString(R.string.play_record_error), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mSpeechPlayer != null){
            mSpeechPlayer.setCallback(null);
        }
    }

    @Override
    public void onDetach(){
        mActivityCallback = null;
        super.onDetach();
    }

    void updateSpeechPlayer(){
        String content = mExerciseManager.getQuestion();
        String recordName = mExerciseManager.getRecordName();
        mSpeechPlayer.setValues(content, recordName);
    }

    @Override
    public void onSpeechStart() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeechButton.setState(SpeechButton.PLAYING);
            }
        });
    }

    @Override
    public void onSpeechCompleted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeechButton.setState(SpeechButton.NORMAL);
            }
        });
    }
}
