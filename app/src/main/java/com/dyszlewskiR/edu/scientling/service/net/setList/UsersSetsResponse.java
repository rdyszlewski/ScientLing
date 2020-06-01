package com.dyszlewskiR.edu.scientling.service.net.setList;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.models.others.UsersSet;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class UsersSetsResponse {
    public static final int OK = 1;
    public static final int USER_UNKNOWN = -1;
    public static final int ERROR = -2;

    private HttpURLConnection mConnection;

    public UsersSetsResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int responseCode = mConnection.getResponseCode();
            switch (responseCode){
                case ResponseStatus.OK:
                    return OK;
                default:
                    return ERROR;
            }
        }
        return ERROR;
    }

    public List<UsersSet> getUsersSetsList() throws IOException {
        if(mConnection == null){
            return null;
        }
        InputStreamReader reader = new InputStreamReader(mConnection.getInputStream());
        JsonReader jsonReader = new JsonReader(reader);
        List<UsersSet> itemsList = new ArrayList<>();
        jsonReader.beginArray();
        UsersSet set;
        while(jsonReader.hasNext()){
            set = getSingleItem(jsonReader);
            if(set!= null){
                itemsList.add(set);
            }
        }
        jsonReader.endArray();
        reader.close();
        return itemsList;
    }

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String L1 = "l1";
    private static final String L2 = "l2";
    private static final String IMAGES = "images";
    private static final String RECORDS = "records";

    private static UsersSet getSingleItem(JsonReader jsonReader) throws IOException {
        UsersSet set = null;
        jsonReader.beginObject();
        String name;
        android.util.JsonToken token;
        do{
            if(set==null){
                set = new UsersSet();
            }
            name = jsonReader.nextName();
            switch (name){
                case ID:
                    set.setId(jsonReader.nextLong()); break;
                case NAME:
                    set.setName(jsonReader.nextString()); break;
                case L1:
                    set.setL1(jsonReader.nextLong()); break;
                case L2:
                    set.setL2(jsonReader.nextLong()); break;
                case IMAGES:
                    set.setHasImages(jsonReader.nextBoolean()); break;
                case RECORDS:
                    set.setHasRecords(jsonReader.nextBoolean()); break;
            }
            token = jsonReader.peek();
        }while (token!=null && !token.equals(android.util.JsonToken.END_OBJECT));
        jsonReader.endObject();
        return set;
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
