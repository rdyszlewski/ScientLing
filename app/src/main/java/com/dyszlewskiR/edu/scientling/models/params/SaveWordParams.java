package com.dyszlewskiR.edu.scientling.models.params;

import android.net.Uri;

import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;

/**
 * Created by Razjelll on 30.03.2017.
 */

public class SaveWordParams {

    private Word mWord;
    private VocabularySet mSet;
    private boolean mEdit;
    private Uri mImageToInsert;
    private Uri mRecordToInsert;
    private Uri mImageToDelete;
    private Uri mRecordToDelete;

    public Word getWord() {
        return mWord;
    }

    public void setWord(Word word) {
        mWord = word;
    }

    public VocabularySet getSet() {
        return mSet;
    }

    public void setSet(VocabularySet set) {
        mSet = set;
    }

    public boolean isEdit() {
        return mEdit;
    }

    public void setEdit(boolean edit) {
        mEdit = edit;
    }

    public Uri getImageToInsert() {
        return mImageToInsert;
    }

    public void setImageToInsert(Uri imageUri) {
        mImageToInsert = imageUri;
    }

    public Uri getRecordToInsert() {
        return mRecordToInsert;
    }

    public void setRecordToInsert(Uri recordUri) {
        mRecordToInsert = recordUri;
    }

    public Uri getImageToDelete(){return mImageToDelete;}

    public void setImageToDelete(Uri imageUri){mImageToDelete = imageUri;}

    public Uri getRecordToDelete(){return mRecordToDelete;}

    public void setRecordToDelete(Uri recordUri){mRecordToDelete = recordUri;}
}
