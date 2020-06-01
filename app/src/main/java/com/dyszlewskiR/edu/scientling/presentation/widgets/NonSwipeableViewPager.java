package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Razjelll on 09.05.2017.
 */

public class NonSwipeableViewPager extends ViewPager{

    public NonSwipeableViewPager(Context context){
        super(context);
        initScroller();
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
        initScroller();
    }

    /**
     * Ma zapewnić płynne przewijanie
     */
    private void initScroller(){
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new PagerScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public class PagerScroller extends Scroller {
        public PagerScroller (Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}
