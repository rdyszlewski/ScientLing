package com.dyszlewskiR.edu.scientling.service.net.register;

import android.util.JsonReader;

import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class RegisterResponse {

    public static final int OK = 1;
    public static final int EXIST_LOGIN = 2;
    public static final int EXIST_EMAIL = 3;
    public static final int EXIST_BOTH = 4;
    public static final int ERROR = 5;

    private final String ERROR_PARAM = "error";
    private final String LOGIN = "login";
    private final String EMAIL = "email";

    private HttpURLConnection mConnection;

    public RegisterResponse(HttpURLConnection connection){
        mConnection = connection;
    }

    public int getResultCode() throws IOException {
        int responseCode = 0;
        if(mConnection != null){
            responseCode = mConnection.getResponseCode();
        }
        switch (responseCode){
            case ResponseStatus.OK:
                return OK;
            case ResponseStatus.PRECONDITION_FAILED:
                return getErrorCode(mConnection.getErrorStream());
        }
        return ERROR;
    }

    private int getErrorCode(InputStream response) throws IOException {
        boolean loginExist = false;
        boolean emailExist = false;
        InputStreamReader reader = new InputStreamReader(response);
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            String name = jsonReader.nextName();
            if (name.equals(ERROR_PARAM)) {
                String value = jsonReader.nextString();
                if (value.equals(LOGIN)) {
                    loginExist = true;
                } else if (value.equals(EMAIL)) {
                    emailExist = true;
                }
            }
            jsonReader.endObject();
        }
        jsonReader.endArray();

        if (loginExist && emailExist) {
            return EXIST_BOTH;
        } else if (loginExist) {
            return EXIST_LOGIN;
        } else {
            return EXIST_EMAIL;
        }
    }

    public void closeConnection(){
        if(mConnection!=null ){
            mConnection.disconnect();
        }
    }
}
