package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.ManageWordsActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.WordEditActivity;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razjelll on 11.01.2017.
 */

public class WordListAdapter extends ArrayAdapter implements Filterable {

    private final int MENU_EDIT = R.string.edit;
    private final int MENU_REMOVE_FROM_HARD = R.string.remove_from_hard;
    private final int MENU_ADD_TO_HARD = R.string.add_to_hard;
    private final int MENU_DELETE = R.string.delete;

    private int EDIT_WORD_REQUEST = 999;

    private List<Word> mItems;
    private List<Word> mFilteredItems;
    private Context mContext;
    private int mResource;
    private ValueFilter mFilter;
    private DataManager mDataManager;
    private int mLastEditedWord;

    public WordListAdapter(Context context, int resource, List<Word> data, DataManager dataManager) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
        mFilteredItems = data;
        mDataManager = dataManager;
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
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.contentTextView.setText(mFilteredItems.get(position).getContent());
        String translations = TranslationListConverter.toString(mFilteredItems.get(position).getTranslations());
        viewHolder.translationTextView.setText(translations);
        if (mFilteredItems.get(position).getCategory() != null) {
            viewHolder.categoryTextView.setText(mFilteredItems.get(position).getCategory().getName());
        }
        if (mFilteredItems.get(position).getPartsOfSpeech() != null) {
            String partOfSpeech = mFilteredItems.get(position).getPartsOfSpeech().getName();
            String translatedPart = ResourceUtils.getString(partOfSpeech, mContext);
            viewHolder.partOfSpeechTextView.setText(translatedPart);
        }
        setupMenu(position, viewHolder);

        return rowView;
    }

    private void setupMenu(final int position, final ViewHolder viewHolder) {

        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.actionButton);
                popupMenu.getMenu().add(mContext.getString(MENU_EDIT));
                if (mFilteredItems.get(position).isSelected()) {
                    popupMenu.getMenu().add(mContext.getString(MENU_REMOVE_FROM_HARD));
                } else {
                    popupMenu.getMenu().add(mContext.getString(MENU_ADD_TO_HARD));
                }
                popupMenu.getMenu().add(mContext.getString(MENU_DELETE));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(mContext.getString(MENU_EDIT))) {
                            Intent intent = new Intent(mContext, WordEditActivity.class);
                            //fWord word = mDataManager.getWord(mFilteredItems.get(position).getId());
                            intent.putExtra("item", mFilteredItems.get(position).getId()); //TODO zobaczyć czy lepiej przesłać id czy całe słówko
                            intent.putExtra("edit", true);
                            mLastEditedWord = position;
                            ((Activity) mContext).startActivityForResult(intent, EDIT_WORD_REQUEST);
                        }
                        if (item.getTitle().equals(mContext.getString(MENU_ADD_TO_HARD))
                                || item.getTitle().equals(mContext.getString(MENU_REMOVE_FROM_HARD))) {
                            mFilteredItems.get(position).setSelected(!mFilteredItems.get(position).isSelected());
                            mDataManager.updateWord(mFilteredItems.get(position));
                        }
                        if (item.getTitle().equals(mContext.getString(MENU_DELETE))) {
                            new DeleteWordAlertDialog(getContext(),
                                    mFilteredItems.get(position),
                                    mDataManager).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_WORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Word word = data.getParcelableExtra("word");
                mFilteredItems.set(mLastEditedWord, word);
                notifyDataSetChanged();
            }
        }
        if (requestCode == ManageWordsActivity.ADD_WORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Word word = data.getParcelableExtra("word");
                mFilteredItems.add(word);
                notifyDataSetChanged();
            }
        }
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ValueFilter();
        }
        return mFilter;
    }

    private static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public TextView setTextView;
        public TextView categoryTextView;
        public TextView partOfSpeechTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
            //setTextView = (TextView) view.findViewById(R.id.set_text_view);
            categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
            partOfSpeechTextView = (TextView) view.findViewById(R.id.part_of_speech_text_view);
            actionButton = (ImageView) view.findViewById(R.id.menu_button);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Word> list = mItems;
            int count = list.size();
            final List<Word> resultList = new ArrayList<>();
            String itemContent;
            String itemTranslation;
            boolean contain;
            for (int i = 0; i < count; i++) {
                contain = false;
                itemContent = list.get(i).getContent();
                for (int j = 0; j < list.get(i).getTranslations().size(); j++) {
                    itemTranslation = list.get(i).getTranslations().get(j).getContent();
                    if (itemTranslation.contains(filterString)) {
                        contain = true;
                    }
                }
                if (itemContent.toLowerCase().contains(filterString) || contain) {
                    resultList.add(mItems.get(i));
                }
            }
            results.values = resultList;
            results.count = resultList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredItems = (ArrayList<Word>) results.values;
            notifyDataSetChanged();
        }
    }

    private class DeleteWordAlertDialog extends AlertDialog {

        public DeleteWordAlertDialog(Context context, final Word word, final DataManager dataManager) {
            super(context);
            setTitle(context.getString(R.string.deleting_word));
            String message = context.getString(R.string.sure_delete_word) + "/n"
                    + word.getContent() + TranslationListConverter.toString(word.getTranslations());
            setMessage(message);
            setButton(BUTTON_POSITIVE, context.getString(R.string.yes), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dataManager.deleteWord(word);
                    mFilteredItems.remove(word);
                    notifyDataSetChanged();
                }
            });
            setButton(BUTTON_NEGATIVE, context.getString(R.string.no), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }
}
