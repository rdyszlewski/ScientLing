package com.dyszlewskiR.edu.scientling.service.net.login;

import android.content.Context;

public class Login {

    public static void login(LoginParams params, LoginCallback callback){
        LoginAsyncTask task = new LoginAsyncTask(callback);
        task.execute(params);
    }

    public interface LoginCallback {
        void onSuccessLogin(String login);

        void onIncorrectData();

        void onError();
    }
}
