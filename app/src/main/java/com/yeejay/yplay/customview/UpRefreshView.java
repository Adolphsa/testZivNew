package com.yeejay.yplay.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jwenfeng.library.pulltorefresh.view.HeadView;
import com.yeejay.yplay.R;

/**
 * 刷新 头部
 * Created by xjg on 2018/01/09.
 */

public class UpRefreshView extends FrameLayout implements HeadView {
    private static final String TAG = "UpRefreshView";

    private static final int MSG_ANIM_2 = 2;

    private ImageView animLoad1;
    private ImageView animLoad2;
    private AnimationDrawable animDrawable1;
    private AnimationDrawable animDrawable2;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ANIM_2:
                    animDrawable1.stop();

                    animLoad1.setVisibility(View.GONE);
                    animLoad2.setVisibility(View.VISIBLE);

                    animDrawable2.start();

                    break;
                default :
                    break;
            }
        }
    };

    public UpRefreshView(Context context) {
        this(context,null);
    }

    public UpRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UpRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_up_refresh_view,null);
        addView(view);
        animLoad1 = (ImageView) view.findViewById(R.id.anim_up_refresh_1);
        animLoad2 = (ImageView) view.findViewById(R.id.anim_up_refresh_2);
        animDrawable1 = (AnimationDrawable) animLoad1.getBackground();
        animDrawable2 = (AnimationDrawable) animLoad2.getBackground();
    }

    @Override
    public void begin() {
        animLoad1.setVisibility(View.VISIBLE);
        animLoad2.setVisibility(View.GONE);
    }

    @Override
    public void progress(float progress, float all) {
        animDrawable1.start();
        mHandler.sendEmptyMessageDelayed(MSG_ANIM_2, 1000);
    }

    @Override
    public void finishing(float progress, float all) {
    }

    @Override
    public void loading() {
    }

    @Override
    public void normal() {
        animDrawable2.stop();
    }

    @Override
    public View getView() {
        return this;
    }

    public void noData(){
    }
}