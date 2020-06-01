package com.dyszlewskiR.edu.scientling.service.net.upload;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UploadSetRequest {
    private static final String UPLOAD_SET = Constants.SERVER_ADDRESS + "/sets";
    private static final int READ_TIMEOUT = 3000;
    public static final int CHUNK_SIZE = 1024;


    public static  HttpURLConnection start(String username, String password) throws IOException, JSONException, InterruptedException {
        HttpURLConnection connection = URLConnector.getHttpConnection(UPLOAD_SET, Constants.SERVER_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        //connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Authorization", Authentication.prepare(username, password));
        connection.setChunkedStreamingMode(CHUNK_SIZE);
        connection.connect();

        return connection;
    }
}
