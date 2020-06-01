package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;

public class DefinitionDialog extends DialogFragment {
    private EditText mDefinitionEditText;
    private EditText mDefinitionTranslationEditText;
    private Button mOkButton;
    private Button mClearButton;
    private Callback mCallback;
    private Definition mDefinition;

    public interface Callback {
        void onDefinitionDialogOk(Definition definition);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setDefinition(Definition definition) {
        mDefinition = definition;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_definition, container, false);
        setupControls(view);
        setValues();
        setListeners();
        getDialog().setTitle(getString(R.string.definition));//TODO można zmienić na wstawianie definicji
        return view;
    }

    private void setupControls(View view) {
        mDefinitionEditText = (EditText) view.findViewById(R.id.definition_edit_text);
        mDefinitionTranslationEditText = (EditText) view.findViewById(R.id.translation_edit_text);
        mClearButton = (Button) view.findViewById(R.id.clear_button);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setValues() {
        if (mDefinition != null) {
            mDefinitionEditText.setText(mDefinition.getContent());
            mDefinitionTranslationEditText.setText(mDefinition.getTranslation());
        }
    }

    private void setListeners() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DefinitionDialog", "Click");
                if (mCallback != null) {
                    String definitionText = mDefinitionEditText.getText().toString();
                    String translationText = mDefinitionTranslationEditText.getText().toString();
                    if (definitionText.isEmpty() && translationText.isEmpty()) {
                        mCallback.onDefinitionDialogOk(null);
                    } else {
                        Definition definition = new Definition();
                        definition.setContent(definitionText);
                        definition.setTranslation(translationText);
                        mCallback.onDefinitionDialogOk(definition);
                    }
                }
                dismiss();
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefinitionEditText.setText("");
                mDefinitionTranslationEditText.setText("");
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mCallback = null;
        super.onDismiss(dialogInterface);
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
