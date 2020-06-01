package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.utils.ResourcesFileOpener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Razjelll on 09.11.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class PartOfSpeechDaoTest {
    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper mDbHelper = new DatabaseHelper(mContext);

    private PartOfSpeechDao mDao;

    private PartOfSpeech mPart1;
    private PartOfSpeech mPart2;

    public PartOfSpeechDaoTest()
    {
        mPart1 = new PartOfSpeech();
        mPart1.setId(1);
        mPart1.setName("Rzeczownik");

        mPart2 = new PartOfSpeech();
        mPart2.setId(2);
        mPart2.setName("Czasownik");

        mDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao =new PartOfSpeechDao(db);
    }

    @Test
    public void testInsertPartOfSpeech()
    {
        long id = mDao.save(mPart1);
        assertEquals(1, id);
        id = mDao.save(mPart2);
        assertEquals(2,id);
    }

    @Test
    public void testGetPartOfSpeech()
    {
        long id = mDao.save(mPart1);
        PartOfSpeech part = mDao.get(id);
        assertEquals(id, part.getId());
        assertEquals("Rzeczownik", part.getName());
    }

    @Test
    public void testDeletePartOfSpeech()
    {
        long id = mDao.save(mPart1);
        PartOfSpeech partOfSpeech = mDao.get(id);
        mDao.delete(partOfSpeech);
        partOfSpeech = mDao.get(id);
        assertNull(partOfSpeech);
    }

    @Test
    public void testGetAllPartsOfSpeech()
    {
        mDao.save(mPart1);
        mDao.save(mPart2);
        ArrayList<PartOfSpeech> partsList = (ArrayList<PartOfSpeech>) mDao.getAll();
        assertEquals(2, partsList.size());
        //TODO tutaj jeszcze coś dopisać
    }

    @Test
    public void testUpdatePartOfSpeech()
    {
        long id = mDao.save(mPart1);
        PartOfSpeech partOfSpeech = mDao.get(id);
        partOfSpeech.setName("Czasownik");
        mDao.update(partOfSpeech);
        partOfSpeech = mDao.get(id);
        assertEquals("Czasownik", partOfSpeech.getName());
    }

}
