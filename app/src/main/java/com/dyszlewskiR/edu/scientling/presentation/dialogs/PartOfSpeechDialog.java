package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

public class PartOfSpeechDialog extends DialogFragment {
    private ListView mListView;
    private List<PartOfSpeech> mPartsList;
    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onPartOfSpeechOk(PartOfSpeech partOfSpeech);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_part_of_speech, container, false);
        setupControls(view);
        setListeners();
        loadData();
        setListAdapter();
        getDialog().setTitle(getString(R.string.part_of_speech));
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
    }

    private void setListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    mCallback.onPartOfSpeechOk(mPartsList.get(position));
                }
                dismiss();
            }
        });
    }

    private void loadData() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mPartsList = dataManager.getPartsOfSpeech();
        mPartsList.add(0, null); //dodanie pustego elemtnu na początek listy
    }

    private void setListAdapter() {
        PartOfSpeechAdapter adapter = new PartOfSpeechAdapter(getContext(), R.layout.item_part_of_speech, mPartsList);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mCallback = null;
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onDestroyView(){
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private class PartOfSpeechAdapter extends ArrayAdapter<PartOfSpeech> {
        private int mResource;
        private LayoutInflater mInflater;

        public PartOfSpeechAdapter(Context context, int resource, List<PartOfSpeech> data) {
            super(context, resource, data);
            mResource = resource;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mPartsList.size();
        }

        @Override
        public PartOfSpeech getItem(int position) {
            return mPartsList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);
            }
            TextView view = (TextView) convertView;
            if (mPartsList.get(position) != null) {
                String text = ResourceUtils.getString(mPartsList.get(position).getName(), getContext());
                view.setText(text);
            } else {
                view.setText(getString(R.string.lack));
            }
            return view;
        }
    }


}
