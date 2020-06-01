package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.LessonSelectionActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.SetEditActivity;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.data.file.FileSizeFormatter;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.service.preferences.Settings;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.management.DeletingSetService;
import com.dyszlewskiR.edu.scientling.service.net.upload.UploadParams;
import com.dyszlewskiR.edu.scientling.service.net.upload.UploadSetService;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

public class SetsManagerFragment extends Fragment implements ServiceConnection, UploadSetService.Callback {

    private final int LAYOUT_RESOURCE = R.layout.fragment_sets_manager;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_set_manager;
    private final int ADD_REQUEST = 7233;
    private final int EDIT_REQUEST = 7334;

    private ViewStub mUploadProgressStub;
    private ViewGroup mUploadProgressContainer;
    private ListView mListView;
    private SetsManagerAdapter mAdapter;

    private int mLastEditedPosition;
    private int mDeletedPosition;

    private UploadSetService mService;
    private boolean mIsServiceBound;

    private ProgressBar mUploadProgressBar;
    private boolean mIsUploading;


    public SetsManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //jeżeli usługa nie była wcześniej uruchamiana uruchamiamy ją
        if (!LingApplication.getInstance().isServiceRunning(UploadSetService.class)) {
            Intent intent = new Intent(getActivity().getApplicationContext(), UploadSetService.class);
            getActivity().getApplicationContext().startService(intent);
        }
        //wiążemy usługe z aktywnością, aby można było wykorzystać metody zwrotne do aktualizowania postepu
        Intent bindIntent = new Intent(getActivity().getApplicationContext(), UploadSetService.class);
        getActivity().getApplication().bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
        //ustawiamy automatyczną zmianę konfiguracji(np. obót ekranu)
        //dzięki temu nie musimy przeciążać metod aby zapisać konfigurację i później jej przywracać
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mService != null) {
            mService.setCallback(this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mService != null) {
            mService.setCallback(null);
        }
    }

    @Override
    public void onDestroy() {
        if (mIsServiceBound) {
            //jeżeli w chwili zamykania aktywności usługa zakończyła swoją pracę usuwamy usługę
            //jeżeli usługa nadal wykonuje swoje zadanie nie robimy nic, ponieważ usługa zakończy sie
            //sama w chwili wykonania działania
            if (mService != null && !mService.isRunning()) {
                Intent intent = new Intent(getActivity().getApplicationContext(), UploadSetService.class);
                getActivity().getApplicationContext().stopService(intent);
            }
            //rozłączamy aktywność od usługi
            //rozłączamy aktywność od usług
            getActivity().getApplicationContext().unbindService(this);
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
        mUploadProgressStub = (ViewStub) view.findViewById(R.id.upload_progress_container);
    }

    private void setListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startLessonActivity(position);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setSetsAdapter();
    }

    private void setSetsAdapter() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        List<VocabularySet> setsList = dataManager.getSets(); //TODO zmienić na metode która pobiera tylko potrzebne wartośći
        mAdapter = new SetsManagerAdapter(getContext(), ADAPTER_ITEM_RESOURCE, setsList);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
    }

    private final int LESSONS = R.string.lessons;
    private final int EDIT = R.string.edit;
    private final int CHOOSE = R.string.choose;
    private final int DELETE = R.string.delete;
    private final int DELETE_IMAGES = R.string.delete_images;
    private final int DELETE_RECORDS = R.string.delete_records;

    private final int UPLOAD_ALL = R.string.upload_all;
    private final int UPLOAD_DATABASE = R.string.upload_database;
    private final int UPLOAD_IMAGES = R.string.upload_images;
    private final int UPLOAD_RECORDS = R.string.upload_records;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        VocabularySet set = mAdapter.getItem(position);
        super.onCreateContextMenu(menu, view, menuInfo);
        //jako numery id elementów menu podajemy numery identyfikacyjne zasobów Androida
        //dzięki temu korzystamy z jednej stałem do przydzielenia numeru id który będzie później
        //wykorzystywany do utworzenia słuchacza, jak i też do ustawienia odpowiedniego napisu
        assert set != null;
        menu.setHeaderTitle(set.getName());
        menu.add(0, LESSONS, 0, getString(LESSONS));
        menu.add(0, EDIT, 0, getString(EDIT));
        menu.add(0, CHOOSE, 0, getString(CHOOSE));
        menu.add(0, DELETE, 0, getString(DELETE));
        boolean hasImages = MediaFileSystem.hasMedia(set.getCatalog(),MediaType.IMAGES, getContext());
        if (hasImages) {
            menu.add(0, DELETE_IMAGES, 0, getString(DELETE_IMAGES));
        }
        boolean hasRecords = MediaFileSystem.hasMedia(set.getCatalog(),MediaType.RECORDS, getContext());
        if (hasRecords) {
            menu.add(0, DELETE_RECORDS, 0, getString(DELETE_RECORDS));
        }
        String loggedUser = LogPref.getLogin(getContext());
        if (loggedUser != null) { //jeżeli użytkownik jest zalogowany
            if (set.getGlobalId() == null) {
                menu.add(0, UPLOAD_ALL, 0, getString(UPLOAD_ALL));
                menu.add(0, UPLOAD_DATABASE, 0, getString(UPLOAD_DATABASE));
            } else {
                if (loggedUser.equals(set.getUploadingUser())) { //jeżli zestaw został wstawiony na serwer przez zalogowanego użytkownika
                    if (hasImages && !set.isImagesUploaded()) {
                        menu.add(0, UPLOAD_IMAGES, 0, getString(UPLOAD_IMAGES));
                    }
                    if (hasRecords && !set.isRecordsUploaded()) {
                        menu.add(0, UPLOAD_RECORDS, 0, getString(UPLOAD_RECORDS));
                    }
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case LESSONS:
                startLessonActivity(position);
                break;
            case EDIT:
                startSetEditActivity(position);
                break;
            case CHOOSE:
                chooseSet(position);
                break;
            case DELETE:
                deleteSet(position);
                break;
            case DELETE_IMAGES:
                showDeleteImagesDialog(position);
                break;
            case DELETE_RECORDS:
                showDeleteRecordsDialog(position);
                break;
            case UPLOAD_ALL:
                showUploadDialog(position, true, true, true);
                break;
            case UPLOAD_DATABASE:
                showUploadDialog(position, true, false, false);
                break;
            case UPLOAD_IMAGES:
                showUploadDialog(position, false, true, false);
                break;
            case UPLOAD_RECORDS:
                showUploadDialog(position, false, false, true);
                break;
        }
        return true;
    }

    private void startSetEditActivity(int position) {
        Intent intent = new Intent(getActivity(), SetEditActivity.class);
        //jeżeli position będzie mniejsze od 0 oznacza to, że uruchmaiamy aktywność w celu dodania nowego zestawu
        //a nie jego edycji
        if (position >= 0) {
            intent.putExtra("setId", mAdapter.getItemId(position));
            mLastEditedPosition = position;
            startActivityForResult(intent, EDIT_REQUEST);
        } else {
            startActivityForResult(intent, ADD_REQUEST);
        }
    }

    private void chooseSet(int position) {
        Intent intent = new Intent();
        //wstawiamy numer id wybranego zestawu i zwracamy wynik do aktywności MainActivity,
        //gdzie ten zestaw zostanie ustawiony jako aktywny
        intent.putExtra("result", mAdapter.getItemId(position));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void showDeleteImagesDialog(int position) {
        DeleteDialog dialog = new DeleteDialog(getContext(), mAdapter.getItem(position), DeleteDialog.IMAGES_MODE,
                new DeleteDialog.DeleteListener() {
                    @Override
                    public void delete(VocabularySet set) {
                        deleteImages(set);
                    }
                });
        dialog.show();
    }

    private void showDeleteRecordsDialog(final int position) {
        DeleteDialog dialog = new DeleteDialog(getContext(), mAdapter.getItem(position), DeleteDialog.RECORDS_MODE,
                new DeleteDialog.DeleteListener() {
                    @Override
                    public void delete(VocabularySet set) {
                        deleteRecords(set);
                    }
                });
        dialog.show();
    }

    private void showUploadingContainer(String setName) {
        mAdapter.setActionButtonEnable(false);
        if (mUploadProgressContainer == null) {
            mUploadProgressContainer = (ViewGroup) mUploadProgressStub.inflate();
            TextView setNameTextView = (TextView) mUploadProgressContainer.findViewById(R.id.name_text_view);
            setNameTextView.setText(setName);
            mUploadProgressBar = (ProgressBar) mUploadProgressContainer.findViewById(R.id.upload_progress_bar);
        } else {
            mUploadProgressContainer.setVisibility(View.VISIBLE);
            mUploadProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showUploadDialog(int position, boolean database, boolean images, boolean records) {
        //jeżeli wysyłamy wszystko lub tylko bazę danych wyświetlamy dialog UploadSetDialog
        UploadListener listener = new UploadListener() {
            @Override
            public void startUpload(VocabularySet set, String description, boolean database, boolean images, boolean records) {
                showUploadingContainer(set.getName());
                //ServerSet.upload(set, description, database, images, records, getContext());
                mService.upload(set.getId(), set.getGlobalId(), description, database, images, records);
                mIsUploading = true;
            }
        };
        VocabularySet set = mAdapter.getItem(position);
        if (database) {
            UploadSetDialog dialog = new UploadSetDialog(getContext(), set, (images && records), listener);
            dialog.show();
        } else {
            //TODO tutaj też zrobić zajefajny dialog i go pokazać
            if (images) {
                UploadMediaDialog dialog = new UploadMediaDialog(getContext(), mAdapter.getItem(position), MediaType.IMAGES, listener);
                dialog.show();
            } else {
                UploadMediaDialog dialog = new UploadMediaDialog(getContext(), mAdapter.getItem(position), MediaType.RECORDS, listener);
                dialog.show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sets_manager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_button:
                startSetEditActivity(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startLessonActivity(int itemPosition) {
        Intent intent = new Intent(getContext(), LessonSelectionActivity.class);
        intent.putExtra("set", mAdapter.getItemId(itemPosition));
        intent.putExtra("manager", true);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                VocabularySet set = data.getParcelableExtra("result");
                if (mAdapter != null) {
                    mAdapter.add(set);
                }
            }
        }
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                VocabularySet set = data.getParcelableExtra("result");
                mAdapter.set(mLastEditedPosition, set);
            }
        }
    }

    public void deleteSet(VocabularySet set) {
        if (mDeletedPosition >= 0) {
            checkAndSetCurrentSet(set.getId());
            startDeletingSetService(set);
            MediaFileSystem.deleteCatalog(set.getCatalog(), getContext()); //TODO tutaj można to ubraż w usługe
            mAdapter.removeItem(mDeletedPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteSet(int position) {
        mDeletedPosition = position;
        DeleteDialog dialog = new DeleteDialog(getContext(), mAdapter.getItem(position), DeleteDialog.SET_MODE,
                new DeleteDialog.DeleteListener() {
                    @Override
                    public void delete(VocabularySet set) {
                        deleteSet(set);
                    }
                });
        dialog.show();
    }

    private void deleteImages(VocabularySet set) {
        if (set != null) {
            String catalog = set.getCatalog();
            MediaFileSystem.deleteMediaCatalog(catalog,MediaType.IMAGES, getContext());
            //TODO aktualizacja
        }
    }

    private void deleteRecords(VocabularySet set) {
        if (set != null) {
            String catalog = set.getCatalog();
            MediaFileSystem.deleteMediaCatalog(catalog,MediaType.RECORDS, getContext());
            DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
            dataManager.updateRecordsUploaded(false, set.getGlobalId());
        }
    }

    private void checkAndSetCurrentSet(long setId) {
        long currentSetId = ((LingApplication) getActivity().getApplication()).getCurrentSetId();
        if (setId == currentSetId) {
            Settings.setCurrentSetId(Constants.NO_SET_ID, getContext());
        }
    }

    private void startDeletingSetService(VocabularySet set) {
        Intent intent = new Intent(getContext(), DeletingSetService.class);
        intent.putExtra("set", set);
        getActivity().startService(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((UploadSetService.LocalBinder) service).getService();
        mService.setCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onOperationProgress(int progress) {
        Log.d(getClass().getSimpleName(), "postęp " + progress);
        if (!mIsUploading) {
            showUploadingContainer(""); //TODO przerzucić tutaj jakoś nazwe
            mIsUploading = true;
        }
        if (mUploadProgressBar != null) {
            mUploadProgressBar.setProgress(progress);
        }
    }

    @Override
    public void onOperationCompleted(long setId, long globalId, UploadParams parts) {
        if (mUploadProgressBar != null) {
            mUploadProgressBar.setProgress(0);
        }
        if (mUploadProgressContainer != null) {
            mUploadProgressContainer.setVisibility(View.GONE);
        }
        mAdapter.setActionButtonEnable(true);
        if (parts.isDatabase()) {
            mAdapter.getItemById(setId).setGlobalId(globalId);
            DataManager dataManager = LingApplication.getInstance().getDataManager();
            String uploadingUser = dataManager.getUploadingUser(setId);
            //można byłoby wstawić po prosut wartość login z LogPref, ale podczas wylogowania użytkownika
            //i wejściu w tę zakładkę mogłybyć złe oznaczenia, dlatego pobieray wartość wstawioną
            // w serwisie
            mAdapter.getItemById(setId).setUploadingUser(uploadingUser);
        }
        if (parts.isImages()) {
            mAdapter.getItemById(setId).setImagesUploaded(true);
        }
        if (parts.isRecords()) {
            mAdapter.getItemById(setId).setRecordsUploaded(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    //region SetsManagerAdapter
    private class SetsManagerAdapter extends ArrayAdapter {

        private Context mContext;
        private int mResource;
        private List<VocabularySet> mItems;
        private boolean mActionButtonEnable;

        public SetsManagerAdapter(Context context, int resource, List<VocabularySet> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mItems = data;
            mActionButtonEnable = true;
        }

        public void setActionButtonEnable(boolean enable) {
            mActionButtonEnable = enable;
            notifyDataSetChanged();
        }

        public void set(int position, VocabularySet set) {
            mItems.set(position, set);
            notifyDataSetChanged();
        }

        public VocabularySet getItemById(long id) {
            for (VocabularySet set : mItems) {
                if (set.getId() == id) {
                    return set;
                }
            }
            return null;
        }

        @Override
        public VocabularySet getItem(int position) {
            if (position >= 0 && position < mItems.size()) {
                return mItems.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (position >= 0 && position < mItems.size()) {
                return mItems.get(position).getId();
            }
            return -1;
        }


        public void removeItem(long id) {
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getId() == id) {
                    mItems.remove(i);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        public void removeItem(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
        }

        private final int UPLOADED_ICON = R.drawable.ic_uploaded;
        private final int DOWNLOADED_ICON = R.drawable.ic_downloaded;

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
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
            VocabularySet set = mItems.get(position);

            viewHolder.nameTextView.setText(set.getName());
            if (set.getGlobalId() != null) {
                viewHolder.uploadedImageView.setVisibility(View.VISIBLE);
                if (set.getUploadingUser() != null && set.getUploadingUser().equals(LogPref.getLogin(getContext()))) {
                    //TODO udatwić poprawną ikonę
                    viewHolder.uploadedImageView.setImageResource(UPLOADED_ICON);
                } else {
                    //TODO ustawić poprawną ikonę
                    viewHolder.uploadedImageView.setImageResource(DOWNLOADED_ICON);
                }
            } else {
                viewHolder.uploadedImageView.setVisibility(View.GONE);
            }
            if (mActionButtonEnable) {
                viewHolder.actionButton.setVisibility(View.VISIBLE);
                viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.showContextMenuForChild(v);
                    }
                });
            } else {
                viewHolder.actionButton.setVisibility(View.INVISIBLE);
            }
            return rowView;
        }
    }

    public static class ViewHolder {
        public TextView nameTextView;
        public ImageView uploadedImageView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            uploadedImageView = (ImageView) view.findViewById(R.id.uploaded_image_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }
    //endregion

}

//region DeleteDialog
class DeleteDialog extends AlertDialog {

    public final static int SET_MODE = 1;
    public final static int IMAGES_MODE = 2;
    public final static int RECORDS_MODE = 3;

    public interface DeleteListener {
        void delete(VocabularySet set);
    }

    DeleteDialog(Context context, final VocabularySet set, final int mode, final DeleteListener listener) {
        super(context);
        if (set == null) {
            return;
        }
        this.setTitle(set.getName());
        String message;
        switch (mode) {
            case SET_MODE:
                message = context.getString(R.string.sure_delete_set);
                break;
            case IMAGES_MODE:
                message = context.getString(R.string.sure_delete_images);
                break;
            case RECORDS_MODE:
                message = context.getString(R.string.sure_delete_records);
                break;
            default:
                return; //nie wyświetlamy , może będzie trzeba zmienić na dismiss
        }

        this.setMessage(message);
        this.setButton(BUTTON_POSITIVE, context.getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //deleteSet(new VocabularySet(id));
                if (listener != null) {
                    listener.delete(set);
                }
                dismiss();
            }
        });
        this.setButton(BUTTON_NEGATIVE, context.getString(R.string.no), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss(); //zamykamy dialog
            }
        });
    }
}

interface UploadListener {
    void startUpload(VocabularySet set, String description, boolean database, boolean images, boolean records);
}

class UploadMediaDialog extends Dialog {
    private final int CONTENT_RESOURCE = R.layout.dialog_upload_media;

    private TextView mMediaTextView;
    private TextView mSizeTextView;
    private Button mUploadButton;

    private UploadListener mListener;


    public static int IMAGES = 1;
    public static int RECORDS = 2;

    public UploadMediaDialog(@NonNull Context context, VocabularySet set, MediaType mediaType, UploadListener listener) {
        super(context);
        mListener = listener;
        setContentView(CONTENT_RESOURCE);
        setupControls();
        setContent(context, set, mediaType);
        setListeners(mediaType, set);
        getData(mediaType, set);
    }

    private void setupControls() {
        mMediaTextView = (TextView) findViewById(R.id.media_text_view);
        mSizeTextView = (TextView) findViewById(R.id.size_text_view);
        mUploadButton = (Button) findViewById(R.id.upload_button);
    }

    private void setContent(Context context, VocabularySet set, MediaType mediaType) {
        setTitle(set.getName());
        switch (mediaType){
            case IMAGES:
                mMediaTextView.setText(context.getString(R.string.upload_images)); break;
            case RECORDS:
                mMediaTextView.setText(context.getString(R.string.upload_records)); break;
        }
    }

    private void setListeners(final MediaType mediaType, final VocabularySet set) {
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    switch (mediaType){
                        case IMAGES:
                            mListener.startUpload(set, null, false, true, false); break;
                        case RECORDS:
                            mListener.startUpload(set, null, false, false, true); break;
                    }
                }
                dismiss();
            }
        });
    }

    private void getData(MediaType mediaType, VocabularySet set) {
        /*MediaSizeAsyncTask task = new MediaSizeAsyncTask(new MediaSizeAsyncTask.Callback() {
            @Override
            public void setSize(float size) {
                String formattedSize = String.format("%.02f", size);
                mSizeTextView.setValues(formattedSize + " MB");
            }
        });
        String path = null;
        if (mediaType == IMAGES) {
            path = MediaFileSystem.getImagePath(set.getCatalog(), getContext());
        }
        if (mediaType == RECORDS) {*
            path = MediaFileSystem.getRecordsPath(set.getCatalog(), getContext());
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);*/

        MediaSizeAsyncTask task  = new MediaSizeAsyncTask(set.getCatalog(), getContext(), new MediaSizeAsyncTask.Callback() {
            @Override
            public void setSize(long size) {
                String formattedSize = FileSizeFormatter.getSize(size);
                mSizeTextView.setText(formattedSize);
            }
        });

        task.execute(mediaType);
    }
}

class MediaSizeAsyncTask extends AsyncTask<MediaType,Void, Long>{

    private String mSetCatalog;
    private Context mContext;
    private Callback mCallback;

    public interface Callback{
        void setSize(long size);
    }

    public MediaSizeAsyncTask(String setCatalog, Context context, Callback callback){
        mSetCatalog = setCatalog;
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected Long doInBackground(MediaType... mediaType) {
        long size = 0;
        switch (mediaType[0]){
            case IMAGES:
                size = MediaFileSystem.getMediasSize(mSetCatalog,MediaType.IMAGES, mContext); break;
            case RECORDS:
                size = MediaFileSystem.getMediasSize(mSetCatalog,MediaType.RECORDS, mContext); break;
        }
        return size;
    }

    @Override
    protected void onPostExecute(Long result){
        if(mCallback != null){
            mCallback.setSize(result);
        }
    }
}

/*class MediaSizeAsyncTask extends AsyncTask<String, Void, Float> {

    private Callback mCallback;

    public interface Callback {
        void setSize(float size);
    }

    public MediaSizeAsyncTask(Callback callback) {
        mCallback = callback;
    }

    /**
     * Liczenie rozmiaru katalogu
     *
     * @param params ścieżka do katalogu dla którego ma zostać obliczony rozmiar
     * @return rozmiar katalogu w bajtach
     */
    /*@Override
    protected Float doInBackground(String... params) {
        return FileSizeCalculator.calculate(params[0], FileSizeCalculator.MB);

    }

    @Override
    protected void onPostExecute(Float result) {
        if (mCallback != null) {
            mCallback.setSize(result);
        }
    }
}*/

class UploadSetDialog extends Dialog {

    private final int CONTENT_RESOURCE = R.layout.dialog_upload_set;

    private TextView mNameTextView;
    private EditText mDescriptionEditText;
    private Button mUploadButton;
    private boolean mUploadAll;
    private VocabularySet mSet;
    private UploadListener mListener;

    private boolean mEditDescription;

    public UploadSetDialog(@NonNull Context context, VocabularySet set, boolean uploadAll, UploadListener listener) {
        super(context);
        mListener = listener;
        mUploadAll = uploadAll;
        mSet = set;
        setTitle(context.getString(R.string.upload)); //TODO zmienić nazwę
        setContentView(CONTENT_RESOURCE);
        setupControls();
        setListeners();
        setValues();
    }

    public void setDescription(String description) {
        mDescriptionEditText.setText(description);
        mEditDescription = true;
    }

    private void setupControls() {
        mNameTextView = (TextView) findViewById(R.id.name_text_view);
        mDescriptionEditText = (EditText) findViewById(R.id.description_edit_text);
        mUploadButton = (Button) findViewById(R.id.upload_button);
    }

    private void setListeners() {
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (mEditDescription) {
                        //TODO ogarnąć jak działa edit description
                    } else {
                        if (mUploadAll) {
                            mListener.startUpload(mSet, mDescriptionEditText.getText().toString(), true, true, true);
                        } else {
                            mListener.startUpload(mSet, mDescriptionEditText.getText().toString(), true, false, false);
                        }
                    }
                    dismiss();
                }
            }
        });
    }

    private void setValues() {
        mNameTextView.setText(mSet.getName());
    }
}

//endregion
