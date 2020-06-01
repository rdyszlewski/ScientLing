package com.dyszlewskiR.edu.scientling.service.net.deleting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DeletingSetService extends Service {

    public static final String BROADCAST_ACTION = "DeleteSetNetAction";

    private long mSetId;
    private boolean mDeleteDatabase;
    private boolean mDeleteImages;
    private boolean mDeleteRecords;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(getClass().getSimpleName(), "onStartCommand");
        mSetId = intent.getLongExtra("set", -1);
        mDeleteDatabase = intent.getBooleanExtra("database", false);
        mDeleteImages = intent.getBooleanExtra("images", false);
        mDeleteRecords = intent.getBooleanExtra("records", false);
        if(mSetId != -1){
            if(mDeleteDatabase){
                DeleteSetAsyncTask task = new DeleteSetAsyncTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSetId);
            } else {
                if(mDeleteImages){
                    DeleteMediaAsyncTask task = new DeleteMediaAsyncTask(DeleteMediaAsyncTask.IMAGE_TYPE);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSetId);
                }
                if(mDeleteRecords){
                    DeleteMediaAsyncTask task = new DeleteMediaAsyncTask(DeleteMediaAsyncTask.RECORD_TYPE);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSetId);
                }
            }
        }
        return START_STICKY;
    }

    private void sendFinishBroadcast(){
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("set", mSetId);
        intent.putExtra("database", mDeleteDatabase);
        intent.putExtra("images", mDeleteImages);
        intent.putExtra("records", mDeleteRecords);
        sendBroadcast(intent);
    }

    private class DeleteSetAsyncTask extends AsyncTask<Long, Void, Long> {

        @Override
        protected Long doInBackground(Long... params) {
            HttpURLConnection connection = null;
            long setId = params[0];
            try {
                DeleteSetRequest request = new DeleteSetRequest(setId);
                connection = request.start(LogPref.getLogin(getBaseContext()), LogPref.getPassword(getBaseContext()));
                DeleteSetResponse response = new DeleteSetResponse(connection);
                if(response.getResponse()){
                    DataManager dataManager = LingApplication.getInstance().getDataManager();
                    dataManager.updateImageUploaded(false, setId);
                    dataManager.updateRecordsUploaded(false, setId);
                    dataManager.updateSetGlobalId(null, setId);

                    return setId;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long result){
            if(result != null) {
                sendFinishBroadcast();
            }
        }
    }

    class DeleteMediaAsyncTask extends AsyncTask<Long, Void, Void>{

        public static final int IMAGE_TYPE = 0;
        public static final int RECORD_TYPE = 1;

        private Context mContext;
        private int mMediaType;
        private DataManager mDataManager;
        private long mSetId;

        public DeleteMediaAsyncTask(int type){
            mMediaType = type;
            mDataManager = LingApplication.getInstance().getDataManager();
        }

        @Override
        protected Void doInBackground(Long... params) {
            HttpURLConnection connection = null;
            mSetId = params[0];
            try {
                switch (mMediaType){
                    case IMAGE_TYPE:
                        DeleteImagesRequest imagesRequest = new DeleteImagesRequest(mSetId);
                        connection = imagesRequest.start(LogPref.getLogin(mContext), LogPref.getPassword(mContext)); break;
                    case RECORD_TYPE:
                        DeleteRecordsRequest recordsRequest = new DeleteRecordsRequest(mSetId);
                        connection = recordsRequest.start(LogPref.getLogin(mContext), LogPref.getPassword(mContext)); break;
                }
                DeleteMediaResponse response = new DeleteMediaResponse(connection);
                response.getResponse();
                response.closeConnection();

                if(response.getResultCode() == DeleteMediaResponse.DELETED){
                    switch (mMediaType){
                        case IMAGE_TYPE:
                            mDataManager.updateImageUploaded(false, mSetId); break;
                        case RECORD_TYPE:
                            mDataManager.updateRecordsUploaded(false, mSetId); break;
                    }
                }
                //connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            sendFinishBroadcast();
        }
    }
}
