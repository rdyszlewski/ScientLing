package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.List;

/**
 * Created by Razjelll on 20.12.2016.
 */

public class LearningWordsAdapter extends BaseAdapter {

    private List<Word> mItems;
    private Context mContext;
    private int mResource;


    public LearningWordsAdapter(Context context, int resource, List<Word> data) {
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    public void setData(List<Word> data) {
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
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            rowView = layoutInflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.contentTextView.setText(mItems.get(position).getContent());
        String translations = TranslationListConverter.toString(mItems.get(position).getTranslations());
        viewHolder.translationTextView.setText(translations);
        if (mItems.get(position).getCategory() != null) {
            String category = ResourceUtils.getString(mItems.get(position).getCategory().getName(), mContext);
            viewHolder.categoryTextView.setText(category);
        }
        viewHolder.difficultTextView.setText(String.valueOf(mItems.get(position).getDifficult()));


        return rowView;
    }

    static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public TextView categoryTextView;
        public TextView difficultTextView;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
            categoryTextView = (TextView) view.findViewById(R.id.word_category_text_view);
            difficultTextView = (TextView) view.findViewById(R.id.word_difficult_text_view);
        }
    }
}
