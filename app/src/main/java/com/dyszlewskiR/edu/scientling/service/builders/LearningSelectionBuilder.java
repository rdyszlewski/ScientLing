package com.dyszlewskiR.edu.scientling.service.builders;

import com.dyszlewskiR.edu.scientling.models.params.LearningParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razjelll on 05.01.2017.
 */

public class LearningSelectionBuilder {

    public static String getStatement(LearningParams params) {
        WordSelectionBuilder builder = new WordSelectionBuilder();
        builder.append(WordSelectionBuilder.Parts.SET_PART)
                .append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.LEARNING_DATE_PART);
        if (params.isFromLesson()) {
            builder.append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.LESSON_PART);
        } else if (params.isFromCategory()) {
            builder.append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.CATEGORY_PART);
        }
        if (params.isDifficult()) {
            builder.append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.GREATER_DIFFICULT_PART);
        }
        return builder.toString();
    }

    public static String[] getArguments(LearningParams params) {
        List<String> argumentsList = new ArrayList<>();
        argumentsList.add(String.valueOf(params.getSetId()));
        if (params.isFromLesson()) {
            argumentsList.add(String.valueOf(params.getLessonId()));
        } else if (params.isFromCategory()) {
            argumentsList.add(String.valueOf(params.getCategoryId()));
        }
        if (params.isDifficult()) {
            argumentsList.add(String.valueOf(params.getDifficult()));
        }
        return argumentsList.toArray(new String[0]);
    }

}
