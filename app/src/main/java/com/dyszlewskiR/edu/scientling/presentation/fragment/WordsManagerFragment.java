package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.LearningActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.WordEditActivity;
import com.dyszlewskiR.edu.scientling.presentation.adapters.CategorySpinnerAdapter;
import com.dyszlewskiR.edu.scientling.presentation.adapters.LessonSpinnerAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.WordsParams;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.management.DeletingWordService;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;
import java.util.List;

public class WordsManagerFragment extends Fragment {

    private final String LOG_TAG = "WordsManagerFaragment";

    private final int SET_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int LESSON_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int CATEGORY_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int TYPES_ADAPTER_RESOURCE = R.layout.item_simple;
    private final int WORD_ADAPTER_RESOURCE = R.layout.item_word_list;

    private final int ADD_WORD_REQUEST = 2239;
    private int EDIT_WORD_REQUEST = 9334;

    private final int ANY_LESSON_ID = -1;
    private final int ANY_CATEGORY_ID = -1;

    //region Variables
    private ViewGroup mFilterContainer;
    private ViewGroup mSearchContainer;
    private ViewGroup mLoadingContainer;
    private Spinner mSetSpinner;
    private Spinner mLessonSpinner;
    private Spinner mCategorySpinner;
    private Spinner mTypeSpinner;
    private EditText mSearchEditText;
    private ImageButton mSearchButton;
    private ListView mListView;

    private VocabularySet mSet;
    private Lesson mLesson;
    private Category mCategory;
    private int mType;
    private SetSpinnerAdapter mSetAdapter;
    private LessonSpinnerAdapter mLessonAdapter;
    private CategorySpinnerAdapter mCategoryAdapter;
    private ArrayAdapter mTypesAdapter;
    private WordAdapter mWordAdapter;

    private DataManager mDataManager;

    private boolean mSetSpinnerTouched;
    private boolean mLessonSpinnerTouched;
    private boolean mCategoryTouched;
    private boolean mTypeSpinnerTouched;

    private int mLastEditedPosition;

    //endregion
    public WordsManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManager = ((LingApplication) getActivity().getApplication()).getDataManager();
        Log.d(LOG_TAG, "onCreate");
        loadData();

