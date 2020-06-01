package com.dyszlewskiR.edu.scientling.service.net.deleting;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;

import java.io.IOException;
import java.net.HttpURLConnection;

class DeleteMediaRequest {

    private long mSetId;
    private String mUrl;

    public void DeleteMediaRequest(String url){
        mUrl = url;
    }

    private HttpURLConnection prepareConnection(String username, String password) throws IOException {
        HttpURLConnection connection = URLConnector.getHttpConnection(mUrl);
        //connection.setDoInput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", Authentication.prepare(username, password));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        return connection;
    }

     public HttpURLConnection start(String username, String password) throws IOException {
        HttpURLConnection connection = prepareConnection(username, password);
        connection.connect();
        return connection;
    }
}
