package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.widgets.AnswerButton;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SpeechButton;

public class ChoosingExerciseFragment extends ExerciseFragment {
    private static final String LOG_TAG = "ChoosingExercise";

    private TextView mWordTextView;
    //TODO zrobić drugi przycisk
    private AnswerButton[] mAnswerButtons;
    private Button mNextButton;

    private boolean mCanAnswer;
    /**Zawiera indeksy przycisków które zostały pokolorowane. Przyciski kolorowane są podczas odpowiediz
     * użytkownika. Poprawna odpowiedź na zielono, błędna na niebiesko. Pierwsze pole oznacza
     * poprawną odpowiedź, ponieważ poprawna zawsze będzie wyświetlona. Drugie pole oznacza błędną
     * odpowiedź, będzie wyświetlona tylko w przypadku błędnej odpowiedzi użytkownika
     */
    private int[] mColoredButtons = {-1,-1};
    private final int CORRECT = 0;
    private final int INCORRECT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_choose_exercise, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view){
        mWordTextView = (TextView)view.findViewById(R.id.word_text_view);
        mSpeechButton = (SpeechButton) view.findViewById(R.id.speech_button);
        mNextButton = (Button)view.findViewById(R.id.next_button);
        setupAnswerButtons(mExerciseManager.getNumAnswers(), view);
    }

    private void setListeners(){
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO tutaj też dodać jakąś magię
                resetButtonsColor();
                boolean hasQuestion = mExerciseManager.nextQuestion();
                if(hasQuestion){
                    showQuestion();
                    setNextButtonVisibility(View.INVISIBLE);
                    mCanAnswer = true;
                } else {
                    mActivityCallback.onExerciseFinish();
                }
            }
        });
    }

    /** Metoda resetująca kolor przycisków które zostały pokolorowane po odpowiedzi użytkownika
     * (na zielono poprawna odpowiedź, na czerwono błędna)
     */
    private void resetButtonsColor(){
       mAnswerButtons[mColoredButtons[CORRECT]].changeState(AnswerButton.NOSET);
        if(mColoredButtons[INCORRECT]!=-1){
            mAnswerButtons[mColoredButtons[INCORRECT]].changeState(AnswerButton.NOSET);
        }
        mColoredButtons[CORRECT] = -1;
        mColoredButtons[INCORRECT] = -1;
    }

    private void setupAnswerButtons(int numAnswerButtons, View view){
        mAnswerButtons = new AnswerButton[numAnswerButtons];
        int[] buttonsResources = {R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4,
                                    R.id.answer5, R.id.answer6};
        for(int i =0; i < numAnswerButtons; i++){
            mAnswerButtons[i] = (AnswerButton) view.findViewById(buttonsResources[i]);
            //wszystkie przyciski są domyślnie niewidoczne, trzeba więc ustawić widoczność dla używanych przycisków
            mAnswerButtons[i].setVisibility(View.VISIBLE);
            final int position = i;
            mAnswerButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO if(mCanAnswer
                    Log.d(LOG_TAG, "AnswerButton Click " + position);
                    if(mCanAnswer){
                        toAnswer(mAnswerButtons[position].getText().toString());
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //dopiero po załadowaniu i wyświetleniu fragmentu można odpowiadać
        mCanAnswer = true;
    }

    @Override
    public void toAnswer(String answer) {
        mCanAnswer = false;
        boolean correct = mExerciseManager.checkAnswer(answer);
        int correctButtonNumber;
        if(correct) {
            correctButtonNumber = findButton(answer);
        }else {
            String correctAnswer = mExerciseManager.getCorrectAnswer();
            correctButtonNumber = findButton(correctAnswer);
            //zaznaczenie złej odpowiedzi
            int incorrectButtonNumber = findButton(answer);
            mColoredButtons[INCORRECT] = incorrectButtonNumber;
            AnswerButton incorrectButton = mAnswerButtons[incorrectButtonNumber];
            incorrectButton.changeState(AnswerButton.INCORRECT);
        }
        //ustawianie dobrej odpowiedzi
        mColoredButtons[CORRECT] = correctButtonNumber;
        AnswerButton correctButton = mAnswerButtons[correctButtonNumber];
        correctButton.changeState(AnswerButton.CORRECT);

        //TODO wstawić animację
        //wysyłamy wiadomość do aktywności aby zaktualizowała pasek postepu
        mActivityCallback.onAnswer(correct);

        //wyświetlamy przycisk który pozwoli przejść do następnego pytania
        setNextButtonVisibility(View.VISIBLE);
    }

    /** Metoda ustawiająca widocznośc przycisku do przechodzenia do następnego pytania
     * Gdy przycisk pojawia się na ostatnim pytaniu jego tekst zmienia się na Zakończ
     * @param visibility widoczność przycisku która ma zostać ustawiona
     */
    private void setNextButtonVisibility(int visibility){
        if(visibility == View.VISIBLE){
            if(!mExerciseManager.hasNextQuestion()){
                mNextButton.setText(getString(R.string.finish));
            }
        }
        mNextButton.setVisibility(visibility);
    }

    private int findButton(String text){
        for(int i=0; i< mAnswerButtons.length; i++){
            if(mAnswerButtons[i].getText().equals(text)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void showQuestion() {
        setQuestion();
        setAnswers();
        updateSpeechPlayer();
    }

    private void  setQuestion(){
        String question = mExerciseManager.getQuestion();
        if(question != null){
            mWordTextView.setText(question);
            //TODO ustawić przycisk od mówienia
            //TODO zrobić animację
        }
    }

    private void setAnswers(){
        String[] answers = mExerciseManager.getAnswers();
        for(int i = 0 ; i < answers.length; i++){
            mAnswerButtons[i].setText(answers[i]);
        }
    }
}
