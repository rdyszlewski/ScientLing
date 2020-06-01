package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.content.Context;

import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import org.json.JSONException;
import java.io.IOException;
import java.net.HttpURLConnection;

public class UploadImagesRequest extends MediaSetRequest {
    private final String UPLOAD_REQUEST = Constants.SERVER_ADDRESS + "/sets/?/images";

    public UploadImagesRequest(long setId, String username, String password, String catalog, Context context) {
        super(setId, username, password, catalog, context);
    }

    public HttpURLConnection start() throws IOException, JSONException {
        if(getSetId()<0){
            return null;
        }
        String requestUri = UPLOAD_REQUEST.replace("?",String.valueOf(getSetId()));
        return start(requestUri, MediaFileSystem.IMAGES);
    }
}
