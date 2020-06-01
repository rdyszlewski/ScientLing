package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.List;

public class SummaryRepetitionAdapter extends ArrayAdapter {

    private final int SELECTED_ICON = R.drawable.ic_check;
    private final int UNSELECTED_ICON = R.drawable.ic_add;

    private List<Word> mItems;
    private Context mContext;
    private int mResource;

    public SummaryRepetitionAdapter(Context context, int resource, List<Word> data) {
        super(context, resource, data);
        mItems = data;
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.contentTextView.setText(mItems.get(position).getContent());
        String translations = TranslationListConverter.toString(mItems.get(position).getTranslations());
        viewHolder.translationTextView.setText(translations);

        /*final boolean isSelected = mItems.get(position).isSelected();
        if(isSelected){
            viewHolder.selectedButton.setBackgroundResource(SELECTED_ICON);
        } else {
            viewHolder.selectedButton.setBackgroundResource(UNSELECTED_ICON);
        }*/
        setSelectedButton(position, viewHolder);
        viewHolder.selectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getSimpleName(), "onClick " + position);

                boolean isSelected = mItems.get(position).isSelected();


                mItems.get(position).setSelected(!isSelected);
                viewHolder.selectedButton.setActivated(!isSelected);
                viewHolder.selectedButton.invalidate();
                Log.d(getClass().getSimpleName(), "selected" + viewHolder.selectedButton.isActivated());
                setSelectedButton(position, viewHolder);
            }
        });
        return rowView;
    }

    private void setSelectedButton(int position, ViewHolder viewHolder) {
        final boolean isSelected = mItems.get(position).isSelected();

        if (isSelected) {
            //viewHolder.selectedButton.setImageDrawable(ContextCompat.getDrawable(mContext,SELECTED_ICON));
            //viewHolder.selectedButton.setBackgroundResource(SELECTED_ICON);
            viewHolder.selectedButton.setImageResource(SELECTED_ICON);
        } else {
            //viewHolder.selectedButton.setImageDrawable(ContextCompat.getDrawable(mContext,UNSELECTED_ICON));
            //viewHolder.selectedButton.setBackgroundResource(UNSELECTED_ICON);
            viewHolder.selectedButton.setImageDrawable(null);
        }
    }

    static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public ImageView selectedButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
            selectedButton = (ImageView) view.findViewById(R.id.selected_button);
        }
    }

}
