package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.CategorySelectionActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.HintsListActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.SentencesListActivity;
import com.dyszlewskiR.edu.scientling.presentation.adapters.PartOfSpeechAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.resources.GalleryUtils;

import java.util.ArrayList;
import java.util.List;

public class WordAdvancedEditFragment extends Fragment {

    public static final int ADD_SENTENCES_REQUEST = 404;
    public static final int ADD_CATEGORY_REQUEST = 403;
    public static final int ADD_HINTS_REQUEST = 402;
    public static final int LOAD_IMAGE_REQUEST = 400;
    public static final int PERMISSION_REQUEST = 3213;

    private Spinner mPartsOfSpeechSpinner;
    private Button mCategoryButton;
    private EditText mDefinitionEditText;
    private EditText mTranslationDefinitionEditText;
    private TextView mTranslationDefinitionLabel;
    private Button mSentencesButton;
    private RatingBar mDifficultRatingbar;
    private Button mHintsButton;
    private Button mImageButton;
    private ImageView mImageView;
    private Button mRemoveImageButton;

    private Word mWord;
    private PartOfSpeechAdapter mPartOfSpeechAdapter;
    private Uri mImageUri;

    private Category mCategory;
    private boolean mViewCreated;

    public WordAdvancedEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getData();
        if (mWord == null) {
            mWord = new Word();
        }
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mWord = intent.getParcelableExtra("item");
    }

    public void setWord(Word word) {
        mWord = word;
        if (mViewCreated) {
            setValues();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_advanced_edit, container, false);
        setupControls(view);
        setListeners(view);
        return view;
    }

    private void setupControls(View view) {
        mPartsOfSpeechSpinner = (Spinner) view.findViewById(R.id.part_of_speech_spinner);
        mDefinitionEditText = (EditText) view.findViewById(R.id.definition_edit_text);
        mTranslationDefinitionEditText = (EditText) view.findViewById(R.id.definition_translation_edit_text);
        mTranslationDefinitionLabel = (TextView) view.findViewById(R.id.definition_translation_label);
        mDifficultRatingbar = (RatingBar) view.findViewById(R.id.difficult_ratingbar);
        mCategoryButton = (Button) view.findViewById(R.id.category_button);
        mSentencesButton = (Button) view.findViewById(R.id.sentencesButton);
        mHintsButton = (Button) view.findViewById(R.id.hints_button);
        mImageButton = (Button) view.findViewById(R.id.image_button);
        mImageView = (ImageView) view.findViewById(R.id.image_image_view);
        mRemoveImageButton = (Button) view.findViewById(R.id.remove_image_button);
    }

    private void setListeners(View view) {
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCategorySelectionActivity();
            }
        });
        mDefinitionEditText.addTextChangedListener(new DefinitionTextWatcher());
        mSentencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSentencesList();
            }
        });
        mHintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHintsList();
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGalleryIntent();
            }
        });
        mRemoveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage();
            }
        });
    }

    private void startSentencesList() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.sentence_list_fragment);
        if (fragment == null || !fragment.isVisible()) {
            Intent intent = new Intent(getActivity(), SentencesListActivity.class);
            intent.putParcelableArrayListExtra("list", mWord.getSentences());
            getActivity().startActivityForResult(intent, ADD_SENTENCES_REQUEST);
        } else {
            //TODO obsługa tabletów
        }
    }

    private void startHintsList() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.hints_list_fragment);
        if (fragment == null) {
            Intent intent = new Intent(getActivity(), HintsListActivity.class);
            intent.putParcelableArrayListExtra("list", mWord.getHints());
            getActivity().startActivityForResult(intent, ADD_HINTS_REQUEST);
        } else {
            //TODO obsługa tabletów
        }
    }

    private void startGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, LOAD_IMAGE_REQUEST);
    }

    private void removeImage() {
        mImageView.setImageResource(0);
        if (mImageView.getVisibility() == View.VISIBLE) {
            mImageView.setVisibility(View.GONE);
        }
        //TODO usuwanie obrazka i nagrania
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewCreated = true;
        setAdapters();
        setValues();
        checkDefinitionField();
    }

    private void setAdapters() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        List<PartOfSpeech> parts = dataManager.getPartsOfSpeech();
        mPartOfSpeechAdapter = new PartOfSpeechAdapter(getActivity(), R.layout.item_simple, parts);
        mPartsOfSpeechSpinner.setAdapter(mPartOfSpeechAdapter);
    }

    private void setValues() {
        if (mWord != null) {
            if (mWord.getPartsOfSpeech() != null) {
                int partPosition = mPartOfSpeechAdapter.getPosition(mWord.getPartsOfSpeech());
                mPartsOfSpeechSpinner.setSelection(partPosition);
            }
            if (mWord.getCategory() != null) {
                mCategoryButton.setText(mWord.getCategory().getName());
            }
            if (mWord.getDefinition() != null) {
                mDefinitionEditText.setText(mWord.getDefinition().getContent());
                mTranslationDefinitionEditText.setText(mWord.getDefinition().getTranslation());
                mTranslationDefinitionEditText.setVisibility(View.VISIBLE);
            }

            setSentencesButtonText();
            setHintsButtonText();
            mDifficultRatingbar.setRating(mWord.getDifficult());
        }
    }

    private void setSentencesButtonText() {
        String buttonText = getString(R.string.sentences_example);
        if (mWord.getSentences() != null && mWord.getSentences().size() != 0) {
            buttonText += "(" + mWord.getSentences().size() + ")";
        }
        mSentencesButton.setText(buttonText);
    }

    private void setHintsButtonText() {
        String buttonText = getString(R.string.hints);
        if (mWord.getHints() != null && mWord.getHints().size() != 0) {
            buttonText += "(" + mWord.getHints().size() + ")";
        }
        mHintsButton.setText(buttonText);
    }

    private void startCategorySelectionActivity() {
        Intent intent = new Intent(getActivity(), CategorySelectionActivity.class);
        //TODO zakomentowano ponieważ zmieniono WordEditActivity
        //TODO zakomentowano ponieważ zmieniono WordEditActivity
        //VocabularySet set = ((WordEditActivity) getActivity()).getSet();
        //intent.putExtra("set", set);
        getActivity().startActivityForResult(intent, ADD_CATEGORY_REQUEST);
    }

    private void checkDefinitionField() {
        if (mDefinitionEditText.getText().toString().trim().isEmpty()) { //jest puste
            if (mTranslationDefinitionEditText.getVisibility() == View.VISIBLE) {
                mTranslationDefinitionLabel.setVisibility(View.GONE);
                mTranslationDefinitionEditText.setVisibility(View.GONE);
            }
        } else {
            if (mTranslationDefinitionEditText.getVisibility() != View.VISIBLE) {
                mTranslationDefinitionLabel.setVisibility(View.VISIBLE);
                mTranslationDefinitionEditText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setImage(Bitmap bitmap) {
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
            if (mImageView.getVisibility() == View.GONE) {
                mImageView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_SENTENCES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Sentence> sentences = data.getParcelableArrayListExtra("result");
            mWord.setSentences(sentences);
            setSentencesButtonText();
            mSentencesButton.requestFocus();
        }
        if (requestCode == ADD_CATEGORY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Category category = data.getParcelableExtra("result");
            mWord.setCategory(category);
            mCategoryButton.setText(category.getName());
            mCategoryButton.requestFocus();
        }

        if (requestCode == ADD_HINTS_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Hint> hints = data.getParcelableArrayListExtra("result");
            mWord.setHints(hints);
            setHintsButtonText();
        }

        if (requestCode == LOAD_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            mImageUri = data.getData();
            Bitmap bitmap = getImageFromIntent(mImageUri);
            if (bitmap != null) {
                setImage(bitmap);
            }
        }
    }

    private Bitmap getImageFromIntent(Uri imageUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            } else {
                return GalleryUtils.getBitmap(imageUri, getActivity());
            }
        } else {
            return GalleryUtils.getBitmap(imageUri, getActivity());
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults) {
        Log.d(getClass().getSimpleName(), "onRequestPermissionsResult");
        switch (requestCode) {
            case PERMISSION_REQUEST:
                Bitmap bitmap = getImageFromIntent(mImageUri);
                if (bitmap != null) {
                    setImage(bitmap);
                }
        }
    }

    public Word getWord() {
        PartOfSpeech partOfSpeech = mPartOfSpeechAdapter.getItem(mPartsOfSpeechSpinner.getSelectedItemPosition());
        mWord.setPartsOfSpeech(partOfSpeech);
        mWord.setDifficult((byte) mDifficultRatingbar.getRating());

        if (mDefinitionEditText.getText().toString().trim().length() != 0) {
            Definition definition = new Definition();
            definition.setTranslation(String.valueOf(mTranslationDefinitionEditText.getText()));
            definition.setContent(String.valueOf(mDefinitionEditText.getText()));
            mWord.setDefinition(definition);
        }

        return mWord;
    }

    public void clear() {
        mPartsOfSpeechSpinner.setSelection(0);
        mDefinitionEditText.setText("");
        mTranslationDefinitionEditText.setText("");
        mDifficultRatingbar.setProgress(0);
        removeImage();
    }

    public boolean validate() {
        return true;
    }

    private class DefinitionTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkDefinitionField();
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("WordAdvanced", s.toString());
        }
    }
}
