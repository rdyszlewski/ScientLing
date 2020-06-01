package com.dyszlewskiR.edu.scientling.service.net.requests;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UsersSetsRequest {
    //TODO tymczasowy adres
    private final String REQUEST_URI = Constants.SERVER_ADDRESS + "/sets/users/";

    private String mUsername;
    private String mPassword;

    public UsersSetsRequest(String username, String password){
        mUsername = username;
        mPassword = password;
    }

    public HttpURLConnection start(String user) throws IOException {
        String requestUri = REQUEST_URI + user;
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        connection.connect();
        return connection;
    }
}
