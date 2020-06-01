package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
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

/**
 * Created by Razjelll on 08.11.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=17)
public class SentenceDaoTest {

    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper mDbHelper = new DatabaseHelper(mContext);
    private SentenceDao mDao;
    private Sentence mSentence1;
    private Sentence mSentence2;

    public SentenceDaoTest()
    {
        mSentence1 = new Sentence();
        mSentence1.setId(1);
        mSentence1.setContent("This is pen");
        mSentence1.setTranslation("To jest długopis");

        mSentence2 = new Sentence();
        mSentence2.setId(2);
        mSentence2.setContent("I have very old dog");
        mSentence2.setTranslation("Mam bardzo starego psa");

        mDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao = new SentenceDao(db);
    }

    @Test
    public void testInsertSentence()
    {
        long id = mDao.save(mSentence1);
        assertEquals(1, id);
        id = mDao.save(mSentence2);
        assertEquals(2,id);
        try
        {
            mDao.save(mSentence1);
        } catch (Exception ex)
        {
            assertTrue(true);
        }
    }

    @Test
    public void testGetSentence()
    {
        mDao.save(mSentence1);
        Sentence sentence = mDao.get(1);
        assertEquals(1, sentence.getId());
        assertEquals("This is pen", sentence.getContent());
        assertEquals("To jest długopis", sentence.getTranslation());
    }

    @Test
    public void testDeleteSentence()
    {
        long id = mDao.save(mSentence1);
        Sentence sentence = mDao.get(id);
        mDao.delete(sentence);
        sentence = mDao.get(id);
        assertNull(sentence);
    }

    @Test
    public void testGetAllSentences()
    {
        mDao.save(mSentence1);
        mDao.save(mSentence2);
        ArrayList<Sentence> sentencesList = (ArrayList<Sentence>) mDao.getAll();
        assertEquals(2, sentencesList.size());
        assertTrue(sentencesList.get(0).equals(mSentence1));
        assertTrue(sentencesList.get(1).equals(mSentence2));
    }

    @Test
    public void testUpdateSentence()
    {
        long id = mDao.save(mSentence1);
        Sentence sentence = mDao.get(id);
        sentence.setTranslation("Ptak latał bardzo nisko");
        mDao.update(sentence);
        sentence = mDao.get(id);
        assertEquals("Ptak latał bardzo nisko", sentence.getTranslation());
    }

    @Test
    public void testGetByContent()
    {
        long id = mDao.save(mSentence1);
        long id2 = mDao.save(mSentence2);
        Sentence sentence = mDao.getByContent("This is pen");
        assertEquals(1, sentence.getId());
        assertEquals("This is pen",sentence.getContent());
        assertEquals("To jest długopis", sentence.getTranslation());
    }

}
