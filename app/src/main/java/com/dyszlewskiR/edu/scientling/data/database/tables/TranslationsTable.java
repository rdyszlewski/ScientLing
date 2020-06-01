package com.dyszlewskiR.edu.scientling.data.database.tables;

/**
 * Created by Razjelll on 11.11.2016.
 */

public class TranslationsTable {
    public static final String TABLE_NAME = "Translations";
    public static final String ALIAS = "T";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumns() {
        String[] columns = new String[2];
        columns[TranslationsColumns.ID_POSITION] = TranslationsColumns.ID;
        columns[TranslationsColumns.CONTENT_POSITION] = TranslationsColumns.CONTENT;

        return columns;
    }

    public static class TranslationsColumns {
        public static final String ID = "id";
        public static final String CONTENT = "content";

        public static final int ID_POSITION = 0;
        public static final int CONTENT_POSITION = 1;

        public static final int COLUMNS_COUNT = 2;
    }
}
