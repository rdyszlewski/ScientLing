package com.dyszlewskiR.edu.scientling.service.net.rating;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class RatingRequest {
    private static final String RATING_REQUEST = Constants.SERVER_ADDRESS + "/sets/?/rating";

    public static HttpURLConnection start(long setId, int rating, String username, String password) throws IOException, JSONException {
        String requestUri = RATING_REQUEST.replace("?", String.valueOf(setId));
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri);
        connection.setRequestProperty("Authorization", Authentication.prepare(username, password));
        connection.setRequestMethod("PUT");
        String content = prepareJson(rating);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(content.length()));
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(content.getBytes());
        outputStream.close();
        return connection;
    }

    private static final String RATING = "rating";

    private static  String prepareJson(int rating) throws JSONException {
        JSONObject object = new JSONObject();
        object.put(RATING, rating);
        return object.toString();
    }
}
