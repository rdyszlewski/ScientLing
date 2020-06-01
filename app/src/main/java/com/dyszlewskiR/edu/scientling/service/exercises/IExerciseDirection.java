package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.models.entity.Word;
import java.util.List;

public interface IExerciseDirection {

    String getQuestion(Word word);

    String getAnswer(Word word);

    String[] getAnswers(List<Word> words);

}
