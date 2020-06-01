package com.dyszlewskiR.edu.scientling.data.file;

class FileSizeConverter {
    public static float bytesToKb(long bytes){
        float size = (float)bytes;
        return bytesToKb(size);
    }

    public static float bytesToKb(float bytes){
        return bytes/1024;
    }

    public static float bytesToMB(long bytes){
        float size = (float)bytes;
        return bytesToMB(size);
    }

    public static float bytesToMB(float bytes){
        return bytes/1048576;
    }

    public static float bytesToGB(long bytes){
        float size = (float)bytes;
        return bytesToGB(size);
    }

    public static float bytesToGB(float bytes){
        return bytes/1073741824;
    }
}
