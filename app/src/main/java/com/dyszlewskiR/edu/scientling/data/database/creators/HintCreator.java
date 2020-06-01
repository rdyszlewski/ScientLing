package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.models.entity.Hint;

import static com.dyszlewskiR.edu.scientling.data.database.tables.HintsTable.HintsColumns.CONTENT;
import static com.dyszlewskiR.edu.scientling.data.database.tables.HintsTable.HintsColumns.ID;

public class HintCreator  {

    public static Hint createFromCursor(Cursor cursor) {
        Hint hint = new Hint();
        int columnsCount = cursor.getColumnCount();
        for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            switch (cursor.getColumnName(columnIndex)) {
                case ID:
                    hint.setId(cursor.getLong(columnIndex));
                    break;
                case CONTENT:
                    hint.setContent(cursor.getString(columnIndex));
                    break;
            }
        }
        return hint;
    }
}
