package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dyszlewskiR.edu.scientling.R;

public class TimePickerPreference extends Preference {
    private final int LAYOUT = R.layout.time_preference;

    private int mHour = 0;
    private int mMinute = 0;
    private boolean mIs24 = true;
    private TextView mValueTextView;
    private SharedPreferences mSharedPreference;

    public TimePickerPreference(Context context) {
        super(context);
        init(context);

    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        setupTime();
        setLayoutResource(LAYOUT);
    }

    private void setupTime() {
        String time = mSharedPreference.getString(getKey(), "00:00");
        String[] timeParts = time.split(":");
        mHour = Integer.valueOf(timeParts[0]);
        mMinute = Integer.valueOf(timeParts[1]);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        mValueTextView = (TextView) view.findViewById(R.id.preference_value);
        if (mValueTextView != null) {
            mValueTextView.setText(getTimeText(mHour, mMinute));
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        setupTime();
        TimePickerDialog dialog = new TimePickerDialog(mHour, mMinute, getContext());
        dialog.show();
    }

    private void savePreference(String value) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(getKey(), value);
        editor.apply();
    }

    private String getTimeText(int hours, int minutes) {
        return String.format("%02d:%02d", hours, minutes);
    }

    private class TimePickerDialog extends Dialog {
        private LinearLayout mLayout;
        private TimePicker mTimePicker;
        private Button mDoneButton;

        public TimePickerDialog(int hour, int minute, Context context) {
            super(context);
            initDialog(hour, minute, context);
        }

        private void initDialog(int hour, int minute, Context context) {
            setContentView(createDialogView(context));
        }

        private View createDialogView(Context context) {
            mLayout = new LinearLayout(context);
            mLayout.setOrientation(LinearLayout.VERTICAL);

            mTimePicker = createTimePicker(context);
            mLayout.addView(mTimePicker);

            mDoneButton = createDoneButton(context);
            setButtonListener();
            mLayout.addView(mDoneButton);

            return mLayout;
        }

        private TimePicker createTimePicker(Context context) {
            TimePicker timePicker = new TimePicker(context);
            timePicker.setCurrentHour(mHour); //TODO zrobić dla nowszych wersji
            timePicker.setCurrentMinute(mMinute);
            timePicker.setIs24HourView(mIs24);
            return timePicker;
        }

        private Button createDoneButton(Context context) {
            Button button = new Button(context, null, android.R.attr.borderlessButtonStyle);
            button.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            button.setGravity(Gravity.CENTER_HORIZONTAL);
            button.setText(context.getString(android.R.string.ok));

            return button;
        }

        private void setButtonListener() {
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHour = mTimePicker.getCurrentHour(); //TODO zrobić dla nowszych wersji
                    mMinute = mTimePicker.getCurrentMinute();
                    String time = getTimeText(mHour, mMinute);
                    mValueTextView.setText(time);
                    savePreference(time);
                    dismiss();
                }
            });
        }

    }

}
