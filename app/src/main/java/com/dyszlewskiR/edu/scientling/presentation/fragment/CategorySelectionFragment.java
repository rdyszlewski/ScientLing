package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.CategoriesAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

public class CategorySelectionFragment extends Fragment {

    private EditText mSearchEditText;
    private ListView mListView;

    private List<Category> mItems;
    private CategoriesAdapter mAdapter;
    private long mLanguageL1Id;

    public CategorySelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mItems = dataManager.getCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_selection, container, false);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mSearchEditText.addTextChangedListener(new SearchWatcher());
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setResultAndFinish(position);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mAdapter = new CategoriesAdapter(getActivity(), R.layout.item_category_selection, mItems, dataManager);
        mListView.setAdapter(mAdapter);
    }

    /**
     * Meotoda ustawiająca wynik aktywności i zwracająca go do aktywności przez którą została wywołana.
     * Nastepnie obecna aktywność jest zamykana
     *
     * @param position
     */
    private void setResultAndFinish(int position) {
        Intent intent = new Intent();
        intent.putExtra("result", mItems.get(position));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }


    private class SearchWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //wyowłujemy metode która filtruje elementy na liście na podstawie zazwartości
            //okienka EditText
            String string = s.toString();
            mAdapter.getFilter().filter(string.toLowerCase());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
