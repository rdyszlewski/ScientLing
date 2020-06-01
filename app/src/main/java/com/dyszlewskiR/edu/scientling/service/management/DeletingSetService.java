package com.dyszlewskiR.edu.scientling.service.management;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;

public class DeletingSetService extends Service {

    private final String LOG_TAG = "DeletingSet";
    private DataManager mDataManager;

    @Override
    public void onCreate() {
        mDataManager = ((LingApplication) getApplication()).getDataManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        VocabularySet set = intent.getParcelableExtra("set");
        DeletingRunnable runnable = new DeletingRunnable(set, mDataManager);
        runnable.run();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DeletingRunnable implements Runnable {
        private VocabularySet mSet;
        private DataManager mDataManager;

        public DeletingRunnable(VocabularySet set, DataManager dataManager) {
            mSet = set;
            mDataManager = dataManager;
        }

        @Override
        public void run() {
            deleteSet();
        }

        private void deleteSet() {
            mDataManager.deleteSet(mSet);
            MediaFileSystem.deleteCatalog(mSet.getCatalog(), getBaseContext());
        }
    }
}
