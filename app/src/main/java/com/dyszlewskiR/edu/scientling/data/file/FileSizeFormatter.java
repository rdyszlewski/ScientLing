package com.dyszlewskiR.edu.scientling.data.file;

public class FileSizeFormatter {

    private static long KB = 1024;
    private static long MB = 1048576;
    private static long GB = 1073741824;

    private static String FORMAT = "%.2f";

    public static String getSize(long bytes){
        if(bytes > GB){
            return String.format(FORMAT,FileSizeConverter.bytesToGB(bytes)) + " GB";
        } else if(bytes>MB){
            return String.format(FORMAT,FileSizeConverter.bytesToMB(bytes)) + " MB";
        } else if(bytes>KB){
            return String.format(FORMAT,FileSizeConverter.bytesToKb(bytes)) + " Kb";
        } else {
            return String.valueOf(bytes) + " b";
        }
    }
}
