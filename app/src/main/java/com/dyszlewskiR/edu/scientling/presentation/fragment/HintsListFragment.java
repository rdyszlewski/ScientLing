package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.HintDialog;

import java.util.ArrayList;
import java.util.List;

public class HintsListFragment extends Fragment implements HintDialog.Callback {

    private final int MENU_EDIT_RESOURCE = R.string.edit;
    private final int MENU_DELETE_RESOURCE = R.string.delete;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_hint_list;

    private ListView mListView;
    private Button mOkButton;
    private List<Hint> mItems;
    private HintsAdapter mAdapter;
    private int mLastEdited;

    public HintsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        setHasOptionsMenu(true);
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mItems = intent.getParcelableArrayListExtra("list");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hint_list, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
        mOkButton = (Button) view.findViewById(R.id.ok_button);
    }

    private void setListeners() {
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish();
            }
        });
    }

    private void setResultAndFinish() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("result", (ArrayList<Hint>) mItems);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new HintsAdapter(getContext(), ADAPTER_ITEM_RESOURCE);
        mListView.setAdapter(mAdapter);
    }

    public void onBackPressed() {
        setResultAndFinish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hints_list_menu, menu);
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_button:

                openHintDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openHintDialog() {
        HintDialog dialog = new HintDialog();
        dialog.setCallback(HintsListFragment.this);
        dialog.show(getFragmentManager(), "HintDialog");
    }

    @Override
    public void onAddHintOk(Hint hint) {
        if (hint != null) {
            mItems.add(hint);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditHintOk(Hint hint) {
        if (hint != null) {
            mItems.set(mLastEdited, hint);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class HintsAdapter extends BaseAdapter {
        private Context mContext;
        private int mResource;
        private LayoutInflater mInflater;

        public HintsAdapter(Context context, int resource) {
            mContext = context;
            mResource = resource;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Hint getItem(int position) {
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
                rowView = mInflater.inflate(mResource, null);
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
                    popupMenu.getMenu().add(mContext.getString(MENU_EDIT_RESOURCE));
                    popupMenu.getMenu().add(mContext.getString(MENU_DELETE_RESOURCE));
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().equals(mContext.getString(MENU_EDIT_RESOURCE))) {
                                startEditHint(position);
                            }
                            if (item.getTitle().equals(mContext.getString(MENU_DELETE_RESOURCE))) {
                                deleteHint(position);
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
            HintDialog dialog = new HintDialog();
            dialog.setCallback(HintsListFragment.this);
            dialog.setHint(mItems.get(position));
            dialog.show(getFragmentManager(), "HintDialog");
        }

        private void deleteHint(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
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
