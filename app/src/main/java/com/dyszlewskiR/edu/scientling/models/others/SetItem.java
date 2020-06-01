package com.dyszlewskiR.edu.scientling.models.others;


import java.util.Date;

public class SetItem {
    private long mId;
    private String mName;
    private String mLanguageL1;
    private String mLanguageL2;
    private int mWordsCount;
    private String mDescription;
    private String mAuthor;
    private int mBasicSize;
    private float mRating;
    private int mDownloads;
    private int mImagesSize;
    private int mRecordsSize;
    private Date mAddedDate;
    private boolean mIsDownloading;
    private boolean mIsDownloaded;
    private int mDownloadingProgress;
    private boolean mImagesDownloaded;
    private boolean mRecordsDownloaded;
    private Boolean mWasDownloaded;
    private Integer mUserRating;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLanguageL1() {
        return mLanguageL1;
    }

    public void setLanguageL1(String language) {
        mLanguageL1 = language;
    }

    public String getLanguageL2() {
        return mLanguageL2;
    }

    public void setLanguageL2(String language) {
        mLanguageL2 = language;
    }

    public int getWordsCount() {
        return mWordsCount;
    }

    public void setWordsCount(int count) {
        mWordsCount = count;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getBasicSize() {
        return mBasicSize;
    }

    public void setBasicSize(int size) {
        mBasicSize = size;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getDownloads() {
        return mDownloads;
    }

    public void setDownloads(int downloads) {
        mDownloads = downloads;
    }

    public int getImagesSize() {
        return mImagesSize;
    }

    public void setImagesSize(int size) {
        mImagesSize = size;
    }

    public int getRecordsSize() {
        return mRecordsSize;
    }

    public void setRecordsSize(int size) {
        mRecordsSize = size;
    }

    public Date getAddedDate(){
        return mAddedDate;
    }
    public void setAddedDate(Date date){
        mAddedDate = date;
    }

    public boolean isDownloading(){return mIsDownloading;}
    public void setDownloading(boolean isDownloading){
        mIsDownloading = isDownloading;
    }

    public boolean isDownloaded(){
        return mIsDownloaded;
    }
    public void setDownloaded(boolean isDownloaded){
        mIsDownloaded = isDownloaded;
    }

    public int getDownloadingProgress(){return mDownloadingProgress;}
    public void setDownloadingProgress(int progress){mDownloadingProgress = progress;}

    public boolean isImagesDownloaded(){return mImagesDownloaded;}
    public void setImagesDownloaded(boolean downloaded){mImagesDownloaded = downloaded;}

    public boolean isRecordsDownloaded(){return mRecordsDownloaded;}
    public void setRecordsDownloaded(boolean downloaded){mRecordsDownloaded = downloaded;}

    public void setDownloadInfo(SetDownloadInfo info){
        if(info != null){
            mImagesDownloaded = info.isImagesDownloaded();
            mRecordsDownloaded = info.isRecordsDownloaded();
        }
    }

    public boolean hasImages(){return mImagesSize != 0;}

    public boolean hasRecords(){return mRecordsSize != 0;}

    public Boolean getWasDownloaded(){return mWasDownloaded;}
    public void setWasDownloaded(Boolean wasDownloaded){mWasDownloaded = wasDownloaded;}

    public Integer getUserRating(){return mUserRating;}
    public void setUserRating(Integer userRating) {mUserRating = userRating;}
}
