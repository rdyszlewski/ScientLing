package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

public class CategoryFragment extends Fragment {

    private EditText mNameEditText;
    private Button mSaveButton;
    private RelativeLayout mLayout;
    private boolean mEdit;
    private Category mCategory;
    private DataManager mDataManager;

    public CategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        getData();
    }

    private void getData() {
        Intent data = getActivity().getIntent();
        mCategory = data.getParcelableExtra("item");
        mEdit = data.getBooleanExtra("edit", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mNameEditText = (EditText) view.findViewById(R.id.category_name_edit_text);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
    }

    private void setListeners() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });
    }

    private void saveCategory() {
        if (validate()) {
            Category category = getCategory();
            if (mEdit) {
                mDataManager.updateCategory(category);
            } else {
                long categoryId = mDataManager.saveCategory(category);
                category.setId(categoryId);
            }
            setResultAndFinish(category);
        }
    }

    private Category getCategory() {
        if (mCategory == null) {
            mCategory = new Category();
        }
        mCategory.setName(mNameEditText.getText().toString());
        return mCategory;
    }

    private boolean validate() {
        if (mNameEditText.getText().toString().trim().length() > 0) { //sprawdzamy czy stawiona wartość nie składa się tylko ze znaków białych
            Category category = mDataManager.getCategoryByName(mNameEditText.getText().toString()); //sprawdzamy czy podana kategoria istnieje już w bazie
            if (category == null) {
                return true;
            } else {
                Snackbar.make(mNameEditText, getString(R.string.category_exist), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            mNameEditText.setError(getString(R.string.not_empty_field));
        }
        return false;
    }

    private void setResultAndFinish(Category resultCategory) {
        Intent data = new Intent();
        data.putExtra("result", resultCategory);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mCategory != null && mEdit) {
            mNameEditText.setText(mCategory.getName());
        }
    }
}
