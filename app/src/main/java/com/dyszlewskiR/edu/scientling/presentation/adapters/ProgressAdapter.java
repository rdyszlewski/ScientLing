package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.others.SetProgress;

import java.util.List;

public class ProgressAdapter extends BaseAdapter {
    private Context mContext;
    private List<SetProgress> mItems;
    private int mResource;

    public ProgressAdapter(Context context, int resource, List<SetProgress> data){
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        if(rowView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        SetProgress set = mItems.get(position);
        viewHolder.mNameTextView.setText(set.getName());
        int wordsCount = set.getWordsCount();
        int learnedCount = set.getLearnedCount();
        int masteredCount = set.getMasteredCount();
        viewHolder.mLearnedTextView.setText(learnedCount + "/" + wordsCount);
        int learnedProgress = 0;
        if(wordsCount != 0){
            learnedProgress = learnedCount * 100 / wordsCount;
        }
        viewHolder.mLearnedProgressBar.setProgress(learnedProgress);
        viewHolder.mLearnedPercentage.setText(learnedProgress + "%");
        viewHolder.mMasteredTextView.setText(masteredCount + "/" + wordsCount);
        int masteredProgress = 0;
        if(wordsCount != 0){
            masteredProgress = masteredCount*100/wordsCount;
        }
        viewHolder.mMasteredProgressBar.setProgress(masteredProgress);
        viewHolder.mMasteredPercentage.setText(masteredProgress + "%");

        return rowView;
    }

    private static class ViewHolder{
        public TextView mNameTextView;
        public TextView mLearnedTextView;
        public ProgressBar mLearnedProgressBar;
        public TextView mLearnedPercentage;
        public TextView mMasteredTextView;
        public ProgressBar mMasteredProgressBar;
        public TextView mMasteredPercentage;

        public ViewHolder(View view){
            mNameTextView = (TextView)view.findViewById(R.id.name_text_view);
            mLearnedTextView = (TextView)view.findViewById(R.id.learned_text_view);
            mLearnedProgressBar = (ProgressBar)view.findViewById(R.id.learned_progress_bar);
            mLearnedPercentage = (TextView)view.findViewById(R.id.learned_percentage);
            mMasteredTextView = (TextView)view.findViewById(R.id.mastered_text_view);
            mMasteredProgressBar = (ProgressBar)view.findViewById(R.id.mastered_progress_bar);
            mMasteredPercentage = (TextView)view.findViewById(R.id.mastered_percentage);
        }
    }
}


