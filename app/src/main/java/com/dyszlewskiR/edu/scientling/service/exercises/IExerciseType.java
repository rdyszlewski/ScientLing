package com.dyszlewskiR.edu.scientling.service.exercises;

/**
 * Created by Razjelll on 16.11.2016.
 */

public interface IExerciseType {

    boolean checkAnswer(String answer, String correctAnswer);

    String getComment(String answer, String question);
}
