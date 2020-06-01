package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;

public class SaveWordDialog extends Dialog {
    private final int LAYOUT_RESOURCE = R.layout.save_word_dialog;

    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private Button mErrorButton;

    private Context mContext;

    private String mTempMessageText;

    public SaveWordDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getName(), "onCreate");
        setContentView(LAYOUT_RESOURCE);
        setupControls();
        setListeners();
        setValues();
        setTitle(mContext.getString(R.string.saving));
    }

    private void setupControls() {
        mMessageTextView = (TextView) findViewById(R.id.message_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorButton = (Button) findViewById(R.id.error_ok_button);

    }

    private void setListeners() {
        mErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nie wykonujemy żadnych akcji, tylko zamykamy okno dialogowe
                dismiss();
            }
        });
    }

    private void setValues() {
        if (mTempMessageText != null) {
            mMessageTextView.setText(mTempMessageText);
            mTempMessageText = null;
        } else {
            mMessageTextView.setText(mContext.getString(R.string.word_saving));
        }
    }

    public void setError(String message) {
        mErrorButton.setVisibility(View.VISIBLE);
        mMessageTextView.setText(message);
    }

    public void setText(String message) {
        if (mMessageTextView != null) {
            mMessageTextView.setText(message);
        } else {
            mTempMessageText = message;
        }
    }


}
