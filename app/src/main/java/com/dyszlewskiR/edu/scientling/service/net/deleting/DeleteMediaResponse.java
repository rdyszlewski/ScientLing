package com.dyszlewskiR.edu.scientling.service.net.deleting;


import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteMediaResponse {

    public static final int DELETED = 1;
    public static final int FAILED = 2;

    private HttpURLConnection mConnection;

    public DeleteMediaResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public boolean getResponse() throws IOException {
        //TODO tymczasowa metoda, powinniśmy pobrać jakąś odpowiedź
        mConnection.getInputStream();
        return true;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int resultCode = mConnection.getResponseCode();
            switch (resultCode){
                case ResponseStatus.OK:
                    return DELETED;
                default:
                    return FAILED;
            }
        }
        return FAILED;
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
