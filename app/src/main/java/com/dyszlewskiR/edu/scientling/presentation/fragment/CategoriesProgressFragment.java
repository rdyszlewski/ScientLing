package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.ProgressAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.others.SetProgress;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

public class CategoriesProgressFragment extends Fragment {
    private ListView mListView;
    private ProgressAdapter mAdapter;
    private long mSetId;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mSetId = intent.getLongExtra("id",-1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        DataManager dataManager = LingApplication.getInstance().getDataManager();
        List<SetProgress> sets = dataManager.getCategoriesProgress(mSetId);
        mAdapter = new ProgressAdapter(getContext(), R.layout.item_progress, sets);
        mListView.setAdapter(mAdapter);
    }
}
