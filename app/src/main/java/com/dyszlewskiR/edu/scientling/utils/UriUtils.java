package com.dyszlewskiR.edu.scientling.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Razjelll on 23.03.2017.
 */

public class UriUtils {
    public static String getFileName(Uri uri, Context context) {
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            return uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            String filename = null;
            String[] projection = {MediaStore.Images.Media.TITLE};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            assert cursor != null;
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                filename = cursor.getString(columnIndex);
            }
            if (cursor != null) {
                cursor.close();
            }
            return filename;
        }
        return null;
    }

    public static byte[] toByteArray(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        return getBytes(inputStream);
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
