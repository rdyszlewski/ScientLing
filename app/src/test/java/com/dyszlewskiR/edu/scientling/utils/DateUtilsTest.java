package com.dyszlewskiR.edu.scientling.utils;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class DateUtilsTest extends TestCase {

    private Date mDate;
    private final int YEAR = 2016;
    private final int MONTH = 6;
    private final int DAY = 2;

    public void setUp()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(YEAR,MONTH,DAY);
        mDate = calendar.getTime();
    }

    public void testGetYear()
    {
        int result = DateUtils.getYear(mDate);
        assertEquals(YEAR, result);
    }

    public void testGetMonth()
    {
        int result = DateUtils.getMonth(mDate);
        assertEquals(MONTH, result);
    }

    public void testGetDayOfMonth()
    {
        int result = DateUtils.getDayOfMonth(mDate);
        assertEquals(DAY, result);
    }

    public void testGetDate()
    {
        Date result = DateUtils.getDate(YEAR, MONTH, DAY);
        assertEquals(mDate, result);
    }
}
