package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.data.file.SizeConverter;
import com.dyszlewskiR.edu.scientling.models.others.SetItem;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.net.rating.RatingRequest;
import com.dyszlewskiR.edu.scientling.service.net.setList.SetDetailRequest;
import com.dyszlewskiR.edu.scientling.service.net.rating.RatingResponse;
import com.dyszlewskiR.edu.scientling.service.net.setList.SetDetailResponse;
import com.dyszlewskiR.edu.scientling.service.net.download.DownloadSetsService;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SetDetailsFragment extends Fragment  implements ServiceConnection, DownloadSetsService.Callback{

    private final int ONE_MB = 1048576;

    private TextView mNameTextView;
    private Button mDownloadButton;
    private TextView mL1TextView;
    private TextView mL2TextView;
    private TextView mAuthorTextView;
    private TextView mRatingTextView;
    private TextView mDownloadCountTextView;
    private TextView mAddedDateTextView;
    private TextView mMoreTextView;

    private View mDescriptionContainer;
    private TextView mDescriptionTextView;
    private TextView mNumWordsTextView;
    private TextView mSizeTextView;
    private TextView mImagesSizeTextView;
    private TextView mRecordsSizeTextView;
    private ProgressBar mProgressBar;
    private TextView mDownloadedTextView;

    private RatingBar mRatingBar;
    private TextView mUploadRatingButton;

    private long mSetId;
    private DownloadSetsService mService;
    private boolean mIsServiceBound;

    private boolean mDownloaded;
    private boolean mImagesToDownload;
    private boolean mRecordsToDownload;
    private boolean mSetHasImages;
    private boolean mSetHasRecords;
    private int mLastDownloading;
    private boolean mIsDownloading;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mSetId = intent.getLongExtra("id",-1);
        mSetHasImages = intent.getBooleanExtra("images", false);
        mSetHasRecords = intent.getBooleanExtra("records", false);
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(), DownloadSetsService.class);
        if(!LingApplication.getInstance().isServiceRunning(DownloadSetsService.class)){
            getActivity().getApplicationContext().startService(serviceIntent);
        }
        getActivity().getApplicationContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        setRetainInstance(true);

    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadSetsService.CUSTOM_INTENT);
        if(mService != null){
            mService.setCallback(this);
        }
        DataManager dataManager = LingApplication.getInstance().getDataManager();
        mDownloaded = dataManager.isSetDownloaded(mSetId);
        String catalog = dataManager.getSetCatalogByGlobalId(mSetId);
        mImagesToDownload = mSetHasImages && !MediaFileSystem.hasMedia(catalog, MediaType.IMAGES, getContext());
        mRecordsToDownload = mSetHasRecords && !MediaFileSystem.hasMedia(catalog, MediaType.RECORDS, getContext());
        setDownloadControls();
        if(mIsDownloading){
            setDownloading(true);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mService != null){
            mService.setCallback(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_details, container, false);
        setupControls(view);
        return view;
    }

    private void setupControls(View view){
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mDownloadButton = (Button)view.findViewById(R.id.download_button);
        mL1TextView = (TextView)view.findViewById(R.id.l1_text_view);
        mL2TextView = (TextView)view.findViewById(R.id.l2_text_view);
        mAuthorTextView = (TextView)view.findViewById(R.id.author_text_view);
        mRatingTextView = (TextView)view.findViewById(R.id.rating_text_view);
        mDownloadCountTextView = (TextView)view.findViewById(R.id.download_count_text_view);
        mAddedDateTextView = (TextView)view.findViewById(R.id.added_date_text_view);
        mMoreTextView = (TextView)view.findViewById(R.id.show_more_text_view);
        mProgressBar = (ProgressBar)view.findViewById(R.id.download_progress_bar);
        mDownloadedTextView = (TextView)view.findViewById(R.id.downloaded_text_view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        setListeners(view);
        registerForContextMenu(mDownloadButton);
        //setDownloadControls();
        GetSetAsyncTask task = new GetSetAsyncTask(view);
        task.execute(mSetId);
    }

    private void setDownloadControls(){
        if(mDownloaded){
            mDownloadedTextView.setVisibility(View.VISIBLE);
            if(!mImagesToDownload && !mRecordsToDownload){
                mDownloadButton.setVisibility(View.INVISIBLE);
            } else {
                mDownloadButton.setVisibility(View.VISIBLE);
            }
        } else {
            mDownloadedTextView.setVisibility(View.INVISIBLE);
            mDownloadButton.setVisibility(View.VISIBLE);
        }

    }

    private void setValues(SetItem item, View view){
        mNameTextView.setText(item.getName());
        mL1TextView.setText(ResourceUtils.getString(item.getLanguageL1(), getActivity()));
        mL2TextView.setText(ResourceUtils.getString(item.getLanguageL2(), getActivity()));
        mAuthorTextView.setText(item.getAuthor());
        mRatingTextView.setText(String.valueOf(item.getRating()));
        mDownloadCountTextView.setText(String.valueOf(item.getDownloads()));
        SimpleDateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        if(item.getAddedDate() != null){
            mAddedDateTextView.setText(format.format(item.getAddedDate()));
        }

        //jeśli użytkownik pobrał dany zestaw wyświetlamy część pozwalającą ocenić zestaw
        if(item.getWasDownloaded() != null && item.getWasDownloaded() && mRatingBar ==null){
            ViewStub stub = (ViewStub)view.findViewById(R.id.rating_container);
            ViewGroup ratingContainer = (ViewGroup)stub.inflate();
            mRatingBar = (RatingBar)ratingContainer.findViewById(R.id.rating_bar);
            mUploadRatingButton = (TextView)ratingContainer.findViewById(R.id.upload_rating_button);

            if(item.getUserRating() > 0){
                mRatingBar.setRating(item.getUserRating());
            }
            mUploadRatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rating = (int)mRatingBar.getRating();
                    RatingParam param = new RatingParam(mSetId, rating, LogPref.getLogin(getActivity()), LogPref.getPassword(getActivity()));
                    RatingAsyncTask task = new RatingAsyncTask();
                    task.execute(param);
                }
            });
        }

        if(mDescriptionContainer != null){
            mDescriptionTextView.setText(item.getDescription());
            mNumWordsTextView.setText(String.valueOf(item.getWordsCount()));
            if(item.getBasicSize() < ONE_MB){
                String size = String.format("%.2f", SizeConverter.bytesToKb(item.getBasicSize()));
                mSizeTextView.setText(size + " kb");
            } else {
                String size = String.format("%.2f", SizeConverter.bytesToMB(item.getBasicSize()));
                mSizeTextView.setText(size +" MB");
            }
            String imagesSize = String.format("%.2f", SizeConverter.bytesToMB(item.getImagesSize()));
            mImagesSizeTextView.setText(imagesSize + " MB");
            String recordsSize = String.format("%.2f", SizeConverter.bytesToMB(item.getRecordsSize()));
            mRecordsSizeTextView.setText(recordsSize + " MB");
        }
    }

    private void setListeners(final View view){
        mMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDescriptionContainer == null){
                    ViewStub stub = (ViewStub)view.findViewById(R.id.more_container);
                    mDescriptionContainer = stub.inflate();
                    mDescriptionTextView = (TextView)mDescriptionContainer.findViewById(R.id.description_text_view);
                    mNumWordsTextView = (TextView)mDescriptionContainer.findViewById(R.id.number_words_text_view);
                    mSizeTextView = (TextView)mDescriptionContainer.findViewById(R.id.size_text_view);
                    mImagesSizeTextView = (TextView)mDescriptionContainer.findViewById(R.id.images_size_text_view);
                    mRecordsSizeTextView = (TextView)mDescriptionContainer.findViewById(R.id.records_size_text_view);

                    mMoreTextView.setText(getString(R.string.less_information));
                    //TODO uruchiomić zadanie które pobierze informacje

                    GetSetAsyncTask task = new GetSetAsyncTask(view);
                    task.execute(mSetId);
                } else if(mDescriptionContainer.getVisibility()==View.VISIBLE){
                    mDescriptionContainer.setVisibility(View.GONE);
                    mMoreTextView.setText(getString(R.string.more_information));
                } else {
                    mDescriptionContainer.setVisibility(View.VISIBLE);
                    mMoreTextView.setText(getString(R.string.less_information));
                }
            }
        });

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().openContextMenu(mDownloadButton);
                mDownloadButton.showContextMenu();
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((DownloadSetsService.LocalBinder) service).getService();
        mService.setCallback(this);
        if(mService.isRunning()){
            setDownloading(true);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onOperationProgress(long setId, int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onOperationCompleted(long setId) {
        mProgressBar.setProgress(0);
        setDownloading(false);
       switch (mLastDownloading){
           case DOWNLOAD_ALL:
               mDownloaded = true; mImagesToDownload = false; mRecordsToDownload = false; break;
           case DOWNLOAD_DATABASE:
               mDownloaded = true; break;
           case DOWNLOAD_IMAGES:
               mImagesToDownload = false; break;
           case DOWNLOAD_RECORDS:
               mRecordsToDownload = false; break;
       }
       setDownloadControls();
    }

    private void setDownloading(boolean isDownloading){
        mProgressBar.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
        mDownloadButton.setVisibility(isDownloading ? View.INVISIBLE : View.VISIBLE);
    }

    public void setResult(){
        Intent intent = new Intent();
        intent.putExtra("id", mSetId);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onDestroy(){
        if(mIsServiceBound){
            if(mService != null && !mService.isRunning()){
                Intent intent = new Intent(getActivity().getApplicationContext(), DownloadSetsService.class);
                getActivity().getApplicationContext().stopService(intent);
            }
            getActivity().getApplicationContext().unbindService(this);
        }


        super.onDestroy();
    }

    private final int DOWNLOAD_ALL = R.string.download_all;
    private final int DOWNLOAD_DATABASE = R.string.download_database;
    private final int DOWNLOAD_IMAGES = R.string.download_images;
    private final int DOWNLOAD_RECORDS = R.string.download_records;

    /** Tworzy menu kontekstowe po naciścięnciu przycisku pobierania.
     * Jeżeli zestaw nie jest pobrany
     * - pobierz wszystko
     * - pobierz bazę danych
     * Jeżeli zestaw jest pobrany
     *      jeżeli obrazki są nie pobramne
     *      - pobierz obrazki
     *      jeżeli nagrania są nie pobrane
     *      -pobierz nagrania
     * @param menu
     * @param view
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        /*DataManager dataManager = LingApplication.getInstance().getDataManager();
        String catalog = dataManager.getSetCatalogByGlobalId(mSetId);
        if(catalog == null){ // zestaw nie został jeszcze pobrany
            long allSize = item.getBasicSize() + item.getImagesSize() + item.getRecordsSize();
            String downloadAllText = getString(DOWNLOAD_ALL) + getSizeText(allSize);
            menu.add(0, DOWNLOAD_ALL, 0, downloadAllText);
            String databaseText = getString(DOWNLOAD_DATABASE) + getSizeText(item.getBasicSize());
            menu.add(0, DOWNLOAD_DATABASE, 0, databaseText);

        }*/
        menu.setHeaderTitle(mNameTextView.getText().toString());
        if(!mDownloaded){
            menu.add(0, DOWNLOAD_ALL, 0, getString(DOWNLOAD_ALL));
            menu.add(0, DOWNLOAD_DATABASE, 0, getString(DOWNLOAD_DATABASE));
        } else {
            if(mImagesToDownload){
                menu.add(0, DOWNLOAD_IMAGES, 0, getString(DOWNLOAD_IMAGES));
            }
            if(mRecordsToDownload){
                menu.add(0, DOWNLOAD_RECORDS, 0, getString(DOWNLOAD_RECORDS));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        String setName = mNameTextView.getText().toString();
        mLastDownloading = item.getItemId();
        switch (item.getItemId()){
            case DOWNLOAD_ALL:
                startDownloadSetService(mSetId, setName, true, true, true); break;
            case DOWNLOAD_DATABASE:
                startDownloadSetService(mSetId, setName, true, false, false); break;
            case DOWNLOAD_IMAGES:
                startDownloadSetService(mSetId, setName, false, true, false); break;
            case DOWNLOAD_RECORDS:
                startDownloadSetService(mSetId, setName, false, false, true); break;
            default:
                return false;
        }
        return true;
    }

    private void startDownloadSetService(long globalId,String name, boolean database, boolean images, boolean records){
        if(mService != null){
            mService.startDownloading(globalId, name, database, images, records);
        }
        setDownloading(true);
    }

    private class GetSetAsyncTask extends AsyncTask<Long, Void, SetItem>{

        private View mView;

        public GetSetAsyncTask(View view){
            mView = view;
        }

        @Override
        protected SetItem doInBackground(Long... params) {
            SetDetailRequest request = new SetDetailRequest(params[0], LogPref.getLogin(getActivity()), LogPref.getPassword(getActivity()));
            try {
                HttpURLConnection connection = request.start();
                SetDetailResponse response = new SetDetailResponse(connection);
                if(response.getResultCode()==SetDetailResponse.OK){
                    return response.getSet();
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SetItem result){
            if(result != null){
                setValues(result, mView);
            }
        }
    }

    private class RatingParam{
        private long mSetId;
        private int mRating;
        private String mUsername;
        private String mPassword;

        public RatingParam(long setId, int rating, String username, String password){
            mSetId = setId;
            mRating = rating;
            mUsername = username;
            mPassword = password;
        }

        public long getSetId(){return mSetId;}
        public int getRating(){return mRating;}
        public String getUsername(){return mUsername;}
        public String getPassword(){return mPassword;}
    }

    private class RatingAsyncTask extends AsyncTask<RatingParam, Void, Boolean> {

        @Override
        protected Boolean doInBackground(RatingParam... params) {
            HttpURLConnection connection = null;
            try {
                connection = RatingRequest.start(params[0].getSetId(), params[0].getRating(),
                        params[0].getUsername(), params[0].getPassword());
                RatingResponse response = new RatingResponse(connection);
                if(response.getResultCode()>0){
                    response.getResponse();
                    response.closeConnection();
                    return true;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(getActivity(), "Ocena została wystawiona", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Wystąpił błąd przy wysyłaniu oceny", Toast.LENGTH_SHORT).show();
            }
            //TODO dodać napisy do stringu

        }
    }
}
