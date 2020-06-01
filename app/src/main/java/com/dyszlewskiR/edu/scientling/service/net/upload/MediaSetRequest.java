package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.content.Context;

import com.dyszlewskiR.edu.scientling.service.net.connection.URLConnector;
import com.dyszlewskiR.edu.scientling.service.net.connection.Authentication;

import java.io.IOException;
import java.net.HttpURLConnection;


public class MediaSetRequest {

    private static final int READ_TIMEOUT = 3000;
    public static final int CHUNK_SIZE = 1024;
    protected static final String TWO_HYPNES = "--";
    protected static final String MULTIPART_BOUNDARY = "*****";
    protected static final String LINE_END = "\r\n";

    protected final String META_NAME = "json";
    protected final String FILE_NAME = "data";
    protected final String FILENAME = "set.zip";

    private long mSetId;
    private String mUsername;
    private String mPassword;
    private String mSetCatalog;
    private Context mContext;

    protected long getSetId(){return mSetId;}
    protected String getUsername(){return mUsername;}
    protected String getPassword(){return mPassword;}

    protected void setSetId(long setId){mSetId = setId;}
    protected void setUsername(String username){mUsername = username;}
    protected void setPassword(String password){mPassword = password;}
    protected void setCatalog(String catalog){mSetCatalog = catalog;}
    protected void setContext(Context context){mContext = context;}


    public MediaSetRequest(long setId, String username, String password, String catalog, Context context){
        mSetId = setId;
        mUsername = username;
        mPassword = password;
        mSetCatalog = catalog;
        mContext = context;
    }

    protected HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection connection = URLConnector.getHttpConnection(url);
        connection.setRequestMethod("POST");
        //connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Authorization", Authentication.prepare(mUsername, mPassword));
        connection.setChunkedStreamingMode(CHUNK_SIZE);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        //connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + MULTIPART_BOUNDARY);
        return connection;
    }

    protected HttpURLConnection start(String request, String filesFolder) throws IOException {
        HttpURLConnection connection = createConnection(request);
        connection.connect();
        return connection;
    }
}
