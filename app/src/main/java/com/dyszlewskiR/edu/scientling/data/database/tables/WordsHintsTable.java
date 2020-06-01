package com.dyszlewskiR.edu.scientling.data.database.tables;

/**
 * Created by Razjelll on 22.12.2016.
 */

public class WordsHintsTable {
    public static final String TABLE_NAME = "WordsHints";
    public static final String ALIAS = "WH";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static class WordsHintsColumns {
        public static final String WORD_FK = "word_fk";
        public static final String HINT_FK = "hint_fk";

        public static final int WORD_FK_POSITION = 0;
        public static final int HINT_FK_POSITION = 1;
    }

    public static final int COLUMNS_COUNT = 2;

    public static String[] getColumns() {
        String[] columns = new String[COLUMNS_COUNT];
        columns[WordsHintsColumns.WORD_FK_POSITION] = WordsHintsColumns.WORD_FK;
        columns[WordsHintsColumns.HINT_FK_POSITION] = WordsHintsColumns.HINT_FK;
        return columns;
    }
}
