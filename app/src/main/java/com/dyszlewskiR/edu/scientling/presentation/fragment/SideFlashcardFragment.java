package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.support.v4.app.Fragment;

import com.dyszlewskiR.edu.scientling.models.entity.Word;

public class SideFlashcardFragment extends Fragment {

    private Word mWord;
    private int mPosition;

    public Word getWord() {
        return mWord;
    }

    public void setWord(Word word) {
        mWord = word;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

}
