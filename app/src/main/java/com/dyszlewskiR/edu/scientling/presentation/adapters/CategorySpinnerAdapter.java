package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter {

    private final int ANY_CATEGORY_ID = -1;

    private List<Category> mItems;
    private Context mContext;
    private int mResource;

    public CategorySpinnerAdapter(Context context, int resource, List<Category> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    @Override
    public Category getItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getRowView(position, convertView);
    }

    private View getRowView(int position, View convertView) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.text_view);
        if (mItems.get(position).getId() == ANY_CATEGORY_ID) {
            textView.setText(mContext.getString(R.string.any));
        } else {
            String categoryName = ResourceUtils.getString(mItems.get(position).getName(), mContext);
            textView.setText(categoryName);
        }

        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getRowView(position, convertView);
    }
}