        setHasOptionsMenu(true);
    }

    /**
     * Pobiera dane z intencji, która została stworzona w poprzedniej aktywności.
     */
    private void loadData() {
        Intent intent = getActivity().getIntent();
        long setId = intent.getLongExtra("set", Constants.DEFAULT_SET_ID);
        mLesson = intent.getParcelableExtra("lesson");
        mSet = mDataManager.getSetById(setId);
        if (mLesson == null) {
            mLesson = new Lesson(ANY_LESSON_ID);
        }
        Log.d(LOG_TAG, "loadData end");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreteView");
        View view = inflater.inflate(R.layout.fragment_words_manager, container, false);
        setupControls(view);
        setListeners();
        return view;
    }

    private void setupControls(View view) {
        mFilterContainer = (ViewGroup) view.findViewById(R.id.filter_container);
        mSearchContainer = (ViewGroup) view.findViewById(R.id.search_container);
        mLoadingContainer = (ViewGroup) view.findViewById(R.id.loading_container);
        mSetSpinner = (Spinner) view.findViewById(R.id.set_spinner);
        mLessonSpinner = (Spinner) view.findViewById(R.id.lesson_spinner);
        mCategorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        mTypeSpinner = (Spinner) view.findViewById(R.id.words_spinner);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mSearchButton = (ImageButton) view.findViewById(R.id.search_button);
        mListView = (ListView) view.findViewById(R.id.list);
    }
    //region Listeners

    private void setListeners() {
        setSetSpinnerListener();
        setLessonSpinnerListener();
        setCategorySpinnerListener();
        setTypeSpinnerListener();
        setSearchButtonListeners();
        setListViewListeners();
        Log.d(LOG_TAG, "setListners end");
    }

    private void setSetSpinnerListener() {
        mSetSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSetSpinnerTouched = true;
                return false;
            }
        });
        mSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSetSpinnerTouched) {
                    mSet = mSetAdapter.getItem(position);
                    List<Lesson> lessons = getLessonsList(mSet);
                    mLessonAdapter.setData(lessons);
                    mLesson = mLessonAdapter.getItem(0);

                    setWordList(mSet, mLesson, mCategory, mType);
                    mSetSpinnerTouched = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSetSpinnerTouched = false;
            }
        });

        Log.d(LOG_TAG, "setSetSpinnerListener end");
    }

    private void setLessonSpinnerListener() {
        mLessonSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLessonSpinnerTouched = true;
                return false;
            }
        });

        mLessonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLessonSpinnerTouched) {
                    mLesson = mLessonAdapter.getItem(position);
                    setWordList(mSet, mLesson, mCategory, mType);
                    mLessonSpinnerTouched = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLessonSpinnerTouched = false;
            }
        });
    }

    private void setCategorySpinnerListener() {
        mCategorySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCategoryTouched = true;
                return false;
            }
        });
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCategoryTouched) {
                    mCategory = mCategoryAdapter.getItem(position);
                    setWordList(mSet, mLesson, mCategory, mType);
                    mCategoryTouched = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategoryTouched = false;
            }
        });
    }

    private void setTypeSpinnerListener() {
        mTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTypeSpinnerTouched = true;
                return false;
            }
        });
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTypeSpinnerTouched) {
                    mType = position; //można tak zrobić, ponieważ typy odpowiadają pozycji
                    setWordList(mSet, mLesson, mCategory, mType);
                    mTypeSpinnerTouched = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTypeSpinnerTouched = false;
            }
        });
    }

    private void setSearchButtonListeners() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String constraint = mSearchEditText.getText().toString();
                mWordAdapter.getFilter().filter(constraint);
            }
        });
    }

    private void setListViewListeners(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               seeWord(position);
            }
        });
    }

    //endregion

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setAdapters();
        setData();
    }

    private void setAdapters() {
        List<VocabularySet> sets = mDataManager.getSets();
        mSetAdapter = new SetSpinnerAdapter(getContext(), SET_ADAPTER_RESOURCE, sets);
        mSetSpinner.setAdapter(mSetAdapter);
        List<Lesson> lessons = getLessonsList(mSet);
        mLessonAdapter = new LessonSpinnerAdapter(getContext(), LESSON_ADAPTER_RESOURCE, lessons);
        mLessonSpinner.setAdapter(mLessonAdapter);
        List<Category> categories = getCategoriesList();
        mCategory = categories.get(0);
        mCategoryAdapter = new CategorySpinnerAdapter(getContext(), CATEGORY_ADAPTER_RESOURCE, categories);
        mCategorySpinner.setAdapter(mCategoryAdapter);
        String[] typesArray = getResources().getStringArray(R.array.words_types);
        mTypesAdapter = new ArrayAdapter(getContext(), TYPES_ADAPTER_RESOURCE, typesArray);
        mTypeSpinner.setAdapter(mTypesAdapter);
        setWordList(mSet, mLesson, mCategory, mType);

        Log.d(LOG_TAG, "setAdapters");
    }

    private List<Lesson> getLessonsList(VocabularySet set) {
        List<Lesson> list = mDataManager.getLessons(set);
        //dodajemy na początek pozycję, która oznacza dowolną lekcję
        Lesson anyLesson = new Lesson(ANY_LESSON_ID);
        list.add(0, anyLesson);
        return list;
    }

    private List<Category> getCategoriesList() {
        List<Category> list = mDataManager.getCategories();
        Category category = new Category(ANY_CATEGORY_ID);
        list.add(0, category);
        return list;
    }

    private void setWordList(VocabularySet set, Lesson lesson, Category category, int type) {
        WordsParams params = new WordsParams();
        params.setSet(set.getId());
        if (lesson.getId() != ANY_LESSON_ID) {
            params.setLessonId(lesson.getId());
        }

        if (category.getId() != ANY_CATEGORY_ID) {
            params.setCategoryId(category.getId());
        }
        params.setType(type);

        Log.d(LOG_TAG, "setWordList before task");
        DownloadWordAsyncTask task = new DownloadWordAsyncTask(mDataManager, getContext());
        //task.execute(params);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void setData() {
        int setSelection = mSetAdapter.getPosition(mSet);
        mSetSpinner.setSelection(setSelection);
        int lessonSelection = mLessonAdapter.getPosition(mLesson);
        mLessonSpinner.setSelection(lessonSelection);
        Log.d(LOG_TAG, "setData");
    }

    private final int SEE = R.string.see;
    private final int EDIT = R.string.edit;
    private final int ADD_TO_HARD = R.string.add_to_hard;
    private final int REMOVE_FROM_HARD = R.string.remove_from_hard;
    private final int DELETE = R.string.delete;

    @Override
    public void onCreateContextMenu(ContextMenu menu,View view, ContextMenu.ContextMenuInfo menuInfo){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        int position = info.position;
        Word word = mWordAdapter.getItem(position);
        assert word != null;
        menu.setHeaderTitle(word.getContent());
        menu.add(0,SEE,0, getString(SEE));
        menu.add(0,EDIT, 0, getString(EDIT));
        if(word.isSelected()){
            menu.add(0, REMOVE_FROM_HARD, 0, getString(REMOVE_FROM_HARD));
        } else {
            menu.add(0, ADD_TO_HARD, 0, getString(ADD_TO_HARD));
        }
        menu.add(0, DELETE, 0, getString(DELETE));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()){
            case SEE:
                seeWord(position); break;
            case EDIT:
                editWord(position); break;
            case ADD_TO_HARD:
                changeHard(position,true); break;
            case REMOVE_FROM_HARD:
                changeHard(position, false); break;
            case DELETE:
                showDeleteDialog(position); break;
        }
        return true;
    }

    private void seeWord(int position){
        Intent intent = new Intent(getContext(), LearningActivity.class);
        //przekazujemy do intencji listę z jednym słowkiem, ponieważ otwierana aktywność
        //oczekuje właśnie takiej formy danych, ponieważ korzystamy z aktywności
        //zaprojektowanej z pracą z listą słówek.
        ArrayList<Word> word = new ArrayList<>();
        word.add(mWordAdapter.getItem(position));
        intent.putParcelableArrayListExtra("items", word);
        intent.putExtra("set", mSet);
        intent.putExtra("learning", false);
        startActivity(intent);
    }

    private void editWord(int position){
        Intent intent = new Intent(getContext(), WordEditActivity.class);
        intent.putExtra("item", mWordAdapter.getItem(position));
        intent.putExtra("set", mSet);
        intent.putExtra("edit", true);
        mLastEditedPosition = position;
        startActivityForResult(intent, EDIT_WORD_REQUEST);
    }

    private void changeHard(int itemPosition, boolean isHard) {
        Word word = mWordAdapter.getItem(itemPosition);
        assert word != null;
        word.setSelected(isHard);
        mDataManager.updateWord(word);
        //TODO zobaczyć czy jeszcze jakieś wyświetlane trudnych jest jeszcze włączone
        //viewHolder.hardImageView.setVisibility(View.VISIBLE);
    }

    private void showDeleteDialog(int position) {
        new DeleteWordAlertDialog(getContext(), mWordAdapter.getItem(position), mDataManager).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_words_manager, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_button:
                startAddWordActivity();
                return true;
            case R.id.filter_button:
                changeFiltersVisibility();
                return true;
            case R.id.search_button:
                changeSearchVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAddWordActivity() {
        Intent intent = new Intent(getContext(), WordEditActivity.class);
        intent.putExtra("exit", true);
        intent.putExtra("set", mSet);
        startActivityForResult(intent, ADD_WORD_REQUEST);
    }

    private void changeFiltersVisibility() {
        if (mFilterContainer.getVisibility() == View.VISIBLE) {
            mFilterContainer.setVisibility(View.GONE);
        } else {
            mFilterContainer.setVisibility(View.VISIBLE);
        }
    }

    private void changeSearchVisibility() {
        if (mSearchContainer.getVisibility() == View.VISIBLE) {
            mSearchContainer.setVisibility(View.GONE);
        } else {
            mSearchContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_WORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(LOG_TAG, "onActivityResult ADD_WORD_REQUEST");
                //TODO na początku wybrano taką nieelegancką metodę
                //można to jeszcze zrobić, że zwracamy to jedno słówko a następnie sprawdzamy czy
                //ma taką samą kategorię, lekcje i typ jak aktualnie wyświetlany
                setWordList(mSet, mLesson, mCategory, mType);
            }
        }
        if (requestCode == EDIT_WORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                setWordList(mSet, mLesson, mCategory, mType);
            }
        }
    }

    private void startDeletingService(Word word, String setCatalog) {
        Intent intent = new Intent(getContext(), DeletingWordService.class);
        intent.putExtra("word", word);
        intent.putExtra("catalog", setCatalog);
        getActivity().startService(intent);
    }

    //region SetSpinnerAdapter
    private class SetSpinnerAdapter extends ArrayAdapter {

        private List<VocabularySet> mItems;
        private Context mContext;
        private int mResource;

        public SetSpinnerAdapter(Context context, int resource, List<VocabularySet> data) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mItems = data;
        }

        @Override
        public VocabularySet getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public int getPosition(Object object) {
            VocabularySet set = (VocabularySet) object;
            for (int i = 0; i < mItems.size(); ++i) {
                if (set.getId() == mItems.get(i).getId()) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getRowView(position, convertView);
        }

        private View getRowView(int position, View convertView) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rowView = inflater.inflate(mResource, null);
            }
            TextView textView = (TextView) rowView.findViewById(R.id.text_view);
            textView.setText(mItems.get(position).getName());
            return rowView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getRowView(position, convertView);
        }

    }
    //endregion

    //region WordAdapter
    private class WordAdapter extends ArrayAdapter implements Filterable {
        private List<Word> mItems;
        private List<Word> mFilteredItems;
        private Context mContext;
        private int mResource;
        private DataManager mDataManager;
        private ValueFilter mFilter;

        public WordAdapter(Context context, int resource, List<Word> data, DataManager dataManager) {
            super(context, resource, data);
            mContext = context;
            mResource = resource;
            mItems = data;
            mFilteredItems = data;
            mDataManager = dataManager;
        }

        public void setData(List<Word> data) {
            mItems.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFilteredItems.size();
        }

        @Override
        public Word getItem(int position) {
            return mFilteredItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mFilteredItems.get(position).getId();
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ValueFilter();
            }
            return mFilter;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
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
                String category = ResourceUtils.getString(mFilteredItems.get(position).getCategory().getName(), mContext);
                viewHolder.categoryTextView.setText(category);
            }
            if (mFilteredItems.get(position).getPartsOfSpeech() != null) {
                String partOfSpeech = mFilteredItems.get(position).getPartsOfSpeech().getName();
                String translatePart = ResourceUtils.getString(partOfSpeech, mContext);
                viewHolder.partOfSpeechTextView.setText(translatePart);
            }
            if (mFilteredItems.get(position).isSelected()) {
                viewHolder.hardImageView.setVisibility(View.VISIBLE);
            }
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.showContextMenuForChild(v);
                }
            });
            return rowView;
        }
        //endregion

        //region ValueFilter
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
                for (int word = 0; word < count; word++) {
                    contain = false;
                    itemContent = list.get(word).getContent();
                    for (int translation = 0; translation < list.get(word).getTranslations().size(); translation++) {
                        itemTranslation = list.get(word).getTranslations().get(translation).getContent();
                        if (itemTranslation.toLowerCase().contains(filterString)) {
                            contain = true;
                        }
                    }
                    if (itemContent.toLowerCase().contains(filterString)) {
                        contain = true;
                    }
                    if (contain) {
                        resultList.add(mItems.get(word));
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
        //endregion
    }

    private static class ViewHolder {
        public TextView contentTextView;
        public TextView translationTextView;
        public TextView categoryTextView;
        public TextView partOfSpeechTextView;
        public ImageView hardImageView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            contentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
            translationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
            categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
            partOfSpeechTextView = (TextView) view.findViewById(R.id.part_of_speech_text_view);
            hardImageView = (ImageView) view.findViewById(R.id.hard_image_view);
            actionButton = (ImageView) view.findViewById(R.id.menu_button);
        }
    }
    //endregion

    //region DownloadWordAsyncTask
    private class DownloadWordAsyncTask extends AsyncTask<WordsParams, Void, List<Word>> {
        private DataManager mDataManager;
        private Context mContext;

        public DownloadWordAsyncTask(DataManager dataManager, Context context) {
            mDataManager = dataManager;
            mContext = context;
            Log.d(LOG_TAG, "DownloadWordAsyncTask");
        }

        @Override
        protected void onPreExecute() {
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            if (mWordAdapter != null) {
                mWordAdapter.clear();
            }
            Log.d(LOG_TAG, "onPreExecute");
        }

        @Override
        protected List<Word> doInBackground(WordsParams... params) {
            Log.d(LOG_TAG, "doInBackground");
            return mDataManager.getWords(params[0], true);
        }

        @Override
        protected void onPostExecute(List<Word> result) {
            mLoadingContainer.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            if (mWordAdapter != null) {
                mWordAdapter.setData(result);
            } else {
                mWordAdapter = new WordAdapter(mContext, WORD_ADAPTER_RESOURCE, result, mDataManager);
                mListView.setAdapter(mWordAdapter);
                registerForContextMenu(mListView);
            }
        }
    }
    //endregion

    //region DeleteWordAlertDialog
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
                    startDeletingService(word, mSet.getCatalog());
                    mWordAdapter.remove(word);
                    mWordAdapter.notifyDataSetChanged();
                }
            });
            setButton(BUTTON_NEGATIVE, context.getString(R.string.no), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }
    //endregion
}
