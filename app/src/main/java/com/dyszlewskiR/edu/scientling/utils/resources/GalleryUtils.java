package com.dyszlewskiR.edu.scientling.utils.resources;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class GalleryUtils {

    public static Bitmap getBitmap(Uri imageUri, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return BitmapFactory.decodeFile(imagePath);
        }
        return null;
    }
}
