package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.utils.ResourcesFileOpener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Razjelll on 06.11.2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk= 17)
public class LanguageDaoTest {

    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper dmDbHelper = new DatabaseHelper(mContext);
    private LanguageDao mDao;
    private Language mLanguage1;
    private Language mLanguage2;
    private Language mLanguage3;
    private Language mLncorrectLangauge;

    public LanguageDaoTest()
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

        mLanguage3 = new Language();
        mLanguage3.setId(3);
        mLanguage3.setName("Hiszpański");
        mLanguage3.setAbbreviation("ESP");
        mLanguage3.setCode("es_ES");

        mLncorrectLangauge = new Language();
        mLncorrectLangauge.setName(null);
        mLncorrectLangauge.setAbbreviation(null);
        mLncorrectLangauge.setCode(null);

        dmDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = dmDbHelper.getWritableDatabase();
        mDao = new LanguageDao(db);
    }

    @Test
    public void testInsertLanguage()
    {
        long id = mDao.save(mLanguage1);
        assertEquals(1, id);
        id = mDao.save(mLanguage2);
        assertEquals(2,id);
        id = mDao.save(mLanguage3);
        assertEquals(3, id);
    }

    @Test
    public void testInsertIncorrectLanguage()
    {
        try
        {
            fail();
        }
        catch(IllegalArgumentException ex)
        {
            assertTrue(true);
        }
    }

    @Test
    public void testInsertNonUniqueLanguages(){
        try
        {
            mDao.save(mLanguage1);
            mDao.save(mLanguage1);
            fail();
        }
        catch (SQLiteConstraintException ex)
        {
            assertTrue(true);
        }
    }

    @Test
    public void testGetLanguage()
    {
        mDao.save(mLanguage1);
        Language language = mDao.get(1);
        assertEquals(1, language.getId());
        assertEquals("Angielski", language.getName());
        assertEquals("ENG", language.getAbbreviation());
        assertEquals("en_EN", language.getCode());
    }

    @Test
    public void testDeleteLanguage()
    {
        long id = mDao.save(mLanguage1);
        Language language = mDao.get(id);
        mDao.delete(language);
        language = mDao.get(id);
        assertNull(language);

    }

    @Test
    public void testGetAllLanguage()
    {
        mDao.save(mLanguage1);
        mDao.save(mLanguage2);
        mDao.save(mLanguage3);
        ArrayList<Language> list = (ArrayList<Language>) mDao.getAll();
        assertEquals(list.size(), 3);
        assertTrue(list.get(0).equals(mLanguage1));
        assertTrue(list.get(1).equals(mLanguage2));
        assertTrue(list.get(2).equals(mLanguage3));
    }


}
