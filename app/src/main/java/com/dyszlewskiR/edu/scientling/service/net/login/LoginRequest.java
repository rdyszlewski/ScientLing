package com.dyszlewskiR.edu.scientling.service.net.login;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class LoginRequest {

    private static final String LOGIN_REQUEST = Constants.SERVER_ADDRESS + "/login";
    private static int TIME_OUT = 5000;


    private final String USERNAME = "username";
    private final String PASSWORD = "password";

    private String mUsername;
    private String mPassword;


    public LoginRequest(String username, String password) {
        mUsername = username;
        mPassword= password;
    }

    public HttpURLConnection start() throws IOException, JSONException {
       /* Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mUsername,mPassword.toCharArray());
            }
        });*/
        HttpURLConnection connection = URLConnector.getHttpConnection(LOGIN_REQUEST, TIME_OUT);
        String content = getJson(mUsername, mPassword);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(content);
        writer.close();
        connection.connect();
        return connection;
    }

    private String getJson(String username, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(USERNAME, username);
        json.put(PASSWORD, password);
        return json.toString();
    }
}
