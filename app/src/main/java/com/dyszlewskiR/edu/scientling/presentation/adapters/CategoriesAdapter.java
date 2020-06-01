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
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.CategoryActivity;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends BaseAdapter implements Filterable {

    private final int MENU_EDIT = R.string.edit;
    private final int MENU_DELETE = R.string.delete;

    private final int EDIT_REQUEST = 2836;

    private List<Category> mItems;
    private List<Category> mFilteredItems;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;
    private ValueFilter mFilter;
    private int mLastEdited;
    private DataManager mDataManager;
    private boolean mIsSpinner;

    public CategoriesAdapter(Context context, int resource, List<Category> data, DataManager dataManager) {
        init(context, resource, data);
        mDataManager = dataManager;
    }

    public CategoriesAdapter(Context context, int resource, List<Category> data, boolean spinner) {
        init(context, resource, data);
        mIsSpinner = spinner;
    }

    private void init(Context context, int resource, List<Category> data) {
        mItems = data;
        mFilteredItems = data;
        mContext = context;
        mResource = resource;
        mInflater = LayoutInflater.from(mContext);
        addEmptyElement();
    }

    private void addEmptyElement() {
        Category category = new Category();
        category.setName(mContext.getString(R.string.lack));
        mItems.add(0, category);
    }

    @Override
    public int getCount() {
        return mFilteredItems.size();
    }

    public Object getItem(int position) {
        return mFilteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        //TODO zobaczyć czy jest to zrobione poprawnie
        if (position < 0) {
            return -1;
        }
        return mFilteredItems.get(position).getId();
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
        viewHolder.categoryTextView.setText(mFilteredItems.get(position).getName());
        if (!mIsSpinner && viewHolder.actionButton != null) {
            setupMenu(position, viewHolder);
            if (mItems.get(position).getName().equals(mContext.getString(R.string.lack))) {
                viewHolder.actionButton.setVisibility(View.GONE);
            } else {
                viewHolder.actionButton.setVisibility(View.VISIBLE);
            }
        }
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
                            Intent intent = new Intent(mContext, CategoryActivity.class);
                            intent.putExtra("item", mFilteredItems.get(position));
                            intent.putExtra("edit", true);
                            ((Activity) mContext).startActivityForResult(intent, EDIT_REQUEST);
                        }
                        if (item.getTitle().equals(mContext.getString(MENU_DELETE))) {
                            new DeleteCategoryAlertDialog(mContext, mFilteredItems.get(position)).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ValueFilter();
        }
        return mFilter;
    }

    static class ViewHolder {
        public final TextView categoryTextView;
        public final ImageView actionButton;

        public ViewHolder(View view) {
            categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }

    private class ValueFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Category> list = mItems;
            int count = list.size();
            final List<Category> nlist = new ArrayList<>(count);
            String itemString;
            for (int i = 0; i < count; i++) {
                itemString = list.get(i).getName();
                if (itemString.toLowerCase().contains(filterString)) {
                    nlist.add(mItems.get(i));
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredItems = (ArrayList<Category>) results.values;
            notifyDataSetChanged();
        }
    }

    private class DeleteCategoryAlertDialog extends AlertDialog {

        protected DeleteCategoryAlertDialog(Context context, final Category category) {
            super(context);
            setTitle(mContext.getString(R.string.deleting_category));
            setMessage(mContext.getString(R.string.sure_delete_category));
            setButton(BUTTON_POSITIVE, mContext.getString(R.string.yes), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDataManager.deleteCategory(category);
                    mFilteredItems.remove(category);
                    notifyDataSetChanged();
                }
            });
            setButton(BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }
}
