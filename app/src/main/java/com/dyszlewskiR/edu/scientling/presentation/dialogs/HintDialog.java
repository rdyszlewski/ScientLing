package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;

public class HintDialog extends DialogFragment {
    private final int LAYOUT_RESOURCE = R.layout.dialog_hint;

    private EditText mHintEditText;
    private Button mOkButton;
    private Hint mHint;
    private boolean mEdit;

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setHint(Hint hint) {
        mHint = hint;
        mEdit = true;
    }

    public interface Callback {
        void onAddHintOk(Hint hint);

        void onEditHintOk(Hint hint);
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
        setValues();

        getDialog().setTitle(getString(R.string.hint));

        return view;
    }

    private void setupControls(View view) {
        mHintEditText = (EditText) view.findViewById(R.id.hint_edit_text);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setListeners() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (mCallback != null) {
                        Hint hint = new Hint();
                        hint.setContent(mHintEditText.getText().toString());
                        if (mEdit) {
                            mCallback.onEditHintOk(hint);
                        } else {
                            mCallback.onAddHintOk(hint);
                        }
                    }
                    dismiss();
                } else {
                    mHintEditText.setError(getString(R.string.not_empty_field));
                }

            }
        });
    }

    private void setValues() {
        if (mHint != null) {
            mHintEditText.setText(mHint.getContent());
        }
    }

    /**
     * Metoda sprawdzająca czy wprowadzone dane są poprawne.
     * -- pole nie może być puste
     *
     * @return
     */
    private boolean validate() {
        return !mHintEditText.getText().toString().isEmpty();
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
