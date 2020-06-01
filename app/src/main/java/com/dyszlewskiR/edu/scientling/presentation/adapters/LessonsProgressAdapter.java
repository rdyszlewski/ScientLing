package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

public class LessonsProgressAdapter extends ArrayAdapter {

    private List<Lesson> mItems;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;

    public LessonsProgressAdapter(Context context, int resource, List<Lesson> data) {
        super(context, resource);

        mItems = data;
        mContext = context;
        mResource = resource;

        mInflater = LayoutInflater.from(mContext);
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
            rowView = mInflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.lessonNumberTextView.setText(String.valueOf(mItems.get(position).getNumber()));
        viewHolder.lessonProgressBar.setProgress(mItems.get(position).getProgress());
        if (!mItems.get(position).getName().equals(Constants.DEFAULT_LESSON_NAME)) {
            viewHolder.lessonNameTextView.setText(mItems.get(position).getName());
        } else {
            viewHolder.lessonNameTextView.setText(mContext.getString(R.string.unallocated));
        }
        viewHolder.lessonProgressTextView.setText(String.valueOf(mItems.get(position).getProgress() + "%"));

        return rowView;
    }

    static class ViewHolder {
        public TextView lessonNumberTextView;
        public ProgressBar lessonProgressBar;
        public TextView lessonNameTextView;
        public TextView lessonProgressTextView;

        public ViewHolder(View view) {
            lessonNumberTextView = (TextView) view.findViewById(R.id.lesson_number_text_view);
            lessonProgressBar = (ProgressBar) view.findViewById(R.id.lesson_progress_bar);
            lessonNameTextView = (TextView) view.findViewById(R.id.lesson_name_text_view);
            lessonProgressTextView = (TextView) view.findViewById(R.id.lesson_progress_text_view);
        }
    }
}
