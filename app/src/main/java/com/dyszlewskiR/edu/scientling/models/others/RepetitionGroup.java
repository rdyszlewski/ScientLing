package com.dyszlewskiR.edu.scientling.models.others;

import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;

/**
 * Model przedstawiający grupę powtórek z jednego dnia i z określonego zestawu.
 * Model jest używany w spisie powtórek
 */

public class RepetitionGroup {
    private int mDate;
    private VocabularySet mSet;
    private int mWordsCount;

    public int getDate() {
        return mDate;
    }

    public void setDate(int date) {
        mDate = date;
    }

    public VocabularySet getSet() {
        return mSet;
    }

    public void setSet(VocabularySet set) {
        mSet = set;
    }

    public void setSet(long id, String name) {
        VocabularySet set = new VocabularySet();
        set.setId(id);
        set.setName(name);
        mSet = set;
    }

    public int getWordsCount() {
        return mWordsCount;
    }

    public void setWordsCount(int wordsCount) {
        mWordsCount = wordsCount;
    }
}
