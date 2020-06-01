package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.LanguagesAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SetFragment extends Fragment {

    private DataManager mDataManager;

    private EditText mNameEditText;
    private Spinner mL2Spinner;
    private Spinner mL1Spinner;
    private Button mSaveButton;

    private List<Language> mLanguages;
    private VocabularySet mSet;
    private boolean mEdit;

    public SetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        mDataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mLanguages = mDataManager.getLanguages();
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mSet = intent.getParcelableExtra("item");
        mEdit = intent.getBooleanExtra("edit", false);
        if (mSet == null) {
            mSet = new VocabularySet();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mNameEditText = (EditText) view.findViewById(R.id.set_name_edit_text);
        mL2Spinner = (Spinner) view.findViewById(R.id.l2_spinner);
        mL1Spinner = (Spinner) view.findViewById(R.id.l1_spinner);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
    }

    private void setListeners() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VocabularySet set = getSet();
                boolean result = saveSet(set);
                if (result) {
                    setResultAndFinish(set);
                } else {
                    showAlert();
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LanguagesAdapter adapter = new LanguagesAdapter(getActivity(), R.layout.item_language, mLanguages);
        mL1Spinner.setAdapter(adapter);
        mL2Spinner.setAdapter(adapter);

        if (mSet != null) {
            mNameEditText.setText(mSet.getName());
            if (mSet.getLanguageL1() != null) {
                int positionL1 = adapter.getPosition(mSet.getLanguageL1().getName());
                mL1Spinner.setSelection(positionL1);
            }
            if (mSet.getLanguageL2() != null) {
                int positionL2 = adapter.getPosition(mSet.getLanguageL2().getName());
                mL2Spinner.setSelection(positionL2);
            }
        }
    }

    private VocabularySet getSet() {
        mSet.setName(String.valueOf(mNameEditText.getText()));
        mSet.setLanguageL2(mLanguages.get(mL2Spinner.getSelectedItemPosition()));
        mSet.setLanguageL1(mLanguages.get(mL1Spinner.getSelectedItemPosition()));
        return mSet;
    }

    private boolean saveSet(VocabularySet set) {
        if (mEdit) {
            mDataManager.updateSet(set);
            return true;
        } else {
            long id = mDataManager.saveSet(set);
            if (id > 0) {
                set.setId(id);
                return true;
            }
        }
        return false;
    }

    private void setResultAndFinish(VocabularySet set) {
        Intent data = new Intent();
        data.putExtra("result", set);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    private void showAlert() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage(getResources().getString(R.string.set_save_failed));
        alertBuilder.setNeutralButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }


}
