package com.dyszlewskiR.edu.scientling.data.file;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class FileSystem {

    public static File saveFile(byte[] data, String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);
        outputStream.close();
        return file;
    }

    public static File getFile(String path){
        return  new File(path);
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.delete();
    }

    public static void deleteDirectory(String path){
        File directory = new File(path);
        deleteDirectory(directory);
    }

    private static void deleteDirectory(File file){
        if(file.exists()){
            if(file.isDirectory()){
                for(File fileEntry : file.listFiles()){
                    deleteDirectory(fileEntry);
                }
                file.delete();
            } else { //file.isFile()
                file.delete();
            }
        }
    }

    public static boolean checkFileExist(String path){
        File file = new File(path);
        return file.exists();
    }

    public static boolean isDirectoryEmpty(String path){
        File directory = new File(path);
        if(directory.exists() && directory.isDirectory()){
            return directory.listFiles().length > 0;
        }
        return false;
    }


    public static long getFileSize(String path){
        File file = new File(path);
        if(file.exists()){
            if(file.isFile()){
                return getFileSize(file);
            } else {
                return getDirectorySize(file);
            }
        }
        return 0;
    }


    private static long getFileSize(File file){
        return file.length();
    }

    private static long getDirectorySize(File file){
        long size = 0;
        for(File fileEntry : file.listFiles()){
            if(fileEntry.isFile()){
                size += fileEntry.length();
            }
        }
        return size;
    }
}
