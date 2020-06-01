package com.dyszlewskiR.edu.scientling.service.net.upload;

//TODO troszkę ta nazwa nie pasuję, pomyślec nad jakąś bardziej wystrzałową
public class UploadParams {
    private boolean mDatabase;
    private boolean mImages;
    private boolean mRecords;

    public boolean isDatabase(){return mDatabase;}
    public boolean isImages(){return mImages;}
    public boolean isRecords(){return mRecords;}

    public void setDatabase(boolean database){mDatabase = database;}
    public void setImages(boolean images){mImages = images;}
    public void setRecords(boolean records){mRecords = records;}
}
