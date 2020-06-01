package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.data.file.AudioDuration;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.UriUtils;

import java.io.IOException;

public class RecordDialog extends DialogFragment {
    private final int LAYOUT_RESOURCE = R.layout.dialog_record;
    private static final String RECORD_FILENAME = "Record";
    private final int OPEN_REQUEST = 6736;

    private final int OWN_RECORD_RESOURCE = R.string.own_record;
    private final int RECORDING_RESOURCE = R.string.recording;

    private TextView mTitleTextView;
    private ImageButton mDeleteButton;
    private ImageButton mPlayButton;
    private ImageButton mRecorderButton;
    private ImageButton mOpenButton;
    private Button mCancelButton;
    private Button mOkButton;

    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private boolean mPlaying;
    private boolean mRecording;
    /**
     * Zmienna określająca czy zostało ustawione nagranie
     */
    private boolean mIsRecord;

    private Uri mRecordUri;
    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onRecordOk(Uri recordUri);
    }

    public void setRecordUri(Uri recordUri) {
        mIsRecord = true;
        mRecordUri = recordUri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("RecordDialog", "onCreateView");
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        init();
        setupControls(view);
        setValues();
        setListeners();
        getDialog().setTitle(getString(R.string.record));
        return view;
    }

    private void init() {
        mRecording = false;
        initMediaPlayer();
        initMediaRecorder();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.reset();
            }
        });
    }

    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setMaxDuration(Constants.RECORD_MAX_DURATION);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    onFinishRecord();
                }
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                    onFinishRecord();
                }
            }
        });
    }

    private void setupControls(View view) {
        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mDeleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        mPlayButton = (ImageButton) view.findViewById(R.id.play_button);
        mRecorderButton = (ImageButton) view.findViewById(R.id.recorder_button);
        mOpenButton = (ImageButton) view.findViewById(R.id.open_button);
        mCancelButton = (Button) view.findViewById(R.id.cancel_button);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setVisibilityButtons(int visibility) {
        mDeleteButton.setVisibility(visibility);
        mPlayButton.setVisibility(visibility);
    }

    private void setValues() {
        if (mRecordUri != null) {
            updateRecordUI();
        }
    }

    private void setListeners() {
        setDeleteButtonListener();
        setPlayButtonListener();
        setRecorderButtonListener();
        setOpenButtonListener();
        setCancelButtonListener();
        setOkButtonListener();
    }

    private void setDeleteButtonListener() {
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
            }
        });
    }

    private void onFinishRecord() {
        mRecordUri = MediaFileSystem.getMediaUriFromCache(RECORD_FILENAME, getContext());
        updateRecordUI();
    }

    private void setPlayButtonListener() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mMediaPlayer.setDataSource(getContext(), mRecordUri);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mIsRecord = true;
                }
            }
        });
    }

    private void setRecorderButtonListener() {
        mRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRecording) {
                    Log.d("RecordDialog", "StartRecord");
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    String outputFilePath = getContext().getCacheDir() + "/" + RECORD_FILENAME;
                    mMediaRecorder.setOutputFile(outputFilePath);
                    try {
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                        mTitleTextView.setText(getString(RECORDING_RESOURCE));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mRecording = true;
                } else {
                    Log.d("RecordDialog", "StopDialog");
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mRecording = false;
                    onFinishRecord();
                }
            }
        });
    }

    /**
     * Metoda aktaulizująca interfej użytkownika po ustawieniu nowego uri.
     */
    private void updateRecordUI() {
        setVisibilityButtons(View.VISIBLE);
        String filename = UriUtils.getFileName(mRecordUri, getContext());
        if (filename != null ? filename.equals(RECORD_FILENAME) : false) {
            mTitleTextView.setText(getString(R.string.own_record));
        } else {
            mTitleTextView.setText(UriUtils.getFileName(mRecordUri, getContext()));
        }
    }

    private void deleteRecord() {
        mRecordUri = null;
        setVisibilityButtons(View.INVISIBLE);
        mTitleTextView.setText(getString(R.string.lack));
        MediaFileSystem.deleteMediaFromCache(RECORD_FILENAME, getContext());
    }

    private void setOpenButtonListener() {
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPEN_REQUEST);
            }
        });
    }

    private void setCancelButtonListener() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //najpierw usuwamy plik z caqhe jeśli istnieje(sprawdzaniem zajmuje się metoda)
                MediaFileSystem.deleteMediaFromCache(RECORD_FILENAME, getContext());
                dismiss();
            }
        });
    }

    private void setOkButtonListener() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onRecordOk(mRecordUri);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("RecordDialog", "onDismiss");
        mCallback = null;
        mMediaPlayer.release();
        mMediaRecorder.release();
        //getFragmentManager().beginTransaction().remove(this).commit();
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onDestroyView(){
        Log.d(getClass().getSimpleName(), "onDestroyView");
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if(AudioDuration.checkDuration(uri, getContext())){
                    mRecordUri = uri;
                    updateRecordUI();
                } else {
                    Toast.makeText(getContext(), getString(R.string.long_records) +
                            AudioDuration.getMaxDurationSeconds() + getString(R.string.seconds), Toast.LENGTH_LONG)
                    .show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void clearCache(Context context) {
        MediaFileSystem.deleteMediaFromCache(RECORD_FILENAME, context);
    }
}
