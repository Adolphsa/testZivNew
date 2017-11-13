package com.yeejay.yplay.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 *
 * Created by Administrator on 2017/11/13.
 */

public class LazyScrollView extends ScrollView{

    private OnScrollChangedListener onScrollChangedListener;

    public LazyScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public LazyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LazyScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(this.onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(t, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
