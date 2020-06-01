package com.dyszlewskiR.edu.scientling.service.asyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.SaveWordParams;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.SaveWordDialog;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;

import java.io.IOException;

//TODO zastanowić się czy zwracać tylko id czy całe słówko. A może nic nie zwracać
public class SaveWordAsyncTask extends AsyncTask<SaveWordParams, Void, Word> {

    private final String TAG = "SaveWordAsyncTask";

    private DataManager mDataManager;
    private Context mContext;
    private SaveWordDialog mDialog;
    private Callback mCallback;


    public interface Callback {
        void onSaveCompleted(Word word);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public SaveWordAsyncTask(DataManager dataManager, Context context) {
        mDataManager = dataManager;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        mDialog = new SaveWordDialog(mContext);
        mDialog.show();
    }

    @Override
    protected Word doInBackground(SaveWordParams... params) {
        Log.d(TAG, "doInBackground");
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Word word = params[0].getWord();
        VocabularySet set = params[0].getSet();
        long wordId;
        if (params[0].isEdit()) {
            updateWord(word);
            wordId = word.getId();
        } else {
            wordId = saveWord(word);
        }
        if(params[0].getImageToDelete() != null){
            deleteImage(params[0].getImageToDelete());
        }
        if(params[0].getRecordToDelete() != null){
            deleteRecord(params[0].getRecordToDelete());
        }

        if (params[0].getImageToInsert() != null) {
            saveImage(word.getImageName(), set.getCatalog(), params[0].getImageToInsert());
        }
        if (params[0].getRecordToInsert() != null) {
            saveRecord(word.getRecordName(), set.getCatalog(), params[0].getRecordToInsert());
        }
        word.setId(wordId);
        return word;
    }

    @Override
    protected void onPostExecute(Word result) {
        Log.d(TAG, "onPostExecute");
        if (mCallback != null) {
            mCallback.onSaveCompleted(result);
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private long saveWord(Word word) {
        return mDataManager.saveWord(word);
    }

    private void updateWord(Word word) {
        mDataManager.updateWord(word);
    }

    private void saveImage(String fileName, String setCatalog, Uri uri) {
        try {
            MediaFileSystem.saveMedia(fileName, setCatalog, uri,true,MediaType.IMAGES, mContext);
        } catch (IOException e) {
            e.printStackTrace(); // TODO pokazać na dialogu  błąd zapisywanie obrazka
        }
    }

    private void saveRecord(String fileName, String setCatalog, Uri uri) {
        try {
            MediaFileSystem.saveMedia(fileName, setCatalog, uri,false, MediaType.RECORDS, mContext);
        } catch (IOException e) {
            e.printStackTrace(); //TODO pokazać na dialogu błąd zapisywania nagrania
        }
    }

    private void deleteImage(Uri uri){
        MediaFileSystem.deleteFile(uri);
    }

    private void deleteRecord(Uri uri){
        MediaFileSystem.deleteFile(uri);
    }
}
