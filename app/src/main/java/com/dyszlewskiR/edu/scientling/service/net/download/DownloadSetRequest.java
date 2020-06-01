package com.dyszlewskiR.edu.scientling.service.net.download;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DownloadSetRequest {
    private static final String DOWNLOAD_SET_REQUEST = Constants.SERVER_ADDRESS + "/sets";

    private long mSetId;
    private String mUsername;
    private String mPassword;

    public DownloadSetRequest(long setId,String username, String password){
        mSetId = setId;
        mUsername = username;
        mPassword = password;
    }

    public HttpURLConnection start() throws IOException {
        String url = DOWNLOAD_SET_REQUEST + "/" + mSetId;
        HttpURLConnection connection = URLConnector.getHttpConnection(url, Constants.SERVER_TIMEOUT);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        connection.connect();
        return connection;
    }
}
