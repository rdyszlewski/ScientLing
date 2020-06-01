package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.SentenceDetailActivity;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

/**
 * Created by Razjelll on 27.11.2016.
 */

public class SentencesAdapter extends ArrayAdapter {

    private final int EDIT_REQUEST = 2845;

    private List<Sentence> mItems;
    private Context mContext;
    private int mResource;

    private int mLastEdited;

    public SentencesAdapter(Context context, int resource, List<Sentence> data) {
        super(context, resource);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        viewHolder.contentTextView.setText(mItems.get(position).getContent());
        viewHolder.translationTextView.setText(mItems.get(position).getTranslation());
        setupMenu(position, viewHolder);
        return rowView;
    }

    private void setupMenu(final int position, final ViewHolder viewHolder) {
        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.actionButton);
                popupMenu.getMenu().add(mContext.getString(Constants.MENU_EDIT));
                popupMenu.getMenu().add(mContext.getString(Constants.MENU_DELETE));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == Constants.MENU_EDIT) {
                            Log.d(getClass().getSimpleName(), "Udało się");
                        }
                        if (item.getTitle().equals(mContext.getString(Constants.MENU_EDIT))) {
                            startEditSentence(position);
                        }
                        if (item.getTitle().equals(mContext.getString(Constants.MENU_DELETE))) {
                            deleteSentence(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void startEditSentence(int position) {
        mLastEdited = position;
        Intent intent = new Intent(mContext, SentenceDetailActivity.class);
        intent.putExtra("item", mItems.get(position));
        intent.putExtra("edit", true);
        ((Activity) mContext).startActivityForResult(intent, EDIT_REQUEST);
    }

    private void deleteSentence(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Sentence sentence = data.getParcelableExtra("result");
                mItems.set(mLastEdited, sentence);
                notifyDataSetChanged();
            }
        }
    }

    public static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.sentence_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.sentence_translation_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }
}
