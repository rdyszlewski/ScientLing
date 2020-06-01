package com.dyszlewskiR.edu.scientling.service.net.download;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;


public class DownloadMediaRequest {
    private static final String DOWNLOAD_IMAGES = Constants.SERVER_ADDRESS + "/sets/?/images";
    private static final String DOWNLOAD_RECORDS = Constants.SERVER_ADDRESS + "/sets/?/records";

    private long mSetId;
    private String mUsername;
    private String mPassword;
    private MediaType mMediaType;

    public DownloadMediaRequest(long setId, String username, String password, MediaType mediaType){
        mSetId = setId;
        mUsername = username;
        mPassword = password;
        mMediaType = mediaType;
    }

    public HttpURLConnection start() throws IOException {
        String requestAddress = null;
        switch (mMediaType) {
            case IMAGES:
                requestAddress = DOWNLOAD_IMAGES;
                break;
            case RECORDS:
                requestAddress = DOWNLOAD_RECORDS;
                break;
        }
        String requestUri = requestAddress.replace("?",String.valueOf(mSetId));
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        connection.setDoInput(true);
        connection.connect();
        return connection;
    }
}
