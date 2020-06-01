package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.LessonsProgressAdapter;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.service.asyncTasks.MainInitializationValuesTask;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.models.params.FlashcardParams;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.FlashcardListDialogFragment;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.LearningOptionsDialog;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;
import com.dyszlewskiR.edu.scientling.service.preferences.Preferences;
import com.dyszlewskiR.edu.scientling.service.preferences.Settings;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.repetitions.RepetitionService;
import com.dyszlewskiR.edu.scientling.service.repetitions.SaveExerciseService;
import com.dyszlewskiR.edu.scientling.utils.Constants;
import com.dyszlewskiR.edu.scientling.utils.DateCalculator;
import com.dyszlewskiR.edu.scientling.utils.DateUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private final int SET_CHANGING_REQUEST = 875;
    private final int LOGIN_REQUEST = 7998;

    private ListView mLessonListView;
    private Button mAddWordButton;
    private Button mRepetitionButton;
    private Button mLearningButton;

    private NavigationView mNavigationDrawer;
    private TextView mLoginTextView;


    private ImageButton mMoreRepetitionsButton;
    private ImageButton mMoreLearningButton;
    private ProgressBar mSetProgressBar;

    private List<Lesson> mLessons;
    private DataManager mDataManager;

    private int mRepetitionCount;

    private boolean mIsLogged;
    private boolean mRepetitionReceiverRegistered;
    private boolean mSaveReceiverRegistered;

    private final BroadcastReceiver mRepetitionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //odbiornik po otrzymaniu komunikatu pobiera liczbę potówek
            //po zakończeniu zadania zostaje wyrejestrowany, ponieważ używany jest tylko raz
            //po uruchomieniu aktywności, zaraz po zakończeniu przesuwaniu zaległych powtórek
            if(intent.getAction().equals(RepetitionService.BORADCAST_ACTION)){
                Log.d(getClass().getSimpleName(), "BroadCastReceiver");
                startRepetitionAsyncTask();
                unregisterReceiver(this);
            }
        }
    };

    private final BroadcastReceiver mSaveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startRepetitionAsyncTask();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "onCreate");
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupControls();
        setListeners();

        registerRepetitionReceiver();
        Intent intent = new Intent(getApplicationContext(), RepetitionService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        Log.d(getClass().getSimpleName(), "onResume");
        setInitialValues();
        //setRepetitionNumber();
        setIsLogged();
        setLoggedNavigationItems(mIsLogged);


        //jeżeli usługa zapisująca materiał którego się uczyliśmy zakończyła swoje działanie
        //uruhamiamy działanie które pobierze libczę powtórek
        // w przeciwnym razie rejestrujemy odbiornik który będzie nasługiwał zakńczenia działania usługi
        //Jest to przydatne w sytuacji kiedy zapisywanie słówek do powtórki trwa dłużej niż uruchomienie
        //głównej aktywności
        if(!LingApplication.getInstance().isServiceRunning(SaveExerciseService.class)){
            Log.d(getClass().getSimpleName(), "Uruchomiono nowe zadanie");
            startRepetitionAsyncTask();
        } else {
            Log.d(getClass().getSimpleName(), "Zarejestrowano odbiornik");
            registerSaveReceiver();
        }

        super.onResume();
    }

    private void registerRepetitionReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RepetitionService.BORADCAST_ACTION);
        registerReceiver(mRepetitionReceiver, intentFilter);
        mRepetitionReceiverRegistered = true;
    }

    private void registerSaveReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SaveExerciseService.BORADCAST_ACTION);
        registerReceiver(mSaveReceiver, intentFilter);
        mSaveReceiverRegistered = true;
    }

    private void startRepetitionAsyncTask(){
        RepetitionAsyncTask task = new RepetitionAsyncTask();
        long setId = Settings.getCurrentSetId(getBaseContext());
        task.execute(setId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        /*if(mRepetitionReceiverRegistered){
            unregisterReceiver(mRepetitionReceiver);
            mRepetitionReceiverRegistered = false;
        }*/
        if(mSaveReceiverRegistered){
            unregisterReceiver(mSaveReceiver);
            mSaveReceiverRegistered = false;
        }

    }

    private void setIsLogged() {
        mIsLogged = LogPref.isLogged(getBaseContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupControls() {
        mNavigationDrawer = (NavigationView) findViewById(R.id.nav_view);
        View drawerHeader = mNavigationDrawer.getHeaderView(0);
        mLoginTextView = (TextView) drawerHeader.findViewById(R.id.drawer_text_view);
        mLessonListView = (ListView) findViewById(R.id.list);
        mAddWordButton = (Button) findViewById(R.id.add_word_button);
        mRepetitionButton = (Button) findViewById(R.id.repetition_button);
        mLearningButton = (Button) findViewById(R.id.learning_button);
        mMoreRepetitionsButton = (ImageButton) findViewById(R.id.more_repetition_button);
        mMoreLearningButton = (ImageButton) findViewById(R.id.more_learning_button);
        mSetProgressBar = (ProgressBar) findViewById(R.id.set_progress_bar);
    }

    private void setListeners() {
        mRepetitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRepetitionCount != 0) {
                    startDefaultRepetition();
                } else {
                    showNoRepetitionMessage();
                }
            }
        });
        mAddWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddWordActivity();
            }
        });
        mLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDefaultLearning();
            }
        });
        mMoreRepetitionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRepetitionActivity();
            }
        });
        mLessonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startLessonLearning(mLessons.get(position).getId());
            }
        });
        mMoreLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLearningDialog();
            }
        });
    }

    private void showNoRepetitionMessage() {
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        Snackbar snackbar = Snackbar.make(view, getString(R.string.no_repetitions_today), Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void showLearningDialog() {
        VocabularySet set = new VocabularySet(Settings.getCurrentSetId(getBaseContext()));
        LearningOptionsDialog dialog = new LearningOptionsDialog();
        dialog.setSetId(set.getId());
        dialog.show(getFragmentManager(), "LearningOptionsDialog");
    }

    private void setInitialValues() {
        mDataManager = ((LingApplication) getApplication()).getDataManager();
        new MainInitializationValuesTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDataManager);
    }

    public void onPostGetDataTask(VocabularySet set, List<Lesson> lessons) {
        if (set != null) {
            if(getSupportActionBar() != null) getSupportActionBar().setTitle(set.getName());
            //sprawdzamy czy progressbar jest widoczny. Jeżeli jest niewidoczy oznacza to że wcześniej
            //nie było ustawionego zestawu i kontrolki są ukryte
            //należy w takim więc pokazać te kontrolki i ukryć dodatkowe
            if (mSetProgressBar.getVisibility() == View.GONE) {
                setupVisibilityControls(false);
            }
        } else {
            if(getSupportActionBar() != null) getSupportActionBar().setTitle(getString(R.string.lack));
            setupVisibilityControls(true);
        }

        setLessonListValues(lessons);
        int setProgress = calculateSetProgress();
        mSetProgressBar.setProgress(setProgress);
    }

    /**
     * Metoda ustawiająca widoczność elementów związaych z ustawionym zestawem. Tymi elementami są
     * progres postepu zestawu, list lekcji zestawu i przyciski powtórek i nauki.
     * W przypadku braku ustawionego zestawu(co może mieć miejsce po instalacji aplikacji lub
     * po usunięciu obecnego zestawu) zostają wyświetlkowe przyciski pozwalające wybrać zestaw
     * lub stworzyć nowy. Metoda będize wykonywana w przypadku braku ustawionego zstawu o raz
     * po zmianie zestawu po stanie w którym nie było zestawu.
     */
    private void setupVisibilityControls(boolean isNoSet) {
        int standardControlsVisibility;
        if (isNoSet) {
            standardControlsVisibility = View.GONE;
        } else {
            standardControlsVisibility = View.VISIBLE;
        }

        LinearLayout noSetContainer = (LinearLayout)findViewById(R.id.no_set_container);
        if(isNoSet && noSetContainer.getVisibility() == View.GONE){
            Log.d(getClass().getSimpleName(), "Uwidaczniamy komuniat");
            noSetContainer.setVisibility(View.VISIBLE);
            Button chooseSetButton = (Button) findViewById(R.id.choose_set_button);
            Button createSetButton = (Button)findViewById(R.id.create_set_button);
            chooseSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSetChangingActivity();
                }
            });
            createSetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSetsManagerActivity();
                }
            });
        } else if(!isNoSet) {
            noSetContainer.setVisibility(View.GONE);
        }
        mSetProgressBar.setVisibility(standardControlsVisibility);
        mLessonListView.setVisibility(standardControlsVisibility);
        mAddWordButton.setVisibility(standardControlsVisibility);
        mRepetitionButton.setVisibility(standardControlsVisibility);
        mLearningButton.setVisibility(standardControlsVisibility);
        mMoreLearningButton.setVisibility(standardControlsVisibility);
        mMoreRepetitionsButton.setVisibility(standardControlsVisibility);

    }

    /**
     * Metoda ustawiająca widoczność elementów związanych z ustawionym zestawem znajdujących się w menu bocznych. Metoda wyłącza tylko
     * elementy dla tkórych trzeba mieć wybrany aktywny zestaw. Elementy zostaną ukryte w przypadku
     * nieustawienia aktywnego zestawu
     *
     * @param enableItems
     */
    private void setVisibleSetNavigationItems(boolean enableItems) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_flashcard).setEnabled(enableItems);
        navigationView.getMenu().findItem(R.id.nav_more_flashcards).setEnabled(enableItems);
        navigationView.getMenu().findItem(R.id.nav_manage_words).setEnabled(enableItems);
    }

    /**
     * Metoda ustawiająca widoczność elementów powiązanych z zalogowanym użytkownikiem znajdujących się w menu bocznym.
     *
     * @param isLogged określa czy użytkownnik jest zalogowany i czy należy pokazać odpowiednie elementy
     */
    private void setLoggedNavigationItems(boolean isLogged) {
        //TODO powstawiac tutaj zmienne
        mNavigationDrawer.getMenu().findItem(R.id.nav_login).setVisible(!isLogged);
        mNavigationDrawer.getMenu().findItem(R.id.nav_download_sets).setVisible(isLogged);
        mNavigationDrawer.getMenu().findItem(R.id.nav_edit_account).setVisible(isLogged);
        mNavigationDrawer.getMenu().findItem(R.id.nav_log_out).setVisible(isLogged);

        if (isLogged) {
            mLoginTextView.setText(LogPref.getLogin(this));
        } else {
            mLoginTextView.setText("");
        }
    }


    public void onPreGetDataTask() {
        mLessonListView.setAdapter(null);
        mLessonListView.deferNotifyDataSetChanged();
        getSupportActionBar().setTitle("");
    }

    private void setRepetitionNumber() {
        long setId = Settings.getCurrentSetId(getBaseContext());
        //int date = 160404;
        int date = DateCalculator.dateToInt(DateUtils.getTodayDate());
        int repetitionCount = mDataManager.getRepetitionsCount(setId, date);
        String repetitionButtonText = getString(R.string.repetitions);
        if (repetitionCount != 0) {
            repetitionButtonText += "(" + repetitionCount + ")";
        }
        mRepetitionButton.setText(repetitionButtonText);
        mRepetitionCount = repetitionCount;
    }

    private void setLessonListValues(List<Lesson> lessons) {
        mLessons = lessons;
        LessonsProgressAdapter adapter = new LessonsProgressAdapter(getBaseContext(), R.layout.item_lesson_progress, mLessons);
        mLessonListView.setAdapter(adapter);
    }

    private int calculateSetProgress() {
        int sum = 0;
        if (mLessons != null && !mLessons.isEmpty()) {
            for (int i = 0; i < mLessons.size(); i++) {
                sum += mLessons.get(i).getProgress();
            }
            return sum / mLessons.size();
        }
        return sum;
    }

    private void startDefaultRepetition() {
        Intent intent = new Intent(getBaseContext(), ExerciseActivity.class);
        intent.putExtra("set", Settings.getCurrentSetId(getBaseContext()));
        //intent.putExtra("repetitionDate", /*DateUtils.getCurrentMonth()*/160404);
        intent.putExtra("repetitionDate", DateCalculator.dateToInt(DateUtils.getTodayDate()));
        intent.putExtra("questions", Preferences.getNumberWordsInRepetitions(getBaseContext()));
        intent.putExtra("answers", Preferences.getNumberAnswers(getBaseContext()));
        Preferences.AnswerConnection connection = Preferences.getAnswerConnection(getBaseContext());
        if (connection == Preferences.AnswerConnection.LESSON) {
            intent.putExtra("fromLesson", true);
        } else {
            intent.putExtra("fromLesson", false);
        }
        if (connection == Preferences.AnswerConnection.CATEGORY) {
            intent.putExtra("fromCategory", true);
        } else {
            intent.putExtra("fromCategory", false);
        }
        intent.putExtra("exercise", Preferences.getDefaultExercise(getBaseContext()));
        intent.putExtra("direction", Preferences.getExerciseDirection(getBaseContext()));
        startActivity(intent);
    }

    private void startDefaultLearning() {
        Intent intent = getDefaultLearningIntent();
        startActivity(intent);
    }

    private void startLessonLearning(long lessonId) {
        Intent intent = getDefaultLearningIntent();
        intent.putExtra("lesson", lessonId);
        startActivity(intent);
    }

    private Intent getDefaultLearningIntent() {
        Intent intent = new Intent(getBaseContext(), LearningListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("set", Settings.getCurrentSetId(getBaseContext()));
        intent.putExtra("order", Preferences.getOrderLearning(getBaseContext()));
        intent.putExtra("limit", Preferences.getNumberWordsInLearning(getBaseContext()));
        return intent;
    }

    private void startRepetitionActivity() {
        Intent intent = new Intent(getBaseContext(), RepetitionsActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final int NAV_LOGIN = R.id.nav_login;
    private final int NAV_FLASHCARD = R.id.nav_flashcard;
    private final int NAV_MORE_FLASHCARD = R.id.nav_more_flashcards;
    private final int NAV_PROGRESS = R.id.nav_progress;
    private final int NAV_MANAGE_WORDS = R.id.nav_manage_words;
    private final int NAV_SETTINGS = R.id.nav_settings;
    private final int NAV_CHANGE_SET = R.id.nav_change_set;
    private final int NAV_MANAGE_SET = R.id.nav_manage_sets;
    private final int NAV_DOWNLOAD_SET = R.id.nav_download_sets;
    private final int NAV_EDIT_ACCOUNT = R.id.nav_edit_account;
    private final int NAV_LOGOUT = R.id.nav_log_out;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case NAV_LOGIN:
                startLoginActivity();
                break;
            case NAV_FLASHCARD:
                startFlashcardActivity();
                break;
            case NAV_MORE_FLASHCARD:
                FlashcardListDialogFragment dialogFragment = new FlashcardListDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("set", Settings.getCurrentSetId(getBaseContext()));
                bundle.putInt("limit", Preferences.getNumberWordsInLearning(getBaseContext()));
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "TAG");
                break;
            case NAV_PROGRESS:
                startProgressActivity();
                break;
            case NAV_MANAGE_WORDS:
                startManagerWordsActivity();
                break;
            case NAV_SETTINGS:
                startSettingsActivity();
                break;
            case NAV_CHANGE_SET:
                startSetChangingActivity();
                break;
            case NAV_MANAGE_SET:
                startSetsManagerActivity();
                break;
            case NAV_DOWNLOAD_SET:
                startDownloadSetsActivity();
                break;
            case NAV_EDIT_ACCOUNT:
                startEditAccount(); break;
            case NAV_LOGOUT:
                logout();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startEditAccount(){
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        intent.putExtra("login",LogPref.getLogin(getBaseContext()));
        startActivity(intent);
    }

    private void startProgressActivity(){
        Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    private void startAddWordActivity() {
        Intent intent = new Intent(getBaseContext(), WordEditActivity.class);
        startActivity(intent);
    }

    private void startFlashcardActivity() {
        Intent intent = new Intent(getBaseContext(), FlashcardActivity.class);
        intent.putExtra("set", Settings.getCurrentSetId(getBaseContext()));
        intent.putExtra("type", FlashcardParams.ChoiceType.LAST_LEARNED.getValue());
        intent.putExtra("limit", Preferences.getNumberFlashcards(getBaseContext()));
        startActivity(intent);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getBaseContext(), PreferenceActivity.class);
        startActivity(intent);
    }

    private void startSetChangingActivity() {
        Intent intent = new Intent(getBaseContext(), CurrentSetSelectionActivity.class);
        startActivityForResult(intent, SET_CHANGING_REQUEST);
    }

    private void startManagerWordsActivity() {
        Intent intent = new Intent(getBaseContext(), WordsManagerActivity.class);
        startActivity(intent);
    }

    private void startSetsManagerActivity() {
        Intent intent = new Intent(getBaseContext(), SetsManagerActivity.class);
        startActivityForResult(intent, SET_CHANGING_REQUEST);
    }

    private void startDownloadSetsActivity() {
        Intent intent = new Intent(getBaseContext(), DownloadSetsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        LogoutAlertDialog dialog = new LogoutAlertDialog(this);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_CHANGING_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(getClass().getSimpleName(), "onActivityResult");
                long newSetId = data.getLongExtra("result", Constants.DEFAULT_SET_ID);
                changeSet(newSetId);
            }
        }
    }

    private void changeSet(long newSetId) {
        Settings.setCurrentSetId(newSetId, getBaseContext());

    }

    private class LogoutAlertDialog extends AlertDialog {

        protected LogoutAlertDialog(@NonNull final Context context) {
            super(context);
            setTitle(getString(R.string.log_outing));
            setMessage(getString(R.string.sure_logout));
            setButton(BUTTON_NEGATIVE, getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            setButton(BUTTON_POSITIVE, getString(R.string.yes), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogPref.setLogged(false, context);
                    LogPref.setLogin(null, context);
                    LogPref.setPassword(null, context);
                    setLoggedNavigationItems(false);
                    dismiss();
                }
            });
        }
    }

    private class RepetitionAsyncTask extends AsyncTask<Long, Void, Integer>{

        @Override
        protected Integer doInBackground(Long... params) {
            long setId = params[0];
            int date = DateCalculator.dateToInt(DateUtils.getTodayDate());
            return mDataManager.getRepetitionsCount(setId, date);
        }

        @Override
        protected void onPostExecute(Integer result){
            String repetitionButtonText = getString(R.string.repetitions);
            if (result != 0) {
                repetitionButtonText += "(" + result + ")";
            }
            mRepetitionButton.setText(repetitionButtonText);
            mRepetitionCount = result;
        }
    }
}
