package com.dyszlewskiR.edu.scientling.service.net.description;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class UpdateDescriptionResponse {
    public static int INSERTED = 1;
    public static int ALREADY_EXIST = -1;
    public static int FAILED = -2;

    private HttpURLConnection mConnection;

    public UpdateDescriptionResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int responseCode = mConnection.getResponseCode();
            switch (responseCode){
                //TODO
            }
            return INSERTED;
        }
        return FAILED;
    }

    public boolean getResponse() throws IOException {
        if(mConnection != null){
            InputStream stream = mConnection.getInputStream();
            stream.close();
            return true;
        }
        return false;
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
