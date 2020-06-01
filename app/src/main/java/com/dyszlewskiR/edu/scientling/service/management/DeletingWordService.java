package com.dyszlewskiR.edu.scientling.service.management;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;

public class DeletingWordService extends Service {
    private final String LOG_TAG = "DeletingWordService";
    private DataManager mDataManager;

    @Override
    public void onCreate() {
        mDataManager = ((LingApplication) getApplication()).getDataManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        Word word = intent.getParcelableExtra("word");
        String setCatalog = intent.getStringExtra("catalog");
        DeletingRunnable runnable = new DeletingRunnable(word, setCatalog, mDataManager);
        runnable.run();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DeletingRunnable implements Runnable {
        private Word mWord;
        private DataManager mDataManager;
        private String mSetCatalog;

        public DeletingRunnable(Word word, String catalog, DataManager dataManager) {
            mWord = word;
            mDataManager = dataManager;
            mSetCatalog = catalog;
        }

        @Override
        public void run() {
            int deletingResult = mDataManager.deleteWord(mWord);
            if(deletingResult >0){
                if (MediaFileSystem.checkMediaExist(mWord.getImageName(), mSetCatalog, MediaType.IMAGES, getBaseContext())) {
                    MediaFileSystem.deleteMedia(mWord.getImageName(), mSetCatalog,MediaType.IMAGES, getBaseContext());
                }
                if (MediaFileSystem.checkMediaExist(mWord.getRecordName(), mSetCatalog,MediaType.RECORDS, getBaseContext())) {
                    MediaFileSystem.deleteMedia(mWord.getRecordName(), mSetCatalog,MediaType.RECORDS, getBaseContext());
                }
            }
        }
    }
}
