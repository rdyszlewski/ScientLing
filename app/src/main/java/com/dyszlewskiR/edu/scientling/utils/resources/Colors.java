package com.dyszlewskiR.edu.scientling.utils.resources;

import android.content.Context;
import android.os.Build;

public class Colors {
    public static int getColor(int resource, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resource);
        } else {
            return context.getResources().getColor(resource);
        }
    }
}
