package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.others.RepetitionGroup;
import com.dyszlewskiR.edu.scientling.service.preferences.Settings;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.DateCalculator;
import com.dyszlewskiR.edu.scientling.presentation.widgets.NumberPicker;

import java.io.IOException;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepetitionsFragment extends Fragment {

    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_repetition;

    private ViewGroup mOptionsContainer;
    private NumberPicker mNumWordsPicker;
    private ListView mListView;
    private Button mStartButton;
    private RepetitionsAdapter mAdapter;

    public RepetitionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repetitions, container, false);
        setupControls(view);
        setAdapters();
        return view;
    }

    private void setupControls(View view) {
        mOptionsContainer = (ViewGroup) view.findViewById(R.id.options_container);
        mNumWordsPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        mListView = (ListView) view.findViewById(R.id.list);
        mStartButton = (Button) view.findViewById(R.id.start_button);
    }

    public void setAdapters() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        List<RepetitionGroup> repetitions = null;
        try {
            long currentSetId = Settings.getCurrentSetId(getContext());
            repetitions = dataManager.getRepetitionsList(currentSetId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAdapter = new RepetitionsAdapter(getContext(), ADAPTER_ITEM_RESOURCE, repetitions);
        mListView.setAdapter(mAdapter);
    }


    private class RepetitionsAdapter extends ArrayAdapter {
        private List<RepetitionGroup> mItems;
        private Context mContext;
        private int mResource;

        public RepetitionsAdapter(Context context, int resource, List<RepetitionGroup> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mItems = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rowView = inflater.inflate(mResource, null);
                viewHolder = new ViewHolder(rowView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.dateTextView.setText(DateCalculator.getShortDate(mItems.get(position).getDate()));
            viewHolder.setTextView.setText(mItems.get(position).getSet().getName());
            viewHolder.countTextView.setText(String.valueOf(mItems.get(position).getWordsCount()));
            return rowView;
        }

    }

    private static class ViewHolder {
        public TextView dateTextView;
        public TextView setTextView;
        public TextView countTextView;

        public ViewHolder(View view) {
            dateTextView = (TextView) view.findViewById(R.id.date_text_view);
            setTextView = (TextView) view.findViewById(R.id.set_text_view);
            countTextView = (TextView) view.findViewById(R.id.words_number_text_view);
        }
    }

}
