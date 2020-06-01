package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.dyszlewskiR.edu.scientling.R;


public class AnswerButton extends android.support.v7.widget.AppCompatButton {
    public static final int NOSET = 0;
    public static final int CORRECT = 1;
    public static final int INCORRECT = 2;

    private static final int [] CORRECT_STATE = {R.attr.correct};
    private static final int [] INCORRECT_STATE = {R.attr.incorrect};

    private int mStateIndex = NOSET;


    public AnswerButton (Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void changeState(int state){
        mStateIndex = state;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace){
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        switch (mStateIndex){
            case CORRECT:
                mergeDrawableStates(drawableState, CORRECT_STATE); break;
            case INCORRECT:
                mergeDrawableStates(drawableState, INCORRECT_STATE); break;
            default:
                mergeDrawableStates(drawableState,new int[]{}); break;
        }
        return drawableState;
    }
}

