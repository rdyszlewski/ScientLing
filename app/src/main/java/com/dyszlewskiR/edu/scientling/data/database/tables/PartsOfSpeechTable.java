package com.dyszlewskiR.edu.scientling.data.database.tables;

/**
 * Created by Razjelll on 11.11.2016.
 */

public class PartsOfSpeechTable {
    public static final String TABLE_NAME = "PartsOfSpeech";
    public static final String ALIAS = "POS";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumns() {
        String[] columns = new String[PartsOfSpeechColumns.COLUMNS_COUNT];
        columns[PartsOfSpeechColumns.ID_POSITION] = PartsOfSpeechColumns.ID;
        columns[PartsOfSpeechColumns.NAME_POSITION] = PartsOfSpeechColumns.NAME;

        return columns;
    }

    public static class PartsOfSpeechColumns {
        public static final String ID = "id";
        public static final String NAME = "name";

        public static final String NAME_ALIAS = "part_of_speech_name";

        public static final int ID_POSITION = 0;
        public static final int NAME_POSITION = 1;

        public static final int COLUMNS_COUNT = 2;
    }
}
