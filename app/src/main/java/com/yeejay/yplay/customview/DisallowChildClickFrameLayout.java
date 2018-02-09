package com.yeejay.yplay.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 不允许子控件获取点击事件
 * Created by Adolph on 2018/2/9.
 */

public class DisallowChildClickFrameLayout extends FrameLayout {

    private static final String TAG = "DisallowChildClickFrame";


    int x;
    int y;
    int curX;
    int curY;

    public DisallowChildClickFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DisallowChildClickFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercepted = false;

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                intercepted = true;
                break;

        }

        return intercepted;
    }
}
