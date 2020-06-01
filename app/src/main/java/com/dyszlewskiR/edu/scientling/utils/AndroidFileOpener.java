package com.dyszlewskiR.edu.scientling.utils;

import java.io.IOException;
import java.io.InputStream;

public abstract class AndroidFileOpener {
    public abstract InputStream getStream(String fileName) throws IOException;
}
