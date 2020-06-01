package com.dyszlewskiR.edu.scientling.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Razjelll on 07.11.2016.
 */

public class ResourcesFileOpener extends AndroidFileOpener {

    @Override
    public InputStream getStream(String fileName) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
