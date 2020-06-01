package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.SentencesPagerAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentencesPagerFragment extends Fragment {

    private List<Sentence> mSentences;
    private ViewPager mViewPager;
    SentencesPagerAdapter mAdapter;

    public SentencesPagerFragment() {
        // Required empty public constructor
    }

    public void setList(List<Sentence> list) {
        //mAdapter.setItems(list);
        mSentences = list;
        mAdapter = createAdapter();
        mViewPager.setAdapter(mAdapter);
        //TODO zobaczyć jak można to zrobić inaczej
    }

    private SentencesPagerAdapter createAdapter() {
        return new SentencesPagerAdapter(getActivity(), R.layout.item_sentence_viewpager, mSentences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mSentences = bundle.getParcelableArrayList("items");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentences_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = createAdapter();
        mViewPager.setAdapter(mAdapter);
    }


}
