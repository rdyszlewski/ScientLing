package com.dyszlewskiR.edu.scientling.service.asyncTasks;

import android.os.AsyncTask;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.WordListAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.WordsListParams;
import com.dyszlewskiR.edu.scientling.presentation.fragment.ManageWordsFragment;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.io.IOException;
import java.util.List;

public class LoadWordAsyncTask extends AsyncTask<WordsListParams, Void, WordListAdapter> {

    private DataManager mDataManager;
    private ManageWordsFragment mFragment;

    public LoadWordAsyncTask(DataManager dataManager, ManageWordsFragment fragment) {
        mDataManager = dataManager;
        mFragment = fragment;
    }

    @Override
    protected WordListAdapter doInBackground(WordsListParams... params) {
        List<Word> words = null;
        try {
            words = mDataManager.getWords(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new WordListAdapter(mFragment.getActivity(), R.layout.item_word_list,
                words, mDataManager);
    }

    @Override
    protected void onPostExecute(WordListAdapter result) {
        mFragment.onPostAsyncTask(result);
    }

}
