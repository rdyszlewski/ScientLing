package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.tables.LessonsTable;
import com.dyszlewskiR.edu.scientling.data.database.creators.LessonCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.LessonsTable.LessonsColumns;

/**
 * Created by Razjelll on 09.11.2016.
 */

public class LessonDao extends BaseDao<Lesson> {


    private final String INSERT_STATEMENT =
            "INSERT INTO " + LessonsTable.TABLE_NAME + "("
                    + LessonsColumns.NAME + ", " + LessonsColumns.NUMBER + ", "
                    + LessonsColumns.SET_FK + ", " +LessonsColumns.GLOBAL_ID +  ") VALUES (?,?,?,?)";
    private final String WHERE_ID = LessonsColumns.ID + "= ?";


    public LessonDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = mDb.compileStatement(INSERT_STATEMENT);

        mTableColumns = LessonsTable.getColumns();
    }

    @Override
    public long save(Lesson entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(LessonsColumns.NAME_POSITION, entity.getName());
        mInsertStatement.bindLong(LessonsColumns.NUMBER_POSITION, entity.getNumber());
        //mInsertStatement.bindLong(LessonsColumns.SET_FK_POSITION, entity.getSet().getId());
        mInsertStatement.bindLong(LessonsColumns.SET_FK_POSITION, entity.getSetId());
        if(entity.getGlobalId() > 0){
            mInsertStatement.bindLong(LessonsColumns.GLOBAL_ID_POSITION, entity.getGlobalId());
        } else {
            mInsertStatement.bindNull(LessonsColumns.GLOBAL_ID_POSITION);
        }
        return mInsertStatement.executeInsert();
    }

    @Override
    public void update(Lesson entity) {
        final ContentValues values = new ContentValues();
        values.put(LessonsColumns.NAME, entity.getName());
        values.put(LessonsColumns.NUMBER, entity.getNumber());
        //values.put(LessonsColumns.SET_FK, entity.getSet().getId());
        values.put(LessonsColumns.SET_FK, entity.getSetId());
        if(entity.getGlobalId() > 0 ){
            values.put(LessonsColumns.GLOBAL_ID, entity.getGlobalId());
        } else {
            values.putNull(LessonsColumns.GLOBAL_ID);
        }

        String[] whereArguments = new String[]{String.valueOf(entity.getId())};
        mDb.update(LessonsTable.TABLE_NAME, values, WHERE_ID, whereArguments);
    }

    @Override
    public int delete(Lesson entity) {
        long id = entity.getId();
        if (id > 0) {
            String[] whereArguments = new String[]{String.valueOf(id)};
            return mDb.delete(LessonsTable.TABLE_NAME, WHERE_ID, whereArguments);
        }
        return 0;
    }

    @Override
    public Lesson get(long id) {
        Lesson lesson = null;
        String[] whereArguments = new String[]{String.valueOf(id)};
        Cursor cursor = mDb.query(LessonsTable.TABLE_NAME, mTableColumns, WHERE_ID, whereArguments,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            lesson = LessonCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return lesson;
    }

    @Override
    public List<Lesson> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                               String groupBy, String having, String orderBy, String limit) {
        List<Lesson> lessonsList = new ArrayList<>();
        Cursor cursor = getAllCursor(distinct, columns, selection, selectionArgs, groupBy, having, orderBy,limit);
        if (cursor.moveToFirst()) {
            Lesson lesson;
            do {
                lesson = LessonCreator.createFromCursor(cursor);
                if (lesson != null) {
                    lessonsList.add(lesson);
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        if(BuildConfig.DEBUG)
            assert cursor.isClosed();

        return lessonsList;
    }

    public Cursor getAllCursor(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                               String groupBy, String having, String orderBy, String limit){
        return mDb.query(distinct,LessonsTable.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public long getId(long globalId){
        String[] columns = {LessonsColumns.ID};
        String selection = LessonsColumns.GLOBAL_ID + "=?";
        String[] selectionArguments = {String.valueOf(globalId)};

        Cursor cursor = mDb.query(LessonsTable.TABLE_NAME, columns, selection, selectionArguments,
                null,null,null,null);
        if(cursor.moveToFirst()){
            return cursor.getLong(0);
        }
        return -1;
    }
}
