package com.dyszlewskiR.edu.scientling.data.database.tables;

public class ImagesTable {
    public static final String TABLE_NAME = "Images";
    public static final String ALIAS = "I";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static class ImagesColumns {
        public static final String WORD_FK = "word_fk";
        public static final String BITMAP = "bitmap";
        public static final String PATH = "path";

        public static final int WORD_FK_POSITION = 0;
        public static final int BITMAP_POSITION = 1;
        public static final int PATH_POSITION = 2;

        public static final int COLUMNS_COUNT = 3;
    }

    public static String[] getColumns() {
        String[] columns = new String[ImagesColumns.COLUMNS_COUNT];
        columns[ImagesColumns.WORD_FK_POSITION] = ImagesColumns.WORD_FK;
        columns[ImagesColumns.BITMAP_POSITION] = ImagesColumns.BITMAP;
        columns[ImagesColumns.PATH_POSITION] = ImagesColumns.PATH;
        return columns;
    }
}
