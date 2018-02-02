package com.tms;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {
	
	
	
	 public static boolean mMapIsTouched=false;

    public TouchableWrapper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapIsTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                mMapIsTouched = false;
                break;
        }

        return super.dispatchTouchEvent(ev);

    }

}
