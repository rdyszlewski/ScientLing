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
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class HintFragment extends Fragment {

    private EditText mContentEditText;
    private Button mOkButton;

    private Hint mHint;
    private boolean mEdit;

    private DataManager mDataManager;

    public HintFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mHint = intent.getParcelableExtra("item");
        mEdit = intent.getBooleanExtra("edit", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hint, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mContentEditText = (EditText) view.findViewById(R.id.content_edit_text);
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

    private void setResultAndFinish() {
        if (validate()) {
            Hint hint = getHint();
            Intent intent = new Intent();
            intent.putExtra("result", hint);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private boolean validate() {
        if (mContentEditText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            mContentEditText.setError(getContext().getString(R.string.not_empty_field));
        }
        return false;
    }

    private Hint getHint() {
        Hint hint;
        if (mHint == null) {
            hint = new Hint();
        } else {
            hint = mHint;
        }
        hint.setContent(mContentEditText.getText().toString());
        return hint;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setValues();
    }

    private void setValues() {
        if (mEdit && mHint != null) {
            mContentEditText.setText(mHint.getContent());
        }
    }
}
