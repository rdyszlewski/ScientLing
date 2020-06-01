package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.file.FileSizeCalculator;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.net.notifications.SetNotification;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class UploadSetService extends Service {
    private final String LOG_TAG = "UploadSetService";

    private Callback mCallback;
    private int mRunningTasks;
    private final LocalBinder mLocalBinder = new LocalBinder();

    public interface Callback {
        void onOperationProgress(int progress);
        void onOperationCompleted(long setId, long globalId, com.dyszlewskiR.edu.scientling.service.net.upload.UploadParams parts);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public class LocalBinder extends Binder {
        public UploadSetService getService() {
            return UploadSetService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public boolean isRunning() {
        return mRunningTasks > 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public void upload(long setId, Long globalId, String description, boolean database, boolean images, boolean records) {
        UploadSetAsyncTask task = new UploadSetAsyncTask(getBaseContext());
        long setGlobalId = globalId != null ? globalId : -1;
        UploadParams params = new UploadParams(setId, setGlobalId, description, database, images, records);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private class UploadParams {
        private long mSetId;
        private Long mSetGlobalId;
        private String mDescription;
        private boolean mUploadDatabase;
        private boolean mUploadImages;
        private boolean mUploadRecords;

        public UploadParams(long setId, long globalId, String description, boolean database, boolean images, boolean records) {
            mSetId = setId;
            mSetGlobalId = globalId;
            mUploadDatabase = database;
            mUploadImages = images;
            mUploadRecords = records;
            mDescription = description;
        }

        public long getSetId() {
            return mSetId;
        }

        public long getSetGlobalId() {
            return mSetGlobalId;
        }

        public String getDescription() {
            return mDescription;
        }

        public boolean isUploadDatabase() {
            return mUploadDatabase;
        }

        public boolean isUploadImages() {
            return mUploadImages;
        }

        public boolean ismUploadRecords() {
            return mUploadRecords;
        }

        public void setSetId(long setId) {
            mSetId = setId;
        }
    }

    private class UploadSetAsyncTask extends AsyncTask<UploadParams, Integer, Long> {

        private boolean mUploadDatabase;
        private boolean mUploadImages;
        private boolean mUploadRecords;
        private long mSetId;
        private SetNotification mNotification;
        private Context mContext;

        public UploadSetAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            mNotification = new SetNotification();
            mNotification.create(mContext);
            startForeground(mNotification.getId(), mNotification.build());
        }

        @Override
        protected Long doInBackground(UploadParams... params) {
            mRunningTasks++;
            long setGlobalId = params[0].getSetGlobalId();
            setupValues(params[0]);
            DataManager dataManager = LingApplication.getInstance().getDataManager();
            VocabularySet set = dataManager.getSetById(mSetId);
            mNotification.setTitle(set.getName());
            if (mUploadDatabase) {
                setGlobalId = startUploadDatabase(mSetId, params[0].getDescription(), dataManager);
                if(setGlobalId < 0) {return null;}
            }
            String catalog = set.getCatalog();
            if (mUploadImages) {
                startUploadImages(setGlobalId, catalog, dataManager);
            }
            if (mUploadRecords) {
                startUploadRecords(setGlobalId, catalog, dataManager);
            }
            return setGlobalId;
        }

        private void setupValues(UploadParams params){
            mSetId = params.getSetId();
            mUploadDatabase = params.isUploadDatabase();
            mUploadImages = params.isUploadImages();
            mUploadRecords = params.ismUploadRecords();
        }

        private long startUploadDatabase(long setId, String description, DataManager dataManager){
            //ustawienie notyfikacji pokazującej jak akcja jest w tej chwili wykonywana
            mNotification.setContent(getString(R.string.uploading_database));
            long setGlobalId = uploadDatabase(mSetId, description, dataManager);
            if (setGlobalId > 0) {
                Log.d(LOG_TAG, "Wysyłanie powiodło sie");
                dataManager.insertSetGlobalId(setGlobalId, mSetId);
                dataManager.updateUploadingUser(LogPref.getLogin(getBaseContext()), mSetId);

            }
            return setGlobalId;
        }

        private long uploadDatabase(long setId, String description, DataManager dataManager) {
            HttpURLConnection connection = null;
            long setGlobalId = -1;
            try {
                final int[] wordsCount = new int[1];
                final int[] wordsUploaded = new int[1];
                connection = UploadSetRequest.start(LogPref.getLogin(getBaseContext()), LogPref.getPassword(getBaseContext()));
                SetWriter setWriter = new SetWriter(connection.getOutputStream(), UploadSetRequest.CHUNK_SIZE, dataManager);
                setWriter.setCallback(new SetWriter.Callback() {
                    @Override
                    public void addWord() {
                        wordsUploaded[0]++;
                        publishProgress(wordsUploaded[0] * 100 / wordsCount[0]);
                    }

                    @Override
                    public void getWordsCount(int count) {
                        wordsCount[0] = count;
                    }
                });
                setWriter.startWriting(setId, description);
                setWriter.close();

                UploadSetResponse response = new UploadSetResponse(connection);
                setGlobalId = response.getId();
            } catch (IOException e) { //TODO zrobić obsługę wyjątków
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return setGlobalId;
        }

        private void startUploadImages(long globalId, String catalog, DataManager dataManager){
            mNotification.setContent(getString(R.string.uploading_images));
            try {
                uploadImages(globalId, catalog);
                dataManager.updateImageUploaded(true, globalId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private long uploadImages(long globalId, String catalog) throws IOException, JSONException {
            Log.d(LOG_TAG, "uploadImages");
            UploadImagesRequest request = new UploadImagesRequest(globalId, LogPref.getLogin(getBaseContext()),
                    LogPref.getPassword(getBaseContext()), catalog, getBaseContext());
            HttpURLConnection connection = null;
            try {
                connection = request.start();
                String imagesCatalogPath = MediaFileSystem.getMediaPath(catalog, MediaType.IMAGES, getBaseContext());
                uploadFiles(imagesCatalogPath, connection.getOutputStream());
                InputStream stream = connection.getInputStream();
                stream.close();
                //TODO zrobić pobieranie odpowiedzi
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return -1;
        }

        private void uploadFiles(String folderPath, OutputStream outputStream) throws IOException {
            final long[] imagesSize = {FileSizeCalculator.calculate(folderPath)};
            final long[] totalUploadedBytes = new long[1];
            MediaStreamWriter mediaStreamWriter = new MediaStreamWriter(outputStream, MediaSetRequest.CHUNK_SIZE);
            mediaStreamWriter.setCallback(new MediaStreamWriter.Callback() {
                @Override
                public void onProgressUpdate(long uploadedBytes) {
                    totalUploadedBytes[0] += uploadedBytes;
                    publishProgress((int) (totalUploadedBytes[0] * 100 / imagesSize[0]));
                }
            });
            mediaStreamWriter.startZipWriting(folderPath);
            mediaStreamWriter.close();
        }

        private void startUploadRecords(long globalId, String catalog, DataManager dataManager){
            mNotification.setContent(getString(R.string.uploading_records));
            try {
                uploadRecords(globalId, catalog);
                dataManager.updateRecordsUploaded(true, globalId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private long uploadRecords(long globalId, String catalog) throws IOException, JSONException {
            Log.d(LOG_TAG, "uploadRecords");
            UploadRecordRequest request = new UploadRecordRequest(globalId, LogPref.getLogin(getBaseContext()),
                    LogPref.getPassword(getBaseContext()), catalog, getBaseContext());
            HttpURLConnection connection = null;
            try {
                connection = request.start();
                String recordsFolderPath = MediaFileSystem.getMediaPath(catalog, MediaType.RECORDS, getBaseContext());
                uploadFiles(recordsFolderPath, connection.getOutputStream());
                InputStream stream = connection.getInputStream();
                stream.close();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return -1;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(LOG_TAG, "Postęp serwis " + progress[0]);
            if(progress[0] != 100){
                mNotification.setProgress(progress[0]);
            } else {
                mNotification.setContent(getString(R.string.finished));
                mNotification.hideProgress();
            }
            if (mCallback != null) {
                mCallback.onOperationProgress(progress[0]);
            }
            mNotification.send();
        }

        @Override
        protected void onPostExecute(Long globalId) {
            mRunningTasks--;
            stopForeground(true); //to spowoduje zniknięcie powiadomienia
            if (mCallback != null) {
                com.dyszlewskiR.edu.scientling.service.net.upload.UploadParams parts = new com.dyszlewskiR.edu.scientling.service.net.upload.UploadParams();
                parts.setDatabase(mUploadDatabase);
                parts.setImages(mUploadImages);
                parts.setRecords(mUploadRecords);
                mCallback.onOperationCompleted(mSetId, globalId, parts);
            } else { //jeżeli callback == null
                //w przypadku gdy aktywność która wywołała metode nie jest na pierwszym planie zamykamy
                //usługę po wykonaniu pracy, ponieważ jeżeli tego nie zrobimy usługa nie zostanie
                // zamknięta
                if (!isRunning()) {
                    stopSelf();
                }
            }
        }
    }
}
