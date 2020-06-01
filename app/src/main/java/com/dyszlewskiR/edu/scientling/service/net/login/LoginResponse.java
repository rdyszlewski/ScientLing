package com.dyszlewskiR.edu.scientling.service.net.login;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class LoginResponse {

    private final String LOGIN = "login";

    public static final int LOGIN_SUCCESS = 1;
    public static final int INCORRECT_DATA = -1;
    public static final int ERROR = -2;

    private HttpURLConnection mConnection;

    public LoginResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        int responseCode = mConnection.getResponseCode();
        switch (responseCode){
            case ResponseStatus.OK:
                return LOGIN_SUCCESS;
            case ResponseStatus.UNAUTHORIZED:
                return INCORRECT_DATA;
        }
        return ERROR;
    }

    public String getLogin() throws IOException {
        InputStream response = mConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(response);
        JsonReader jsonReader = new JsonReader(reader);
        try {
            jsonReader.beginObject();
            if (jsonReader.nextName().equals(LOGIN)) {
                return jsonReader.nextString();
            }
        } catch (IOException e) {

            return null;
        }
        return null;
    }

    public Params getParams() throws IOException {
        Params params = new Params();
        int resultCode = getResultCode();
        params.setResponseCode(getResultCode());
        if(resultCode > 0){
            params.setLogin(getLogin());
        }
        return params;
    }

    public void closeConnection(){
        mConnection.disconnect();
    }

    public class Params {
        private int mResponseCode;
        private String mLogin;
        private String mPassword;

        public int getResponseCode(){return mResponseCode;}
        public void setResponseCode(int code){mResponseCode = code;}

        public String getLogin(){return mLogin;}
        public void setLogin(String login) {mLogin = login;}
    }
}
