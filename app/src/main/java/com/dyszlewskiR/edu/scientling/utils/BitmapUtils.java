package com.dyszlewskiR.edu.scientling.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESS_QUALITY = 100;
    private static final int BASE64_MODE = Base64.DEFAULT;

    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, bos);
        return bos.toByteArray();
    }

    public static Bitmap getBitmap(String input) {
        byte[] decodedByte = Base64.decode(input, BASE64_MODE);
        return getBitmap(decodedByte);
    }

    public static Bitmap getBitmap(byte[] input) {
        return BitmapFactory.decodeByteArray(input, 0, input.length);
    }

    public static Bitmap resize(Bitmap bitmap, float maxImageSize, boolean filter) {
        float ratio = Math.min(maxImageSize / bitmap.getWidth(), maxImageSize / bitmap.getHeight());
        int width = Math.round(ratio * bitmap.getWidth());
        int height = Math.round(ratio * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, width, height, filter);
    }

    public static Bitmap resize(Bitmap bitmap, int width, int height, boolean filter) {
        return Bitmap.createScaledBitmap(bitmap, width, height, filter);
    }

    //TODO nie wiem czy ta moetoda powinna być w tym miejscu
    public static void saveBitmap(Bitmap bitmap, String directory, String filename) throws IOException {
        File file = new File(directory, filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
    }
}
