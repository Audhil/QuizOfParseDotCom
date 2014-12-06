package com.mns.quiz.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mns.quiz.Debug;

public class PuzzleViewPager extends ViewPager {

	private boolean enabled;

	public PuzzleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}
		

		return false;
	}

	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	

	public boolean detectSwipeToRight(MotionEvent event){

		 int initialXValue = 0; // as we have to detect swipe to right
		 final int SWIPE_THRESHOLD = 100; // detect swipe
		 boolean result = false;

		        try {                
		            float diffX = event.getX() - initialXValue;

		                if (Math.abs(diffX) > SWIPE_THRESHOLD ) {
		                    if (diffX > 0) {
		                        // swipe from left to right detected ie.SwipeRight
		                    	Debug.print("Right");
		                        result = false;
		                    } else {
		                        // swipe from right to left detected ie.SwipeLeft
		                        result = true;
		                        Debug.print("left");
		                    }
		                }
		            } 
		         catch (Exception exception) {
		            exception.printStackTrace();
		        }
		        return result;
		    }
}