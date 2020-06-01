package com.dyszlewskiR.edu.scientling.data.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.BitmapUtils;
import com.dyszlewskiR.edu.scientling.utils.UriUtils;

import java.io.File;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za zapisywanie obrazków i nagrań.
 * TODO Klasa powinna odczytywaj w jakiej pamięci ma zostać zapisany plik
 * Klasa stanowi dodatkową warstwę zapisu pliku, na której można wykonywać dodatkowe działania,
 * takie jak przeskalowanie obrazków
 */
public class MediaFileSystem {

    private static final int MAX_IMAGE_SIZE = 300;
    public static final String IMAGES = "images";
    public static final String RECORDS = "records";

    private static String getImagesCatalog(String setCatalog) {
        return setCatalog + "/" + IMAGES;
    }

    private static String getRecordsCatalog(String setCatalog) {
        return setCatalog + "/" + RECORDS;
    }

    public static String getMediaCatalog(String setCatalog, MediaType mediaType) {
        switch (mediaType) {
            case IMAGES:
                return getImagesCatalog(setCatalog);
            case RECORDS:
                return getRecordsCatalog(setCatalog);
        }
        return null;
    }

    private static Storage getStorage() {
        //TODO zrobić wykrywanie z jakiej pamięci korjzystamy
        return new InternalStorage();
    }

    public static File saveMedia(String filename, String setCatalog, byte[] data, boolean resize, MediaType mediaType, Context context) throws IOException {
        checkAndCreateMediaCatalogs(setCatalog, mediaType, context);
        if (mediaType == MediaType.IMAGES && resize) {
            data = getResizedData(data, MAX_IMAGE_SIZE);
        }
        return getStorage().saveFile(filename, getMediaCatalog(setCatalog, mediaType), data, context);
    }

    private static byte[] getResizedData(byte[] data, int maxSize) {
        Bitmap bitmap = BitmapUtils.getBitmap(data);
        //w przypadku obrazka mniejszego od rozmiaru do którego chcieliśmy konwertować zwracamy orginalne dane
        if (bitmap.getWidth() < maxSize && bitmap.getHeight() < maxSize) {
            return data;
        }
        bitmap = BitmapUtils.resize(bitmap, maxSize, false);
        return BitmapUtils.toByteArray(bitmap);
    }

    public static File saveMedia(String filename, String setCatalog, Uri mediaUri, boolean resize, MediaType mediaType, Context context) throws IOException {
        byte[] data = UriUtils.toByteArray(mediaUri, context);
        return saveMedia(filename, setCatalog, data, resize, mediaType, context);
    }

    public static void checkAndCreateMediaCatalogs(String setCatalog, MediaType mediaType, Context context) {
        Storage storage = getStorage();
        File setDirectory = storage.getDirectory(setCatalog, context);
        if (!setDirectory.exists()) {
            setDirectory.mkdir();
        }
        File mediaDirectory = storage.getDirectory(getMediaCatalog(setCatalog, mediaType), context);
        if (!mediaDirectory.exists()) {
            mediaDirectory.mkdir();
        }
    }

    public static Uri getMediaUri(String filename, String setCatalog, MediaType mediaType, Context context) {
        File file = getStorage().getFile(filename, getMediaCatalog(setCatalog, mediaType), context);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getMediaUriFromCache(String filename, Context context){
        File file = getStorage().getFileFromCache(filename, context);
        if(file.exists()){
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getMedia(String filename, String setCatalog, MediaType mediaType, Context context) {
        return getStorage().getFile(filename, getMediaCatalog(setCatalog, mediaType), context);
    }

    public static File getCatalog(String catalog, Context context) {
        return getStorage().getDirectory(catalog, context);
    }

    public static boolean deleteMedia(String filename, String setCatalog, MediaType mediaType, Context context) {
        if (filename != null && setCatalog != null) {
            return getStorage().deleteFile(filename, getMediaCatalog(setCatalog, mediaType), context);
        }
        return false;
    }

    public static boolean deleteMediaFromCache(String filename, Context context){
        return getStorage().deleteFileFromCache(filename, context);
    }

    public static boolean deleteFile(Uri imageUri) {
        File file = new File(imageUri.getPath());
        return file.exists() && file.delete();
    }

    public static void deleteCatalog(String catalogName, Context context) {
        getStorage().deleteDirectory(catalogName, context);
    }

    public static void deleteMediaCatalog(String setCatalog, MediaType mediaType, Context context) {
        getStorage().deleteDirectory(getMediaCatalog(setCatalog, mediaType), context);
    }

    public static boolean hasMedia(String setCatalog, MediaType mediaType, Context context) {
        return getStorage().isDirectoryEmpty(getMediaCatalog(setCatalog, mediaType), context);
    }

    public static boolean checkMediaExist(String filename, String setCatalog, MediaType mediaType, Context context) {
        return getStorage().checkFileExist(filename, getMediaCatalog(setCatalog, mediaType), context);
    }

    public static boolean checkDirectoryExist(String catalogName, Context context) {
        return getStorage().checkDirectoryExist(catalogName, context);
    }

    public static long getMediasSize(String setCatalog, MediaType mediaType, Context context) {
        return getStorage().getDirectorySize(getMediaCatalog(setCatalog, mediaType), context);
    }

    public static String getMediaPath(String setCatalog, MediaType mediaType, Context context) {
        return getStorage().getPath(getMediaCatalog(setCatalog, mediaType), context);
    }

}
