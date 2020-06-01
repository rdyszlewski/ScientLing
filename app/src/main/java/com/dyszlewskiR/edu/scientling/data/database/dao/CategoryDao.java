package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable;
import com.dyszlewskiR.edu.scientling.data.database.creators.CategoryCreator;
import com.dyszlewskiR.edu.scientling.models.entity.Category;

import java.util.ArrayList;
import java.util.List;

import static com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable.CategoriesColumns;
import static com.dyszlewskiR.edu.scientling.data.database.tables.CategoriesTable.TABLE_NAME;

public class CategoryDao extends BaseDao<Category> {

    private final String INSERT_STATEMENT =
            "INSERT INTO " + CategoriesTable.TABLE_NAME + "("
                    + CategoriesColumns.NAME + ") VALUES (?)";

    public CategoryDao(SQLiteDatabase db) {
        super(db);
        mInsertStatement = mDb.compileStatement(INSERT_STATEMENT);
        mTableColumns = CategoriesTable.getColumn();
    }

    @Override
    public long save(Category entity) {
        mInsertStatement.clearBindings();
        mInsertStatement.bindString(1, entity.getName());

        return mInsertStatement.executeInsert();
    }

    @Override
    public void update(Category entity) {
        final ContentValues values = new ContentValues();
        values.put(CategoriesColumns.NAME, entity.getName());
        mDb.update(TABLE_NAME, values, getWhereStatement(), getWhereArguments(entity));
    }

    private String getWhereStatement() {
        return CategoriesColumns.ID + "=?";
    }

    private String[] getWhereArguments(Category entity) {
        return new String[]{String.valueOf(entity.getId())};
    }

    @Override
    public int delete(Category entity) {
        long id = entity.getId();
        if (id > 0) {
            return mDb.delete(CategoriesTable.TABLE_NAME, getWhereStatement(), getWhereArguments(entity));
        }
        return 0;
    }

    @Override
    public Category get(long id) {
        Category category = null;
        Cursor cursor = mDb.query(TABLE_NAME, mTableColumns, getWhereStatement(), getWhereArguments(id),
                null, null, null, null);
        if (cursor.moveToFirst()) {
            category = CategoryCreator.createFromCursor(cursor);
        }
        closeCursor(cursor);
        return category;
    }

    private String[] getWhereArguments(long id) {
        return new String[]{String.valueOf(id)};
    }

    @Override
    public List<Category> getAll(boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, String limit) {

        List<Category> categoriesList = new ArrayList<>();
        Cursor cursor = mDb.query(distinct, TABLE_NAME, columns, selection, selectionArgs,
                groupBy, having, orderBy, limit);
        if (cursor.moveToFirst()) {
            Category category;
            do {
                category = CategoryCreator.createFromCursor(cursor);
                if (category != null) {
                    categoriesList.add(category);
                }
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return categoriesList;
    }
}
