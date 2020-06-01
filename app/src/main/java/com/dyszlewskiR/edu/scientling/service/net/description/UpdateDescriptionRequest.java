package com.dyszlewskiR.edu.scientling.service.net.description;

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

public class UpdateDescriptionRequest {

    private static final String UPDATE_REQUEST = Constants.SERVER_ADDRESS + "/sets/?/description";

    public static HttpURLConnection start(long setId, String description, String username, String password) throws IOException, JSONException {
        String requestUri = UPDATE_REQUEST.replace("?",String.valueOf(setId));
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri);
        String content = prepareJson(setId, description);
        connection.setRequestProperty("Authorization", Authentication.prepare(username, password));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(content);
        writer.close();
        connection.connect();
        return connection;
    }

    private static String ID = "id";
    private static String DESCRIPTION = "desc";

    private static String prepareJson(long setId, String description) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ID, setId);
        json.put(DESCRIPTION, description);
        return json.toString();
    }
}
