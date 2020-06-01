package com.dyszlewskiR.edu.scientling.service.net.deleting;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteSetRequest {
    private final String DELETE_STATEMENT = Constants.SERVER_ADDRESS + "/sets/";

    private String mUrl;

    public DeleteSetRequest(long setId) {
        mUrl = DELETE_STATEMENT + setId;
    }

    public HttpURLConnection start(String username, String password) throws IOException {
        HttpURLConnection connection = URLConnector.getHttpConnection(mUrl);
        connection.setDoInput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", Authentication.prepare(username, password));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        return connection;
    }
}
