package com.dyszlewskiR.edu.scientling.models.others;

public class SetProgress {
    private long mId;
    private String mName;
    private int mWordsCount;
    private int mLearnedCount;
    private int mMasteredCount;

    public long getId(){return mId;}
    public void setId(long id){mId = id;}

    public String getName(){return mName;}
    public void setName(String name){mName = name;}

    public int getWordsCount(){return mWordsCount;}
    public void setWordsCount(int wordsCount){mWordsCount = wordsCount;}

    public int getLearnedCount(){return mLearnedCount;}
    public void setLearnedCount(int learnedCount){mLearnedCount = learnedCount;}

    public int getMasteredCount(){return mMasteredCount;}
    public void setMasteredCount(int masteredCount){mMasteredCount = masteredCount;}
}
