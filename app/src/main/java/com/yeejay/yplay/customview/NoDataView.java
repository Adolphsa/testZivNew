package com.yeejay.yplay.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.view.HeadView;
import com.yeejay.yplay.R;

/**
 * 没有更多数据的view
 * Created by Administrator on 2017/11/29.
 */

public class NoDataView extends FrameLayout implements HeadView {

    private TextView tv;

    public NoDataView(@NonNull Context context) {
        super(context);
    }

    public NoDataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_no_more_data,null);
        addView(view);
        tv = (TextView) view.findViewById(R.id.no_data_tv);
    }

    public void setTv(String text){
        tv.setText(text);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {

    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {

    }

    @Override
    public void normal() {

    }

    @Override
    public View getView() {
        return this;
    }
}
