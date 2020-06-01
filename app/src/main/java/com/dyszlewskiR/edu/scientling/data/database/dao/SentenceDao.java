package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.data.database.tables.ExampleSentences;
import com.dyszlewskiR.edu.scientling.data.database.tables.SentencesTable;
import com.dyszlewskiR.edu.scientling.data.database.creators.SentenceCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;

import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.SentencesTable.SentencesColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.SentencesTable.TABLE_NAME;

/**
 * Created by Razjelll on 08.11.2016.
 */

public class SentenceDao extends BaseDao<Sentence> {


    private final String INSERT_STATEMENT =
            "INSERT INTO " + TABLE_NAME + "("
                    + SentencesColumns.CONTENT + ", " + SentencesColumns.TRANSLATION
                    + ") VALUES (?,?)";

    private final String SELECT_LINK_STATEMENT =
            "SELECT S.* FROM " + ExampleSentences.TABLE_NAME + " ES JOIN "
                    + SentencesTable.TABLE_NAME + " S ON ES."
                    + ExampleSentences.ExampleSentencesColumns.SENTENCE_FK + "= S."
                    + SentencesColumns.ID + " WHERE ES." + ExampleSentences.ExampleSentencesColumns.WORD_FK
                    + " = ?";

    public SentenceDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = mDb.compileStatement(INSERT_STATEMENT);
        mTableColumns = SentencesTable.getColumns();
    }

    @Override
    public long save(Sentence entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(SentencesColumns.CONTENT_POSITION, entity.getContent());
        if(entity.getTranslation() != null){
            mInsertStatement.bindString(SentencesColumns.TRANSLATION_POSITION, entity.getTranslation());
        } else {
            mInsertStatement.bindNull(SentencesColumns.TRANSLATION_POSITION);
        }

        return mInsertStatement.executeInsert();
    }

    @Override
    public void update(Sentence entity) {
        final ContentValues values = new ContentValues();
        values.put(SentencesColumns.CONTENT, entity.getContent());
        if(entity.getTranslation() != null){
            values.put(SentencesColumns.TRANSLATION, entity.getTranslation());
        } else {
            values.putNull(SentencesColumns.TRANSLATION);
        }

        String where = SentencesColumns.ID + "= ?";
        String[] whereArguments = new String[]{String.valueOf(entity.getId())};
        mDb.update(TABLE_NAME, values, where, whereArguments);
    }

    @Override
    public int delete(Sentence entity) {
        long id = entity.getId();
        if (id > 0) {
            String where = SentencesColumns.ID + "= ?";
            String[] whereArguments = new String[]{String.valueOf(id)};
            return mDb.delete(TABLE_NAME, where, whereArguments);
        }
        return 0;
    }

    @Override
    public Sentence get(long id) {
        Sentence sentence = null;
        String where = SentencesColumns.ID + "= ?";
        String[] whereArguments = new String[]{String.valueOf(id)};
        Cursor cursor = mDb.query(TABLE_NAME, mTableColumns, where, whereArguments,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            sentence = SentenceCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return sentence;
    }

    @Override
    public List<Sentence> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, String limit) {
        List<Sentence> sentencesList = new ArrayList<>();
        Cursor cursor = mDb.query(distinct, TABLE_NAME, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        if (cursor.moveToFirst()) {
            Sentence sentence;
            do {
                sentence = SentenceCreator.createFromCursor(cursor);
                if (sentence != null) {
                    sentencesList.add(sentence);
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return sentencesList;
    }

    //Tutaj znajdują sie metody, ktróre obsługują tabele ExampelSentences

    public void link(long sentenceId, long wordId) {
        final ContentValues values = new ContentValues();
        values.put(ExampleSentences.ExampleSentencesColumns.WORD_FK, wordId);
        values.put(ExampleSentences.ExampleSentencesColumns.SENTENCE_FK, sentenceId);
        mDb.insert(ExampleSentences.TABLE_NAME, null, values);
    }

    public void unlink(long wordId) {
        String where = ExampleSentences.ExampleSentencesColumns.WORD_FK + " =?";
        String[] whereArguments = new String[]{String.valueOf(wordId)};
        mDb.delete(ExampleSentences.TABLE_NAME, where, whereArguments);
    }

    public List<Sentence> getLinked(long wordId) {
        List<Sentence> sentencesList = new ArrayList<>();
        String[] whereArguments = new String[]{String.valueOf(wordId)};
        Cursor cursor = mDb.rawQuery(SELECT_LINK_STATEMENT, whereArguments);
        if (cursor.moveToFirst()) {
            Sentence sentence;
            do {
                sentence = SentenceCreator.createFromCursor(cursor);
                if (sentence != null) {
                    sentencesList.add(sentence);
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return sentencesList;
    }

    public Sentence getByContent(String content) {
        Sentence sentence = null;
        String where = SentencesColumns.CONTENT + " = ?";
        String[] whereArguments = {content};
        Cursor cursor = mDb.query(SentencesTable.TABLE_NAME, mTableColumns, where, whereArguments,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            sentence = SentenceCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return sentence;
    }

    /**
     * Metoda usuwająca wszystkie niepowiązane zdania. Metoda usuwa takie zdanie, których klucze nie
     * znajdują sie w tabeli ExampleSentences
     * <p>
     * DELETE FROM Sentences
     * WHERE id IS NOT IN(
     * SELECT sentence_fk
     * FROM EampleSentences )
     */
    public int deleteUnlinked() {
        String statement = new StringBuilder()
                //.append("DELETE FROM ").append(SentencesTable.TABLE_NAME)
                //.append(" WHERE ")
                .append(SentencesColumns.ID).append(" NOT IN ")
                .append("(SELECT ").append(ExampleSentences.ExampleSentencesColumns.SENTENCE_FK)
                .append(" FROM ").append(ExampleSentences.TABLE_NAME).append(")").toString();
        /*mDb.execSQL(statement);
        String where = SentencesColumns.ID + " NOT IN ?";
        String whereArg = "( SELECT " + ExampleSentences.ExampleSentencesColumns.SENTENCE_FK
                + " FROM " + ExampleSentences.TABLE_NAME + ")";*/

        return mDb.delete(SentencesTable.TABLE_NAME, statement, null);

    }
}
