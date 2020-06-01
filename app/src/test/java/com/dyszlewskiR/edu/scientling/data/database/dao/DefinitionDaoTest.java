package com.dyszlewskiR.edu.scientling.data.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dyszlewskiR.edu.scientling.BuildConfig;
import com.dyszlewskiR.edu.scientling.data.database.DatabaseHelper;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;
import com.dyszlewskiR.edu.scientling.utils.ResourcesFileOpener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class DefinitionDaoTest {
    private Context mContext = RuntimeEnvironment.application;
    private DatabaseHelper mDbHelper = new DatabaseHelper(mContext);

    private DefinitionDao mDao;

    private Definition mDefinition1;
    private Definition mDefinition2;

    public DefinitionDaoTest()
    {
        mDefinition1 = new Definition();
        mDefinition1.setId(1);
        mDefinition1.setContent("a highly variable domestic mammal (Canis familiaris) " +
                "closely related to the gray wolf");
        mDefinition1.setTranslation("domowy ssak blisko powiązany z szarym wilkiem");

        mDefinition2 = new Definition();
        mDefinition2.setId(2);
        mDefinition2.setContent("mzwierzę roślinożerne podobne do zająca, o miękkim, białym, " +
                "czarnym lub brunatnoszarym futrze, długich uszach i małym ogonku");


        mDbHelper.setFileOpener(new ResourcesFileOpener());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDao = new DefinitionDao(db);
    }

    @Test
    public void testInsertDefinition()
    {
        long id = mDao.save(mDefinition1);
        assertEquals(1, id);
        id = mDao.save(mDefinition2);
        assertEquals(2, id);
    }

    @Test
    public void testGetDefinition()
    {
        long id = mDao.save(mDefinition1);
        Definition definition = mDao.get(id);
        assertEquals(id, definition.getId());
        assertEquals("a highly variable domestic mammal (Canis familiaris) closely related to the " +
                "gray wolf", definition.getContent());

        id = mDao.save(mDefinition2);
        definition = mDao.get(id);
        assertEquals(id, definition.getId());
        assertNull(definition.getTranslation());

        definition = mDao.get(3);
        assertNull(definition);
    }

    @Test
    public void testDeleteDefinition()
    {
        long id = mDao.save(mDefinition1);
        Definition definition = mDao.get(id);
        mDao.delete(definition);
        definition = mDao.get(id);
        assertNull(definition);
    }

    @Test
    public void testGetAllDefinitions()
    {
        mDao.save(mDefinition1);
        mDao.save(mDefinition2);
        List<Definition> definitionsList = mDao.getAll();
        assertEquals(2, definitionsList.size());
    }

    @Test
    public void testUpdateDefinition()
    {
        long id = mDao.save(mDefinition1);
        Definition definition = mDao.get(id);
        definition.setTranslation(null);
        mDao.update(definition);
        definition = mDao.get(id);
        assertNull(definition.getTranslation());
    }




}
