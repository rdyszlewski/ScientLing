package com.dyszlewskiR.edu.scientling.models.others;

public class SetDownloadInfo {
    private long mId;
    private long mLocalId;
    private boolean mUploaded;
    private boolean mImagesDownloaded;
    private boolean mRecordsDownloaded;

    public long getId(){return mId;}
    public void setId(long id){mId = id;}

    public long getLocalId(){return mLocalId;}
    public void setLocalId(long id){mLocalId = id;}

    public boolean isUploaded(){return mUploaded;}
    public void setuploaded(boolean uploaded){mUploaded = uploaded;}

    public boolean isImagesDownloaded(){return mImagesDownloaded;}
    public void setImagesDownloaded(boolean downloaded){mImagesDownloaded = downloaded;}
    public void setImagesDownloaded(int downloaded){
        mImagesDownloaded = downloaded==1?true:false;
    }

    public boolean isRecordsDownloaded(){return mRecordsDownloaded;}
    public void setRecordsDownloaded(boolean downloaded){mRecordsDownloaded = downloaded;}
    public void setRecordsDownloaded(int downloaded){
        mRecordsDownloaded = downloaded==1?true:false;
    }
}
