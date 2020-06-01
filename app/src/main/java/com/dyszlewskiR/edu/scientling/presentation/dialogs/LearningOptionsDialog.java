package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.LearningListActivity;
import com.dyszlewskiR.edu.scientling.presentation.adapters.CategorySpinnerAdapter;
import com.dyszlewskiR.edu.scientling.presentation.adapters.LessonSpinnerAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.presentation.widgets.NumberPicker;

import java.util.List;


public class LearningOptionsDialog extends DialogFragment {

    private final int LAYOUT_RESOURCE = R.layout.dialog_learning_option;
    private final int LESSON_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int CATEGORY_ADAPTER_RESOURCE = R.layout.item_simple;

    private long mSetId;
    private List<Lesson> mLessons;
    private List<Category> mCategories;

    private Spinner mLessonSpinner;
    private Spinner mCategorySpinner;
    private Spinner mDifficultSpinner;
    private NumberPicker mWordsNumberPicker;
    private Button mStartButton;

    private LessonSpinnerAdapter mLessonsAdapter;
    private CategorySpinnerAdapter mCategoriesAdapter;

    public void setSetId(long setId) {
        mSetId = setId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        getData();
        setAdapters();
        return view;
    }

    public void getData() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mLessons = dataManager.getLessons(new VocabularySet(mSetId));
        Lesson anyLesson = new Lesson(-1);
        mLessons.add(0, anyLesson);
        mCategories = dataManager.getCategories();
        Category anyCategory = new Category(-1);
        mCategories.add(0, anyCategory);

    }

    private void setupControls(View view) {
        mLessonSpinner = (Spinner) view.findViewById(R.id.lesson_spinner);
        mCategorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        mDifficultSpinner = (Spinner) view.findViewById(R.id.difficult_spinner);
        mWordsNumberPicker = (NumberPicker) view.findViewById(R.id.words_number_picker);
        mStartButton = (Button) view.findViewById(R.id.start_button);

        mWordsNumberPicker.setMax(Constants.MAX_WORD_LEARNING);
        mWordsNumberPicker.setMin(Constants.MIN_WORD_LEARNING);
    }

    private void setListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearningListActivity.class);
                intent.putExtra("set", mSetId);
                long lessonId = mCategoriesAdapter.getItemId(mLessonSpinner.getSelectedItemPosition());
                if (lessonId > 0) {
                    intent.putExtra("lesson", lessonId);
                }
                long categoryId = mCategoriesAdapter.getItemId(mCategorySpinner.getSelectedItemPosition());
                if (categoryId > 0) {
                    intent.putExtra("category", categoryId);
                }
                if (mDifficultSpinner.getSelectedItemPosition() != 0) {
                    intent.putExtra("difficult", mDifficultSpinner.getSelectedItemPosition());
                }
                intent.putExtra("order", Preferences.getOrderLearning(getActivity()));
                intent.putExtra("limit", mWordsNumberPicker.getValue());
                getActivity().startActivity(intent);
                dismiss();
            }
        });
    }

    private void setAdapters() {
        mLessonsAdapter = new LessonSpinnerAdapter(getActivity(), LESSON_ADAPTER_RESOURCE, mLessons);
        mLessonSpinner.setAdapter(mLessonsAdapter);

        mCategoriesAdapter = new CategorySpinnerAdapter(getActivity(), CATEGORY_ADAPTER_RESOURCE, mCategories);
        mCategorySpinner.setAdapter(mCategoriesAdapter);

        String[] difficultLevels = {getActivity().getString(R.string.lack), "1", "2", "3", "4", "5"};
        ArrayAdapter<String> difficultAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_difficult_spinner, difficultLevels);
        mDifficultSpinner.setAdapter(difficultAdapter);
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
