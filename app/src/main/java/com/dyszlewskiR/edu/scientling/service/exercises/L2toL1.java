package com.dyszlewskiR.edu.scientling.service.exercises;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.List;


public class L2toL1 implements IExerciseDirection {

    @Override
    public String getQuestion(Word word) {
        return word.getContent();
    }

    @Override
    public String getAnswer(Word word) {
        assert word != null && word.getTranslations() != null;
        return TranslationListConverter.toString(word.getTranslations());
    }

    @Override
    public String[] getAnswers(List<Word> words) {
        if(BuildConfig.DEBUG)
            assert words != null && words.size() != 0;
        int length = words.size();
        String[] answers = new String[length];
        for (int i = 0; i < length; i++) {
            assert words.get(i).getTranslations() != null;
            answers[i] = TranslationListConverter.toString(words.get(i).getTranslations());
        }
        return answers;
    }
}
