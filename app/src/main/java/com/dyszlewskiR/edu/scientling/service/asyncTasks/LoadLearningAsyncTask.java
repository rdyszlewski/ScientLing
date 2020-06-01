package com.dyszlewskiR.edu.scientling.service.asyncTasks;

import android.os.AsyncTask;

import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.LearningParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

/**
 * Klasa odpowiedzialana za pobranie słówek. Klasa wybiera słówka, które nie zostały jeszcze rozpoczęte,
 * kiedując się losowo lub podanymi parametrami.
 */

public class LoadLearningAsyncTask extends AsyncTask<LearningParams, Void, List<Word>> {

    private DataManager mDataManager;

    public LoadLearningAsyncTask(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected List<Word> doInBackground(LearningParams... params) {
        return mDataManager.getWords(params[0]);
    }
}
