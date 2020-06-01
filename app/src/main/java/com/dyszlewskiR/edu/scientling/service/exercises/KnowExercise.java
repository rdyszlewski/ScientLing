package com.dyszlewskiR.edu.scientling.service.exercises;

/**
 * Created by Razjelll on 17.11.2016.
 */

public class KnowExercise implements IExerciseType {

    public static final String KNOW_ANSWER = "Know";
    public static final String DONT_KNOW_ANSWER = "DontKnow";

    @Override
    public boolean checkAnswer(String answer, String question) {
        return answer.equals(KNOW_ANSWER);
    }

    @Override
    public String getComment(String answer, String question) {
        return null;
    }
}
