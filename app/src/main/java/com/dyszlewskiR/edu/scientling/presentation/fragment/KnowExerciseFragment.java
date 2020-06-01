package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.exercises.KnowExercise;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SpeechButton;

public class KnowExerciseFragment  extends ExerciseFragment{

    private TextView mWordTextView;
    private TextView mTranslationTextView;
    //private SpeechButton mSpeechButton;
    private Button mShowAnswerButton;
    private ViewGroup mKnowButtonsContainer;
    private Button mKnowButton;
    private Button mDontKnowButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_know_exercise, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view){
        mWordTextView = (TextView) view.findViewById(R.id.word_text_view);
        mTranslationTextView = (TextView)view.findViewById(R.id.translation_text_view);
        mShowAnswerButton = (Button)view.findViewById(R.id.show_answer_button);
        mSpeechButton = (SpeechButton)view.findViewById(R.id.speech_button);
        mKnowButtonsContainer = (ViewGroup)view.findViewById(R.id.know_button_container);
        mKnowButton = (Button)view.findViewById(R.id.know_button);
        mDontKnowButton = (Button)view.findViewById(R.id.dont_know_button);
    }

    private void setListeners(){
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
                setAnswerVisibility(true);
            }
        });

        mKnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAnswer(KnowExercise.KNOW_ANSWER);
            }
        });

        mDontKnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAnswer(KnowExercise.DONT_KNOW_ANSWER);
            }
        });
    }

    private void showAnswer(){
        String answer = mExerciseManager.getCorrectAnswer();
        mTranslationTextView.setText(answer);
    }

    /** Pokazuje odpowiedź po naciśnięciu przez użytkownika przycisku Pokaż odpowiedx
     */
    private void setAnswerVisibility(boolean answerVisible){
        if(answerVisible){
            setAnswerVisibility(View.VISIBLE);
            mShowAnswerButton.setVisibility(View.GONE);
        } else {
            setAnswerVisibility(View.INVISIBLE);
            mShowAnswerButton.setVisibility(View.VISIBLE);
        }
    }

    private void setAnswerVisibility(int visibility){
        mKnowButtonsContainer.setVisibility(visibility);
        mTranslationTextView.setVisibility(visibility);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        showQuestion();
    }

    @Override
    public void onDetach(){
        mActivityCallback = null;
        super.onDetach();
    }


    @Override
    public void toAnswer(String answer) {
        boolean correctAnswer = mExerciseManager.checkAnswer(answer);
        if(mActivityCallback != null){
            mActivityCallback.onAnswer(correctAnswer);
        }
        setAnswerVisibility(false);
        boolean hasQuestion = mExerciseManager.nextQuestion();
        if(hasQuestion){
            showQuestion();
        } else {
            mActivityCallback.onExerciseFinish();
        }
    }

    @Override
    public void showQuestion() {
        String question = mExerciseManager.getQuestion();
        mWordTextView.setText(question);
        updateSpeechPlayer();
    }
}
