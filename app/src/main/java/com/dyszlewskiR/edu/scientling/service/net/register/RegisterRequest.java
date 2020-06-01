package com.dyszlewskiR.edu.scientling.service.net.register;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class RegisterRequest {

    private final String REGISTER_REQUEST = Constants.SERVER_ADDRESS + "/registration";
    private final int TIMEOUT = 2000;

    private final String LOGIN = "login";
    private final String EMAIL = "email";
    private final String PASSWORD = "password";

    public HttpURLConnection start(String login, String email, String password) throws IOException, JSONException {
        HttpURLConnection connection = URLConnector.getHttpConnection(REGISTER_REQUEST, TIMEOUT);
        String content = getJson(login, email, password);
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

    private String getJson(String login, String email, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(LOGIN, login);
        json.put(EMAIL, email);
        json.put(PASSWORD, password);
        return json.toString();
    }


}
