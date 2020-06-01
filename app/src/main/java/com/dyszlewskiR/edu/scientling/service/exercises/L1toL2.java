package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.List;

public class L1toL2 implements IExerciseDirection {

    @Override
    public String getQuestion(Word word) {
        assert word != null;
        assert word.getTranslations() != null;
        return TranslationListConverter.toString(word.getTranslations());
    }

    @Override
    public String getAnswer(Word word) {
        return word.getContent();
    }

    @Override
    public String[] getAnswers(List<Word> words) {
        if(BuildConfig.DEBUG)
            assert words != null && words.size() != 0;
        int length = words.size();
        String[] answers = new String[length];
        for (int i = 0; i < length; i++) {
            answers[i] = words.get(i).getContent();
        }
        return answers;
    }
}
