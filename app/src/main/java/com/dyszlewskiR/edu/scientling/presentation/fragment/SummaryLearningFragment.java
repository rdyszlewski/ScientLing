package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.adapters.SummaryRepetitionAdapter;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.service.repetitions.SaveExerciseService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SummaryLearningFragment extends Fragment {

    private List<Word> mWords;
    private ListView mListView;
    private Button mSaveButton;

    public SummaryLearningFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        mWords = intent.getParcelableArrayListExtra("items");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_learning, container, false);
        setupControls(view);
        return view;
    }

    private void setupControls(View view) {
        mListView = (ListView) view.findViewById(R.id.list);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SummaryRepetitionAdapter adapter = new SummaryRepetitionAdapter(getActivity(), R.layout.item_summary_exercise, mWords);
        mListView.setAdapter(adapter);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getSimpleName(), "onClick service");
                Intent intent = new Intent(getActivity().getBaseContext(), SaveExerciseService.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<Word>) mWords);
                getActivity().startService(intent);
                getActivity().finish();
            }
        });
    }
}
