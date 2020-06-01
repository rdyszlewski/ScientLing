package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.FlashcardAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.FlashcardParams;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.OKFinishAlertDialog;
import com.dyszlewskiR.edu.scientling.controlers.FlashcardsControls;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlashcardFragment extends Fragment {

    private final String TAG = "FlashcardFragment";


    private ViewPager mViewPager;
    private FlashcardsControls mControler;

    public FlashcardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mControler = new FlashcardsControls();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FlashcardParams params = getFlashcardParams();
        List<Word> words = null;
        try {
            words = mControler.getWords(params);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (words.size() == 0) {
            closeActivity();
        }
        final FlashcardAdapter adapter = new FlashcardAdapter(getActivity(), R.layout.item_flashcard, words);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            private boolean moved;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    moved = true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) {
                        v.performClick();
                    }
                }
                return false;
            }
        });
    }

    private FlashcardParams getFlashcardParams() {
        Intent intent = getActivity().getIntent();
        long setId = intent.getLongExtra("set", Constants.DEFAULT_SET_ID);
        int type = intent.getIntExtra("type", 0);
        int limit = intent.getIntExtra("limit", 0);

        FlashcardParams params = new FlashcardParams();
        params.setSetId(setId);
        params.setChoiceType(type);
        params.setLimit(limit);
        return params;
    }

    /**
     * Metoda wyświetlana w przypadku braku posujących słowek
     */
    private void closeActivity() {
        new OKFinishAlertDialog(getActivity(), getString(R.string.no_words), getString(R.string.not_found_words)).show();
    }


}
