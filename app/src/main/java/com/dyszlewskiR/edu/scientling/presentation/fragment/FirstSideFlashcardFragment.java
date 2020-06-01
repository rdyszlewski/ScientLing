package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstSideFlashcardFragment extends SideFlashcardFragment {

    private Word mWord;
    private TextView mContentTextView;

    public FirstSideFlashcardFragment() {
        // Required empty public constructor
    }

    public void setWord(Word word) {
        mWord = word;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_side_flashcard, container, false);
        mContentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContentTextView.setText(mWord.getContent());
    }

}
