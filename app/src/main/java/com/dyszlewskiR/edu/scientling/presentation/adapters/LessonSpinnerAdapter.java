package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

/**
 * Created by Razjelll on 10.04.2017.
 */
public class LessonSpinnerAdapter extends ArrayAdapter {

    private final int ANY_LESSON_ID = -1;

    private List<Lesson> mItems;
    private Context mContext;
    private int mResource;

    public LessonSpinnerAdapter(Context context, int resource, List<Lesson> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    public void setData(List<Lesson> data) {
        mItems = data;
        notifyDataSetChanged();
    }

    @Override
    public int getPosition(Object object) {
        Lesson lesson = (Lesson) object;
        for (int i = 0; i < mItems.size(); ++i) {
            if (lesson.getId() == mItems.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Lesson getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, convertView);
    }

    private View getRowView(int position, View convertView) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) rowView.findViewById(R.id.text_view);
        if (mItems.get(position).getId() != ANY_LESSON_ID) {
            if (mItems.get(position).getNumber() != Constants.DEFAULT_LESSON_NUMBER) {
                textView.setText(mItems.get(position).getName());
            } else {
                textView.setText(mContext.getString(R.string.unallocated));
            }
        } else {
            textView.setText(mContext.getString(R.string.any));
        }
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, convertView);
    }
}
