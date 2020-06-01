package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.dyszlewskiR.edu.scientling.models.entity.Language;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

public class LanguageDialog extends DialogFragment {
    private final int LAYOUT_RESOURCE = R.layout.dialog_language;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_language;

    private ListView mListView;
    private Callback mCallback;
    private List<Language> mItems;
    private LanguageAdapter mAdapter;

    public interface Callback {
        void onLanguageOk(Language language);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        loadData();
        setLanguageAdapter();
        getDialog().setTitle(getString(R.string.language));
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
                    mCallback.onLanguageOk(mItems.get(position));
                }
                dismiss();
            }
        });
    }

    private void loadData() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mItems = dataManager.getLanguages();
    }

    private void setLanguageAdapter() {
        mAdapter = new LanguageAdapter(getContext(), ADAPTER_ITEM_RESOURCE, mItems);
        mListView.setAdapter(mAdapter);
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

    //region LanguageAdapter
    private class LanguageAdapter extends ArrayAdapter {

        private final String NAME_RESOURCE_TYPE = "string";

        private List<Language> mLanguages;
        private Context mContext;
        private int mResource;

        public LanguageAdapter(Context context, int resource, List<Language> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mLanguages = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
            //TODO przemyśleć czy da radę zrobić to inaczej
            //int stringIdentyfier = mContext.getResources().getIdentifier(mLanguages.get(position).getFileName(), NAME_RESOURCE_TYPE, mContext.getPackageName());
            //viewHolder.nameTextView.setValues(mContext.getResources().getString(stringIdentyfier));
            viewHolder.nameTextView.setText(ResourceUtils.getString(mItems.get(position).getName(), mContext));

            return rowView;
        }
    }

    private static class ViewHolder {
        public TextView nameTextView;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.language_name);
        }
    }



    //endregion
}
