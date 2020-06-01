package com.dyszlewskiR.edu.scientling.data.database.tables;

/**
 * Created by Razjelll on 11.11.2016.
 */

public class SentencesTable {
    public static final String TABLE_NAME = "Sentences";
    public static final String ALIAS = "S";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumns() {
        String[] columns = new String[SentencesColumns.COLUMNS_COUNT];
        columns[SentencesColumns.ID_POSITION] = SentencesColumns.ID;
        columns[SentencesColumns.CONTENT_POSITION] = SentencesColumns.CONTENT;
        columns[SentencesColumns.TRANSLATION_POSITION] = SentencesColumns.TRANSLATION;

        return columns;
    }

    public static class SentencesColumns {
        public static final String ID = "id";
        public static final String CONTENT = "content";
        public static final String TRANSLATION = "translation";

        public static final int ID_POSITION = 0;
        public static final int CONTENT_POSITION = 1;
        public static final int TRANSLATION_POSITION = 2;

        public static final int COLUMNS_COUNT = 3;
    }
}
