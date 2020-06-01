package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.FlashcardActivity;


public class FlashcardListDialogFragment extends DialogFragment {

    private long mSetId;
    private int mLimit;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mSetId = arguments.getLong("set");
        mLimit = arguments.getInt("limit");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(R.array.flashcard_mode, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), FlashcardActivity.class);
                intent.putExtra("set", mSetId);
                intent.putExtra("type", which);
                intent.putExtra("limit", mLimit);
                getActivity().startActivity(intent);
                dismiss();
            }
        });
        return builder.create();
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
