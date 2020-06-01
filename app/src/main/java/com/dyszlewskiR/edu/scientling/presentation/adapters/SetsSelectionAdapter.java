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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.SetActivity;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.List;


/**
 * Created by Razjelll on 29.11.2016.
 */

public class SetsSelectionAdapter extends ArrayAdapter<VocabularySet> {

    private final int MENU_EDIT = R.string.edit;
    private final int MENU_DELETE = R.string.delete;
    private final int EDIT_REQUEST = 2145;

    private List<VocabularySet> mItems;
    private Context mContext;
    private int mResource;
    private ViewHolder mViewHolder;
    private DataManager mDataManager;
    private int mLastEdited;

    public SetsSelectionAdapter(Context context, int resource, List<VocabularySet> data, DataManager dataManager) {
        super(context, resource, data);

        mItems = data;
        mContext = context;
        mResource = resource;

        mDataManager = dataManager;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public VocabularySet getItem(int position) {
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

        viewHolder.nameTextView.setText(mItems.get(position).getName());
        String language = ResourceUtils.getString(mItems.get(position).getLanguageL2().getName(), mContext);
        viewHolder.languageTextView.setText(language);

        if (mItems.get(position).getId() == Constants.DEFAULT_SET_ID) {
            viewHolder.actionButton.setVisibility(View.GONE);
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
                popupMenu.getMenu().add(mContext.getString(MENU_DELETE));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(mContext.getString(MENU_EDIT))) {
                            mLastEdited = position;
                            Intent intent = new Intent(mContext, SetActivity.class);
                            intent.putExtra("item", mItems.get(position));
                            intent.putExtra("edit", true);
                            ((Activity) mContext).startActivityForResult(intent, EDIT_REQUEST);

                        }
                        if (item.getTitle().equals(mContext.getString(MENU_DELETE))) {
                            new DeleteSetAlertDialog(mContext, mItems.get(position)).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                VocabularySet set = data.getParcelableExtra("result");
                mItems.set(mLastEdited, set);
                notifyDataSetChanged();
            }
        }
    }

    private class ViewHolder {
        public TextView nameTextView;
        public TextView languageTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.set_list_name);
            languageTextView = (TextView) view.findViewById(R.id.set_list_language);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }

    private class DeleteSetAlertDialog extends AlertDialog {

        protected DeleteSetAlertDialog(Context context, final VocabularySet set) {
            super(context);
            setTitle(mContext.getString(R.string.deleting_set));
            setMessage(mContext.getString(R.string.sure_delete_set));
            setButton(BUTTON_POSITIVE, mContext.getString(R.string.yes), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDataManager.deleteSet(set);
                }
            });
            setButton(BUTTON_NEGATIVE, mContext.getString(R.string.no), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }

    private class SimpleSetAlertDialog extends AlertDialog {

        protected SimpleSetAlertDialog(Context context, String message) {
            super(context);
            setTitle(mContext.getString(R.string.deleting_set));
            setMessage(message);
            setButton(BUTTON_NEUTRAL, mContext.getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }
}
