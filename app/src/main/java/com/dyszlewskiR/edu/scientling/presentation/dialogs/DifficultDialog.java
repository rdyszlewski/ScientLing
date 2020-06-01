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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.utils.Constants;

public class DifficultDialog extends DialogFragment {
    //korzystamy z tego samego układu który był użyty w PartOfSpeechDialog ponieważ posiada identyczne elementy
    private final int LAYOUT_RESOURCE = R.layout.dialog_part_of_speech;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_difficult;


    private ListView mListView;
    private int[] mDifficults;
    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onDifficultOk(int difficult);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        fillDifficultsData();
        setupControls(view);
        setListeners();
        setListAdapter();
        getDialog().setTitle(getString(R.string.difficult));
        return view;
    }

    private void fillDifficultsData() {
        //wartośc określająca jak wielka będzie tablica. Tablica będzie o 1 większa niż liczba poziomów trudności
        //poniważ na początku tablicy będzie znajdowała się wartość oznaczająca brak wybranego poziomu trudności
        int difficultsCount = Constants.MAX_DIFFICULT_LEVEL + 1;
        mDifficults = new int[difficultsCount];
        for (int i = 0; i < difficultsCount; i++) {
            mDifficults[i] = i;
        }
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
    }

    private void setListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    //można zastosować po porsu (position) poziomy trudności w tablicy odpowiadają
                    //numerom ineksów, ale z racji możliwości zmiany w przyszłości rozmieszczenia
                    //poziomów trudności w tablicy zastosowano poniżesze rozwiązanie
                    mCallback.onDifficultOk(mDifficults[position]);
                }
                dismiss();
            }
        });
    }

    private void setListAdapter() {
        PartOfSpeechAdapter adapter = new PartOfSpeechAdapter(getContext(), ADAPTER_ITEM_RESOURCE);
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

    private class PartOfSpeechAdapter extends BaseAdapter {
        private int mResource;
        private LayoutInflater mInflater;

        public PartOfSpeechAdapter(Context context, int resource) {

            mResource = resource;
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return mDifficults.length;
        }

        @Override
        public Integer getItem(int position) {
            return mDifficults[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);
            }
            TextView view = (TextView) convertView;
            if (mDifficults[position] != 0) {
                view.setText(String.valueOf(mDifficults[position]));
            } else {
                view.setText(getString(R.string.lack));
            }
            return view;
        }
    }

}
