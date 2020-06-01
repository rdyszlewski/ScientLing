package com.dyszlewskiR.edu.scientling.service.repetitions;

import com.dyszlewskiR.edu.scientling.models.entity.Repetition;
import com.dyszlewskiR.edu.scientling.utils.DateCalculator;
import com.dyszlewskiR.edu.scientling.utils.DateUtils;

import java.util.Date;

public class RepetitionsManager {
    public static Repetition prepareRepetition(long wordId, int masterLevel){
        Repetition repetition = new Repetition();
        repetition.setWordId(wordId);
        int interval = Interval.getInterval(masterLevel);
        int date = getDate(DateUtils.getTodayDate(), interval);
        repetition.setDate(date);
        return repetition;
    }

    public static int getDate(int startDate, int masterLevel){
        Date date = DateCalculator.intToDate(startDate);
        return getDate(date, masterLevel);
    }

    public static int getDate(Date startDate, int masterLevel){
        int interval = Interval.getInterval(masterLevel);
        Date newDate = DateUtils.addDays(startDate, interval);
        return DateCalculator.dateToInt(newDate);
    }
}
