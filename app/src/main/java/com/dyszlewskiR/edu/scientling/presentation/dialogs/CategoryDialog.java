package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryDialog extends DialogFragment {
    private final int LAYOUT_RESOURCE = R.layout.dialog_category;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_part_of_speech;

    private EditText mSearchEditText;
    private ListView mListView;
    private Callback mCallback;

    private List<Category> mItems;
    private CategoryAdapter mAdapter;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onCategoryOk(Category category);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        loadData();
        setCategoryAdapter();
        getDialog().setTitle(getString(R.string.category));
        return view;
    }

    private void setupControls(View view) {
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mListView = (ListView) view.findViewById(R.id.list);
    }

    private void setListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    mCallback.onCategoryOk(mAdapter.getItem(position));
                }
                dismiss();
            }
        });

        mSearchEditText.addTextChangedListener(new CategorySearchWatcher());
    }

    private void loadData() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        mItems = dataManager.getCategories();
        Category emptyCategory = new Category(-1);
        //wstawiamy nazwę taką jak w pliku string oznaczony jest wyraz brak
        //będzie to pomagało filtrować nazwy. Bez tego brak nie byłby uwzględniany
        emptyCategory.setName(Constants.LACK_STRING);
        mItems.add(0, emptyCategory);
        //tutaj nie dodajemy pustego elementu na początek, ponieważ w bazie danych istnieje kategoria brak
    }

    private void setCategoryAdapter() {
        mAdapter = new CategoryAdapter(getContext(), ADAPTER_ITEM_RESOURCE, mItems);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mCallback = null;
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onDestroyView(){
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()){
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private class CategoryAdapter extends BaseAdapter implements Filterable {

        private final Context mContext;
        private final int mResource;
        private final LayoutInflater mInflater;

        private ValueFilter mFilter;
        private List<Category> mFilteredItems;

        public CategoryAdapter(Context context, int resource, List<Category> data) {
            mContext = context;
            mResource = resource;
            mInflater = LayoutInflater.from(context);
            mFilteredItems = data;
        }


        @Override
        public int getCount() {
            return mFilteredItems.size();
        }

        @Override
        public Category getItem(int position) {
            return mFilteredItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (mFilteredItems.get(position) != null) {
                return mFilteredItems.get(position).getId();
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);
            }
            TextView view = (TextView) convertView;
            if (mFilteredItems.get(position) != null) { //TODO zastanowić się czy taki przypadek może wystąpić
                String text = ResourceUtils.getString(mFilteredItems.get(position).getName(), mContext);
                view.setText(text);
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ValueFilter();
            }
            return mFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                final List<Category> list = mItems;
                int count = list.size();
                final List<Category> nList = new ArrayList<>(count);
                String itemString;
                for (int i = 0; i < count; i++) {
                    itemString = ResourceUtils.getString(list.get(i).getName(), mContext);
                    assert itemString != null;
                    if (itemString.toLowerCase().contains(filterString)) {
                        nList.add(mItems.get(i));
                    }
                }
                results.values = nList;
                results.count = nList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredItems = (ArrayList<Category>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    private class CategorySearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            mAdapter.getFilter().filter(text.toLowerCase());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
