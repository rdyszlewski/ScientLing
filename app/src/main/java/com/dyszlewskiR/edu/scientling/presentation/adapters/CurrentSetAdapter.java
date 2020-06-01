package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

public class CurrentSetAdapter extends ArrayAdapter<VocabularySet> {
    private List<VocabularySet> mItems;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;

    public CurrentSetAdapter(Context context, int resource, List<VocabularySet> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public VocabularySet getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.nameTextView.setText(mItems.get(position).getName());
        if (mItems.get(position).getLanguageL1() != null && mItems.get(position).getLanguageL2() != null) {
            viewHolder.languagesTextView.setText(
                    ResourceUtils.getString(mItems.get(position).getLanguageL1().getName(), mContext)
                            + " - " +
                            ResourceUtils.getString(mItems.get(position).getLanguageL2().getName(), mContext)
            );
        }
        return rowView;
    }

    static class ViewHolder {
        public TextView nameTextView;
        public TextView languagesTextView;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            languagesTextView = (TextView) view.findViewById(R.id.set_languages_text_view);
        }
    }


}
