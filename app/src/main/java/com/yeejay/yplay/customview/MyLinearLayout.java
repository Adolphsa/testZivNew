package com.yeejay.yplay.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by XJG on 2018/2/4.
 */

public class MyLinearLayout extends LinearLayout {
    private OnSizeChangedListener onSizeChangedListener;

    public static interface OnSizeChangedListener{
        void onSizeChange(int width, int height, int oldWidth, int oldHeight);
    }

    public MyLinearLayout(Context context) {
        super(context, null);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChange(w, h, oldw, oldh);
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener){
        this.onSizeChangedListener = onSizeChangedListener;
    }
}