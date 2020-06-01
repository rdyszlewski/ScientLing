package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.others.RepetitionItem;

import java.util.List;

/**
 * Created by Razjelll on 22.11.2016.
 */

public class RepetitionsAdapter extends ArrayAdapter {

    private List<RepetitionItem> mItems;
    private Context mContext;
    private int mResource;


    public RepetitionsAdapter(Context context, int resource, List<RepetitionItem> data) {
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
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.checkBox.setSelected(mItems.get(position).isSelected());
        viewHolder.wordTextView.setText(mItems.get(position).getWord());

        viewHolder.translationTextView.setText(mItems.get(position).getTranslation());

        return rowView;
    }

    private class ViewHolder {
        public CheckBox checkBox;
        public TextView wordTextView;
        public TextView translationTextView;

        public ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.repetitionCheck);
            wordTextView = (TextView) view.findViewById(R.id.repetitionWordText);
            translationTextView = (TextView) view.findViewById(R.id.repetitionTranslationText);
        }
    }
}
