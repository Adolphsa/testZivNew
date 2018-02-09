package com.yeejay.yplay.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 不响应点击事件的listView
 * Created by Adolph on 2018/2/9.
 */

public class NotClickListView extends ListView{

    private static final String TAG = "NotClickListView";

    public NotClickListView(Context context) {

        super(context);
    }

    public NotClickListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "dispatchTouchEvent: notClickList点击");
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
