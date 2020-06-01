package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.SummaryLearningActivity;
import com.dyszlewskiR.edu.scientling.data.file.MediaFileSystem;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.service.net.values.MediaType;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.ISpeechCallback;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.SpeechPlayer;
import com.dyszlewskiR.edu.scientling.utils.ResourceUtils;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;
import com.dyszlewskiR.edu.scientling.presentation.widgets.SpeechButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LearningFragment extends Fragment implements ISpeechCallback {

    private final String LOG_TAG = "LearningFragment";

    private static final int SENTENCES = 0;
    private static final int IMAGE = 1;
    private static final int DEFINITION = 2;
    private static final int HINTS = 3;

    private List<Word> mWords;
    private LinearLayout mLayout;
    private RelativeLayout mFragmentContainer;
    private TextView mContentTextView;
    private TextView mTranslationTextView;
    private TextView mPartOfSpeechTextView;
    private TextView mCategoryTextView;
    private SpeechButton mSpeechButton;
    private FrameLayout mFragmentsFrame;
    private Button mPreviousButton;
    private Button mNextButton;

    private ImageButton[] mElementsButtons;
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private int mCurrentFragment;
    private Uri mRecordUri;
    private SpeechPlayer mSpeechPlayer;

    private VocabularySet mSet;

    private int mCurrentPosition;
    private boolean mLearningMode;

    public LearningFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        mWords = intent.getParcelableArrayListExtra("items");
        mLearningMode = intent.getBooleanExtra("learning", true);
        mSet = intent.getParcelableExtra("set");

        mFragmentManager = getFragmentManager();
        mCurrentPosition = 0;

        initSpeechPlayer();
        if(savedInstanceState != null){
            mCurrentFragment = savedInstanceState.getInt("fragment");
            mCurrentPosition = savedInstanceState.getInt("position");
        } else {
            mCurrentFragment = -1;
        }
        //setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt("fragment", mCurrentFragment);
        outState.putInt("position", mCurrentPosition);
        super.onSaveInstanceState(outState);
    }

    private void initSpeechPlayer() {
        mSpeechPlayer = new SpeechPlayer(getContext());
        mSpeechPlayer.setMediaCatalog(mSet.getCatalog());
        mSpeechPlayer.setLanguageCode(mSet.getLanguageL2().getCode());
        mSpeechPlayer.setCallback(this);
        Word word = mWords.get(mCurrentPosition);
        mSpeechPlayer.setValues(word.getContent(), word.getRecordName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning, container, false);
        setupControls(view);
        return view;
    }

    private void setupControls(View view) {
        mLayout = (LinearLayout) view.findViewById(R.id.layouts_container);
        mFragmentContainer = (RelativeLayout) view.findViewById(R.id.fragment_container);
        mContentTextView = (TextView) view.findViewById(R.id.word_content_text_view);
        mTranslationTextView = (TextView) view.findViewById(R.id.word_translation_text_view);
        mPartOfSpeechTextView = (TextView) view.findViewById(R.id.word_part_of_speech_text_view);
        mCategoryTextView = (TextView) view.findViewById(R.id.word_category_text_view);
        mSpeechButton = (SpeechButton) view.findViewById(R.id.speech_button);
        mFragmentsFrame = (FrameLayout) view.findViewById(R.id.fragment_frame_layout);

        mElementsButtons = new ImageButton[4];
        mElementsButtons[SENTENCES] = (ImageButton) view.findViewById(R.id.sentences_button);
        mElementsButtons[DEFINITION] = (ImageButton) view.findViewById(R.id.definition_button);
        mElementsButtons[IMAGE] = (ImageButton) view.findViewById(R.id.image_button);
        mElementsButtons[HINTS] = (ImageButton) view.findViewById(R.id.hints_button);

        mPreviousButton = (Button) view.findViewById(R.id.previous_button);
        mNextButton = (Button) view.findViewById(R.id.next_button);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fillComponents(mCurrentPosition);
        setListeners();
        if (!mLearningMode) {
            hideButtons();
        }
    }

    private void hideButtons() {
        mNextButton.setVisibility(View.GONE);
        mPreviousButton.setVisibility(View.GONE);
    }

    /**
     * Metoda która zwraca numer fragmentu który zostanie załadowany do FrameLayout.
     * Metoda sprawdza, czy potrzebne wartości do stworzenia fragmentu są potrzebne,
     * i zwraca numer pierwszego fragmentu, który będzie miał potrzebnyą wartość.
     *
     * @return numer fragmentu który zostanie umieszczony w miejscu Framelayout
     */
    private int getNumberExistingFragment() {
        for (int i = 0; i < mElementsButtons.length; i++) {
            if (mElementsButtons[i].getVisibility() == View.VISIBLE) {
                return i;
            }
        }
        return -1;
    }

    private void setListeners() {
        for (int i = 0; i < mElementsButtons.length; i++) {
            final int element = i;
            mElementsButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentFragment != element) {
                        replaceFragment(element, mCurrentPosition);
                    }
                }
            });
        }

        mSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWord();
            }
        });
    }

    private void speech() {
        if (!mSpeechPlayer.isInit()) {
            mSpeechButton.setState(SpeechButton.LOADING);
        }
        try {
            mSpeechPlayer.speak();
        } catch (IOException e) {
            Toast.makeText(getContext(), getString(R.string.play_record_error), Toast.LENGTH_SHORT);
        }
    }

    private void nextWord() {
        if (mCurrentPosition != mWords.size() - 1) {
            mCurrentPosition++;
            startAnimation(false);
        } else {
            //resetColorButtons();
            startSummaryActivity();
        }
    }

    private void startAnimation(boolean previousAnimation) {
        final Animation animationIn = AnimationUtils.makeInAnimation(getActivity(), previousAnimation);
        Animation animationOut = AnimationUtils.makeOutAnimation(getActivity(), previousAnimation);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //po zakończeniu pierwszej animacji(znikanie zawartości ekranu) ustawiamy kolejne słówko
                //oraz uruchamiamy drugą animację (animację pojawienia się zawartości)
                setWord();
                mLayout.startAnimation(animationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mLayout.startAnimation(animationOut);
    }

    private void setWord() {
        Word word = mWords.get(mCurrentPosition);
        mSpeechPlayer.setValues(word.getContent(), word.getRecordName());
        fillComponents(mCurrentPosition);
    }

    private void startSummaryActivity() {
        Intent intent = new Intent(getActivity(), SummaryLearningActivity.class);
        ArrayList<Word> arrayList = new ArrayList<>(mWords);
        intent.putParcelableArrayListExtra("items", arrayList);
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void previousWord() {
        mCurrentPosition--;
        startAnimation(true);
    }

    private void fillComponents(int position) {
        setTexts(position);
        setVisibilityButtons(position);
        int newFragmentNumber;
        if(mCurrentFragment < 0){
            newFragmentNumber = getNumberExistingFragment();
        } else {
            newFragmentNumber = mCurrentFragment;
        }
        replaceFragment(newFragmentNumber, position);

        mRecordUri = MediaFileSystem.getMediaUri(mWords.get(position).getRecordName(), mSet.getCatalog(), MediaType.RECORDS, getContext());
    }

    /**
     * Metoda ustawiająca wartości tekstowe w pola dotyczące słówka w aktywności
     * @param position numer aktualnego słowka
     */
    private void setTexts(int position) {
        mContentTextView.setText(mWords.get(position).getContent());
        String translations = TranslationListConverter.toString(mWords.get(position).getTranslations());
        mTranslationTextView.setText(translations);
        if (mWords.get(position).getPartsOfSpeech() != null) {
            mPartOfSpeechTextView.setText(mWords.get(position).getPartsOfSpeech().getName());
        }
        if (mWords.get(position).getCategory() != null) {
            String category = ResourceUtils.getString(mWords.get(position).getCategory().getName(), getContext());
            mCategoryTextView.setText(category);
        }
    }

    /**
     * Metoda sprawdza czy słówko zawiera elementy danego typu. Jeżeli ich nie ma ukrywa przycisk
     * odpowiedzialny za ich pokazywanie.
     * Dostępne przyciski:
     * - zdania
     * - definicja
     * - obrazek
     * - podpowiedzi
     * - nestępne pytanie
     * - poprzednie pytanie
     *
     * @param position pozycja aktualnego słówka
     */
    private void setVisibilityButtons(int position) {
        Word word = mWords.get(position);
        mElementsButtons[SENTENCES].setVisibility(word.hasSentences() ? View.VISIBLE : View.GONE);
        mElementsButtons[DEFINITION].setVisibility(word.hasDefinition() ? View.VISIBLE : View.GONE);
        boolean hasImages = MediaFileSystem.checkMediaExist(mWords.get(position).getImageName(), mSet.getCatalog(), MediaType.IMAGES, getContext());
        mElementsButtons[IMAGE].setVisibility(hasImages ? View.VISIBLE : View.GONE);
        mElementsButtons[HINTS].setVisibility(word.hasHints() ? View.VISIBLE : View.GONE);

        mPreviousButton.setVisibility(mCurrentPosition != 0 ? View.VISIBLE : View.INVISIBLE);
        int lastPosition = mWords.size() - 1;
        mNextButton.setText(mCurrentFragment != lastPosition-1 ? getString(R.string.next_item) : getString(R.string.finish));
    }

    private void replaceFragment(int fragmentNumber, int position) {
        Fragment fragment = getFragment(fragmentNumber, position);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_frame_layout, fragment);
            fragmentTransaction.commit();
            mCurrentFragment = fragmentNumber;
            mFragment = fragment;
            changeButtonsColor(fragmentNumber);
        }
    }

    private Fragment getFragment(int fragmentNumber, int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (fragmentNumber) {
            case SENTENCES:
                fragment = new SentencesPagerFragment();
                bundle.putParcelableArrayList("items", mWords.get(position).getSentences());
                break;
            case DEFINITION:
                fragment = new DefinitionPagerFragment();
                bundle.putParcelable("item", mWords.get(position).getDefinition());
                break;
            case HINTS:
                fragment = new HintsPagerFragment();
                bundle.putParcelableArrayList("items", mWords.get(position).getHints());
                break;
            case IMAGE:
                fragment = new ImagePagerFragment();
                Uri uri = MediaFileSystem.getMediaUri(mWords.get(position).getImageName(), mSet.getCatalog(), MediaType.IMAGES, getContext());
                bundle.putParcelable("image", uri);
                break;
        }
        if (fragment != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    private void changeButtonsColor(int selectedButton) {
        for (int i = 0; i < mElementsButtons.length; i++) {
            mElementsButtons[i].setSelected(i==selectedButton);
        }
    }

    @Override
    public void onDestroy() {
        mSpeechPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onSpeechStart() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeechButton.setState(SpeechButton.PLAYING);
            }
        });

    }

    @Override
    public void onSpeechCompleted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeechButton.setState(SpeechButton.NORMAL);
            }
        });
    }
}
