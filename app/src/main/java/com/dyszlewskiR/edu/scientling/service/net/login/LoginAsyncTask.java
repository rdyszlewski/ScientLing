package com.dyszlewskiR.edu.scientling.service.net.login;

import android.content.Context;
import android.os.AsyncTask;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;

import org.json.JSONException;

import java.io.IOException;

public class LoginAsyncTask extends AsyncTask<LoginParams, Void, LoginResponse.Params> {

    private Context mContext;

    private Login.LoginCallback mCallback;
    private LoginParams mParams;

    public LoginAsyncTask(Login.LoginCallback callback) {
        mContext = LingApplication.getInstance().getApplicationContext();
        mCallback = callback;
    }

    @Override
    protected LoginResponse.Params doInBackground(LoginParams... params) {
        mParams = params[0];
        LoginRequest request = new LoginRequest(params[0].getUsername(), params[0].getPassword());
        try {
            LoginResponse response = new LoginResponse(request.start());
            LoginResponse.Params responseParams = response.getParams();
            response.closeConnection();
            return responseParams;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(LoginResponse.Params result) {
        if (result != null) {
            switch (result.getResponseCode()) {
                case LoginResponse.LOGIN_SUCCESS:
                    login(result.getLogin());
                    break;
                case LoginResponse.INCORRECT_DATA:
                    mCallback.onIncorrectData();
                    break;
                case LoginResponse.ERROR:
                    mCallback.onError();
                    break;
            }
        } else {
            mCallback.onError();
        }
    }

    private void login(String login) {
        LogPref.setLogged(true, mContext);
        LogPref.setLogin(login, mContext);
        LogPref.setPassword(mParams.getPassword(), mContext);
        if (mCallback != null) {
            mCallback.onSuccessLogin(login);
        }
    }
}
