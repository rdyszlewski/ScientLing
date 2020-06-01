package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.CurrentSetAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

public class CurrentSetSelectionFragment extends Fragment {

    private final int SET_REQUEST = 400;

    private ListView mListView;
    private List<VocabularySet> mItems;
    private CurrentSetAdapter mAdapter;

    public CurrentSetSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mItems = dataManager.getSets();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_set_selection, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new CurrentSetAdapter(getActivity(), R.layout.item_current_set, mItems);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Settings.setCurrentSetId(mItems.get(position).getWordId(),getActivity());
                setResultAndFinish(position);
            }
        });
    }


    private void setResultAndFinish(int position) {
        Intent intent = new Intent();
        intent.putExtra("result", mItems.get(position).getId());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
