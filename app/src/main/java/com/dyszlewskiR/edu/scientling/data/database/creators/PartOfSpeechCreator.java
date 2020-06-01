package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.data.database.tables.PartsOfSpeechTable;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;

/**
 * Created by Razjelll on 22.04.2017.
 */

public class PartOfSpeechCreator {

    public static PartOfSpeech createFromCursor(Cursor cursor){
        PartOfSpeech partOfSpeech = null;
        if (cursor != null) {
            partOfSpeech = new PartOfSpeech();
            partOfSpeech.setId(cursor.getLong(PartsOfSpeechTable.PartsOfSpeechColumns.ID_POSITION));
            partOfSpeech.setName(cursor.getString(PartsOfSpeechTable.PartsOfSpeechColumns.NAME_POSITION));
        }
        return partOfSpeech;
    }
}
