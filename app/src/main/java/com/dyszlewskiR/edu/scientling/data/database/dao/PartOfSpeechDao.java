package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.data.database.tables.PartsOfSpeechTable;
import com.dyszlewskiR.edu.scientling.data.database.creators.PartOfSpeechCreator;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;

import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.PartsOfSpeechTable.PartsOfSpeechColumns;

/**
 * Created by Razjelll on 09.11.2016.
 */

public class PartOfSpeechDao extends BaseDao<PartOfSpeech> {


    private final String INSERT_STATEMENT =
            "INSERT INTO " + PartsOfSpeechTable.TABLE_NAME + "("
                    + PartsOfSpeechColumns.NAME
                    + ") VALUES (?)";
    private final String WHERE_ID = PartsOfSpeechColumns.ID + "= ?";


    public PartOfSpeechDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = mDb.compileStatement(INSERT_STATEMENT);
        mTableColumns = PartsOfSpeechTable.getColumns();
    }

    @Override
    public long save(PartOfSpeech entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(PartsOfSpeechColumns.NAME_POSITION, entity.getName());
        return mInsertStatement.executeInsert();
    }

    @Override
    public void update(PartOfSpeech entity) {
        final ContentValues values = new ContentValues();
        values.put(PartsOfSpeechColumns.NAME, entity.getName());

        String[] whereArguments = new String[]{String.valueOf(entity.getId())};
        mDb.update(PartsOfSpeechTable.TABLE_NAME, values, WHERE_ID, whereArguments);
    }

    @Override
    public int delete(PartOfSpeech entity) {
        long id = entity.getId();
        if (id > 0) {
            String[] whereArguments = new String[]{String.valueOf(id)};
            return mDb.delete(PartsOfSpeechTable.TABLE_NAME, WHERE_ID, whereArguments);
        }
        return 0;
    }

    @Override
    public PartOfSpeech get(long id) {
        PartOfSpeech partOfSpeech = null;
        String[] whereArguments = new String[]{String.valueOf(id)};
        Cursor cursor = mDb.query(PartsOfSpeechTable.TABLE_NAME, mTableColumns, WHERE_ID, whereArguments,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            partOfSpeech = PartOfSpeechCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return partOfSpeech;
    }

    @Override
    public List<PartOfSpeech> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                     String groupBy, String having, String orderBy, String limit) {
        List<PartOfSpeech> partsOfSpeechList = new ArrayList<>();
        Cursor cursor = mDb.query(distinct, PartsOfSpeechTable.TABLE_NAME, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        if (cursor.moveToFirst()) {
            PartOfSpeech partOfSpeech;
            do {
                partOfSpeech = PartOfSpeechCreator.createFromCursor(cursor);
                if (partOfSpeech != null) {
                    partsOfSpeechList.add(partOfSpeech);
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return partsOfSpeechList;
    }
}
