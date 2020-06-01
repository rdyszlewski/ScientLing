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
import com.dyszlewskiR.edu.scientling.presentation.activity.SetSelectionActivity;
import com.dyszlewskiR.edu.scientling.presentation.adapters.SetsSelectionAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

public class SetSelectionFragment extends Fragment {

    private List<VocabularySet> mItems;

    private ListView mListView;

    private DataManager mDataManager;
    private SetsSelectionAdapter mAdapter;

    public SetSelectionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mItems = mDataManager.getSets();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_list, container, false);

        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent();
                result.putExtra("result", mItems.get(position));
                getActivity().setResult(Activity.RESULT_OK, result);
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new SetsSelectionAdapter(getActivity(), R.layout.item_set_selection, mItems, mDataManager);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SetSelectionActivity.ADD_SET_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                VocabularySet set = data.getParcelableExtra("result");
                //Nowo dodane wstawiamy na pierwsze miejsce. Nie pobieramy ponownie elementów z bazy
                mItems.add(0, set);
                mListView.invalidateViews();
            }
        } else { //obsługa przekazywana do adaptera
            mAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }
}
