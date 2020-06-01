package com.dyszlewskiR.edu.scientling.service.net.login;

import com.dyszlewskiR.edu.scientling.utils.MD5;

import java.security.NoSuchAlgorithmException;

public class LoginParams {
    private String mUsername;
    private String mPassword;

    public LoginParams(String username, String password) throws NoSuchAlgorithmException {
        mUsername = username;
        mPassword = MD5.getMD5(password);
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }
}