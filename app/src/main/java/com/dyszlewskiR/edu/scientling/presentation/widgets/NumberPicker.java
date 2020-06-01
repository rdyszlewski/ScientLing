package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dyszlewskiR.edu.scientling.R;

/**
 * Created by Razjelll on 10.04.2017.
 */

public class NumberPicker extends LinearLayout {

    private final int LAYOUT_RESOURCE = R.layout.widget_number_picker;
    private final int MAX_NUMBER = 30;
    private final int MIN_NUMBER = 2;

    private Button mMinusButton;
    private Button mPlusButton;
    private EditText mValueEditText;

    private int mMaxNumber;
    private int mMinNumber;
    private int mValue;

    public NumberPicker(Context context) {
        super(context);
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), LAYOUT_RESOURCE, this);
        mMinNumber = MIN_NUMBER;
        mMaxNumber = MAX_NUMBER;
        mValue = MIN_NUMBER;
        setupControls();
        setListeners();
        mValueEditText.setText(String.valueOf(mValue));
    }

    private void setupControls() {
        mMinusButton = (Button) findViewById(R.id.minus_button);
        mPlusButton = (Button) findViewById(R.id.plus_button);
        mValueEditText = (EditText) findViewById(R.id.number_edit_text);
    }

    private void setListeners() {
        setValueEditTextListener();
        setMinusButtonListener();
        setPlusButtonListener();
    }

    private void setValueEditTextListener() {
        mValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    int number = Integer.parseInt(s.toString());
                    if (number > mMaxNumber) {
                        mValueEditText.setText(String.valueOf(mMaxNumber));
                    } else if (number < mMinNumber) {
                        mValueEditText.setText(String.valueOf(mMinNumber));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setMinusButtonListener() {
        mMinusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousValue = Integer.parseInt(mValueEditText.getText().toString());
                int value = previousValue - 1;
                if (value >= mMinNumber) {
                    mValueEditText.setText(String.valueOf(value));
                }
            }
        });
    }

    private void setPlusButtonListener() {
        mPlusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousValue = Integer.parseInt(mValueEditText.getText().toString());
                int value = previousValue + 1;
                if (value >= mMinNumber) {
                    mValueEditText.setText(String.valueOf(value));
                }
            }
        });
    }

    public void setValue(int value) {
        mValue = value;
        mValueEditText.setText(String.valueOf(value));
    }

    public int getValue() {
        if (mValueEditText.getText().length() == 0) {
            return mMinNumber;
        }
        int number = Integer.parseInt(mValueEditText.getText().toString());
        if (number > mMaxNumber) {
            number = mMaxNumber;
        } else if (number < mMinNumber) {
            number = mMinNumber;
        }
        return number;
    }

    public void setMax(int value) {
        mMaxNumber = value;
    }

    public void setMin(int value) {
        mMinNumber = value;
    }
}
