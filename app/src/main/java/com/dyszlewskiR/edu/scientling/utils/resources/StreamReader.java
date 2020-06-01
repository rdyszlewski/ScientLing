package com.dyszlewskiR.edu.scientling.utils.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader {
    public static String getString(InputStream stream) throws IOException {
        if(stream != null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        }
        return null;
    }
}
