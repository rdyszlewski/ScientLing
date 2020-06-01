package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondSideFlashcardFragment extends SideFlashcardFragment {

    private Word mWord;
    private TextView mTranslationTextView;


    public SecondSideFlashcardFragment() {
        // Required empty public constructor
    }

    public void setWord(Word word) {
        mWord = word;
        setTexts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_side_flashcard, container, false);
        mTranslationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setTexts();
    }

    private void setTexts() {
        String translation = TranslationListConverter.toString(mWord.getTranslations());
        mTranslationTextView.setText(translation);
    }

}
