package com.dyszlewskiR.edu.scientling.models.others;

import android.os.Parcel;
import android.os.Parcelable;

public class SetListItem implements Parcelable {

    public enum ServerState{
        NONE("none"),
        DOWNLOADED("downloaded"),
        UPLOADED("uploaded");

        final private String mValue;
        public String getValue(){return mValue;}
        ServerState(String value){mValue = value;}
    }

    private long mId;
    private String mName;


    private ServerState mServerState;

    public SetListItem(){
        mServerState = ServerState.NONE;
    }

    public SetListItem(long id, String name){
        mId = id;
        mName = name;
        mServerState = ServerState.NONE;
    }

    public long getId(){return mId;}
    public void setId(long id){mId = id;}

    public String getName(){return mName;}
    public void setName(String name){mName = name;}

    public ServerState getServerState(){return mServerState;}
    public void setServerState(ServerState serverState){mServerState = serverState;}

    protected SetListItem(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mServerState = ServerState.valueOf(in.readString());
    }

    public static final Creator<SetListItem> CREATOR = new Creator<SetListItem>() {
        @Override
        public SetListItem createFromParcel(Parcel in) {
            return new SetListItem(in);
        }

        @Override
        public SetListItem[] newArray(int size) {
            return new SetListItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mServerState.name());
    }

}
