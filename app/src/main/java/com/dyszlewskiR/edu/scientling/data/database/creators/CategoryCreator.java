package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.models.entity.Category;

import static com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable.CategoriesColumns.ID;
import static com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable.CategoriesColumns.NAME;

public class CategoryCreator  {

    public static Category createFromCursor(Cursor cursor) {
        Category category = new Category();
        int columnsCount = cursor.getColumnCount();
        for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            switch (cursor.getColumnName(columnIndex)) {
                case ID:
                    category.setId(cursor.getLong(columnIndex));
                    break;
                case NAME:
                    category.setName(cursor.getString(columnIndex));
                    break;
            }
        }
        return category;
    }

    private static final String CATEGORY_ID = "category";
}
