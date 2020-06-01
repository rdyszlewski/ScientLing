package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.WordsManagerActivity;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.LessonDialog;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.management.DeletingLessonService;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

public class LessonSelectionFragment extends Fragment implements LessonDialog.Callback {

    private final int LAYOUT_RESOURCE = R.layout.fragment_lesson_selection;
    private final int ADAPTER_ITEM_RESOURCE = R.layout.item_lesson;
    private final String DEFUALT_LESSON_NUMBER = "0";

    private ListView mListView;
    private long mSetId;

    private LessonAdapter mAdapter;

    private int mLastEditedPosition;

    /**
     * Określa czy aktywność działa w trybie managera(true) czy w trybie wyboru lekcji(false)
     */
    private boolean mManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        setHasOptionsMenu(true);
    }

    private void getData() {
        Intent intent = getActivity().getIntent();
        mSetId = intent.getLongExtra("set", Constants.DEFAULT_SET_ID);
        mManager = intent.getBooleanExtra("manager", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT_RESOURCE, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
    }

    private void setListeners() {
        if (!mManager) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("result", mAdapter.getItem(position));
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
        } else {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startWordsManagerActivity(position);
                }
            });
        }
    }

    private void startWordsManagerActivity(int itemPosition) {
        Intent intent = new Intent(getContext(), WordsManagerActivity.class);
        intent.putExtra("set", mSetId);
        intent.putExtra("lesson", mAdapter.getItem(itemPosition));
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
    }

    private void setAdapter() {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        //można stworzyć nowy zbiór, ponieważ przy zapytaniu brane pod uwagę jest tylko id
        List<Lesson> lessons = dataManager.getLessons(new VocabularySet(mSetId));
        mAdapter = new LessonAdapter(getActivity(), ADAPTER_ITEM_RESOURCE, lessons);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
    }

    private final int WORDS = R.string.words;
    private final int EDIT = R.string.edit;
    private final int DELETE = R.string.delete;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        Lesson lesson = mAdapter.getItem(position);
        if (lesson.getName().equals("")) {
            menu.setHeaderTitle(getString(R.string.unallocated));
        } else {
            menu.setHeaderTitle(lesson.getName());
        }
        menu.add(0, WORDS, 0, getString(WORDS));
        if (lesson.getNumber() != Constants.DEFAULT_LESSON_NUMBER) {
            menu.add(0, EDIT, 0, getString(EDIT));
            menu.add(0, DELETE, 0, getString(DELETE));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId()){
            case WORDS:
                startWordsManagerActivity(position); break;
            case EDIT:
                editItem(position); break;
            case DELETE:
                showDeleteDialog(position); break;
        }
        return true;
    }

    private void editItem(int position){
        LessonDialog dialog = new LessonDialog();
        dialog.setCallback(LessonSelectionFragment.this);
        dialog.setLesson(mAdapter.getItem(position));
        dialog.show(getFragmentManager(), "LessonDialog");
    }

    private void showDeleteDialog(int position){
        DeleteDialog dialog = new DeleteDialog(getContext(), mAdapter.getItem(position));
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.lesson_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO
                return true;
            case R.id.add_button:
                openLessonDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openLessonDialog() {
        LessonDialog dialog = new LessonDialog();
        dialog.setCallback(this);
        //TODO może będzie potrzebne przekazanie lekcji
        dialog.show(getFragmentManager(), "LessonDialog");
    }

    @Override
    public void onLessonOk(Lesson lesson, boolean edit) {
        long lessonId = saveLessonInDb(lesson, edit);
        lesson.setId(lessonId);
        if (!edit) { //dodawanie nowej lekcji
            mAdapter.add(lesson);
        } else { //edycja
            mAdapter.set(mLastEditedPosition, lesson);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Zapisuje lub aktualizuje lekcje w bazie danych.
     *
     * @param lesson model lekcji który ma zostać zapisany
     * @param update określa czy lekcja ma zostać zaktualizowana lub zapisana
     * @return numer identyfikacyjny zapisanej lekcji
     */
    private long saveLessonInDb(Lesson lesson, boolean update) {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        //TODO chyba można tak zrobić, przetestować czy będzie poprawnie zapisywać
        //lesson.setSet(new VocabularySet(mSetId));
        lesson.setSetId(mSetId);
        long id;
        if (update) {
            dataManager.updateLesson(lesson);
            id = lesson.getId();
        } else {
            id = dataManager.saveLesson(lesson);

        }
        return id;
    }

    private void deleteLesson(Lesson lesson, long newLessonId) {
        DataManager dataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        //TODO pobranie listy obrazków i nagrań
        dataManager.deleteLesson(lesson, newLessonId);
        //TODO jeśli wszystko poszło ok usunięcie obrazków i nagrań
    }

    //region LessonAdapter
    private class LessonAdapter extends BaseAdapter {

        private final int MENU_EDIT = R.string.edit;
        private final int MENU_DELETE = R.string.delete;
        private final int MENU_WORDS = R.string.words;
        private final int WORDS_REQUEST = 9673;

        private List<Lesson> mItems;
        private Context mContext;
        private int mResource;

        public LessonAdapter(Context context, int resource, List<Lesson> data) {
            mContext = context;
            mResource = resource;
            mItems = data;
        }

        public void add(Lesson lesson){
            mItems.add(lesson);
            notifyDataSetChanged();
        }

        public void set(int position, Lesson lesson){
            mItems.set(position, lesson);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Lesson getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
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

            if (mItems.get(position).getName().equals("")) {
                viewHolder.lessonNameTextView.setText(getString(R.string.unallocated));
                viewHolder.lessonNumberTextView.setText(DEFUALT_LESSON_NUMBER);
            } else {
                viewHolder.lessonNameTextView.setText(mItems.get(position).getName());
                viewHolder.lessonNumberTextView.setText(String.valueOf(mItems.get(position).getNumber()));
            }
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.showContextMenuForChild(v);
                }
            });
            return rowView;
        }
    }

    private static class ViewHolder {
        public TextView lessonNameTextView;
        public TextView lessonNumberTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            lessonNameTextView = (TextView) view.findViewById(R.id.lesson_text_view);
            lessonNumberTextView = (TextView) view.findViewById(R.id.lesson_number_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }
    }
    //endregion

    //region DeleteDialog
    private class DeleteDialog extends AlertDialog implements LessonDialog.Callback {

        protected DeleteDialog(@NonNull Context context, final Lesson lesson) {
            super(context);
            this.setTitle(getString(R.string.deleting_lesson));
            String message = getString(R.string.sure_delte_lesson) + "\n" + lesson.getName();
            this.setMessage(message);
            this.setButton(BUTTON_POSITIVE, getString(R.string.delete_with_words), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //deleteLesson(lesson, -1);
                    startDeletingService(lesson, -1, mSetId);
                    //mAdapter.remove(lesson); //TODO tutaj to zakomentowałem
                    mAdapter.notifyDataSetChanged();
                }
            });
            this.setButton(BUTTON_NEGATIVE, getString(R.string.no), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }

        private void startDeletingService(Lesson lesson, long newLessonId, long setId) {
            Intent intent = new Intent(getContext(), DeletingLessonService.class);
            intent.putExtra("lesson", lesson.getId());
            intent.putExtra("newLessonId", newLessonId);
            intent.putExtra("set", setId);
            getActivity().startService(intent);
        }

        @Override
        public void onLessonOk(Lesson lesson, boolean edit) {
            //TODO zobaczyć gdzie to jest wywoływane
            //deleteLesson(lesson, lesson.getId());
            startDeletingService(lesson, lesson.getId(), mSetId);
        }
    }
    //endregion
}
