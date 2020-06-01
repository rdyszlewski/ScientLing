package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

/**
 * Created by Razjelll on 17.01.2017.
 */

public class PartOfSpeechAdapter extends ArrayAdapter<PartOfSpeech> {

    private List<PartOfSpeech> mItems;
    private Context mContext;
    private int mResource;

    public PartOfSpeechAdapter(Context context, int resource, List<PartOfSpeech> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public PartOfSpeech getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public int getPosition(PartOfSpeech item) {
        return mItems.indexOf(item);
    }

    public int getPosition(String name) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(ResourceUtils.getString(mItems.get(position).getName(), mContext));
        return view;
    }

    @Override
    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(ResourceUtils.getString(mItems.get(position).getName(), mContext));
        return view;
    }

}
