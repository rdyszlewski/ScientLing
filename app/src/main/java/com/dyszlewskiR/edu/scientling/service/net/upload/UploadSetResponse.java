package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class UploadSetResponse {
    public static final int OK = 1;
    public static final int UNAUTHORIZED = 2;
    public static final int BAD_REQUEST = 3;
    public static final int TIMEOUT = 4;
    public static final int FAILED = 5;

    private final String ID = "id";

    private HttpURLConnection mConnection;

    public UploadSetResponse(HttpURLConnection connection){
        mConnection =connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int responseCode = mConnection.getResponseCode();
            switch (responseCode){
                case ResponseStatus.OK:
                    return OK;
                case ResponseStatus.UNAUTHORIZED:
                    return UNAUTHORIZED;
                case ResponseStatus.REQUEST_TIMEOUT:
                    return TIMEOUT;
                case ResponseStatus.BAD_REQUEST:
                    return BAD_REQUEST;
                default:
                    return FAILED;
            }

        }
        return FAILED;
    }

    public long getId() throws IOException {
        if(mConnection != null){
            JsonReader reader = new JsonReader(new InputStreamReader(mConnection.getInputStream()));
            reader.beginObject();
            String name = reader.nextName();
            if(name.equals(ID)){
                return reader.nextLong();
            }
        }
        return -1;
    }
}
