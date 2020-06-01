package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;

/**
 * A placeholder fragment containing a simple view.
 */
public class SentenceDetailFragment extends Fragment {

    private EditText mSentenceContent;
    private EditText mSentenceTranslation;
    private Button mOkButton;

    private Sentence mSentence;
    private boolean mEdit;

    public SentenceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mSentence = intent.getParcelableExtra("item");
        mEdit = intent.getBooleanExtra("edit", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentence_detail, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mSentenceContent = (EditText) view.findViewById(R.id.content_edit_text);
        mSentenceTranslation = (EditText) view.findViewById(R.id.translation_edit_text);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setListeners() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish();
            }
        });
    }

    private boolean validate() {
        if (mSentenceContent.getText().toString().trim().length() != 0) {
            return true;
        } else {
            mSentenceContent.setError(getString(R.string.not_empty_field));
        }
        return false;
    }

    private void setResultAndFinish() {
        if (validate()) {
            Sentence sentence = getSentence();
            Intent result = new Intent();
            result.putExtra("result", sentence);
            getActivity().setResult(Activity.RESULT_OK, result);
            getActivity().finish();
        }
    }

    private Sentence getSentence() {
        Sentence sentence;
        if (mSentence == null) {
            sentence = new Sentence();
        } else {
            sentence = mSentence;
        }
        sentence.setContent(mSentenceContent.getText().toString());
        if (mSentenceTranslation.getText().toString().trim().length() != 0) {
            sentence.setTranslation(mSentenceTranslation.getText().toString());
        }
        return sentence;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setValues();
    }

    private void setValues() {
        if (mEdit && mSentence != null) {
            mSentenceContent.setText(mSentence.getContent());
            if (mSentence.getTranslation() != null) {
                mSentenceTranslation.setText(mSentence.getTranslation());
            }
        }
    }
}
