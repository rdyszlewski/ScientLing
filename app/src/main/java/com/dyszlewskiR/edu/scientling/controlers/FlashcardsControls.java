package com.dyszlewskiR.edu.scientling.controlers;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.service.asyncTasks.LoadFlashcardsAsyncTask;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.FlashcardParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FlashcardsControls {

    private List<Word> mWords;
    private DataManager mDataManager;

    public FlashcardsControls(){
        mDataManager = LingApplication.getInstance().getDataManager();
    }

    public DataManager getDataManager(){
        return mDataManager;
    }

    public List<Word> getWords(FlashcardParams params) throws ExecutionException, InterruptedException {
        LoadFlashcardsAsyncTask task = new LoadFlashcardsAsyncTask(mDataManager);
        return task.execute(params).get();
    }



}
