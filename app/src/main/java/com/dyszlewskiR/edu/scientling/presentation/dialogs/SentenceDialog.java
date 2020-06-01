package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;


public class SentenceDialog extends DialogFragment {

    private EditText mSentenceEditText;
    private EditText mSentenceTranslationEditText;
    private Button mokButton;
    private Button mClearButton;
    private Callback mCallback;
    private Sentence mSentence;

    private boolean mIsEdit;

    public interface Callback {
        void onSentenceCreateOk(Sentence sentence);

        void onSentenceEditOk(Sentence sentence);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setSentence(Sentence sentence) {
        mSentence = sentence;
        mIsEdit = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sentence, container, false);
        setupControls(view);
        setValues();
        setListeners();
        getDialog().setTitle(getString(R.string.sentence));
        return view;
    }

    private void setupControls(View view) {
        mSentenceEditText = (EditText) view.findViewById(R.id.sentence_edit_text);
        mSentenceTranslationEditText = (EditText) view.findViewById(R.id.translation_edit_text);
        mClearButton = (Button) view.findViewById(R.id.clear_button);
        mokButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setValues() {
        if (mSentence != null) {
            mSentenceEditText.setText(mSentence.getContent());
            mSentenceTranslationEditText.setText(mSentence.getTranslation());
        }
    }

    private void setListeners() {
        setOkButtonListener();
        setClearButtonListener();
    }

    private void setOkButtonListener() {
        mokButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SentenceDialog", "OKClick");
                if (mCallback != null) {
                    String sentenceText = mSentenceEditText.getText().toString();
                    String translationText = mSentenceTranslationEditText.getText().toString();
                    if (validate()) {
                        returnSentenceAndDismiss(sentenceText, translationText);
                    } else {
                        mSentenceEditText.setError(getString(R.string.not_empty_field));
                    }
                }
            }
        });
    }

    private void returnSentenceAndDismiss(String sentenceText, String translationText) {
        Sentence sentence = new Sentence();
        sentence.setContent(sentenceText);
        sentence.setTranslation(translationText);
        if (mIsEdit) {
            mCallback.onSentenceEditOk(sentence);
        } else {
            mCallback.onSentenceCreateOk(sentence);
        }
        dismiss();
    }

    private void setClearButtonListener() {
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSentenceEditText.setText("");
                mSentenceTranslationEditText.setText("");
            }
        });
    }

    /**
     * Metoda sprawdzająca poprawność wprowadzonych danych.
     * - zawartość zdania nie może być pusta
     *
     * @return
     */
    private boolean validate() {
        return !mSentenceEditText.getText().toString().isEmpty();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mCallback = null;
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onDestroyView(){
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
