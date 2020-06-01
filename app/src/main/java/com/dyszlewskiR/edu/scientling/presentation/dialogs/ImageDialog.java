package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.utils.BitmapUtils;

import java.io.File;
import java.io.IOException;

public class ImageDialog extends DialogFragment {
    private final int LAYOUT_RESOURCE = R.layout.dialog_image;
    private static final String IMAGE_FILENAME = "Image.jpg";
    private final int OPEN_REQUEST = 6736;
    private final int CAMERA_REQUEST = 2263;

    private ImageView mImageView;
    private ImageButton mCameraButton;
    private ImageButton mOpenButton;
    private ImageButton mDeleteButton;
    private Button mOkButton;

    private Uri mImageUri;

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onImageOk(Uri imageUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ImageDialog", "OnCreateView");
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        setValues();
        getDialog().setTitle(getString(R.string.image));

        return view;
    }

    private void setupControls(View view) {
        mImageView = (ImageView) view.findViewById(R.id.image_image_view);
        mCameraButton = (ImageButton) view.findViewById(R.id.camera_button);
        mOpenButton = (ImageButton) view.findViewById(R.id.open_button);
        mDeleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setListeners() {
        setCameraButtonListener();
        setOpenButtonListener();
        setDeleteButtonListener();
        setOkButtonListener();
    }

    private void setValues() {
        if (mImageUri != null) {
            updateImageUI();
        }
    }

    /**
     * Metoda uruchamiająca aparat i przechwytująca zdjęcie. Nestępnie wynik przechwytywania jest obsługiwany w
     * metodzie onActivityResult. Wynik można zapisać od razu w pliku, ale działa to tylko korzystając
     * z External Storage, ponieważ ta pamięć traktowana jest jako globalna. Pamięć Internal Storage
     * jest prywatna, dlatego inne aplikacje nie mogą zapisywać w niej bezpośrednio danych.
     */
    private void setCameraButtonListener() {
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageUri = Uri.fromFile(new File(getContext().getCacheDir(), IMAGE_FILENAME));
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri); //to działa tylko z external storage
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
    }

    private void setOpenButtonListener() {
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OPEN_REQUEST);
            }
        });
    }

    private void setDeleteButtonListener() {
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
    }

    private void setOkButtonListener() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onImageOk(mImageUri);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mImageUri = data.getData();
                updateImageUI();
            }
        }
        /*Podczas zapisywania zdjęcia w pamięci podręcznej nie zmniejszamy obrazka, ponieważ ten proces
        będzie przeprowadzony zaraz przez zapisem słowka, z tego powodu że podczas wybierania istniejącego
        obrazka nie zmniejszamy jego rozmiaru i nie zapisujemy go w pamięci cache tylko przekazujemy jego
        Uri. Wprowadzając zmaianę rozmiaru tutaj było by niespójne.
         */
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                //Bitmap resizedBitmap = BitmapUtils.resize(bitmap, Constants.MAX_IMAGE_SIZE, false); // zmniejszenie bitmapy
                try {
                    //BitmapUtils.saveBitmap(resizedBitmap, getContext().getCacheDir().getAbsolutePath(), IMAGE_FILENAME);
                    BitmapUtils.saveBitmap(bitmap, getContext().getCacheDir().getAbsolutePath(), IMAGE_FILENAME);
                    mImageUri = MediaFileSystem.getMediaUriFromCache(IMAGE_FILENAME, getContext());
                    updateImageUI();
                } catch (IOException e) {
                    e.printStackTrace(); //TODO obsłużyć
                }
            }
        }
    }

    /**
     * Metoda aktualizująca interfejs użytkownika po ustawieniu nowego uri
     */
    private void updateImageUI() {
        mImageView.setImageURI(mImageUri);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    private void deleteImage() {
        mImageUri = null;
        mImageView.setImageURI(null);
        mDeleteButton.setVisibility(View.INVISIBLE);
        MediaFileSystem.deleteMediaFromCache(IMAGE_FILENAME, getContext());
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("ImageDialog", "onDismiss");
        mCallback = null;
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onDestroyView(){
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public static void clearCache(Context context) {
        MediaFileSystem.deleteMediaFromCache(IMAGE_FILENAME, context);
    }
}
