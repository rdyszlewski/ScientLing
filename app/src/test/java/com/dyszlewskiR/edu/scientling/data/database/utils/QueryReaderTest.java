package com.dyszlewskiR.edu.scientling.data.database.utils;

import android.content.Context;

import com.dyszlewskiR.edu.scientling.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Razjelll on 07.11.2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 17)
public class QueryReaderTest {

    private Context mContext;
    private QueryReader mReader;



    @Before
    public void setUp()
    {
        mContext = RuntimeEnvironment.application;
        mReader = new QueryReader();
    }

    @Test
    public void testReadQuery() throws IOException {
        String query  = "SELECT jeden \n FROM tablica; SELECT dwa FROM talica;" +
                " INSERT INTO Ziemniaki(Bulwa) \n VALUES (Kartofel);";
        String correctResult = "SELECT jeden FROM tablica;";
        InputStream inputStream = new ByteArrayInputStream(query.getBytes());
        ArrayList<String> sqlQueries = mReader.readFromStream(inputStream);
        assertEquals(3, sqlQueries.size());
        assertEquals(correctResult, sqlQueries.get(0));
    }

    @Test
    public void testReadQueryWithComments() throws IOException {
        String query = "SELECT * --To jest komentarz \n"
                + " FROM Tablica;";
        String query2 = "SELECT *--To jest komentarz \n"
                + " FROM Tablica;";
        String query3 = "SELECT/*KOMENTARZ*/* FROM Tablica;";
        String correctResult = "SELECT * FROM Tablica;";
        InputStream inputStream = new ByteArrayInputStream(query.getBytes());
        ArrayList<String> sqlQueries = mReader.readFromStream(inputStream);
        assertEquals(correctResult, sqlQueries.get(0));
        inputStream = new ByteArrayInputStream(query2.getBytes());
        sqlQueries = mReader.readFromStream(inputStream);
        assertEquals(correctResult, sqlQueries.get(0));
        inputStream = new ByteArrayInputStream(query3.getBytes());
        sqlQueries = mReader.readFromStream(inputStream);
        assertEquals(correctResult, sqlQueries.get(0));
    }

}
