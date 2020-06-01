package com.dyszlewskiR.edu.scientling.data.file;

import android.content.Context;

import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;

/**
 * Klasa odpowiedzialna za tworzenie nowych klas dla plików ktore chcemy zapisać.
 */

public class FileNameCreator {

    private static final String IMAGE_EXTENSION =".jpg";
    private static final String RECORD_EXTENSION = ".wav";

    /**
     * Liczba określająca ile pierwszych liter rootname ma posłużyć do utworzenia nazwy pliku.
     * Wartość -1 oznacza, że żadne ograniczenie nie będzie zastosowane.
     * Ograniczenie długości ma na celu zmniejszenie rozmiaru bazy danych.
     */
    private static final int ROOT_FILE_NAME_LENGTH = 6;
    private static final int ROOT_CATALOG_NAME_LENGTH = 20;

    /**
     * Metoda znajdująca pierwszą wolną nazwę utworzoną na podstawie rootName
     * Jeżeli rootName już istnieje na dysku, wtedy do nazwy zostanie dodana liczba a następnie
     * nastąpi ponowne sprawdzenie. W przypadku znalezienia podanej nazwy numer zostanie zwiększony
     * i nazwa zostanie sprawdzona jeszcze raz
     *
     * @param rootName  nazwa na podstawie której będzie tworzona nazwa dla pliku
     * @param catalog   katalog w którym bedzie sprawdzane istnienie pliku
     * @param extension rozszerzenie jakie będize miał plik
     * @return
     */
    private static String getFileName(String rootName, String catalog, String extension, Context context) {
        boolean found = true;
        int number = 0;
        int name_length = rootName.length() < ROOT_FILE_NAME_LENGTH ? rootName.length() : ROOT_FILE_NAME_LENGTH;
        String shortRootName = rootName.substring(0, name_length);
        String fileName = shortRootName + extension;
        do {
            if (number != 0) {
                fileName = shortRootName + number  + extension;
            }
            //find = FileUtils.checkFileExist(catalog, fileName);
            switch (extension){
                case IMAGE_EXTENSION:
                    found = MediaFileSystem.checkMediaExist(fileName, catalog, MediaType.IMAGES,context); break;
                case RECORD_EXTENSION:
                    found = MediaFileSystem.checkMediaExist(fileName, catalog,MediaType.RECORDS, context); break;
            }
            number++;
        } while (found);
        return fileName;
    }



    public static String getImageName(String rootName, String catalog, Context context){
        return getFileName(rootName, catalog, IMAGE_EXTENSION, context);
    }

    public static String getRecordName(String rootName, String catalog, Context context){
        return getFileName(rootName, catalog, RECORD_EXTENSION, context);
    }

    public static String getCatalogName(String rootName, Context context) {
        boolean find;
        int number = 0;
        String catalogName = rootName;
        do {
            if (number != 0) {
                catalogName = rootName + number;
            }
            find = MediaFileSystem.checkDirectoryExist(catalogName, context);
            number++;
        } while (find);
        return catalogName;
    }
}
