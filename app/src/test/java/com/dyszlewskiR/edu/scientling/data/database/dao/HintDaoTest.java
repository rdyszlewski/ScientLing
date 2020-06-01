package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
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
 * Created by Razjelll on 08.11.2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class HintDaoTest {

    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper mDbHelper = new DatabaseHelper(mContext);

    private HintDao mDao;

    private Hint mHint1;
    private Hint mHint2;

    public HintDaoTest()
    {
        mHint1 = new Hint();
        mHint1.setId(1);
        mHint1.setContent("Odmiana 'to be' I am you are...");

        mHint2 = new Hint();
        mHint2.setId(2);
        mHint2.setContent("");

        mDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao = new HintDao(db);
    }

    @Test
    public void testInsertTip()
    {
        long id = mDao.save(mHint1);
        assertEquals(1,id);
        id = mDao.save(mHint2);
        assertEquals(2, id);
        try
        {
            fail("Złamano ograniczenie unikalnej wartości");
        } catch(SQLiteConstraintException ex )
        {
            assertTrue(true);
        }
    }

    @Test
    public void testGetTip()
    {
        mDao.save(mHint1);
        Hint hint = mDao.get(1);
        assertEquals(1, hint.getId());
        assertEquals("Odmiana 'to be' I am you are...", hint.getContent());
    }

    @Test
    public void testDeleteTip()
    {
        long id = mDao.save(mHint1);
        Hint hint = mDao.get(id);
        mDao.delete(hint);
        hint = mDao.get(id);
        assertNull(hint);
    }

    @Test
    public void testGetAllTips()
    {
        mDao.save(mHint1);
        mDao.save(mHint2);
        ArrayList<Hint> tipsList = (ArrayList<Hint>)mDao.getAll();
        assertEquals(2, tipsList.size());
        assertTrue(tipsList.get(0).equals(mHint1));
        assertTrue(tipsList.get(1).equals(mHint2));
    }

    @Test
    public void testUpdateTip()
    {
        long id = mDao.save(mHint1);
        Hint hint = mDao.get(id);
        hint.setContent("Brak");
        mDao.update(hint);
        hint = mDao.get(id);
        assertEquals("Brak", hint.getContent());
    }

}
