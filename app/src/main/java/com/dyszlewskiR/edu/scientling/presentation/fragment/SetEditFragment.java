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
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.file.FileNameCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.LanguageDialog;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

public class SetEditFragment extends Fragment implements LanguageDialog.Callback {

    private final int LAYOUT_RESOURCE = R.layout.fragment_set_edit;
    private final int ADD_REQUEST = 7233;

    private final int L1 = 0;
    private final int L2 = 1;
    
    private EditText mNameEditText;
    private Button mL2Button;
    private Button mL1Button;
    private Button mOkButton;

    private VocabularySet mSet;
    private boolean mEdit;

    private int mOpenedDialog;

    public SetEditFragment() {
        mSet = new VocabularySet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        loadData();
        return view;
    }

    private void setupControls(View view) {
        mNameEditText = (EditText) view.findViewById(R.id.name_edit_text);
        mL2Button = (Button) view.findViewById(R.id.l2_button);
        mL1Button = (Button) view.findViewById(R.id.l1_button);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setListeners() {
        mL2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog(L2);
            }
        });
        mL1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageDialog(L1);
            }
        });
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    VocabularySet set = getSet();
                    saveSet(set);
                    setResultAndFinish(set);
                }
            }
        });
    }

    private void openLanguageDialog(int language) {
        LanguageDialog dialog = new LanguageDialog();
        dialog.setCallback(this);
        dialog.show(getFragmentManager(), "LanguageDialog");
        mOpenedDialog = language;
    }

    private boolean validate() {
        boolean correct = true;
        if (mNameEditText.getText().toString().equals("")) {
            mNameEditText.setError(getString(R.string.not_empty_field));
            correct = false;
        } else {
            mNameEditText.setError(null);
        }
        if (mL1Button.getText().toString().equals("")) {
            mL1Button.setError(getString(R.string.not_empty_field));
            correct = false;
        } else {
            mL1Button.setError(null);
        }
        if (mL2Button.getText().toString().equals("")) {
            mL2Button.setError(getString(R.string.not_empty_field));
            correct = false;
        } else {
            mL2Button.setError(null);
        }
        return correct;
    }

    /**
     * Zapisuje nowy zestaw lub aktualizuje już istniejący
     * TODO można przenieść zapisywanie do AsyncTask
     *
     * @param set zestaw który ma być zapisany lub zaktualizowany
     * @return numer identyfikacyjny zapisanego zestawu
     */
    private long saveSet(VocabularySet set) {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        if (mEdit) {
            dataManager.updateSet(set);
            return set.getId();
        } else { //!mEdit
            long setId = dataManager.saveSet(set);
            set.setId(setId);
            saveDefaultLesson(set, dataManager);
            return setId;
        }
    }

    private void saveDefaultLesson(VocabularySet set, DataManager dataManager) {
        Lesson defaultLesson = new Lesson();
        defaultLesson.setName("");
        defaultLesson.setNumber(Constants.DEFAULT_LESSON_NUMBER);
        //defaultLesson.setSet(set);
        defaultLesson.setSetId(set.getId());
        long lessonId = dataManager.saveLesson(defaultLesson);
    }

    private void setResultAndFinish(VocabularySet item) {
        Intent intent = new Intent();
        intent.putExtra("result", item);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private VocabularySet getSet() {
        VocabularySet set = mSet;
        set.setName(mNameEditText.getText().toString());
        String catalog = FileNameCreator.getCatalogName(set.getName(), getContext());
        set.setCatalog(catalog);
        return set;
    }

    private void loadData() {
        Intent intent = getActivity().getIntent();
        //VocabularySet set = intent.getParcelableExtra("item");
        long setId = intent.getLongExtra("setId",-1);
        if(setId > 0){
            DataManager dataManager = ((LingApplication)getActivity().getApplication()).getDataManager();
            mSet = dataManager.getSetById(setId);
            Language l1 = dataManager.getLanguageById(mSet.getLanguageL1().getId());
            Language l2 = dataManager.getLanguageById(mSet.getLanguageL2().getId());
            mSet.setLanguageL1(l1);
            mSet.setLanguageL2(l2);
            mEdit = true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mEdit) {
            setData();
        }
    }

    private void setData() {
        mNameEditText.setText(mSet.getName());
        setL2ButtonText(mSet.getLanguageL2().getName());
        setL1ButtonText(mSet.getLanguageL1().getName());
    }

    private void setL2ButtonText(String text) {
        mL2Button.setText(ResourceUtils.getString(text, getContext()));
    }

    private void setL1ButtonText(String text) {
        mL1Button.setText(ResourceUtils.getString(text, getContext()));
    }

    @Override
    public void onLanguageOk(Language language) {
        if (mOpenedDialog == L1) {
            setL1ButtonText(language.getName());
            mSet.setLanguageL1(language);
        } else if (mOpenedDialog == L2) {
            setL2ButtonText(language.getName());
            mSet.setLanguageL2(language);
        }
    }
}
