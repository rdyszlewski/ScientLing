package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.app.LingApplication;
import com.dyszlewskiR.edu.scientling.models.entity.VocabularySet;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.ExercisesListDialogFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.ChoosingExerciseFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.ExerciseFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.KnowExerciseFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.SummaryExerciseFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.WritingExerciseFragment;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseDirection;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseManager;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseParams;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseType;
import com.dyszlewskiR.edu.scientling.service.exercises.IExerciseCallback;
import com.dyszlewskiR.edu.scientling.service.speech.textToSpeech.SpeechPlayer;
import com.dyszlewskiR.edu.scientling.utils.Constants;


public class ExerciseActivity extends AppCompatActivity implements IExerciseCallback,SummaryExerciseFragment.Callback{
    private static final String LOG_TAG = "ExerciseActivity";

    private ProgressBar mExerciseProgress;
    private ImageView mCloseButton;

    private ExerciseManager mExerciseManager;
    private ExerciseType mExerciseType;
    private ExerciseDirection mExerciseDirection;
    private SpeechPlayer mSpeechPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        setupToolbar();
        setupControls();
        ExerciseParams params = getExerciseParams();
        setValues(params);
        setListeners();

       loadExercise();

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setTitle(""); //usunięcie tytułu aplikacji z górnego paska
        setSupportActionBar(toolbar);
    }

    private void setupControls(){
        mExerciseProgress = (ProgressBar)findViewById(R.id.exercise_progress_bar);
        mCloseButton = (ImageView)findViewById(R.id.close_button);
    }

    private void setValues(ExerciseParams params){
        /*mExerciseProgress.setMax(params.getNumberQuestion());
        mExerciseProgress.setProgress(0);*/
        mExerciseType = ExerciseType.get(params.getFirstExercise());
        mExerciseDirection = ExerciseDirection.get(params.getExerciseDirection());
        //TODO ustalić kierunek i jakie ćwiczenia ma być wykonane
    }

    private void setListeners(){
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitAlertDialog();
            }
        });
    }

    private void loadExercise(){
        DataManager dataManager = LingApplication.getInstance().getDataManager();
        CreateExerciseTask createExerciseTask = new CreateExerciseTask(dataManager, new CreateExerciseTask.Callback() {
            @Override
            public void onCreateExercise(ExerciseManager exerciseManager) {
                ExerciseActivity.this.onCreateExercise(exerciseManager);
            }
        });
        createExerciseTask.execute(getExerciseParams());
    }

    private void onCreateExercise(ExerciseManager exerciseManager){
        mExerciseManager = exerciseManager;
        initProgressBarValue(mExerciseManager.getNumQuestions());
        hideLoadingProgressBar();
        mExerciseManager.setExerciseDirection(mExerciseDirection);
        initSpeechPlayer();
        setFragment(ExerciseType.CHOOSING);


    }

    private void initSpeechPlayer(){
        mSpeechPlayer = new SpeechPlayer(getBaseContext());
        VocabularySet set = mExerciseManager.getSet();
        mSpeechPlayer.setMediaCatalog(set.getCatalog());
        mSpeechPlayer.setLanguageCode(set.getLanguageL2().getCode());
    }

    @Override
    public void onDestroy(){
        if(mSpeechPlayer != null){
            mSpeechPlayer.release();
        }
        super.onDestroy();
    }

    /** metoda ustawiająca wartości paska postępu ćwiczenia.
     * Robimy to w tym miejscu ponieważ dopiero tutaj będziemy znać liczbę pytań jaka została pobrana.
     * Nie możemy ustawić maksymalnego postepu w motodzie onCreate ponieważ może się zdarzyć,
     * że nie znajdzie się tyle pasujących słówek ile chcieliśmy pobrać
     * @param numQuestions liczba pytań obecnych w ćwiczeniu
     */
    private void initProgressBarValue(int numQuestions){
        mExerciseProgress.setMax(numQuestions);
        mExerciseProgress.setProgress(0);
    }

    private void setFragment(ExerciseType exerciseType){
        mExerciseManager.setExerciseType(exerciseType);
        ExerciseFragment fragment = getFragment(exerciseType);
        replaceFragment(fragment);
    }

    private void hideLoadingProgressBar(){
        findViewById(R.id.loading_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.loading_text_view).setVisibility(View.GONE);
    }

    private ExerciseFragment getFragment(ExerciseType exerciseType){
        ExerciseFragment fragment = null;
        switch (exerciseType){
            case CHOOSING:
                fragment = new ChoosingExerciseFragment(); break;
            case WRITING:
                fragment = new WritingExerciseFragment(); break;
            case KNOW:
                fragment = new KnowExerciseFragment(); break;
        }
        fragment.setExerciseManager(mExerciseManager);
        fragment.setExerciseCallback(this);
        fragment.setExerciseDirection(mExerciseDirection);
        fragment.setSpeechPlayer(mSpeechPlayer);

        return fragment;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.exercise_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void showExitAlertDialog(){
        new ExitAlertDialog(this).show();
    }

    private ExerciseParams getExerciseParams() {
        ExerciseParams params = new ExerciseParams();
        Intent intent = getIntent();
        long setId = intent.getLongExtra("set", Constants.DEFAULT_SET_ID);
        int numberQuestions = intent.getIntExtra("questions", Constants.DEFAULT_NUMBEr_QUESTIONS);
        int numberAnswers = intent.getIntExtra("answers", Constants.DEFAULT_NUMBER_ANSWERS);
        int repetitionDate = intent.getIntExtra("repetitionDate", 0);
        boolean fromLesson = intent.getBooleanExtra("fromLesson", false);
        boolean fromCategory = intent.getBooleanExtra("fromCategory", false);
        int firstExercise = /*intent.getIntExtra("exercise", 0);*/0;
        int direction = intent.getIntExtra("direction", 0);

        params.setSetId(setId);
        params.setNumberQuestion(numberQuestions);
        params.setNumberAnswers(numberAnswers);
        if (repetitionDate != 0) {
            params.setRepetitionDate(repetitionDate);
        }
        params.setAnswerFromLesson(fromLesson);
        params.setAnswerFromCategory(fromCategory);
        params.setFirstExercise(firstExercise);
        params.setExerciseDirection(direction);

        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.item_change_exercise:
                hideKeyboard();
                ExercisesListDialogFragment dialogFragment = new ExercisesListDialogFragment();
                dialogFragment.init(mExerciseType.getPosition(), new ExercisesListDialogFragment.Callback() {
                    @Override
                    public void onClick(int position) {
                        changeExerciseType(position);
                    }
                });
                dialogFragment.show(getFragmentManager(), "ExerciseListDialog");
                return true;
            case R.id.item_reverse_exercise:
                ChangeDirectionAlertDialog directionAlertDialog = new ChangeDirectionAlertDialog(this, new ChangeDirectionAlertDialog.Callback() {
                    @Override
                    public void onChangeDirection() {
                        changeExerciseDirection();
                    }
                });
                directionAlertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeExerciseType(int position){
        mExerciseType = ExerciseType.get(position);
        mExerciseManager.setExerciseType(mExerciseType);
        setFragment(mExerciseType);
    }

    private void changeExerciseDirection(){
        mExerciseDirection = mExerciseDirection.reverse();
        mExerciseManager.setExerciseDirection(mExerciseDirection);
        restartExercise();
    }

    private void restartExercise(){
        mExerciseManager.restart();
        mExerciseProgress.setProgress(0);
        setFragment(mExerciseType);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if(view == null){
            view = new View(this);
        }
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAnswer(boolean correct) {
        if(correct){
            mExerciseProgress.incrementProgressBy(1);
        }
    }

    @Override
    public void onExerciseFinish(){
        SummaryExerciseFragment fragment = new SummaryExerciseFragment();
        fragment.setCurrentExercise(mExerciseType.getPosition());
        fragment.setCallback(this);
        fragment.setExerciseManager(mExerciseManager);
        replaceFragment(fragment);
    }

    @Override
    public void setExercise(int position) {
        restartExercise();
        changeExerciseType(position);
    }
}

class ExitAlertDialog extends AlertDialog{

    protected ExitAlertDialog(final Activity activity) {
        super(activity);
        this.setTitle(activity.getString(R.string.close_exercise));
        String message = activity.getString(R.string.your_sure_leave) + " " + activity.getString(R.string.you_lost_progress_exercise);
        this.setMessage(message);
        this.setCancelable(true);
        this.setButton(BUTTON_POSITIVE, activity.getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        this.setButton(BUTTON_NEUTRAL, activity.getString(R.string.back), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }
}

class ChangeDirectionAlertDialog extends AlertDialog{

    public interface Callback{
        void onChangeDirection();
    }
    protected ChangeDirectionAlertDialog(Context context, final Callback callback) {
        super(context);
        this.setTitle(context.getString(R.string.you_are_sure));
        this.setMessage(context.getString(R.string.change_direction_message));
        this.setCancelable(true);
        this.setButton(BUTTON_POSITIVE, context.getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onChangeDirection();
            }
        });
        this.setButton(BUTTON_NEGATIVE, context.getString(R.string.back), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}

//TODO to zadanie można napisać jakoś inaczej
class CreateExerciseTask extends AsyncTask<ExerciseParams, Void, ExerciseManager> {

    private DataManager mDataManager;
    private Callback mCallback;

    public interface Callback{
        void onCreateExercise(ExerciseManager exerciseManager);
    }

    public CreateExerciseTask(DataManager dataManager, Callback callback){
        mDataManager = dataManager;
        mCallback = callback;
    }

    @Override
    protected ExerciseManager doInBackground(ExerciseParams... params) {
        return new ExerciseManager(params[0], mDataManager);
    }

    @Override
    protected void onPostExecute(ExerciseManager result){
        if(mCallback != null){
            mCallback.onCreateExercise(result);
        }
    }
}
