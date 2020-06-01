package com.dyszlewskiR.edu.scientling.presentation.fragment;

import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseManager;

public interface IExerciseFragment {
    void toAnswer(String answer);
    void showQuestion();
    void setExerciseManager(ExerciseManager exerciseManager);
    void setExerviceCallback(); //TODO dodać parametr
}
