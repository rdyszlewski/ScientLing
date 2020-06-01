package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Language;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class CategoryDaoTest {

    private final Context mContext = RuntimeEnvironment.application;
    private final DatabaseHelper mDbHelper = new DatabaseHelper(mContext);

    private final CategoryDao mDao;
    private Category mCategory1;
    private Category mCategory2;
    private Category mCategory3;

    private Language mLanguage1;
    private Language mLanguage2;

    public CategoryDaoTest()
    {
        mLanguage1 = new Language();
        mLanguage1.setId(1); //wstawiono w celach testowych, aby porównywać obiekty w metodzie testGetAllLanguages
        mLanguage1.setName("Angielski");
        mLanguage1.setAbbreviation("ENG");
        mLanguage1.setCode("en_EN");

        mLanguage2 = new Language();
        mLanguage2.setId(2);
        mLanguage2.setName("Polski");
        mLanguage2.setAbbreviation("POL");
        mLanguage2.setCode("pl-PL");

        mCategory1 = new Category();
        mCategory1.setId(1);
        mCategory1.setName("Home");
        //mCategory1.setLanguage(mLanguage1);

        mCategory2 = new Category();
        mCategory2.setId(2);
        mCategory2.setName("Ubrania");
        //mCategory2.setLanguage(mLanguage2);

        mCategory3 = new Category();
        mCategory3.setName("Dom");
        //mCategory3.setLanguage(mLanguage2);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao = new CategoryDao(db);
    }

    @Test
    public void testInsertLanguage()
    {
        long id = mDao.save(mCategory1);
        assertEquals(id,1);
        id = mDao.save(mCategory2);
        assertEquals(id,2);
    }

    @Test
    public void testGetCategory()
    {
        mDao.save(mCategory1);
        Category category = mDao.get(1);
        assertEquals(1, category.getId());
        assertEquals("Home", category.getName());
    }

    @Test
    public void testDeleteCategory()
    {
        long id = mDao.save(mCategory1);
        Category category = new Category();
        category.setId(id);
        mDao.delete(category);
        category = mDao.get(id);
        assertNull(category);
    }

    @Test
    public void testUpdateCategory()
    {
        long id = mDao.save(mCategory1);
        Category category = mDao.get(id);
        category.setName("Japoński");
        mDao.update(category);
        category = mDao.get(id);
        assertEquals("Japoński", category.getName());
    }

    @Test
    public void testGetAllCategories()
    {
        mDao.save(mCategory1);
        mDao.save(mCategory2);
        mDao.save(mCategory3);
        ArrayList<Category> categoriesList = (ArrayList<Category>) mDao.getAll();
        assertEquals(3, categoriesList.size());
    }

}
