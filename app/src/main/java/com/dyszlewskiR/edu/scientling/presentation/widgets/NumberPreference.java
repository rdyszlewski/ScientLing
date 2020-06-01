package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;

public class NumberPreference extends Preference {

    private final int LAYOUT = R.layout.edit_text_preference_with_value;

    private int mMinValue = 2;
    private int mMaxValue;
    private int mCurrentValue;
    private SharedPreferences mSharedPreferences;

    private TextView mValueTextView;

    public NumberPreference(Context context) {
        super(context);
        init(context);
    }

    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stringValue = mSharedPreferences.getString(getKey(), String.valueOf(mMinValue));
        mCurrentValue = Integer.valueOf(stringValue);
    }

    public void setMaxValue(int max) {
        mMaxValue = max;
        setLayoutResource(LAYOUT);
    }

    public void setMinValue(int min) {
        if (mCurrentValue < min) {
            mCurrentValue = min;
        }
        mMinValue = min;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        mValueTextView = (TextView) view.findViewById(R.id.preference_value);
        if (mValueTextView != null) {
            mValueTextView.setText(String.valueOf(mCurrentValue));
        }
    }

    @Override
    public void onClick() {
        new NumberPickerDialog(getContext()).show();
    }


    private void savePreference(int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getKey(), String.valueOf(value));
        editor.apply();
    }

    private class NumberPickerDialog extends Dialog {

        private LinearLayout mLayout;
        private NumberPicker mNumberPicker;
        private Button mDoneButton;

        public NumberPickerDialog(Context context) {
            super(context);
            setContentView(createDialogView(context));
            setListners();
        }

        public View createDialogView(Context context) {
            mLayout = new LinearLayout(context);
            mLayout.setOrientation(LinearLayout.VERTICAL);

            mNumberPicker = new NumberPicker(context);
            mNumberPicker.setMinValue(mMinValue);
            mNumberPicker.setMaxValue(mMaxValue);
            mNumberPicker.setValue(mCurrentValue);

            mDoneButton = new Button(getContext(), null, android.R.attr.borderlessButtonStyle);
            mDoneButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            mDoneButton.setGravity(Gravity.CENTER_HORIZONTAL);
            mDoneButton.setText(getContext().getString(android.R.string.ok));

            mLayout.addView(mNumberPicker);
            mLayout.addView(mDoneButton);

            return mLayout;
        }

        private void setListners() {
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentValue = mNumberPicker.getValue();
                    savePreference(mCurrentValue);
                    mValueTextView.setText(String.valueOf(mCurrentValue));
                    dismiss();
                }
            });
        }
    }

}
