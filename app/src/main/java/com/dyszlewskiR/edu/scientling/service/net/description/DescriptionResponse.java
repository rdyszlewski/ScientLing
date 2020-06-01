package com.dyszlewskiR.edu.scientling.service.net.description;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class DescriptionResponse {
    public static final int OK = 1;
    public static final int NOT_FOUND = -1;
    public static final int ERROR = -2;

    private HttpURLConnection mConnection;

    public DescriptionResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int responseCode = mConnection.getResponseCode();
            switch (responseCode){
                case ResponseStatus.OK:
                    return OK;
                case ResponseStatus.NOT_FOUND:
                    return NOT_FOUND;
                //TODO
            }
        }
        return ERROR;
    }

    private final String DESCRIPTION = "desc";

    public String getDescription() throws IOException {
        if(mConnection == null){
            return null;
        }
        InputStream inputStream = mConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.beginObject();
        String description = null;
        if (jsonReader.nextName().equals(DESCRIPTION)) {
            description = jsonReader.nextString();
        }
        jsonReader.endObject();
        jsonReader.close();
        return description;
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
