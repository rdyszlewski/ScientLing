package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.SetSpinnerAdapter;
import com.dyszlewskiR.edu.scientling.presentation.adapters.WordListAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.service.asyncTasks.LoadWordAsyncTask;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.WordsListParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.List;

public class ManageWordsFragment extends Fragment {

    private final int ALL_WORDS = 0;
    private final int OWN_WORDS = 1;
    private final int HARD_WORDS = 2;

    private TabLayout mTabs;
    private ListView mListView;
    private EditText mSearchEditText;
    private Spinner mSetSpinner;
    private List<Word> mWords;
    private int mSet;
    private WordListAdapter mListAdapter;

    public ManageWordsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreateView");
        View view = inflater.inflate(R.layout.fragment_manage_words, container, false);
        setupControls(view);
        return view;
    }

    private void setupControls(View view) {
        mTabs = (TabLayout) view.findViewById(R.id.tabs);
        mListView = (ListView) view.findViewById(R.id.list);
        mSetSpinner = (Spinner) view.findViewById(R.id.set_spinner);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        addTabs();
        mSearchEditText.setSelected(false);
    }

    private void addTabs() {
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.all_words)));
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.own_words)));
        mTabs.addTab(mTabs.newTab().setText(getString(R.string.selected_words)));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onViewCreated");
        setListeners();
        getSetsSpinner();
        getData(ALL_WORDS);
    }

    private void setListeners() {
        setTabsListener();
        setSpinnerListener();
        setEditTextListener();
    }

    private void setTabsListener() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case ALL_WORDS:
                        getData(ALL_WORDS);
                        break;
                    case OWN_WORDS:
                        getData(OWN_WORDS);
                        break;
                    case HARD_WORDS:
                        getData(HARD_WORDS);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setSpinnerListener() {
        mSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSet != position) {
                    getData(mTabs.getSelectedTabPosition());
                    mSet = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setEditTextListener() {
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((WordListAdapter) mListView.getAdapter()).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getData(int tab) {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        LoadWordAsyncTask task = new LoadWordAsyncTask(dataManager, this);
        long setId = mSetSpinner.getSelectedItemId();
        WordsListParams params = new WordsListParams();
        params.setSetId(setId);
        switch (tab) {
            case ALL_WORDS:
                params.setTab(WordsListParams.Tabs.ALL);
                break;
            case OWN_WORDS:
                params.setTab(WordsListParams.Tabs.OWN);
                break;
            case HARD_WORDS:
                params.setTab(WordsListParams.Tabs.HARD);
                break;
        }
        /*try {
            mWords = task.execute(params).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        task.execute(params);
        //setAdapter();
    }

    public void onPostAsyncTask(WordListAdapter adapter) {
        mListAdapter = adapter;
        setAdapter();
    }


    private void getSetsSpinner() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        List<VocabularySet> sets = dataManager.getSets();
        VocabularySet emptySet = new VocabularySet(0);
        emptySet.setName(getString(R.string.lack));
        sets.add(0, emptySet);
        SetSpinnerAdapter adapter = new SetSpinnerAdapter(getActivity(), R.layout.item_set_spinner, sets);
        mSetSpinner.setAdapter(adapter);
    }

    private void setAdapter() {
        //DataManager dataManager = ((LingApplication)getActivity().getApplication()).getDataManager();
        //mListAdapter = new WordListAdapter(getActivity(),R.layout.item_word_list, mWords, dataManager);
        mListAdapter.getFilter().filter(mSearchEditText.getText());
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
        //((WordListAdapter)mListView.getAdapter()).notifyDataSetChanged();
        Log.d(getClass().getSimpleName(), "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(getClass().getSimpleName(), "onDestroyView");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mListAdapter.onActivityResult(requestCode, resultCode, data);
    }

}
