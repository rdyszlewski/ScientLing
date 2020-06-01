package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.utils.Constants;

/**
 * Created by Razjelll on 17.11.2016.
 */

public class WriteExercise implements IExerciseType {
    @Override
    public boolean checkAnswer(String answer, String correctAnswer) {
        String[] correctAnswerArray = correctAnswer.split(Constants.TRANSLATION_SEPARATOR);
        for (String word : correctAnswerArray) {
            if (word.equals(answer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getComment(String answer, String question) {
        return null;
    }
}
