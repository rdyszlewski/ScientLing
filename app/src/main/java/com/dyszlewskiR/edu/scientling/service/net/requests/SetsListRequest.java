package com.dyszlewskiR.edu.scientling.service.net.requests;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

public class SetsListRequest {

    private final String REQUEST_URI = Constants.SERVER_ADDRESS + "/sets";
    private final int LIMIT = 10;

    private final long EMPTY_LANGAUGE = -1;

    private long mL1;
    private long mL2;
    private String mSearchText;
    private int mSorting;
    private int mPage;
    private String mUsername;
    private String mPassword;

    public SetsListRequest(long l1, long l2, String searchText, int sorting, int page, String username, String password){
        mL1 = l1;
        mL2 = l2;
        mSearchText = searchText;
        mSorting = sorting;
        mPage = page;

        mUsername = username;
        mPassword = password;
    }

    public HttpURLConnection start() throws IOException {
        String requestUri = prepareRequestUri(mL1, mL2, mSearchText,mSorting,mPage, LIMIT);
        HttpURLConnection connection = URLConnector.getHttpConnection(requestUri);
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        connection.connect();
        return connection;
    }

    private final String PARAM_L2 = "l2";
    private final String PARAM_L1 = "l1";
    private final String PARAM_NAME = "name";
    private final String PARAM_SORT = "sort";
    private final String PARAM_PAGE = "page";
    private final String PARAM_LIMIT = "limit";

    private String prepareRequestUri(long l1, long l2, String searchText, int sorting, int page,int limit){
        StringBuilder stringBuilder = new StringBuilder(REQUEST_URI);
        stringBuilder.append("?");
        if(l2 != EMPTY_LANGAUGE){
            stringBuilder.append(PARAM_L2).append("=").append(String.valueOf(l2)).append("&");
        }
        if(l1 != EMPTY_LANGAUGE){
            stringBuilder.append(PARAM_L1).append("=").append(String.valueOf(l1)).append("&");
        }
        if(searchText != null && !searchText.isEmpty()){
            stringBuilder.append(PARAM_NAME).append("=").append(searchText).append("&");
        }
        if(sorting != 0){
            stringBuilder.append(PARAM_SORT).append("=").append(String.valueOf(sorting)).append("&");
        }
        if(page!=0){
            stringBuilder.append(PARAM_PAGE).append("=").append(String.valueOf(page)).append("&");
        }
        stringBuilder.append(PARAM_LIMIT).append("=").append(limit);
        return stringBuilder.toString();
    }
}
