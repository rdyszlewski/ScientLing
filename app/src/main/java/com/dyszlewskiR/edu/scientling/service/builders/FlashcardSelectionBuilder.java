package com.dyszlewskiR.edu.scientling.service.builders;

import com.dyszlewskiR.edu.scientling.models.params.FlashcardParams;

import java.util.ArrayList;
import java.util.List;

public class FlashcardSelectionBuilder {

    public static String getStatement(FlashcardParams params) {
        WordSelectionBuilder builder = new WordSelectionBuilder();
        builder.append(WordSelectionBuilder.Parts.SET_PART);
        switch (params.getChoiceType()) {
            case LAST_LEARNED:
                builder.append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.LEARNING_DATE_PART_NULL);
                break;
            case ONLY_LEARNED:
                builder.append(WordSelectionBuilder.Parts.AND).append(WordSelectionBuilder.Parts.LEARNING_DATE_PART);
                break;
        }
        return builder.toString();
    }

    public static String[] getArguments(FlashcardParams params) {
        List<String> argumentsList = new ArrayList<>();
        argumentsList.add(String.valueOf(params.getSetId()));
        return argumentsList.toArray(new String[0]);
    }
}
