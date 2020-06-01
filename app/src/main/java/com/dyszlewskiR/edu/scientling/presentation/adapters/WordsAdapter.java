package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razjelll on 09.12.2016.
 */

public class WordsAdapter extends BaseAdapter implements Filterable {

    private List<Word> mItems;
    private List<Word> mFilteredItems;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;
    private ValueFilter mFilter;

    public WordsAdapter(Context context, int resource, List<Word> data) {
        mItems = data;
        mFilteredItems = data;
        mContext = context;
        mResource = resource;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mFilteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFilteredItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        if (rowView == null) {
            rowView = mInflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.wordTextView.setText(mFilteredItems.get(position).getContent());
        String translations = TranslationListConverter.toString(mFilteredItems.get(position).getTranslations());
        viewHolder.translationTextView.setText(translations);
        if (mFilteredItems.get(position).getCategory() != null)
            viewHolder.categoryTextView.setText(mFilteredItems.get(position).getCategory().getName());
        if (mFilteredItems.get(position).getPartsOfSpeech() != null) {
            String partOfSpeech = mFilteredItems.get(position).getPartsOfSpeech().getName();
            String translatedPart = ResourceUtils.getString(partOfSpeech, mContext);
            viewHolder.partOfSpeechTextView.setText(translatedPart);
        }
        return rowView;
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ValueFilter();
        }
        return mFilter;
    }

    static class ViewHolder {
        public TextView wordTextView;
        public TextView translationTextView;
        public TextView categoryTextView;
        public TextView partOfSpeechTextView;

        public ViewHolder(View view) {
            wordTextView = (TextView) view.findViewById(R.id.word_text_view);
            translationTextView = (TextView) view.findViewById(R.id.translation_text_view);
            categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
            partOfSpeechTextView = (TextView) view.findViewById(R.id.part_of_speech_text_view);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Word> list = mItems;
            int count = list.size();
            final List<Word> nList = new ArrayList<>(count);
            String itemContent;
            String itemTranslation;
            boolean contain;
            for (int i = 0; i < count; i++) {
                contain = false;
                itemContent = list.get(i).getContent();
                //sprawdzejnie czy któreś z tłumaczeń zawierają szukaną frazę
                for (int j = 0; j < list.get(i).getTranslations().size(); j++) {
                    itemTranslation = list.get(i).getTranslations().get(j).getContent();
                    if (itemTranslation.contains(filterString)) {
                        contain = true;
                    }
                }
                if (itemContent.toLowerCase().contains(filterString) || contain) {
                    nList.add(mItems.get(i));
                }
            }
            results.values = nList;
            results.count = nList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredItems = (ArrayList<Word>) results.values;
            notifyDataSetChanged();
        }
    }
}
