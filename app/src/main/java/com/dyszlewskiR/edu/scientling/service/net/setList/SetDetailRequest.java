package com.dyszlewskiR.edu.scientling.service.net.setList;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class SetDetailRequest {
    private static final String REQUEST_URI = Constants.SERVER_ADDRESS + "/sets/?/details";

    private long mSetId;
    private String mUsername;
    private String mPassword;

    public SetDetailRequest(long setId, String username, String password){
        mSetId = setId;
        mUsername = username;
        mPassword = password;
    }

    public HttpURLConnection start() throws IOException, JSONException {
        String requestUri = REQUEST_URI.replace("?", String.valueOf(mSetId));
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri, Constants.SERVER_TIMEOUT);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        /*String content = prepareContent();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        connection.getDoOutput();
        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(content);
        writer.close();*/
        connection.connect();
        return connection;
    }

    private final String USERNAME = "username";

    private String prepareContent() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(USERNAME, mUsername);
        return object.toString();
    }

}
