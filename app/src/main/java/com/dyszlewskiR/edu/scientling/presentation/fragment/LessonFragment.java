package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class LessonFragment extends Fragment {

    private EditText mNumberTextView;
    private EditText mNameTextView;
    private Button mSaveButton;

    private VocabularySet mSet;

    private DataManager mDataManager;
    private Lesson mLesson;
    private boolean mEdit;

    public LessonFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        mDataManager = ((LingApplication) getActivity().getApplication()).getDataManager(); //TODO sprawdzić czy nie zrobić tego w innym miejscu
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mSet = intent.getParcelableExtra("set");
        mLesson = intent.getParcelableExtra("item");
        mEdit = intent.getBooleanExtra("edit", false);
        if (mSet == null && mLesson != null) {
            //mSet = mLesson.getSet();
            mSet = new VocabularySet(mLesson.getSetId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mNumberTextView = (EditText) view.findViewById(R.id.lesson_number_edit_text);
        mNameTextView = (EditText) view.findViewById(R.id.lesson_name_edit_text);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
    }

    private void setListeners() {
        mNumberTextView.addTextChangedListener(new CheckNumber());

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lesson lesson = saveAndReturnLesson();
                setResultAndFinish(lesson);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mLesson != null) {
            mNameTextView.setText(mLesson.getName());
            mNumberTextView.setText(String.valueOf(mLesson.getNumber()));

        }
    }

    private Lesson saveAndReturnLesson() {
        if (mLesson == null) {
            mLesson = new Lesson();
        }
        mLesson.setName(String.valueOf(mNameTextView.getText()));
        mLesson.setNumber(Long.parseLong(String.valueOf(mNumberTextView.getText())));
        //mLesson.setSet(mSet);
        mLesson.setSetId(mSet.getId());
        if (mEdit) {
            mDataManager.updateLesson(mLesson);
        } else {
            long id = mDataManager.saveLesson(mLesson);
            if (id < 0) {
                //TODO wyświetlenie komunikatu
            } else {
                mLesson.setId(id);
            }
        }
        return mLesson;
    }

    private void setResultAndFinish(Lesson lesson) {
        Intent intent = new Intent();
        intent.putExtra("result", lesson);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    class CheckNumber implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (Integer.parseInt(s.toString()) < 1) {
                    s.replace(0, s.length(), "1");
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
