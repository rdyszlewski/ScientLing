package com.dyszlewskiR.edu.scientling.models.others;

public class UsersSet {
    private long mId;
    private String mName;
    private long mL1;
    private long mL2;
    private boolean mHasImages;
    private boolean mHasRecords;

    public long getId(){return mId;}
    public void setId(long id){
        mId = id;
    }

    public String getName(){return mName;}
    public void setName(String name){mName = name;}

    public long getL1(){return mL1;}
    public void setL1(long l1){mL1 =l1;}

    public long getL2(){return mL2;}
    public void setL2(long l2){mL2 = l2;}

    public boolean hasImages(){return mHasImages;}
    public void setHasImages(boolean hasImages){mHasImages = hasImages;}

    public boolean hasRecords(){return mHasRecords;}
    public void setHasRecords(boolean hasRecords){mHasRecords = hasRecords;}
}
