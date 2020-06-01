package com.dyszlewskiR.edu.scientling.data.file;

public class SizeConverter {

    public static float bytesToKb(long bytes){
        return (float)bytes/1024;
    }

    public static float bytesToMB(long bytes){
        return bytesToKb(bytes)/1024;
    }
}
