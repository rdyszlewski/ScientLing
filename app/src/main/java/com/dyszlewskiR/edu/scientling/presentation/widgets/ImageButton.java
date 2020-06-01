package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;

public class ImageButton extends android.support.v7.widget.AppCompatImageView{
    public static final int NOSET = 0;
    public static final int SELECTED = 1;

    public ImageButton(Context context) {
        super(context);
    }


    public void setState(int state){
        if(state == NOSET){
            setSelected(false);
        }
        if(state == SELECTED){
            setSelected(true);
        }
        refreshDrawableState();
    }


}
