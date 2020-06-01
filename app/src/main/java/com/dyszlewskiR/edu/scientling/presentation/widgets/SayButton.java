package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyszlewskiR.edu.scientling.R;

public class SayButton extends android.support.v7.widget.AppCompatImageView {

    public static final int NORMAL = 0;
    public static final int RECORDING = 1;

    private final int LAYOUT_RESOURCE = R.layout.widget_say_button;
    private final int SAY_IMAGE_RESOURCE = R.drawable.ic_say;
    private final int RECORDING_IMAGE_RESOURCE = R.drawable.ic_stop;

    private ImageView mSayButton;
    private Context mContext;

    public SayButton(Context context) {
        super(context);
        init(context);
    }

    public SayButton(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public SayButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
       // mSayButton = getSayButton(context);
        setSayButton();
    }

    /*private ImageView getSayButton(Context context){
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(getResources().getDrawable(R.drawable.round_button));
        } else {
            imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
        }
        imageView.setImageResource(SAY_IMAGE_RESOURCE);
        return imageView;
    }*/

    private void setSayButton(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getResources().getDrawable(R.drawable.round_button));
        } else {
            setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
        }
        setImageResource(SAY_IMAGE_RESOURCE);
    }

    public void setState(int state){
        switch (state){
            case NORMAL:
                setImageResource(SAY_IMAGE_RESOURCE); break;
            case RECORDING:
                setImageResource(RECORDING_IMAGE_RESOURCE); break;
        }
    }
}
