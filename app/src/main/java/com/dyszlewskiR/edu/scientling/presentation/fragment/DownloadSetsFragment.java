package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.SetDetailsActivity;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.file.FileSizeFormatter;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.models.others.SetItem;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.service.net.download.DownloadSetsService;
import com.dyszlewskiR.edu.scientling.service.net.requests.SetsListRequest;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.net.setList.SetsListResponse;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class DownloadSetsFragment extends Fragment implements ServiceConnection, DownloadSetsService.Callback{

    private final String LOG_TAG = "DownloadSetsFragment";
    private final int SIMPLE_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int DETAILS_REQUEST = 2038;

    private final int ITEMS_ON_LIST = 10;
    private final int EMPTY_LANGUAGE_ID = -1;

    private ListView mListView;
    private DownloadSetAdapter mAdapter;
    private LanguageSpinnerAdapter mLanguageAdapter;

    private ViewGroup mLoadingContainer;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingTextView;

    private EditText mSearchEditText;
    private TextView mTextView;
    private ViewGroup mFilterContainer;
    private Spinner mL1Spinner;
    private Spinner mL2Spinner;
    private Spinner mSortingSpinner;
    private Button mSearchButton;

    private View mListFooter;
    private Button mFooterButton;
    private ProgressBar mFooterProgressBar;
    private int mPage;

    private DownloadSetsService mService;

    private boolean mIsServiceBound;

    private boolean mIsFilterSpinnerChanged;

    private SetsListAsyncTask mTask;

    public DownloadSetsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //jeżeli usługa nie była wcześniej uruchomiona to uruchamiamy ją
        if(!LingApplication.getInstance().isServiceRunning(DownloadSetsService.class)){
            Intent intent = new Intent(getActivity().getApplicationContext(), DownloadSetsService.class);
            getActivity().getApplicationContext().startService(intent);
        }
        //wiązemy usługę z fragmentem dzięki czemu będziemy mogli pokazywać postęp pobierania
        Intent bindIntent = new Intent(getActivity().getApplicationContext(), DownloadSetsService.class);
        getActivity().getApplicationContext().bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;

        //pozwala przetrwać konfiguracji okna obrót ekranu
        //fragment jest przywracany do stanu w jakim był przed obrotem
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_sets, container, false);
        setupControls(view);
        setListFooter();
        setAdapters();
        fillList();
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        mLoadingContainer = (ViewGroup) view.findViewById(R.id.loading_container);
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);
        mLoadingTextView = (TextView) view.findViewById(R.id.loading_text_view);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mFilterContainer = (ViewGroup) view.findViewById(R.id.filter_container);
        mL1Spinner = (Spinner) view.findViewById(R.id.l1_spinner);
        mL2Spinner = (Spinner) view.findViewById(R.id.l2_spinner);
        mSortingSpinner = (Spinner) view.findViewById(R.id.sorting_spinner);
        mSearchButton = (Button) view.findViewById(R.id.get_button);
    }

    private void setListFooter() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mListFooter = inflater.inflate(R.layout.footer_download_set, null);
        mFooterButton = (Button) mListFooter.findViewById(R.id.more_button);
        mFooterProgressBar = (ProgressBar) mListFooter.findViewById(R.id.footer_progress_bar);
        mListView.addFooterView(mListFooter);
    }

    private void setAdapters() {
        int ADAPTER_ITEM_RESOURCE = R.layout.item_downloaded_set;
        if(mAdapter == null){
            mAdapter = new DownloadSetAdapter(getContext(), ADAPTER_ITEM_RESOURCE, new ArrayList<SetItem>());
        }

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        String[] sortingList = getContext().getResources().getStringArray(R.array.sorting_downloaded_set);
        mSortingSpinner.setAdapter(new ArrayAdapter<>(getContext(), SIMPLE_ADAPTER_RESOURCE, sortingList));
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        List<Language> languageList = dataManager.getLanguages();
        Language anyLanguage = new Language(EMPTY_LANGUAGE_ID);
        anyLanguage.setName("any");

        languageList.add(0, anyLanguage);
        mLanguageAdapter = new LanguageSpinnerAdapter(getContext(), SIMPLE_ADAPTER_RESOURCE, languageList);
        mL2Spinner.setAdapter(mLanguageAdapter);
        mL1Spinner.setAdapter(mLanguageAdapter);
    }

    private void fillList(){
        if(mAdapter.isEmpty()){
            mTask = new SetsListAsyncTask(false);
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getRequestParam());
        } else {
            mListView.setAdapter(mAdapter);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadSetsService.CUSTOM_INTENT);
        if(mService != null) {
            mService.setCallback(this);
            mLoadingContainer.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mListFooter.setVisibility(View.VISIBLE);
            mService.setCallback(this);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
       if(mService!=null){
           mService.setCallback(null);
       }
    }

    @Override
    public void onDestroy(){
        if(mIsServiceBound){
            if(mService != null && !mService.isRunning()){
                Log.d(LOG_TAG, "Właśnie zabijam usługę");
                Intent intent = new Intent(getActivity().getApplicationContext(), DownloadSetsService.class);
                getActivity().getApplication().stopService(intent);
            }
            getActivity().getApplicationContext().unbindService(this);

        }
        if(!mTask.isCancelled()){
            mTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setListeners();
    }

    private void setListeners() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPage = 0;
                SetsListAsyncTask task = new SetsListAsyncTask(false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getRequestParam());
            }
        });

        mFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mPage;
                SetsListAsyncTask task = new SetsListAsyncTask(true);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getRequestParam());
            }
        });

        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mIsFilterSpinnerChanged){
                    mPage = 0;
                    SetsListAsyncTask task = new SetsListAsyncTask(false);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getRequestParam());
                    mIsFilterSpinnerChanged = false;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSortingSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mIsFilterSpinnerChanged = true;
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startDetailsActivity(position);
            }
        });

    }

    private void startDetailsActivity(int position){
        SetItem item = mAdapter.getItem(position);
        Intent intent = new Intent(getContext(), SetDetailsActivity.class);
        intent.putExtra("id", item != null ? item.getId() : 0);
        intent.putExtra("images",  item.hasImages());
        intent.putExtra("records", item.hasRecords());
        startActivityForResult(intent, DETAILS_REQUEST);
    }

    private final int INFO = R.string.info;
    private final int DOWNLOAD_ALL = R.string.download_all;
    private final int DOWNLOAD_DATABASE = R.string.download_database;
    private final int DOWNLOAD_IMAGES = R.string.download_images;
    private final int DOWNLOAD_RECORDS = R.string.download_records;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        int position = info.position;
        SetItem item = mAdapter.getItem(position);
        menu.setHeaderTitle(item.getName());
        menu.add(0, INFO, 0, getString(INFO));
        if(!item.isDownloaded()){
            long allSize = item.getBasicSize() + item.getImagesSize() + item.getRecordsSize();
            String downloadAllText = getString(DOWNLOAD_ALL) + getSizeText(allSize);
            menu.add(0, DOWNLOAD_ALL, 0, downloadAllText);
            String databaseText = getString(DOWNLOAD_DATABASE) + getSizeText(item.getBasicSize());
            menu.add(0, DOWNLOAD_DATABASE, 0, databaseText);
        } else {
            if(item.hasImages() && !item.isImagesDownloaded()){
                String imagesText = getString(DOWNLOAD_IMAGES) + getSizeText(item.getImagesSize());
                menu.add(0, DOWNLOAD_IMAGES, 0, imagesText);
            }
            if(item.hasRecords() && !item.isRecordsDownloaded()){
                String recordsText = getString(DOWNLOAD_RECORDS) + getSizeText(item.getRecordsSize());
                menu.add(0, DOWNLOAD_RECORDS, 0, recordsText);
            }
        }
    }

    private String getSizeText(long size){
        return " (" + FileSizeFormatter.getSize(size) + ")";
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        SetItem setItem = mAdapter.getItem(position);
        switch(item.getItemId()){
            case INFO:
                startDetailsActivity(position); break;
            case DOWNLOAD_ALL:
                startDownloadSetService(setItem, true, true, true); break;
            case DOWNLOAD_DATABASE:
                startDownloadSetService(setItem, true, false , false); break;
            case DOWNLOAD_IMAGES:
                startDownloadSetService(setItem, false, true, false); break;
            case DOWNLOAD_RECORDS:
                startDownloadSetService(setItem, false , false, true); break;
        }
        return true;
    }

    private void startDownloadSetService(SetItem item,boolean database, boolean images, boolean records) {
        if(mService != null){
            mService.startDownloading(item.getId(), item.getName(),database, images, records);
        }
        item.setDownloadingProgress(0);
        item.setDownloading(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_download_sets, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                setSearchControlsVisibility();
                return true;
            case R.id.filter_button:
                setFilterContainerVisibility();
                return true;
            case R.id.sorting_button:
                setSortingControlsVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSearchControlsVisibility() {
        if (mSearchEditText.getVisibility() == View.VISIBLE) {
            mSearchEditText.setVisibility(View.GONE);
            if (mFilterContainer.getVisibility() == View.GONE) {
                mSearchButton.setVisibility(View.GONE);
            }
        } else {
            mSearchEditText.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
        }
    }

    private void setFilterContainerVisibility() {
        if (mFilterContainer.getVisibility() == View.VISIBLE) {
            mFilterContainer.setVisibility(View.GONE);
            if (mSearchEditText.getVisibility() == View.GONE) {
                mSearchButton.setVisibility(View.GONE);
            }
        } else {
            mFilterContainer.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
        }
    }

    private void setSortingControlsVisibility() {
        if (mSortingSpinner.getVisibility() == View.VISIBLE) {
            mSortingSpinner.setVisibility(View.GONE);
        } else {
            mSortingSpinner.setVisibility(View.VISIBLE);
        }
    }

    private RequestParam getRequestParam(){
        RequestParam param = new RequestParam();
        assert mL1Spinner != null;
        assert mL2Spinner != null;
        assert mLanguageAdapter != null;
        param.setL1(mLanguageAdapter.getItem(mL1Spinner.getSelectedItemPosition()).getId());
        param.setL2(mLanguageAdapter.getItem(mL2Spinner.getSelectedItemPosition()).getId());
        if(mSearchEditText.getVisibility() == View.VISIBLE && !mSearchEditText.getText().toString().equals("")){
            param.setSearchText(mSearchEditText.getText().toString());
        }
        param.setSorting(mSortingSpinner.getSelectedItemPosition());
        param.setPage(mPage);

        param.setUsername(LogPref.getLogin(getContext()));
        param.setPassword(LogPref.getPassword(getContext()));
        return param;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((DownloadSetsService.LocalBinder) service).getService();
        mService.setCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onOperationProgress(long setId,int progress) {
        mAdapter.setDownloadProgress(setId, progress);
    }

    @Override
    public void onOperationCompleted(long setId) {
        Log.d(LOG_TAG, "onOperationCompleted" + setId);
        mAdapter.setDownloaded(setId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(getClass().getSimpleName(), "onActivityResult Start");
        if(requestCode == DETAILS_REQUEST){
            if(resultCode == SetDetailsActivity.RESULT_OK){
                Log.d(getClass().getSimpleName(), "onActivityResult");
                long globalId = data.getLongExtra("id", -1);
                mAdapter.setDownloaded(globalId);
            }
        }
    }

    private class RequestParam{
        private long mL1;
        private long mL2;
        private String mSearchText;
        private int mSorting;
        private int mPage;
        private int mLimit;
        private String mUsername;
        private String mPassword;

        public long getL1(){return mL1;}
        public long getL2(){return mL2;}
        public String getSearchText(){return mSearchText;}
        public int getSorting(){return mSorting;}
        public int getPage(){return mPage;}
        public int getLimit(){return mLimit;}
        public String getUsername(){return mUsername;}
        public String getPassword(){return mPassword;}

        public void setL1(long l1){mL1 = l1;}
        public void setL2(long l2){mL2 = l2;}
        public void setSearchText(String text){mSearchText = text;}
        public void setSorting(int sorting) {mSorting = sorting;}
        public void setPage(int page){mPage = page;}
        public void setLimit(int limit){mLimit = limit;}
        public void setUsername(String username){mUsername = username;}
        public void setPassword(String password){mPassword = password;}
    }
    //region SetsListAsyncTask
    private class SetsListAsyncTask extends AsyncTask<RequestParam, Void, List<SetItem>> {

        /**
         * określa czy pobieramy więcej elementów i dodajemy je do listy
         * false oznacza, że podczas pobierania usuwane są wszystkie elementy i dodawane pobrane
         */
        private boolean mDownloadMore;

        public SetsListAsyncTask(boolean isDownloadingMore){
            mDownloadMore = isDownloadingMore;
        }

        @Override
        protected void onPreExecute() {
            if (mDownloadMore) {
                mFooterButton.setVisibility(View.GONE);
                mFooterProgressBar.setVisibility(View.VISIBLE);
            } else {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<SetItem> doInBackground(RequestParam... params) {
            HttpURLConnection connection = null;
            try {
                RequestParam param = params[0];
                SetsListRequest request = new SetsListRequest(
                        param.getL1(), param.getL2(),param.getSearchText(),
                        param.getSorting(), param.getPage(),
                        param.getUsername(), param.getPassword()
                );
                connection = request.start();
                SetsListResponse response = new SetsListResponse(connection);
                List<SetItem> items = response.getSetItemsList();
                DataManager dataManager = ((LingApplication)getActivity().getApplication()).getDataManager();
                for(SetItem item : items){
                    //pobieramy nazwę katalogu pyrzpisaną do zestawu który ma podany globalId
                    //jeżeli catalog == null oznacza to, że nie pobrano jeszcze danego zestawu
                    String catalog = dataManager.getSetCatalogByGlobalId(item.getId());
                    if(catalog !=null){
                        //jeżeli catalog nie jest nullem zaznaczamy że zestaw został już pobranuy
                        //a nastepnie sprawdzamy czy zestaw ma pobrane już jakieś obrazki i nagrania
                        // sprawdzając to w systemi plików
                        item.setDownloaded(true);
                        item.setImagesDownloaded(MediaFileSystem.hasMedia(catalog, MediaType.IMAGES, getContext()));
                        item.setRecordsDownloaded(MediaFileSystem.hasMedia(catalog,MediaType.RECORDS, getContext()));
                    }
                }
                return items;
            } catch (SocketTimeoutException e) {
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(List<SetItem> result) {

            if (result != null) {

                mAdapter.addAll(result);
                if (mDownloadMore) { //dociąganie elementów
                    mFooterProgressBar.setVisibility(View.GONE);
                    mFooterButton.setVisibility(View.VISIBLE);
                } else { //pobieranie nowej listy
                    if (result.size() > 0) {
                        mLoadingContainer.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        mListFooter.setVisibility(View.VISIBLE);
                    } else { // w przypadku jeśli nie będzie żadnych pasujących zestawów
                        mLoadingProgressBar.setVisibility(View.GONE);
                        mLoadingTextView.setText(getString(R.string.sets_not_found));
                    }
                }
                //jeśli pobraliśmy już wszystko co mogliśmy pobrać ukrywamy przycisk
                if (result.size() < ITEMS_ON_LIST) {
                    mListFooter.setVisibility(View.GONE);
                }
            } else {
                mLoadingProgressBar.setVisibility(View.GONE);
                mLoadingTextView.setText(getString(R.string.connection_error));
            }
        }
    }

    //endregion

    //region DownloadSetAdapter
    private class DownloadSetAdapter extends ArrayAdapter {

        private List<SetItem> mItems;
        private Context mContext;
        private int mResource;
        private DataManager mDataManager;

        public DownloadSetAdapter(Context context, int resource, List<SetItem> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mItems = data;
            mDataManager = ((LingApplication)getActivity().getApplication()).getDataManager();
        }

        public List<SetItem> getItems()
        {
            return mItems;
        }
        @Override
        public SetItem getItem(int position){
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position){
            return mItems.get(position).getId();
        }

        public SetItem getItemById(long id){
            for(SetItem item : mItems){
                if(item.getId() == id){
                    return item;
                }
            }
            return null;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
            View rowView = convertView;
            final ViewHolder viewHolder;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rowView = inflater.inflate(mResource, null);
                viewHolder = new ViewHolder(rowView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            final SetItem item = mItems.get(position);
            viewHolder.nameTextView.setText(item.getName());
            if (item.getLanguageL2() != null) {
                viewHolder.languageL2TextView.setText(ResourceUtils.getString(item.getLanguageL2(), getContext()));
            }
            if (item.getLanguageL1() != null) {
                viewHolder.languageL1TextView.setText(ResourceUtils.getString(item.getLanguageL1(), getContext()));
            }

            //viewHolder.authorTextView.setValues(mItems.get(position).getAuthor());
            viewHolder.numberWordsTextView.setText(String.valueOf(item.getWordsCount()));
            //viewHolder.sizeTextView.setValues(String.valueOf(mItems.get(position).getBasicSize()));
            viewHolder.ratingTextView.setText(String.valueOf(item.getRating()));
            viewHolder.downloadsTextView.setText(String.valueOf(item.getDownloads()));
            //if(checkSetIsDownloaded(mItems.get(position).getId())){
            //TODO poustawiać wszystko tak jak powinno być
            if(item.getDownloadingProgress() >0){
                item.setDownloading(true);
            }

            if(item.isDownloading()){
                showDownloading(viewHolder);
                int downloadingProgress = mItems.get(position).getDownloadingProgress();
                if( downloadingProgress!= 0){
                    viewHolder.downloadProgressBar.setProgress(downloadingProgress);
                }
            } else if(item.isDownloaded()){
                if((item.hasImages() && !item.isImagesDownloaded()) || (item.hasRecords() && item.isRecordsDownloaded())) {
                    showDownloaded(viewHolder, false);

                } else {
                    showDownloaded(viewHolder, true);
                }

            } else{
                showDownloadButton(viewHolder);
            }

            if(viewHolder.downloadButton.getVisibility()==View.VISIBLE){
                viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.showContextMenuForChild(v);
                    }
                });
            }
            //viewHolder.downloadProgressBar.setVisibility(View.VISIBLE);
            viewHolder.downloadsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
            return rowView;
        }

        public void setDownloadProgress(long setId, int progress){
            if(progress > 0){
                for(SetItem item : mItems){
                    if(item.getId() == setId){
                        item.setDownloadingProgress(progress);
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

        void setDownloaded(long globalSetId){
            for(SetItem item : mItems){
                if(item.getId() == globalSetId){
                    item.setDownloaded(true);
                    item.setDownloading(false);
                    item.setDownloadingProgress(0);
                    //TODO to można było by zrobić dużo fajniej, będzie trzeba do tego wrócic
                    String catalog = mDataManager.getSetCatalogByGlobalId(globalSetId);
                    item.setImagesDownloaded(MediaFileSystem.hasMedia(catalog,MediaType.IMAGES, mContext));
                    item.setRecordsDownloaded(MediaFileSystem.hasMedia(catalog,MediaType.RECORDS, mContext));
                    //item.setDownloadInfo(mDataManager.getSetDownloadInfo(globalSetId));
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        private void showDownloading(ViewHolder viewHolder){
            if(viewHolder.downloadingTextView.getVisibility()!=View.VISIBLE){
                viewHolder.downloadButton.setVisibility(View.GONE);
                viewHolder.downloadingTextView.setVisibility(View.VISIBLE);
                viewHolder.downloadProgressBar.setVisibility(View.VISIBLE);
                viewHolder.downloadProgressBar.setMax(100);
                viewHolder.downloadProgressBar.setProgress(0);
            }
        }

        private void showDownloaded(ViewHolder viewHolder, boolean allDownloaded){
            if(allDownloaded){
                viewHolder.downloadButton.setVisibility(View.GONE);
            } else {
                viewHolder.downloadButton.setVisibility(View.VISIBLE);
            }
            viewHolder.downloadingTextView.setVisibility(View.GONE);
            viewHolder.downloadedTextView.setVisibility(View.VISIBLE);
            viewHolder.downloadProgressBar.setVisibility(View.GONE);
        }

        private void showDownloadButton(ViewHolder viewHolder){
            viewHolder.downloadedTextView.setVisibility(View.GONE);
            viewHolder.downloadingTextView.setVisibility(View.GONE);
            viewHolder.downloadButton.setVisibility(View.VISIBLE);
        }

    }
    //endregion

    //region LanguageSpinnerAdapter
    private class LanguageSpinnerAdapter extends ArrayAdapter {
        private Context mContext;
        private int mResource;
        private List<Language> mLanguages;

        public LanguageSpinnerAdapter(Context context, int resource, List<Language> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mLanguages = data;
        }

        @Override
        public Language getItem(int position) {
            return mLanguages.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rowView = inflater.inflate(mResource, null);
            }
            TextView textView = (TextView) rowView.findViewById(R.id.text_view);
            String name = mLanguages.get(position).getName();
            textView.setText(ResourceUtils.getString(name, mContext));

            return rowView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rowView = inflater.inflate(mResource, null);
            }
            TextView textView = (TextView) rowView.findViewById(R.id.text_view);
            String name = mLanguages.get(position).getName();
            textView.setText(ResourceUtils.getString(name, mContext));

            return rowView;
        }
    }

    //endregion

    private static class ViewHolder {
        public TextView nameTextView;
        public TextView languageL1TextView;
        public TextView languageL2TextView;
        //public TextView authorTextView;
        public TextView numberWordsTextView;
        //public TextView sizeTextView;
        public TextView ratingTextView;
        public TextView downloadsTextView;
        public Button downloadButton;
        public TextView downloadedTextView;
        public TextView downloadingTextView;
        public ProgressBar downloadProgressBar;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            languageL1TextView = (TextView) view.findViewById(R.id.language_l1_text_view);
            languageL2TextView = (TextView) view.findViewById(R.id.language_l2_text_view);
            //authorTextView = (TextView) view.findViewById(R.id.author_text_view);
            numberWordsTextView = (TextView) view.findViewById(R.id.number_words_text_view);
            //sizeTextView = (TextView) view.findViewById(R.id.size_text_view);
            ratingTextView = (TextView) view.findViewById(R.id.rating_text_view);
            downloadsTextView = (TextView) view.findViewById(R.id.downloads_text_view);
            downloadButton = (Button) view.findViewById(R.id.download_button);
            downloadedTextView = (TextView)view.findViewById(R.id.downloaded_text_view);
            downloadingTextView = (TextView)view.findViewById(R.id.is_download_text_view);
            downloadProgressBar = (ProgressBar)view.findViewById(R.id.download_progress_bar);
        }

    }
}
