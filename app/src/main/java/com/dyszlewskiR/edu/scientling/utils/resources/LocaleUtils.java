package com.dyszlewskiR.edu.scientling.utils.resources;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

/**
 * Created by Razjelll on 29.12.2016.
 */

public class LocaleUtils {

    public static Locale getLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)//
        {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }
}
