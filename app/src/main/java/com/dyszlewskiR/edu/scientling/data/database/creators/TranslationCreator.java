package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.models.entity.Translation;

import static com.dyszlewskiR.edu.scientling.data.database.tables.TranslationsTable.TranslationsColumns.*;

public class TranslationCreator {

    public static Translation createFromCursor(Cursor cursor){
        Translation translation = null;
        if (cursor != null) {
            translation = new Translation();
            translation.setId(cursor.getLong(ID_POSITION));
            translation.setContent(cursor.getString(CONTENT_POSITION));
        }
        return translation;
    }
}


