package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Word;
import com.dyszlewskiR.edu.scientling.utils.TranslationListConverter;

import java.util.ArrayList;
import java.util.List;

public class FlashcardAdapter extends PagerAdapter {

    private final String TAG = "FlashcardAdapter";

    private SparseArray<View> mViews;

    private List<Word> mItems;
    private List<Boolean> mSides;
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;
    private TextView mFlashcardTextView;

    private boolean mFirstSide;

    private int mPosition;

    public FlashcardAdapter(Context context, int resource, List<Word> data) {
        mContext = context;
        mResource = resource;
        mItems = data;

        mInflater = LayoutInflater.from(mContext);
        mSides = new ArrayList<>();
        for (int i = 0; i < mItems.size(); i++) {
            mSides.add(true);
        }

        mViews = new SparseArray<>();
        mPosition = 0;
    }

    private void changeSide(final int position) {
        final AnimatorSet animatorIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flashcard_flip_left_in);
        animatorIn.setTarget(mViews.get(position));
        AnimatorSet animatorOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.flashcard_flip_right_out);
        animatorOut.setTarget(mViews.get(position));

        animatorOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                boolean side = !(mSides.get(position));
                mSides.set(position, side);
                notifyDataSetChanged();
                mPosition = position;
                animatorIn.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        animatorOut.start();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Log.d(TAG, "instantiateItem " + position);
        mFirstSide = true;
        final View view = mInflater.inflate(mResource, null);
        setTexts(view, position);

        container.addView(view);
        mViews.put(position, view);
        return view;
    }

    private void setTexts(View view, final int position) {
        mFlashcardTextView = (TextView) view.findViewById(R.id.flashcard_text_view);
        if (mSides.get(position)) {
            mFlashcardTextView.setText(mItems.get(position).getContent());
        } else {
            String translation = TranslationListConverter.toString(mItems.get(position).getTranslations());
            mFlashcardTextView.setText(translation);
        }
        mFlashcardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                //boolean side = !mSides.get(position);
                //mSides.set(position,side);

                //instantiateItem(container,position);
                changeSide(position);
            }
        });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d("TAG", "destroyItem " + position);
        View view = (View) object;
        container.removeView(view);
        mViews.remove(position);
    }

    @Override
    public void notifyDataSetChanged() {
        int key;
        for (int i = 0; i < mViews.size(); i++) {
            key = mViews.keyAt(i);
            View view = mViews.get(key);
            setTexts(view, key);
        }
        super.notifyDataSetChanged();

    }
}
