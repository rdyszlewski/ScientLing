package com.dyszlewskiR.edu.scientling.service.net.rating;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RatingResponse {
    public static final int OK = 1;
    public static final int NON_AUTHORIZED = -1;
    public static final int FAILED = -2;

    private HttpURLConnection mConnection;

    public RatingResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection == null){
            return FAILED;
        }
        int responseCode = mConnection.getResponseCode();
        switch (responseCode){
            case ResponseStatus.OK:
                return OK;
            //TOOD zrobić pozostałe kody
        }
        return FAILED;
    }

    public void getResponse() throws IOException {
        if(mConnection != null){
            mConnection.getInputStream();
        }
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}

