package com.dyszlewskiR.edu.scientling.service.repetitions;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Repetition;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.DateCalculator;
import com.dyszlewskiR.edu.scientling.utils.DateUtils;

import java.util.List;

public class RepetitionService extends Service{
    public static final String BORADCAST_ACTION = "RepetitionServiceAction";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        DataManager dataManager = LingApplication.getInstance().getDataManager();
        int todayDate = DateCalculator.dateToInt(DateUtils.getTodayDate());
        List<Repetition> repetitionsList = dataManager.getOldRepetition(todayDate);
        for(Repetition repetition : repetitionsList){
            int wordMasterLevel = dataManager.getMasterLevel(repetition.getWordId());
            int newDate = RepetitionsManager.getDate(DateUtils.getTodayDate(), wordMasterLevel);
            repetition.setDate(newDate);
            repetition.setActions(Repetition.Action.UPDATE);
            dataManager.updateRepetition(repetition);
        }

        sendBroadcast();

        return START_STICKY; //TODO zastanowić się czy takie ustawienie jest poprawne
    }

    private void sendBroadcast(){
        Intent intent = new Intent();
        intent.setAction(BORADCAST_ACTION);
        sendBroadcast(intent);
    }
}
