package com.dyszlewskiR.edu.scientling.utils;

import android.content.Context;

/**
 * Created by Razjelll on 04.12.2016.
 */

public class ResourceUtils {
    public static String getString(String resourceName, Context context) {
        int resource = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        if (resource == 0) {
            return null;
        }
        return context.getResources().getString(resource);
    }

}
