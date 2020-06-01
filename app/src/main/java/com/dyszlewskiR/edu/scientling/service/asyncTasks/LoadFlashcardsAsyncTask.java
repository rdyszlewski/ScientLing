package com.dyszlewskiR.edu.scientling.service.asyncTasks;

import android.os.AsyncTask;

import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.FlashcardParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

/**
 * Created by Razjelll on 05.01.2017.
 */

public class LoadFlashcardsAsyncTask extends AsyncTask<FlashcardParams, Void, List<Word>> {
    private DataManager mDataManager;

    public LoadFlashcardsAsyncTask(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected List<Word> doInBackground(FlashcardParams... params) {
        return mDataManager.getWords(params[0]);
    }
}
