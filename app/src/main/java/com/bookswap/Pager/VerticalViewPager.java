package com.bookswap.Pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(@NonNull Context context) {
        super ( context );
    }

    public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super ( context, attrs );

        setPageTransformer ( true,new VerticalPagerTransformer () );
        setOverScrollMode ( OVER_SCROLL_NEVER );
    }

    private class VerticalPagerTransformer implements ViewPager.PageTransformer
    {
        // Here we will check positions
        @Override
        public void transformPage(@NonNull View page, float position) {

            if(position < -1)
            {
                //{-inifinity , -1}
                //if this page is way off to screen to left
                page.setAlpha ( 0 );
            }else if(position<=0)
            {
                //[-1,0]
                // use default slide transition when moving to left page
                page.setAlpha(1);
                // counteract the default slide transition
                page.setTranslationX ( page.getWidth () * -position);

                //set y position to swap in from top
                float yPosition =position * page.getHeight ();
                page.setTranslationY ( yPosition );
                page.setScaleX ( 1 );
                page.setScaleY ( 1 );

            }else if(position <=1)
            {
                //[0,1]
                page.setTranslationX ( page.getWidth () * -position );

                float scale = 0.75f + (1 - 0.75f) * (Math.abs ( position ));

                //if you remove below 2 lines then there no motion in swipe container
                //page.setScaleX ( scale );
                //page.setScaleY ( scale );
            }else
            {
                //[1 , + infinity]
                // this page is way off screen to right

                page.setAlpha ( 0 );
            }
        }



    }

    private MotionEvent swapXYCordinate(MotionEvent event)
    {
        // now we will swap x y cordinate using this

        float width = getWidth ();
        float height = getHeight ();

        float newX = (event.getY ()/height) * width;
        float newY = (event.getX ()/width) * height;

        event.setLocation ( newX,newY );
        return  event;

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent ( swapXYCordinate(ev) );

        swapXYCordinate ( ev ); // return touch cordinates
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent ( swapXYCordinate ( ev ) );
    }
}

