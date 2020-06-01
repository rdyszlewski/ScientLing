package com.dyszlewskiR.edu.scientling.data.database.tables;

/**
 * Created by Razjelll on 22.12.2016.
 */

public class HintsTable {
    public static final String TABLE_NAME = "Hints";
    public static final String ALIAS = "H";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static class HintsColumns {
        public static final String ID = "id";
        public static final String CONTENT = "content";

        public static final int ID_POSITION = 0;
        public static final int CONTENT_POSITION = 1;
    }

    public static final int COLUMNS_COUNT = 2;

    public static String[] getColumns() {
        String[] columns = new String[COLUMNS_COUNT];
        columns[HintsColumns.ID_POSITION] = HintsColumns.ID;
        columns[HintsColumns.CONTENT_POSITION] = HintsColumns.CONTENT;
        return columns;
    }

}
