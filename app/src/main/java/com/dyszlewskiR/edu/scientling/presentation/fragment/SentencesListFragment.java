package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.SentenceDialog;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SentencesListFragment extends Fragment implements SentenceDialog.Callback {

    public final int ADD_REQUEST = 456;
    private final int EDIT_REQUEST = 123;
    private List<Sentence> mItems;
    private ListView mListView;
    private int mLastEdited;

    private Button mOkButton;
    private SentencesAdapter mAdapter;

    private int mEditedPosition;

    public SentencesListFragment() {
        mEditedPosition = -1;
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
        Intent data = getActivity().getIntent();
        mItems = data.getParcelableArrayListExtra("list");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentences_list, container, false);
        view.setFocusable(false);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.startEditSentence(position);
            }
        });
    }

    private void setResultAndFinish() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("result", (ArrayList<Sentence>) mItems);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
    }

    private void setAdapter() {
        mAdapter = new SentencesAdapter(getActivity(), R.layout.item_sentences, mItems);
        mListView.setAdapter(mAdapter);
    }

    public void addSentence(Sentence sentence) {
        mItems.add(sentence);
    }

    public void onBackPressed() {
        setResultAndFinish();
    }

    @Override
    public void onSentenceCreateOk(Sentence sentence) {
        if (sentence != null) {
            mItems.add(sentence);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Metoda wywołująca po zakończeniu edytowania przykładowego zdania. W przypadku jeśli
     * dane są poprawnie wypełnione następuje podmiana zdania na liście.
     *
     * @param sentence
     */
    @Override
    public void onSentenceEditOk(Sentence sentence) {
        if (sentence != null) {
            mItems.set(mLastEdited, sentence);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sentences_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_button:
                Log.d("SentencesListActivity", "Add Click");
                openSentenceDialog(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metoda tworząca i pokazująca okno dialogowe do wprowadzania danych. Podczas tworzenia nastepuje
     * ustawienie metody zwrotenej.
     *
     * @param editedPosition określa która pozycja będzie edytowana. W przypadku tworzenia nowego zdania
     *                       należy przekazać wartosć -1
     */
    private void openSentenceDialog(int editedPosition) {
        SentenceDialog dialog = new SentenceDialog();
        dialog.setCallback(this);
        if (editedPosition >= 0) {
            dialog.setSentence(mItems.get(editedPosition));
        }
        dialog.show(getFragmentManager(), "SentenceListFragment");
    }

    //region SentencesAdapter
    private class SentencesAdapter extends ArrayAdapter {
        private List<Sentence> mItems;
        private Context mContext;
        private int mResource;

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
            com.dyszlewskiR.edu.scientling.presentation.adapters.SentencesAdapter.ViewHolder viewHolder;
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(mResource, null);
                viewHolder = new com.dyszlewskiR.edu.scientling.presentation.adapters.SentencesAdapter.ViewHolder(rowView);

                rowView.setTag(viewHolder);
            } else {
                viewHolder = (com.dyszlewskiR.edu.scientling.presentation.adapters.SentencesAdapter.ViewHolder) rowView.getTag();
            }

            viewHolder.contentTextView.setText(mItems.get(position).getContent());
            viewHolder.translationTextView.setText(mItems.get(position).getTranslation());
            setupMenu(position, viewHolder);
            return rowView;
        }

        private void setupMenu(final int position, final com.dyszlewskiR.edu.scientling.presentation.adapters.SentencesAdapter.ViewHolder viewHolder) {
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
            openSentenceDialog(position);
        }

        private void deleteSentence(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.sentence_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.sentence_translation_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }
    //endregion
}
