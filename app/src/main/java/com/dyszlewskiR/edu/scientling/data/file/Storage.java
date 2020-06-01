package com.dyszlewskiR.edu.scientling.data.file;


import android.content.Context;

import java.io.File;
import java.io.IOException;

public abstract class Storage {
    
    public  File saveFile(String filename, String catalog, byte[] data, Context context) throws IOException {
        return FileSystem.saveFile(data, getPath(filename, catalog, context));
    }

    protected abstract String getPath(String filename, String catalog, Context context);

    public boolean deleteFile(String filename, String catalog, Context context){
        return FileSystem.deleteFile(getPath(filename, catalog, context));
    }

    //TODO dorobić usuwanie za pomocą Uri

    public void deleteDirectory(String directoryPath, Context context){
        FileSystem.deleteDirectory(getPath(directoryPath, context));
    }

    public abstract String getPath(String catalog, Context context);
    //TODO dorobić metodę zwracającą uri pliku

    public File getFile(String name, String catalog, Context context){
        return FileSystem.getFile(getPath(name, catalog, context));
    }

    public File getDirectory(String catalog, Context context){
        return FileSystem.getFile(getPath(catalog, context));
    }

    public File saveFileInCache(String filename, byte[] data, Context context) throws IOException {
        return FileSystem.saveFile(data, getCachePath(filename, context));
    }

    protected abstract String getCachePath(String filename, Context context);

    public File getFileFromCache(String filename, Context context){
        return FileSystem.getFile(getCachePath(filename, context));
    }

    public boolean deleteFileFromCache(String filename, Context context){
        return FileSystem.deleteFile(getCachePath(filename, context));
    }

    //TODO Uri createEmptyFileInCache

    public long getFileSize(String filename, String catalog, Context context){
        return FileSystem.getFileSize(getPath(filename,catalog, context ));
    }

    public long getDirectorySize(String catalog, Context context){
        return FileSystem.getFileSize(getPath(catalog, context));
    }

    public boolean checkFileExist(String filename, String catalog, Context context){
        if(filename == null || filename.isEmpty() || catalog== null || catalog.isEmpty()){
            return false;
        }
        return FileSystem.checkFileExist(getPath(filename, catalog, context));
    }

    public boolean checkDirectoryExist(String catalogName, Context context){
        return FileSystem.checkFileExist(getPath(catalogName, context));
    }

    public boolean isDirectoryEmpty(String catalog, Context context){
        return FileSystem.isDirectoryEmpty(getPath(catalog, context));
    }

}
