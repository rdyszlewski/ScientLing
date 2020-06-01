package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable;
import com.dyszlewskiR.edu.scientling.data.database.tables.DefinitionsTable;
import com.dyszlewskiR.edu.scientling.data.database.tables.LessonsTable;
import com.dyszlewskiR.edu.scientling.data.database.tables.PartsOfSpeechTable;
import com.dyszlewskiR.edu.scientling.data.database.tables.WordsTable;
import com.dyszlewskiR.edu.scientling.data.database.utils.QueryReader;
import com.dyszlewskiR.edu.scientling.data.database.creators.WordCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.WordsTable.WordsColumns;

public class WordDao extends BaseDao<Word> {

    private final String QUERIES_FOLDER = "sql/queries/";
    private final String SELECT_QUERY_NAME = "selectFullWords.sql";

    private final String INSERT_STATEMENT =
            "INSERT INTO " + WordsTable.TABLE_NAME + "("
                    + WordsColumns.CONTENT + ", " + WordsColumns.TRANSCRIPTION + ", "
                    + WordsColumns.DEFINITION_FK + ", "
                    + WordsColumns.LESSON_FK + ", " + WordsColumns.PART_OF_SPEECH_FK + ", "
                    + WordsColumns.CATEGORY_FK + ", " + WordsColumns.DIFFICULT + ", "
                    + WordsColumns.MASTER_LEVEL + " , " + WordsColumns.SELECTED + " , "
                    + WordsColumns.OWN + " , " + WordsColumns.LEARNING_DATE + " , "
                    + WordsColumns.IMAGE_NAME + " , " + WordsColumns.RECORD_NAME
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?, ?,?)";

    private final String SELECT_STATEMENT =
            " W." + WordsColumns.ID + ", W." + WordsColumns.CONTENT
                    + ", W." + WordsColumns.TRANSCRIPTION + ", W." + WordsColumns.DEFINITION_FK
                    + ", W." + WordsColumns.LESSON_FK + ", W." + WordsColumns.PART_OF_SPEECH_FK
                    + ", W." + WordsColumns.CATEGORY_FK + ", W." + WordsColumns.DIFFICULT + ", W." + WordsColumns.MASTER_LEVEL
                    + ", W." + WordsColumns.SELECTED + ", W." + WordsColumns.OWN + ", W." + WordsColumns.LEARNING_DATE
                    + ", W." + WordsColumns.IMAGE_NAME + ", W." + WordsColumns.RECORD_NAME
                    + ", D." + DefinitionsTable.DefinitionsColumns.CONTENT + " AS " + DefinitionsTable.DefinitionsColumns.CONTENT_ALIAS
                    + ", D." + DefinitionsTable.DefinitionsColumns.TRANSLATION + " AS " + DefinitionsTable.DefinitionsColumns.TRANSLATION_ALIAS
                    + ", P." + PartsOfSpeechTable.PartsOfSpeechColumns.NAME + " AS " + PartsOfSpeechTable.PartsOfSpeechColumns.NAME_ALIAS
                    + ", C." + CategoriesTable.CategoriesColumns.NAME + " AS " + CategoriesTable.CategoriesColumns.NAME_ALIAS
                    + " FROM " + WordsTable.TABLE_NAME + " W LEFT OUTER JOIN " + DefinitionsTable.TABLE_NAME + " D ON "
                    + "W." + WordsColumns.DEFINITION_FK + " = " + "D." + DefinitionsTable.DefinitionsColumns.ID
                    + " LEFT OUTER JOIN " + PartsOfSpeechTable.TABLE_NAME + " P ON W." + WordsColumns.PART_OF_SPEECH_FK
                    + " = P." + PartsOfSpeechTable.PartsOfSpeechColumns.ID
                    + " LEFT OUTER JOIN " + CategoriesTable.TABLE_NAME + " C ON "
                    + "W." + WordsColumns.CATEGORY_FK + " = C." + CategoriesTable.CategoriesColumns.ID;
    // + " WHERE W." + WordsColumns.ID + " = ?";

    private final String SIMPLE_SELECT_STATEMENT =
            WordsColumns.ID + ", " + WordsColumns.CONTENT
                    + " FROM " + WordsTable.TABLE_NAME;

    private final String WHERE_ID = WordsColumns.ID + "= ?";

    public WordDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = mDb.compileStatement(INSERT_STATEMENT);
        mTableColumns = WordsTable.getColumns();
    }

    @Override
    public long save(Word entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(WordsColumns.CONTENT_POSITION, entity.getContent());
        if (entity.getTranscription() != null) {
            mInsertStatement.bindString(WordsColumns.TRANSCRIPTION_POSITION, entity.getTranscription());

        } else {
            mInsertStatement.bindNull(WordsColumns.TRANSCRIPTION_POSITION);
        }
        if (entity.getDefinition() != null) {
            mInsertStatement.bindLong(WordsColumns.DEFINITION_FK_POSITION, entity.getDefinition().getId());
        } else {
            mInsertStatement.bindNull(WordsColumns.DEFINITION_FK_POSITION);
        }
        mInsertStatement.bindLong(WordsColumns.LESSON_FK_POSITION, entity.getLessonId());
        if (entity.getPartsOfSpeech() != null) {
            mInsertStatement.bindLong(WordsColumns.PART_OF_SPEECH_FK_POSITION, entity.getPartsOfSpeech().getId());
        } else {
            mInsertStatement.bindNull(WordsColumns.PART_OF_SPEECH_FK_POSITION);
        }
        if (entity.getCategory() != null) {
            mInsertStatement.bindLong(WordsColumns.CATEGORY_FK_POSITION, entity.getCategory().getId());
        } else {
            mInsertStatement.bindNull(WordsColumns.CATEGORY_FK_POSITION);
        }
        mInsertStatement.bindLong(WordsColumns.DIFFICULT_POSITION, entity.getDifficult());
        mInsertStatement.bindLong(WordsColumns.MASTER_LEVEL_POSITION, entity.getMasterLevel());
        long selected = entity.isSelected() ? 1 : 0;
        mInsertStatement.bindLong(WordsColumns.SELECTED_POSITION, selected);
        long own = entity.isOwn() ? 1 : 0;
        mInsertStatement.bindLong(WordsColumns.OWN_POSITION, own);
        if (entity.isLearningDate()) {
            long date = entity.getLearningDate();
            mInsertStatement.bindLong(WordsColumns.LEARNING_DATE_POSITION, date);
        }
        if (entity.getImageName() != null) {
            mInsertStatement.bindString(WordsColumns.IMAGE_NAME_POSITION, entity.getImageName());
        } else {
            mInsertStatement.bindNull(WordsColumns.IMAGE_NAME_POSITION);
        }

        if (entity.getRecordName() != null) {
            mInsertStatement.bindString(WordsColumns.RECORD_NAME_POSITION, entity.getRecordName());
        } else {
            mInsertStatement.bindNull(WordsColumns.RECORD_NAME_POSITION);
        }
        return mInsertStatement.executeInsert();
    }

    @Override
    public void update(Word entity) {
        final ContentValues values = new ContentValues();
        values.put(WordsColumns.CONTENT, entity.getContent());
        values.put(WordsColumns.TRANSCRIPTION, entity.getTranscription());

        if (entity.getDefinition() != null) {
            values.put(WordsColumns.DEFINITION_FK, entity.getDefinition().getId());
        } else {
            values.putNull(WordsColumns.DEFINITION_FK);
        }

        values.put(WordsColumns.LESSON_FK, entity.getLessonId());

        if (entity.getPartsOfSpeech() != null) {
            values.put(WordsColumns.PART_OF_SPEECH_FK, entity.getPartsOfSpeech().getId());
        } else {
            values.putNull(WordsColumns.PART_OF_SPEECH_FK);
        }

        if (entity.getCategory() != null) {
            values.put(WordsColumns.CATEGORY_FK, entity.getCategory().getId());
        } else {
            values.putNull(WordsColumns.CATEGORY_FK);
        }

        values.put(WordsColumns.DIFFICULT, entity.getDifficult());
        values.put(WordsColumns.MASTER_LEVEL, entity.getMasterLevel());

        long selected = entity.isSelected() ? 1 : 0;
        values.put(WordsColumns.SELECTED, selected);

        long own = entity.isOwn() ? 1 : 0;
        values.put(WordsColumns.OWN, own);

        long date = entity.getLearningDate();
        values.put(WordsColumns.LEARNING_DATE, date);

        //wstawinaie obrazka
        if (entity.getImageName() != null) {
            values.put(WordsColumns.IMAGE_NAME, entity.getImageName());
        } else {
            values.putNull(WordsColumns.IMAGE_NAME);
        }
        //wstawianie nagrania
        if (entity.getRecordName() != null) {
            values.put(WordsColumns.RECORD_NAME, entity.getRecordName());
        } else {
            values.putNull(WordsColumns.RECORD_NAME);
        }

        String[] whereArguments = new String[]{String.valueOf(entity.getId())};
        mDb.update(WordsTable.TABLE_NAME, values, WHERE_ID, whereArguments);
    }

    /**
     * Metoda uaktualniająca słówka które spełniają podany warunek o nową wartośc w podanej kolumnie
     *
     * @param column             kolumna która ma zostać zmodyfikowana
     * @param value              wartość która ma zostać wstawiona do wybranej kolumny
     * @param selection          warunek okreslający które wiersze zostaną zmodyfikowane
     * @param selectionArguments argumenty warunku
     */
    public void update(String column, String value, String selection, String[] selectionArguments) {
        StringBuffer sqlBuilder = new StringBuffer();
        sqlBuilder.append("UPDATE ").append(WordsTable.TABLE_NAME)
                .append(" SET ").append(column).append(" = ").append(value)
                .append(" WHERE ").append(selection);
        mDb.execSQL(sqlBuilder.toString(), selectionArguments);
    }

    @Override
    public int delete(Word entity) {
        long id = entity.getId();
        if (id > 0) {
            String[] whereArguments = new String[]{String.valueOf(id)};
            return mDb.delete(WordsTable.TABLE_NAME, WHERE_ID, whereArguments);
        }
        return 0;
    }

    /**
     * Metoda usuwająca wszystkie słówka spełniające podany warunek
     *
     * @param selection          warunek, który muszą spełnić usuwane słówka
     * @param selectionArguments argumenty warunku
     */
    public int delete(String selection, String[] selectionArguments) {
        return mDb.delete(WordsTable.TABLE_NAME, selection, selectionArguments);
    }

    @Override
    public Word get(long id) {
        Word word = null;
        String[] whereArguments = new String[]{String.valueOf(id)};
        StringBuilder queryBuilder = null;

        try {
            String query = QueryReader.getQuery(QUERIES_FOLDER + SELECT_QUERY_NAME);
            queryBuilder = new StringBuilder(query);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert queryBuilder != null;
        queryBuilder.append(" WHERE W." + WordsColumns.ID + " = ?");
        Cursor cursor = mDb.rawQuery(queryBuilder.toString(), whereArguments);
        if (cursor.moveToFirst()) {
            word = WordCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return word;
    }

    @Override
    public List<Word> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                             String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = mDb.query(distinct, WordsTable.TABLE_NAME + " " + WordsTable.ALIAS, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        List<Word> wordsList = getWordsListFromCursor(cursor);
        closeCursor(cursor);
        return wordsList;
    }

    public List<Word> getAll(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs,
                             String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = mDb.query(distinct, table, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        List<Word> wordsList = getWordsListFromCursor(cursor);
        closeCursor(cursor);
        return wordsList;
    }

    private List<Word> getWordsListFromCursor(Cursor cursor) {
        List<Word> wordsList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            Word word;
            do {
                word = WordCreator.createFromCursor(cursor);
                if (word != null) {
                    wordsList.add(word);
                }
            } while (cursor.moveToNext());
        }
        return wordsList;
    }

    public List<Word> getAll(String statement, String selection, String[] selectionArgs,
                             String groupBy, String having, String orderBy, String limit) {
        String query = QueryBuilder.build(statement, selection, groupBy, having, orderBy, limit);
        Cursor cursor = mDb.rawQuery(query, selectionArgs);
        List<Word> words = getWordsListFromCursor(cursor);
        closeCursor(cursor);
        return words;
    }

    public List<Word> getAllWithJoins(boolean distinct, String selection, String[] selectionArgs,
                                      String groupBy, String having, String orderBy, String limit) {

        Cursor cursor = getAllCursor(distinct, selection,selectionArgs, groupBy, having, orderBy, limit);
        List<Word> wordsList = getWordsListFromCursor(cursor);
        closeCursor(cursor);
        if(BuildConfig.DEBUG)
            assert cursor.isClosed();

        return wordsList;
    }

    public Cursor getAllCursor(boolean distinct, String selection, String[] selectionArgs,
                               String groupBy, String having, String orderBy, String limit){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        if (distinct) {
            queryBuilder.append("DISTINCT ");
        }
        queryBuilder.append(SELECT_STATEMENT);
        queryBuilder.append(" LEFT OUTER JOIN ").append(LessonsTable.TABLE_NAME).append(" ");
        queryBuilder.append(LessonsTable.ALIAS).append(" ON ").append(WordsTable.ALIAS_DOT).append(WordsColumns.LESSON_FK);
        queryBuilder.append(" = ").append(LessonsTable.ALIAS_DOT).append(LessonsTable.LessonsColumns.ID);
        if (selection != null) {
            queryBuilder.append(" WHERE ").append(selection);
        }
        if (groupBy != null) {
            queryBuilder.append(" GROUP BY ").append(groupBy);
        }
        if (having != null) {
            queryBuilder.append(" HAVING ").append(having);
        }
        if (orderBy != null) {
            queryBuilder.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            queryBuilder.append(" LIMIT ").append(limit);
        }

        return mDb.rawQuery(queryBuilder.toString(), selectionArgs);
    }

    public List<Word> getSimpleWords(String selection, String[] selectionArgs, String orderBy,
                                     String limit) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(SIMPLE_SELECT_STATEMENT);
        if (selection != null) {
            queryBuilder.append(" WHERE ").append(selection);
        }
        if (orderBy != null) {
            queryBuilder.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            queryBuilder.append(" LIMIT ").append(limit);
        }
        Cursor cursor = mDb.rawQuery(queryBuilder.toString(), selectionArgs);
        List<Word> wordsList = getWordsListFromCursor(cursor);

        closeCursor(cursor);
        return wordsList;
    }

    public int getCount(String selection, String[] selectionArgs) {
        Cursor cursor = mDb.query(false, WordsTable.TABLE_NAME + " " + WordsTable.ALIAS, new String[]{"COUNT(1)"}, selection, selectionArgs,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return 0;
    }

    /**
     * Metoda służąca zarówno do pobierania listy nagrań jak i obrazów.
     *
     * @param query     zapytanie sql
     * @param arguments parametry zapytania
     * @return lista nazw
     */
    public List<String> getNamesList(String query, String[] arguments) {
        List<String> resultList = new ArrayList<>();
        Cursor cursor = mDb.rawQuery(query, arguments);
        if (cursor.moveToFirst()) {
            do {
                String imageName = cursor.getString(0);
                resultList.add(imageName);
            } while (cursor.moveToNext());
        }
        return resultList;
    }

    public int getIntValue(String column, String selection, String[] selectionArgs){
        String[] columns = {column};
        Cursor cursor = mDb.query(false, WordsTable.TABLE_NAME, columns, selection, selectionArgs, null,null,null,null);
        if(cursor.moveToFirst()){
            return cursor.getInt(0);
        }
        return -1;
    }
}
