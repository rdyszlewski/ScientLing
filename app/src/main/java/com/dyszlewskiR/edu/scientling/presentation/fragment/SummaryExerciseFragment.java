package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.SummaryRepetitionAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.presentation.dialogs.ExercisesListDialogFragment;
import com.dyszlewskiR.edu.scientling.service.exercises.ExerciseManager;
import com.dyszlewskiR.edu.scientling.service.repetitions.SaveExerciseService;

import java.util.ArrayList;


public class SummaryExerciseFragment extends Fragment {
    private ExerciseManager mExerciseManager;
    private TextView mNumberWordsTextView;
    private ListView mWordList;
    private Button mRepeatButton;
    private Button mFinishButton;

    private int mCurrentExercise;

    private Callback mCallback;

    public interface Callback{
        void setExercise(int position);
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public void setCurrentExercise(int exercise){
        mCurrentExercise = exercise;
    }

    public void setExerciseManager(ExerciseManager exerciseManager){
        mExerciseManager = exerciseManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_summary_exercise, container, false);
        setupControls(view);
        return view;
    }

    private void setupControls(View view){
        mNumberWordsTextView = (TextView) view.findViewById(R.id.number_words_text_view);
        mWordList = (ListView)view.findViewById(R.id.list);
        mRepeatButton = (Button)view.findViewById(R.id.repeat_button);
        mFinishButton = (Button)view.findViewById(R.id.finish_button);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        int mNumQuestions = mExerciseManager.getNumQuestions();
        mNumberWordsTextView.setText(String.valueOf(mNumQuestions));

        SummaryRepetitionAdapter adapter = new SummaryRepetitionAdapter(getActivity(), R.layout.item_summary_exercise, mExerciseManager.getQuestions());
        mWordList.setAdapter(adapter);
        setListeners();
    }

    private void setListeners(){
        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExercisesListDialogFragment dialog = new ExercisesListDialogFragment();
                dialog.init(mCurrentExercise, new ExercisesListDialogFragment.Callback() {
                    @Override
                    public void onClick(int position) {
                        if(mCallback != null){
                            mCallback.setExercise(position);
                        }
                    }
                });
                dialog.show(getActivity().getFragmentManager(), "ExercisesList");
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getBaseContext(), SaveExerciseService.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<Word>)mExerciseManager.getWordsToRepetition());
                intent.putExtra("correctness", mExerciseManager.getAnswersCorrectness());
                getActivity().startService(intent);
                getActivity().finish();
            }
        });
    }

}
