package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.utils.ResourcesFileOpener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Razjelll on 08.11.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class SetDaoTest {
    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper mDbHelper = new DatabaseHelper(mContext);

    private SetDao mDao;

    private VocabularySet mSet1;
    private VocabularySet mSet2;

    public SetDaoTest()
    {
        Language language = new Language();
        language.setId(1);
        language.setName("Angielski");
        language.setAbbreviation("ENG");
        language.setCode("en_EN");
        mSet1 = new VocabularySet();
        mSet1.setId(1);
        mSet1.setName("Angielski: 1000 najpolujarniejszych słów");
        mSet1.setLanguageL2(language);

        mDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao = new SetDao(db);
    }

    @Test
    public void testInsertSet()
    {
        long id = mDao.save(mSet1);
        assertEquals(1, id);
        //TODO rozbudować testy
    }

    @Test
    public void testGetSet()
    {
        mDao.save(mSet1);
        VocabularySet set = mDao.get(1);
        assertEquals(1, set.getId());
        assertEquals("Angielski: 1000 najpolujarniejszych słów", set.getName());
        assertEquals(1, set.getLanguageL2().getId());
    }

    @Test
    public void testDeleteSet()
    {
        long id = mDao.save(mSet1);
        VocabularySet set = mDao.get(id);
        mDao.delete(set);
        set = mDao.get(id);
        assertNull(set);
    }
}
