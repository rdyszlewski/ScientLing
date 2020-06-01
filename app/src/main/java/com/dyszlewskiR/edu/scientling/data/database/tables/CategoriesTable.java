package com.dyszlewskiR.edu.scientling.data.database.tables;

public class CategoriesTable {
    public static final String TABLE_NAME = "Categories";
    private static final String ALIAS = "C";
    public static final String ALIAS_DOT = ALIAS + ".";

    public static String[] getColumn() {
        String[] columns = new String[CategoriesColumns.COLUMNS_COUNT];
        columns[CategoriesColumns.ID_POSITION] = CategoriesColumns.ID;
        columns[CategoriesColumns.NAME_POSITION] = CategoriesColumns.NAME;
        return columns;
    }

    public static class CategoriesColumns {
        public static final String ID = "id";
        public static final String NAME = "name";

        public static final String NAME_ALIAS = "category_name";

        public static final int ID_POSITION = 0;
        public static final int NAME_POSITION = 1;

        public static final int COLUMNS_COUNT = 2;
    }
}
