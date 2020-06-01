package com.dyszlewskiR.edu.scientling.service.net.download;

import android.content.Context;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.service.net.values.ResponseStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadMediaResponse {
    public static final int OK = 1;
    public static final int UNAUTHORIZED = -1;
    public static final int ERROR = -2;

    private final int BUFFER_SIZE = 1024;

    private HttpURLConnection mConnection;
    private Context mContext;
    private MediaType mMediaType;

    public DownloadMediaResponse(HttpURLConnection connection, MediaType mediaType, Context context){
        mConnection =connection;
        mContext = context;
        mMediaType = mediaType;
    }

    public int getResultCode() throws IOException {
        if(mConnection != null){
            int resultCode = mConnection.getResponseCode();
            switch (resultCode){
                case ResponseStatus.OK:
                    return OK;
                //TODO zrobić kody
            }
        }
        return ERROR;
    }

    public long getContentLength(){
        if(mConnection!=null){
            return mConnection.getContentLength();
           // return Long.parseLong(mConnection.getHeaderField("X-Content-Length"));
        }
        return 0;
    }

    public interface SaveCallback{
        void onSave(int length);
        void onSaveCompleted();
    }

    public void saveFile(String setCatalog, SaveCallback callback) throws IOException {
        if(mConnection == null){
            return;
        }
        MediaFileSystem.checkAndCreateMediaCatalogs(setCatalog, mMediaType, mContext);

        InputStream inputStream = mConnection.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        byte[]buffer = new byte[BUFFER_SIZE];
        ZipEntry zipEntry;
        File file;
        while((zipEntry = zipInputStream.getNextEntry()) != null){
            Log.d(getClass().getSimpleName(), "start Entry");
            file = MediaFileSystem.getMedia(zipEntry.getName(), setCatalog,mMediaType, mContext);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            int length;
            while((length = zipInputStream.read(buffer,0, buffer.length))>0){
                outputStream.write(buffer,0, length);
                if(callback != null){
                    callback.onSave(length);
                }
            }
            outputStream.close();
        }
        zipInputStream.close();
        if(callback != null){
            callback.onSaveCompleted();
        }
    }

    public void closeConnection(){
        mConnection.disconnect();
    }
}
