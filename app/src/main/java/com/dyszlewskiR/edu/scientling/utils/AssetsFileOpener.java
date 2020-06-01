package com.dyszlewskiR.edu.scientling.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetsFileOpener extends AndroidFileOpener {

    private final Context mContext;

    public AssetsFileOpener(Context context) {
        mContext = context;
    }

    @Override
    public InputStream getStream(String fileName) throws IOException {
        return mContext.getAssets().open(fileName);
    }
}
