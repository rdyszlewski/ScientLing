package com.dyszlewskiR.edu.scientling.data.file;

import android.content.Context;

public class InternalStorage extends Storage{

    @Override
    protected String getPath(String filename, String catalog, Context context) {
        return context.getFilesDir() + "/" + catalog + "/" + filename;
    }

    @Override
    public String getPath(String catalog, Context context) {
        return context.getFilesDir() + "/" + catalog;
    }

    @Override
    protected String getCachePath(String filename, Context context) {
        return context.getCacheDir() + "/" + filename;
    }
}
