package com.dyszlewskiR.edu.scientling.data.file;

import java.io.File;

public class FileSizeCalculator {

    public final static int KB = 1;
    public final static int MB = 2;
    public final static int GB = 3;

    public static long calculate(String path) {
        File file = new File(path);
        long size = 0;
        if(file.exists()){
            for (File fileEntry : file.listFiles()) {
                size += fileEntry.length();
            }
        }
        return size;
    }

    public static float calculate(String path, int unit)
    {
        long sizeInBytes = calculate(path);
        return calculate(sizeInBytes, unit);
    }

    private static float calculate(long longInBytes, int unit){
        switch (unit){
            case GB:
                return FileSizeConverter.bytesToGB(longInBytes);
            case MB:
                return FileSizeConverter.bytesToMB(longInBytes);
            case KB:
                return FileSizeConverter.bytesToKb(longInBytes);
            default:
                return longInBytes;
        }
    }
}