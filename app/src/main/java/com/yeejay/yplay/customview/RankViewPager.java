package com.yeejay.yplay.customview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 禁止父控件滑动的viewPager
 * Created by Adolph on 2018/2/9.
 */

public class RankViewPager extends ViewPager{

    private static final String TAG = "RankViewPager";

    PointF downP = new PointF();
    PointF curP = new PointF();

    public RankViewPager(Context context) {
        super(context);
    }

    public RankViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if(getChildCount()<=1)

        {

            return super.onTouchEvent(arg0);

        }

        curP.x = arg0.getX();

        curP.y = arg0.getY();



        if(arg0.getAction() == MotionEvent.ACTION_DOWN)

        {



            //记录按下时候的坐标

            //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变

            downP.x = arg0.getX();

            downP.y = arg0.getY();

            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            Log.i(TAG, "onTouchEvent: 请求父控件不要拦截事件");

            getParent().requestDisallowInterceptTouchEvent(true);

        }



        if(arg0.getAction() == MotionEvent.ACTION_MOVE){

            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰

            getParent().requestDisallowInterceptTouchEvent(true);

        }



        if(arg0.getAction() == MotionEvent.ACTION_UP || arg0.getAction() == MotionEvent.ACTION_CANCEL){

            //在up时判断是否按下和松手的坐标为一个点

            //如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick

            getParent().requestDisallowInterceptTouchEvent(false);

            if(downP.x==curP.x && downP.y==curP.y){



                return true;

            }

        }

        super.onTouchEvent(arg0); //注意这句不能 return super.onTouchEvent(arg0); 否则触发parent滑动

        return true;
    }

}
