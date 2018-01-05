package com.yeejay.yplay.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.view.FooterView;
import com.yeejay.yplay.R;

/**
 * 加载更多
 * Created by Administrator on 2017/11/21.
 */

public class LoadMoreView extends FrameLayout implements FooterView {

    private TextView tv;
    private ImageView arrow;
    private ProgressBar progressBar;
    private boolean mLoadingFlag = false;

    public LoadMoreView(Context context) {
        this(context,null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_header,null);
        addView(view);
        tv = (TextView) view.findViewById(R.id.header_tv);
        arrow = (ImageView) view.findViewById(R.id.header_arrow);
        progressBar = (ProgressBar) view.findViewById(R.id.header_progress);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {
        float s = progress / all;
        if (s >= 0.9f){
            arrow.setRotation(0);
        }else{
            arrow.setRotation(180);
        }
        if (progress >= all-10){
            tv.setText("松开加载更多");
        }else{
            tv.setText("上拉加载");
        }
    }

    @Override
    public void finishing(float progress, float all) {
    }

    @Override
    public void loading() {
        arrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tv.setText("加载中...");

        if (mLoadingFlag) {
            mLoadingFlag = false;
            noData();
        }
    }

    @Override
    public void normal() {
        arrow.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tv.setText("上拉加载");
    }

    @Override
    public View getView() {
        return this;
    }

    public void noData(){
        tv.setText("数据已加载完毕");
        arrow.setVisibility(INVISIBLE);
    }

    public void noData(boolean flag){
        if (flag) {
            mLoadingFlag = true;
        }
    }

}
