package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

public abstract class BaseDao<T> {

    protected final SQLiteDatabase mDb;
    protected SQLiteStatement mInsertStatement;
    protected String[] mTableColumns;

    public BaseDao(SQLiteDatabase db) {
        mDb = db;

    }

    public abstract long save(T entity);

    public abstract void update(T entity);

    public abstract int delete(T entity);

    public abstract T get(long id);

    public abstract List<T> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                   String groupBy, String having, String orderBy, String limit);

    public List<T> getAll() {
        return getAll(false, mTableColumns, null, null, null, null, null, null);
    }

    public List<T> getAll(boolean distinct, String selection, String[] selectionArgs,
                          String groupBy, String having, String orderBy, String limit) {
        return getAll(distinct, mTableColumns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    protected void closeCursor(Cursor cursor) {
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }


}
