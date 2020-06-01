package com.dyszlewskiR.edu.scientling.service.speech.speechToText;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Klasa służąca do rozpoznawania mowy.
 * <p>
 * Rozpoznawanie mowy w systemach Android wspiera mniej języków niż synteza mowy na podstawie tekstu.
 * Rozpoznawanie mowy umożliwa alternatywny sposób komnunikacji z programem, przydatny zwłaszcza
 * w przypakach, kiedy użytkownik nie moze skorzystać z tradycyjnydch metod sterowaniem programem.
 * Aby używać funkcji rozpoznawania mowy, należy w zadeklarować w manifeście uprawnienie
 * android.oermission.RECORD_AUDIO.
 */

public class SpeechToText {
    private final String TAG = "SpeechToText";

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Context mContext;
    private String mLanguage;

    public SpeechToText(Context context, String language, ISpeechRecognitionResult callback) {
        mContext = context;
        mLanguage = language;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(callback));
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //TODO zobaczyc co oznaczaja te właściwośći
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguage);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());

    }

    public void setLanguage(String languageCode){
        mLanguage = languageCode;
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguage);
    }

    public void startListening() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    private class SpeechRecognitionListener implements RecognitionListener {
        private ISpeechRecognitionResult mCallback;

        public SpeechRecognitionListener(ISpeechRecognitionResult callback) {
            mCallback = callback;
        }

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); //TODO ciekawy błąd java.util.ArrayList cannot be cast to java.lang.Stirng[]
            if (matches != null) {
                for (int i = 0; i < matches.size(); ++i) {
                    Log.d(TAG, "onResult " + i + " : " + matches.get(i));
                }
            }
            mCallback.receiveRecognitionResult(matches != null ? matches.toArray(new String[0]) : new String[0]);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }
}
