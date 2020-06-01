package com.dyszlewskiR.edu.scientling.data.database.tables;

public class DefinitionsTable {
    public static final String TABLE_NAME = "Definitions";
    private static final String ALIAS = "D";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumns() {
        String[] columns = new String[DefinitionsColumns.COLUMNS_COUNT];
        columns[DefinitionsColumns.ID_POSITION] = DefinitionsColumns.ID;
        columns[DefinitionsColumns.CONTENT_POSITION] = DefinitionsColumns.CONTENT;
        columns[DefinitionsColumns.TRANSLATION_POSITION] = DefinitionsColumns.TRANSLATION;

        return columns;
    }

    public static class DefinitionsColumns {
        public static final String ID = "id";
        public static final String CONTENT = "content";
        public static final String TRANSLATION = "translation";

        public static final String CONTENT_ALIAS = "definition_content";
        public static final String TRANSLATION_ALIAS = "definition_translation";

        public static final int ID_POSITION = 0;
        public static final int CONTENT_POSITION = 1;
        public static final int TRANSLATION_POSITION = 2;

        public static final int COLUMNS_COUNT = 3;
    }
}

