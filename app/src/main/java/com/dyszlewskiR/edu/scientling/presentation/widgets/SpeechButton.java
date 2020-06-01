package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dyszlewskiR.edu.scientling.R;

public class SpeechButton extends RelativeLayout {
    private final int SPEECH_IMAGE_RESOURCE = R.drawable.ic_speak;
    private final int PAUSE_IMAGE_RESOURCE = R.drawable.ic_stop;

    private ImageView mSpeechButton;
    private ProgressBar mProgressBar;
    private Context mContext;
    private boolean mLoading;

    public static final int NORMAL = 0;
    public static final int LOADING = 1;
    public static final int PLAYING = 2;

    public SpeechButton(Context context) {
        super(context);
        init(context);
    }

    public SpeechButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpeechButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        initControls();
    }

    private void initControls() {
        mSpeechButton = getSpeechButton(getContext());
        addView(mSpeechButton);
        mProgressBar = getProgressBar(getContext());
        addView(mProgressBar);
        setState(NORMAL);
    }

    private ImageView getSpeechButton(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(getResources().getDrawable(R.drawable.round_button));
        } else {
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
        }
        imageView.setImageResource(SPEECH_IMAGE_RESOURCE);
        return imageView;
    }

    private ProgressBar getProgressBar(Context context) {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressBar.setVisibility(GONE);
        return progressBar;
    }

    public boolean isLoadingState() {
        return mLoading;
    }

    public void setState(int state) {
        switch (state) {
            case NORMAL:
                mProgressBar.setVisibility(GONE);
                mSpeechButton.setImageResource(SPEECH_IMAGE_RESOURCE);
                break;
            case LOADING:
                mProgressBar.setVisibility(VISIBLE);
                mSpeechButton.setImageURI(null); //usuwanie obrazka
                break;
            case PLAYING:
                mProgressBar.setVisibility(GONE);
                mSpeechButton.setImageResource(PAUSE_IMAGE_RESOURCE);
                break;
        }
    }
}
