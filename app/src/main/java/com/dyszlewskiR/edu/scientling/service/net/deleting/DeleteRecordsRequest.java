package com.dyszlewskiR.edu.scientling.service.net.deleting;

import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeleteRecordsRequest extends DeleteMediaRequest {
    private static final String DELETE_REQUEST = Constants.SERVER_ADDRESS + "/sets/?/records";

    private String mUrl;

    public DeleteRecordsRequest(long setId){
        mUrl = DELETE_REQUEST.replace("?", String.valueOf(setId));
        super.DeleteMediaRequest(mUrl);
    }

    public HttpURLConnection start(String username, String password) throws IOException {
        return start(username, password);
    }
}
