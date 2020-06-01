package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseType;

public class ExercisesListDialogFragment extends DialogFragment{
    private int mCurrentExercise;
    private Callback mCallback;

    public interface Callback{
        void onClick(int position);
    }

    public void init(int currentExercise, Callback callback){
        mCurrentExercise = currentExercise;
        mCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final ExerciseType[] exerciseTypes = ExerciseType.values();
        String[] exercisesNames = new String[exerciseTypes.length];
        for(int i=0; i<exerciseTypes.length; i++){
            exercisesNames[i] = getActivity().getString(exerciseTypes[i].getNameResource());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choosing_exercise));
        builder.setSingleChoiceItems(exercisesNames, mCurrentExercise, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mCallback != null){
                    mCallback.onClick(exerciseTypes[which].getPosition());
                }
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface){
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
