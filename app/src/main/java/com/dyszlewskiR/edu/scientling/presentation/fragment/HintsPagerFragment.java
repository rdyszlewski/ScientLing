package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.HintsPagerAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HintsPagerFragment extends Fragment {

    private List<Hint> mHints;
    private ViewPager mViewPager;
    private HintsPagerAdapter mAdapter;

    public HintsPagerFragment() {
        // Required empty public constructor
    }

    public void setList(List<Hint> list) {
        mHints = list;
        mAdapter = createAdapter();
        mViewPager.setAdapter(mAdapter);

    }

    private HintsPagerAdapter createAdapter() {
        return new HintsPagerAdapter(getActivity(), R.layout.item_hints_viewpager, mHints);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mHints = bundle.getParcelableArrayList("items");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hints_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = createAdapter();
        mViewPager.setAdapter(mAdapter);
    }


}
