package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.HintsListActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.LessonSelectionActivity;
import com.dyszlewskiR.edu.scientling.presentation.activity.SentencesListActivity;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.service.asyncTasks.SaveWordAsyncTask;
import com.dyszlewskiR.edu.scientling.data.file.FileNameCreator;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.Category;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;
import com.dyszlewskiR.edu.scientling.models.entity.Hint;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.PartOfSpeech;
import com.dyszlewskiR.edu.scientling.models.entity.Sentence;
import com.dyszlewskiR.edu.scientling.models.entity.Translation;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.models.params.SaveWordParams;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.CategoryDialog;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.DefinitionDialog;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.DifficultDialog;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.ImageDialog;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.PartOfSpeechDialog;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.RecordDialog;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;

public class WordEditFragment extends Fragment implements DefinitionDialog.Callback, PartOfSpeechDialog.Callback,
        DifficultDialog.Callback, CategoryDialog.Callback, RecordDialog.Callback, ImageDialog.Callback, SaveWordAsyncTask.Callback {

    //region Constants
    private final String TAG = "WordEditFragment";
    private final int EMPTY_CATEGORY_STRING = R.string.lack;

    private final int SENTENCE_REQUEST = 7968;
    private final int HINTS_REQUEST = 4468;
    private final int LESSON_REQUEST = 5377;

    //endregion

    //region Variables
    private View mRootView;
    /**
     * Kontener przechowujący dodatkowe informacje o słóku, domyślnie jest ukryty, aby nie pokazywać
     * użytkownikowi zbyt wielu kontrolek. Jeśli będą mu potrzebne odkrywa je klikając na mMoreButton
     */
    private View mAdvancedContainer;
    /**
     * Treść słóka
     */
    private EditText mWordEditText;
    /**
     * Tłumaczenie słówka. Jeśli słowko ma wiele tłumaczeń należy je odzielić znakiem określonym w
     * Constants.SEPARATOR
     */
    private EditText mTranslationEditText;
    /**
     * Przycisk do wyboru lekcji do jakiej zostanie przypisane słówko. Po naciśnięciu otwiera się
     * aktywność z listą lekcji.
     */
    private Button mLessonButton;
    /**
     * Przycisk definicji. Po naciśnięciu wyskakuje okno dialogowe w którym znajdują się kontrolki
     * do wpisania definicji i tłumaczenie definicji
     */
    private Button mDefinitionButton;
    /**
     * Przycik wybotu części mowy słówka. Po naciśnięciu wyskakuje lista części mowy. W tym wypadku
     * można zastosować listę, ponieważ liczba wartośći będzie zawsze stałą, ponieważ nie ma możliwościo
     * dodania części mowy
     */
    private Button mPartOfSpeechButton;
    /**
     * Przycisk wyboru trudności włóka. Po naciśnięciu wyskakuje lista poziomów trudności(wartości od 1
     * do 5 oraz brak poziomu). Można zastosować listę, ponieważ wartość poziomów trudności jest stała
     */
    private Button mDifficultButton;
    /**
     * Przycisk wyboru kategorii. Po naciśnięciu uruchamiana jest nowa aktywność (StartActivityForResult).
     * W nowej aktywności będzie możliwość wyboru istniejącej kategorii, a także stworzenie nowej.
     * Z tego powodu nie stosujemy tutaj listy. Implementacja dodawania nowej kategorii korzystając
     * z listy mogłoby być dość uciążliwe
     */
    private Button mCategoryButton;
    /**
     * Przycisk dodania przyskładowych zdań dla danego słówka. Po naciśnięciu uruchamiana jest akrtywność
     * która powinna zwracać w rezultacie listę obiektów Sentences.
     */
    private Button mSentencesButton;
    /**
     * Przycisk wybory podpowiedzi. Po naciśnięciu uruchamiana jest nowa aktywność (StartActivityForResult).
     * W nowej aktywności będzie możliwość wyboru istniejącej podpowiedzi, a także stworzenie nowej.
     */
    private Button mHintsButton;
    /**
     * Przycisk wyboru obrazka dla tworzonego slówka. Po naciśnięciu powinno pokazać się okno dialogowe
     * w którym będzie możliwość wybrania istniejącego obrazka lub skorzystanie z aparatu, aby
     * stworzyć nowe zdjęcie
     */
    private Button mImageButton;
    private ImageView mImageView;
    /**
     * Przycisk wyboru nagrania dla tworzonego słowka. Po naciśnięciu powinno pokazać się okno dialogowe
     * w którym będzie możliwośc wyboru istniejącego pliku dźwiękowego, a także skorzystania
     * z urządzenia nagrywajacego aby nagrać dźwięk
     */
    private Button mRecordButton;
    /**
     * Przycisk rozwijający i zwijający kontener z dodatkowymi kontrolkami. Domyślnie kontener jest ukryty.
     */
    private ImageView mMoreButton;
    /**
     * Przycisk zapisujący tworzone słówko. Po naciśnięciu powinna nastąpić walidacja(słówko i tłumaczenie)
     * a nastepnie powinien rozpocząć się proces zapisu
     */
    private Button mSaveButton;

    /**
     * Zestaw do którego będziemy dodawać słówko
     */
    private VocabularySet mSet;

    private Word mWord;
    private Uri mImageUri;
    private Uri mRecordUri;
    private boolean mEdit;
    private Uri mOldImageUri;
    private Uri mOldRecordUri;
    /**
     * Określa czy po dodaniu lub aktualizacji nowego słówka aktywność ma zostać zamknięta
     */
    private boolean mExitAfter;
    private View mFragmentView;

    /**
     * Zmienna określająca czy zakończenie aktywności nastąpiło ze względu zmiany konfiguracji(np obrotu ekranu)
     */
    private boolean mIsStateChange;
    private DataManager mDataManager;

    //endregion

    //region LifeCycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWord = new Word();

        mDataManager = LingApplication.getInstance().getDataManager();
        loadData();
        setRetainInstance(true);
    }

    private void loadData() {
        Intent intent = getActivity().getIntent();
        mEdit = intent.getBooleanExtra("edit", false);
        mExitAfter = intent.getBooleanExtra("exit", false);

        if (mEdit) {
            /*long wordId  = intent.getLongExtra("item", 0);
            DataManager dataManager = ((LingApplication)getActivity().getApplication()).getDataManager();
            mWord = dataManager.getWord(wordId);*/
            mWord = intent.getParcelableExtra("item");
            mExitAfter = true; //przy edycji nie ma sensu czyścić aktywności, laeży ją zamknąć
        }
        mSet = intent.getParcelableExtra("set");
        if (mSet == null) {
            long setId = LingApplication.getInstance().getCurrentSetId();
            mSet = mDataManager.getSetById(setId);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (!mIsStateChange) {
            Log.d(TAG, "clearCache");
            ImageDialog.clearCache(getContext());
            RecordDialog.clearCache(getContext());
        }
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_edit, container, false);
        setupControls(view);
        mFragmentView = view;
        return view;
    }

    private void setupControls(View view) {
        mRootView = view;
        mWordEditText = (EditText) view.findViewById(R.id.word_edit_text);
        mTranslationEditText = (EditText) view.findViewById(R.id.translation_edit_text);
        mMoreButton = (ImageView) view.findViewById(R.id.more_image_button);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setData();
        setListeners();
        if(mAdvancedContainer != null){
            loadAdvancedContainer();
        }
    }

    /**
     * Ustawianie wartości edytowanego słówka. Metoda jest wykonywana tylko wtedy kiedy edytujemy istniejące
     * już słówko.
     */
    private void setData() {
        if (mEdit) {
            mWordEditText.setText(mWord.getContent());
            String translations = TranslationListConverter.toString(mWord.getTranslations());
            mTranslationEditText.setText(translations);
        }

    }

    private void setListeners() {
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MoreButton Click");
                setVisibilityAdvancedContainer();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SaveButton Click");
                saveWord();
            }
        });
    }

    //endregion

    //region SaveWord
    private void saveWord() {
        if (validate()) {
            SaveWordAsyncTask task = new SaveWordAsyncTask(mDataManager, getActivity());
            SaveWordParams params = new SaveWordParams();
            params.setEdit(mEdit);
            Word word = getWord(mSet.getCatalog());


            //wstawianie plików do zapisu i do usunięcia
            if(mOldImageUri==null ){
                //jeżeli nie było poprzedniej wartości obrazka wstawiamy obecną, nawet jeśli jest null
                //ponieważ jeżeli będzie null nie zostanie zapisana
                if(mImageUri != null){
                    params.setImageToInsert(mImageUri);
                    String imageName = FileNameCreator.getImageName(word.getContent(), mSet.getCatalog(), getContext());
                    word.setImageName(imageName);
                }
            } else if(mImageUri == null){
                //jeżeli obrazek był wstawiony i został usunięty
                params.setImageToDelete(mOldImageUri);
                word.setImageName(null);
            } else if(!mOldImageUri.equals(mImageUri)){
                //jeżeli żadna z wartości nie jest nullem, obrazek został zmieniony na inny
                params.setImageToDelete(mOldImageUri);
                params.setImageToInsert(mImageUri);
                //tutaj nie zmieniamy nazwy obrazka
            }

            if(mOldRecordUri == null){
                if (mRecordUri != null) {
                    params.setRecordToInsert(mRecordUri);
                    String recordName = FileNameCreator.getRecordName(word.getContent(), mSet.getCatalog(), getContext());
                    word.setRecordName(recordName);
                }
            } else if(mRecordUri == null) {
                params.setRecordToDelete(mOldRecordUri);
                word.setRecordName(null);
            } else if(!mOldRecordUri.equals(mRecordUri)){
                params.setRecordToDelete(mOldRecordUri);
                params.setRecordToInsert(mRecordUri);
                //tutaj nie zmieniamy nazwy nagrania
            }
            params.setWord(getWord(mSet.getCatalog()));
            params.setSet(mSet);
            task.setCallback(this);
            task.execute(params);
        }
    }

    private Word getWord(String catalog) {
        Word word = mWord;
        word.setContent(mWordEditText.getText().toString());
        word.setTranslations(getTranslations());
        word.setOwn(true);
        if (word.getLessonId() == 0) { //jeśli użytkownik nie ustawiał słówka
            Lesson lesson = mDataManager.getDefaultLesson(mSet.getId());
            word.setLessonId(lesson.getId());
        }
        //TODO przerobić na jakieś domyślne nazwy rozszerzeń, zobaczyć czy lepiej jest z kropką czy bez
        /*if (mImageUri != null) {
            String imageName = FileNameCreator.getImageName(word.getContent(), catalog, getContext());
            word.setImageName(imageName);
        }
        if (mRecordUri != null) {
            String recordName = FileNameCreator.getRecordName(word.getContent(), catalog, getContext());
            word.setRecordName(recordName);
        }*/

        return word;
    }

    private ArrayList<Translation> getTranslations() {
        String[] translations = mTranslationEditText.getText().toString().split(Constants.TRANSLATION_SEPARATOR);
        Translation translation;
        ArrayList<Translation> translationsList = new ArrayList<>();
        for (String s : translations) {
            translation = new Translation();
            translation.setContent(s);
            translationsList.add(translation);
        }
        return translationsList;
    }

    private boolean validate() {
        boolean correct = true;
        if (mWordEditText.getText().toString().equals("")) {
            mWordEditText.setError(getString(R.string.field_not_empty));
            correct = false;
        }
        if (mTranslationEditText.getText().toString().equals("")) {
            mTranslationEditText.setError(getString(R.string.field_not_empty));
            correct = false;
        }
        return correct;
    }

    /**
     * Metoda czyszcząca wszystkie pola w aktywności. metoda będize wywoływana po zakończeniu zapisywania
     * słówka w klasie SaveWordAsyncTask
     */
    public void clear() {
        mWord = new Word();
        mImageUri = null;
        mRecordUri = null;

        mWordEditText.setText("");
        mTranslationEditText.setText("");
        mLessonButton.setText("");
        mDefinitionButton.setText("");
        mPartOfSpeechButton.setText("");
        mDifficultButton.setText("");
        mCategoryButton.setText("");
        mSentencesButton.setText("");
        mHintsButton.setText("");
        mImageButton.setText("");
        mRecordButton.setText("");
    }

    //endregion

    //region ButtonsClicks
    private void startSentenceActivity() {
        Intent intent = new Intent(getContext(), SentencesListActivity.class);
        if (mWord.getSentences() != null) {
            intent.putParcelableArrayListExtra("list", mWord.getSentences());
        }
        startActivityForResult(intent, SENTENCE_REQUEST);
    }

    private void startHintsActivity() {
        Intent intent = new Intent(getContext(), HintsListActivity.class);
        if (mWord.getHints() != null) {
            intent.putParcelableArrayListExtra("list", mWord.getHints());
        }
        startActivityForResult(intent, HINTS_REQUEST);
    }

    private void startLessonActivity() {
        Intent intent = new Intent(getContext(), LessonSelectionActivity.class);
        intent.putExtra("set", mSet.getId());
        startActivityForResult(intent, LESSON_REQUEST);
    }

    private void setVisibilityAdvancedContainer() {
        if(mAdvancedContainer == null){
            loadAdvancedContainer();
        } else {
            if (mAdvancedContainer.getVisibility() == View.VISIBLE) {
                mAdvancedContainer.setVisibility(View.GONE);
                mMoreButton.setImageResource(R.drawable.ic_more);
            } else {
                mAdvancedContainer.setVisibility(View.VISIBLE);
                mMoreButton.setImageResource(R.drawable.ic_less);
            }
        }
    }

    private void loadAdvancedContainer(){
        ViewStub viewStub = (ViewStub)mRootView.findViewById(R.id.more_container);
        mAdvancedContainer = viewStub.inflate();
        setupControlsAdvancedContainer(mAdvancedContainer);
        setDataAdvancedContainer();
        setAdvancedListener();
    }

    private void setupControlsAdvancedContainer(View view){
        mLessonButton = (Button) view.findViewById(R.id.lesson_button);
        mDefinitionButton = (Button) view.findViewById(R.id.definition_button);
        mPartOfSpeechButton = (Button) view.findViewById(R.id.part_of_speech_button);
        mDifficultButton = (Button) view.findViewById(R.id.difficult_button);
        mCategoryButton = (Button) view.findViewById(R.id.category_button);
        mSentencesButton = (Button) view.findViewById(R.id.sentences_button);
        mHintsButton = (Button) view.findViewById(R.id.hints_button);
        mImageButton = (Button) view.findViewById(R.id.image_button);
        mImageView = (ImageView) view.findViewById(R.id.image_image_view);
        mRecordButton = (Button) view.findViewById(R.id.record_button);
    }

    private void setDataAdvancedContainer(){
        if(mEdit){
            Lesson lesson = mDataManager.getLessonById(mWord.getLessonId());
            setLessonButton(lesson);
            setDefinitionButton(mWord.getDefinition());
            setPartOfSpeechButton(mWord.getPartsOfSpeech());
            setDifficultButton(mWord.getDifficult());
            setCategoryButton(mWord.getCategory());
            if (mWord.getSentences() != null)
                setSentenceButton(mWord.getSentences().size());
            if (mWord.getHints() != null)
                setHintButton(mWord.getHints().size());
            mImageUri = MediaFileSystem.getMediaUri(mWord.getImageName(), mSet.getCatalog(), MediaType.IMAGES, getContext());
            if(mImageUri != null){
                mOldImageUri = Uri.parse(mImageUri.toString());
            }

            setImageButton(mImageUri);
            mRecordUri = MediaFileSystem.getMediaUri(mWord.getRecordName(), mSet.getCatalog(),MediaType.RECORDS, getContext());
            if(mRecordUri != null){
                mOldRecordUri = Uri.parse(mRecordUri.toString());
            }

            setRecordButton(mRecordUri);
        }
    }

    private void setAdvancedListener(){
        mLessonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LessonButton Click");
                startLessonActivity();
            }
        });
        mDefinitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "DefinitionButton Click");
                DefinitionDialog dialog = new DefinitionDialog();
                dialog.setCallback(WordEditFragment.this);
                dialog.setDefinition(mWord.getDefinition());
                dialog.show(getFragmentManager(), "DefinitionDialog");
            }
        });
        mPartOfSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "PartOfSpeechButton Click");
                PartOfSpeechDialog dialog = new PartOfSpeechDialog();
                dialog.setCallback(WordEditFragment.this);
                dialog.show(getFragmentManager(), "PartOfSpeechDialog");
            }
        });
        mDifficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "DifficultButton Click");
                DifficultDialog dialog = new DifficultDialog();
                dialog.setCallback(WordEditFragment.this);
                dialog.show(getFragmentManager(), "DifficultDialog");
            }
        });
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CategoryButton Click");
                CategoryDialog dialog = new CategoryDialog();
                dialog.setCallback(WordEditFragment.this);
                dialog.show(getFragmentManager(), "CateogryDialog");

            }
        });
        mSentencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SentencesButton Click");
                startSentenceActivity();
            }
        });
        mHintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "HintsButton Click");
                startHintsActivity();
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ImageButton Click");
                ImageDialog dialog = new ImageDialog();
                dialog.setCallback(WordEditFragment.this);
                if (mImageUri != null) {
                    dialog.setImageUri(mImageUri);
                }
                dialog.show(getFragmentManager(), "ImageDialog");
            }
        });
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "RecordButton Click");
                RecordDialog dialog = new RecordDialog();
                dialog.setCallback(WordEditFragment.this);
                if (mRecordUri != null) {
                    dialog.setRecordUri(mRecordUri);
                }
                //TODO setRecord
                dialog.show(getFragmentManager(), "RecordDialog");

            }
        });
    }
    //endregion

    //region ActivityResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SENTENCE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<Sentence> sentences = data.getParcelableArrayListExtra("result");
                mWord.setSentences(sentences);
                setSentenceButton(sentences.size());
            }
        }
        if (requestCode == HINTS_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<Hint> hints = data.getParcelableArrayListExtra("result");
                mWord.setHints(hints);
                setHintButton(hints.size());
            }
        }

        if (requestCode == LESSON_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Lesson lesson = data.getParcelableExtra("result");
                mWord.setLessonId(lesson.getId());
                setLessonButton(lesson);
            }
        }
    }


    //endregion

    //region SetControlsValue

    private void setSentenceButton(int elementsCount) {
        String text = "";
        if (elementsCount > 0) {
            text = getString(R.string.sentence) + " (" + elementsCount + " )";
        }
        mSentencesButton.setText(text);
    }

    private void setHintButton(int elementsCount) {
        String text = "";
        if (elementsCount > 0) {
            text = getString(R.string.hint) + " (" + elementsCount + ")";
        }
        mHintsButton.setText(text);
    }

    private void setLessonButton(Lesson lesson) {
        if (lesson != null && lesson.getNumber() != Constants.DEFAULT_LESSON_NUMBER) {
            mLessonButton.setText(lesson.getName());
        } else {
            mLessonButton.setText("");
        }
    }

    private void setDefinitionButton(Definition definition) {
        if (definition != null) {
            mDefinitionButton.setText(definition.getContent() + "\n" + definition.getTranslation());
        } else {
            mDefinitionButton.setText("");
            mDefinitionButton.setHint(getString(R.string.definition));
        }
    }

    private void setPartOfSpeechButton(PartOfSpeech partOfSpeech) {
        if (partOfSpeech != null) {

            mPartOfSpeechButton.setText(ResourceUtils.getString(partOfSpeech.getName(), getContext()));
        } else {
            mPartOfSpeechButton.setText("");
        }
    }

    private void setDifficultButton(int difficult) {
        if (difficult != 0) {
            mDifficultButton.setText(String.valueOf(difficult));
        } else {

            mDifficultButton.setText("");
        }
    }

    private void setCategoryButton(Category category) {
        if (category != null && category.getId() > 0) {
            mCategoryButton.setText(ResourceUtils.getString(category.getName(), getContext()));
        } else {
            mCategoryButton.setText("");
        }
    }

    private void setRecordButton(Uri uri) {
        if (uri != null) {
            mRecordButton.setText(getString(R.string.record));
        } else {
            mRecordButton.setText("");
        }
    }

    private void setImageButton(Uri uri) {
        if (uri != null) {
            mImageButton.setText(getString(R.string.image));
        } else {
            mImageButton.setText("");
        }
    }
    //endregion

    //region Callbacks
    @Override
    public void onDefinitionDialogOk(Definition definition) {
        Log.d(TAG, "onDefinitionDialogOk");
        setDefinitionButton(definition);
        mWord.setDefinition(definition);
    }

    @Override
    public void onPartOfSpeechOk(PartOfSpeech partOfSpeech) {
        setPartOfSpeechButton(partOfSpeech);
        mWord.setPartsOfSpeech(partOfSpeech);
    }

    @Override
    public void onDifficultOk(int difficult) {
        setDifficultButton(difficult);
        setDifficultValue(difficult);
    }

    private void setDifficultValue(int difficult) {
        if (difficult > 0) {
            mWord.setDifficult((byte) difficult);
        } else {
            //TODO zobaczyć jaka wartość odpowiada braku wartości
            mWord.setDifficult((byte) -1);
        }
    }

    /**
     * Metoda zwrotna dialogu kategorii. Do dialogu została sztucznie wprowadzona pozycja "brak"
     * z powodu łatwiejszego filtrowania. Pozycja ta ma id -1. Jeżeli zostanie zwrócona ta pozycja
     * ustawiamy kategorię na null i usuwamy tekst, aby pokazał się ustawiony hint
     *
     * @param category
     */
    @Override
    public void onCategoryOk(Category category) {
        setCategoryButton(category);
        setCategoryValue(category);
    }

    private void setCategoryValue(Category category) {
        if (category.getId() > 0) {
            mWord.setCategory(category);
        } else {
            mWord.setCategory(null);
        }
    }

    @Override
    public void onRecordOk(Uri recordUri) {
        mRecordUri = recordUri;
        setRecordButton(recordUri);
    }

    @Override
    public void onImageOk(Uri imageUri) {
        mImageUri = imageUri;
        setImageButton(imageUri);
    }

    @Override
    public void onSaveCompleted(Word result) {

        if (mExitAfter) {
            Intent intent = new Intent();
            intent.putExtra("result", result);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            clear();
        }
        Snackbar snackbar = Snackbar.make(mFragmentView, getString(R.string.save_word_success), Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
    //endregion
}

