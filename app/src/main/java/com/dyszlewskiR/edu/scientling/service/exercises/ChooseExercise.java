package com.dyszlewskiR.edu.scientling.service.exercises;

public class ChooseExercise implements IExerciseType {
    @Override
    public boolean checkAnswer(String answer, String correctAnswer) {
        return answer.equals(correctAnswer);
    }

    @Override
    public String getComment(String answer, String question) {
        return null;
    }
}
