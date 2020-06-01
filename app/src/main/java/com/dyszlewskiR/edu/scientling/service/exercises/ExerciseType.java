package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.R;

public enum ExerciseType {
    CHOOSING(0, R.string.choosing_exercise),
    WRITING(1, R.string.writing_exercise),
    KNOW(2, R.string.know_exercise);

    ExerciseType(int position, int nameResource){
        mPosition = position;
        mNameResource = nameResource;
    }
    private int mPosition;
    private int mNameResource;

    public int getPosition(){return mPosition;}
    public int getNameResource(){return mNameResource;}

    public static ExerciseType get(int position){
        for(ExerciseType type : values()){
            if(type.getPosition() == position){
                return type;
            }
        }
        return null;
    }
}
