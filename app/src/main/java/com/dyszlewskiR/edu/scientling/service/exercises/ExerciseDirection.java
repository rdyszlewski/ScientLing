package com.dyszlewskiR.edu.scientling.service.exercises;

public enum ExerciseDirection {
    L1_TO_L2(0),
    L2_TO_L1(1);

    private int mPosition;
    ExerciseDirection (int position){
        mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }

    public static ExerciseDirection get(int position){
        for(ExerciseDirection direction : values()){
            if(direction.getPosition()== position){
                return direction;
            }
        }
        return null;
    }

    public ExerciseDirection reverse(){
        switch (this){
            case L1_TO_L2:
                return L2_TO_L1;
            case L2_TO_L1:
                return L1_TO_L2;
            default:
                return null;
        }
    }
}
