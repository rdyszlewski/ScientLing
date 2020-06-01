package com.dyszlewskiR.edu.scientling.service.exercises;

public interface IExerciseCallback {
    void onAnswer(boolean correct);
    void onExerciseFinish();

}
