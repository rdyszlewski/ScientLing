package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseDirection;
import com.dyszlewskiR.edu.scientling.service.speech.speechToText.ISpeechRecognitionResult;
import com.dyszlewskiR.edu.scientling.service.speech.speechToText.SpeechToText;
import com.dyszlewskiR.edu.scientling.utils.StringSimilarityCalculator;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SayButton;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SpeechButton;

public class WritingExerciseFragment extends ExerciseFragment implements ISpeechRecognitionResult {

    private TextView mWordTextView;
    private SayButton mSayButton;
    private EditText mAnswerEditText;
    private Button mCheckAnswerButton;
    private SpeechToText mSpeechToText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_write_exercise, container, false);
        setupControls(view);
        setListeners();
        setSayButtonVisible();
        return view;
    }

    public void setupControls(View view){
        mWordTextView = (TextView) view.findViewById(R.id.word_text_view);
        mSpeechButton = (SpeechButton) view.findViewById(R.id.speech_button);
        mSayButton = (SayButton)view.findViewById(R.id.say_button);
        mAnswerEditText = (EditText)view.findViewById(R.id.answer_edit_text);
        mCheckAnswerButton = (Button) view.findViewById(R.id.check_answer_button);
    }

    private void setListeners(){
        mSayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("WritingFragment", "Say Button click");
                if(mSpeechToText== null){
                    VocabularySet set = mExerciseManager.getSet();
                    mSpeechToText = new SpeechToText(getContext(), set.getLanguageL2().getCode(), WritingExerciseFragment.this);

                }
                mSayButton.setState(SayButton.RECORDING);
                mSpeechToText.startListening();

            }
        });

        //TODO mAnswerEditText.setOnEditorActionListener
        mCheckAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAnswer(mAnswerEditText.getText().toString());
            }
        });
    }

    private void setSayButtonVisible(){
        if(mExerciseDirection== ExerciseDirection.L2_TO_L1){
            mSayButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void toAnswer(String answer) {
        boolean correct = mExerciseManager.checkAnswer(answer);
        mActivityCallback.onAnswer(correct);
        if(correct){
            new CorrectDialog(getActivity(), new AnswerDialogListener() {
                @Override
                public void onClose() {
                    nextQuestion();
                }
            }).show();
        } else {
            String correctAnswer = mExerciseManager.getCorrectAnswer();
            new IncorrectDialog(getActivity(),answer, correctAnswer, new AnswerDialogListener(){
                @Override
                public void onClose(){
                    nextQuestion();
                }
            }).show();
        }
    }

    private void nextQuestion(){
        boolean hasQuestion = mExerciseManager.nextQuestion();
        if(hasQuestion){
            showQuestion();
        } else {
            mActivityCallback.onExerciseFinish();
        }
    }

    @Override
    public void showQuestion() {
        //resetujemy pole do wpisywania odpowiedzi
        mAnswerEditText.setText("");
        String question = mExerciseManager.getQuestion();
        mWordTextView.setText(question);
        updateSpeechPlayer();
    }

    /** Metoda zwrotna przetwarzania mowy na tekst. Metoda otrzymuje listę wyników zwróconą przez
     * system rozpoznawania mowy. Aplikacja potrafi rozpoznawać angielską mowę bez korzystania
     * z internetu, ale poprawność wyników jest wtedy znacznie mniejsza. Z tego powodu
     * z dostepnych możliwych rozwiązań wybieramy najbardziej podobną do odpowiedzi. Dzieki
     * temu mamy większą pewność, że wybrane słówko będzie tym, które powiedział użytkownik
     * @param result lista słówek zwrócona przez system rozpoznawania mowy
     */
    @Override
    public void receiveRecognitionResult(String[] result) {
        if(result != null){
            String answer = mExerciseManager.getCorrectAnswer();
            String mostSimilarAnswer = StringSimilarityCalculator.getMostSimilarLevenshtein(answer, result);
            mAnswerEditText.setText(mostSimilarAnswer);
        }
        mSayButton.setState(SayButton.NORMAL);
    }
}

class CorrectDialog extends Dialog {
    private Button mNextButton;
    private AnswerDialogListener mListener;

    public CorrectDialog(Context context, AnswerDialogListener listener){
        super(context);
        mListener = listener;
        setupDialog();
        setupControls();
        setListeners();
    }

    private void setupDialog(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.correct_dialog);
        setCancelable(false);
    }

    private void setupControls(){
        mNextButton = (Button) findViewById(R.id.next_button);
    }

    private void setListeners(){
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClose();
                }
                dismiss();
            }
        });
    }
}

class IncorrectDialog extends Dialog {
    private TextView mUsersAnswerTextView;
    private TextView mCorrectAnswerTextView;
    private Button mNextButton;
    private AnswerDialogListener mListener;

    public IncorrectDialog(Context context, String usersAnswer, String correctAnswer, AnswerDialogListener listener){
        super(context);
        mListener = listener;
        setupDialog();
        setupControls();
        setListener();
        setValues(usersAnswer, correctAnswer);
    }

    private void setupDialog(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.incorrect_dialog);
        setCancelable(false);
    }

    private void setupControls(){
        mUsersAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mCorrectAnswerTextView = (TextView)findViewById(R.id.correct_answer_text_view);
        mNextButton = (Button) findViewById(R.id.next_button);
    }

    private void setListener(){
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClose();
                }
                dismiss();
            }
        });
    }

    private void setValues(String answer, String correctAnswer){
        mUsersAnswerTextView.setText(answer);
        mCorrectAnswerTextView.setText(correctAnswer);
    }
}

interface AnswerDialogListener{
    void onClose();
}
