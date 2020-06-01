package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

/**
 * Created by Razjelll on 11.01.2017.
 */

public class SetSpinnerAdapter extends BaseAdapter {

    private List<VocabularySet> mItems;
    private Context mContext;
    private int mResource;


    public SetSpinnerAdapter(Context context, int resource, List<VocabularySet> data) {
        //super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    public int getCount() {
        return mItems.size();
    }

    public VocabularySet getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
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
        viewHolder.nameTextView.setText(mItems.get(position).getName());
        if (mItems.get(position).getLanguageL2() != null) {
            String language = ResourceUtils.getString(mItems.get(position).getLanguageL2().getName(), mContext);
            viewHolder.languageTextView.setText(language);
        }
        return rowView;
    }

    private static class ViewHolder {
        public TextView nameTextView;
        public TextView languageTextView;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            languageTextView = (TextView) view.findViewById(R.id.set_language_text_view);
        }
    }
}
