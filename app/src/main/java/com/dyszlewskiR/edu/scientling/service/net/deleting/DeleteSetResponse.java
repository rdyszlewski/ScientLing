package com.dyszlewskiR.edu.scientling.service.net.deleting;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteSetResponse {
    private HttpURLConnection mConnection;

    public DeleteSetResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public boolean getResponse() throws IOException {
        //TODO tymczasowa metoda, powinniśmy pobrać jakąś odpowiedź
        mConnection.getInputStream();
        return true;
    }

    public void closeConnection(){
        if(mConnection != null){
            mConnection.disconnect();
        }
    }
}
