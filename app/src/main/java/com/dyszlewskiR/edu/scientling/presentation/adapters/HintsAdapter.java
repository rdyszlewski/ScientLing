package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.HintActivity;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;

import java.util.List;

/**
 * Created by Razjelll on 16.01.2017.
 */

public class HintsAdapter extends BaseAdapter {

    private final int MENU_EDIT = R.string.edit;
    private final int MENU_DELETE = R.string.delete;

    private final int EDIT_REQUEST = 2313;

    private List<Hint> mItems;
    private Context mContext;
    private int mResource;
    private int mLastEdited;

    public HintsAdapter(Context context, int resource, List<Hint> data) {
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

        viewHolder.contentTextView.setText(mItems.get(position).getContent());
        setupMenu(position, viewHolder);
        return rowView;
    }

    private void setupMenu(final int position, final ViewHolder viewHolder) {
        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.actionButton);
                popupMenu.getMenu().add(mContext.getString(MENU_EDIT));
                popupMenu.getMenu().add(mContext.getString(MENU_DELETE));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(mContext.getString(MENU_EDIT))) {
                            startEditHint(position);
                        }
                        if (item.getTitle().equals(mContext.getString(MENU_DELETE))) {
                            removeHint(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void startEditHint(int position) {
        mLastEdited = position;
        Intent intent = new Intent(mContext, HintActivity.class);
        intent.putExtra("item", mItems.get(position));
        intent.putExtra("edit", true);
        ((Activity) mContext).startActivityForResult(intent, EDIT_REQUEST);
    }

    private void removeHint(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Hint hint = data.getParcelableExtra("result");
                mItems.set(mLastEdited, hint);
                notifyDataSetChanged();
            }
        }
    }

    private static class ViewHolder {
        public TextView contentTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.content_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }
}
