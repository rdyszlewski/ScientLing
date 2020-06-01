package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Definition;

public class DefinitionPagerFragment extends Fragment {

    private Definition mDefinition;

    private TextView mContentTextView;
    private TextView mTranslationTextView;
    private Button mShowTranslationButton;

    public DefinitionPagerFragment() {
        // Required empty public constructor
    }

    public void setDefinition(Definition definition) {
        mDefinition = definition;
        fillComponents();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefinition = getArguments().getParcelable("item");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition_pager, container, false);
        mContentTextView = (TextView) view.findViewById(R.id.definition_content_text_view);
        mTranslationTextView = (TextView) view.findViewById(R.id.definition_translation_text_view);
        mShowTranslationButton = (Button) view.findViewById(R.id.definition_translation_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fillComponents();
        mShowTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTranslationTextView.getVisibility() == View.VISIBLE) {
                    hideTranslation();
                } else {
                    showTranslation();
                }
            }
        });
    }

    private void fillComponents() {
        if (mDefinition.getTranslation() == null) {
            mShowTranslationButton.setVisibility(View.INVISIBLE);
            mTranslationTextView.setVisibility(View.INVISIBLE);
        } else {
            mShowTranslationButton.setText(getResources().getString(R.string.show_translation));
            mTranslationTextView.setVisibility(View.INVISIBLE);
            mTranslationTextView.setText(mDefinition.getTranslation());
        }
        mContentTextView.setText(mDefinition.getContent());
    }

    private void showTranslation() {
        mTranslationTextView.setVisibility(View.VISIBLE);
        mShowTranslationButton.setText(getResources().getString(R.string.hide_translation));
    }

    private void hideTranslation() {
        mTranslationTextView.setVisibility(View.INVISIBLE);
        mShowTranslationButton.setText(getResources().getString(R.string.show_translation));
    }


}
