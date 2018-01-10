package com.yeejay.yplay.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yeejay.yplay.R;

/**
 * 字母侧边栏
 * Created by Adolph on 2018/1/9.
 */

public class SideView extends View{

    private static final String TAG = "SideView";

    private OnTouchingLetterChangedListener listener;

    String[] b = {  "#","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    int choose = -1;//用于标记点击存放字母数组中的下标
    Paint paint = new Paint();
//    boolean showBkg = false;

    public SideView(Context context) {
        super(context);
    }

    public SideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();

        int singleHeight = height / b.length;

        for (int i = 0; i < b.length; i++){

            //绘制字母text的颜色
            paint.setColor(Color.parseColor("#000000"));
            //绘制字母text的字体大小
            paint.setTextSize(20);
            //绘制字母text的字体样式
            paint.setTypeface(Typeface.DEFAULT);
            //设置抗锯齿样式
            paint.setAntiAlias(true);

            float xPos = width / 2 - paint.measureText(b[i]) / 2;//得到绘制字母text的起点的X坐标
            float yPos = singleHeight * i + singleHeight;//得到绘制字母text的起点的Y坐标

            if (i==choose){

                paint.setColor(Color.parseColor("#A1DDDE"));
                canvas.drawCircle(width/2,yPos-(singleHeight/4),width/2,paint);

                paint.setColor(Color.parseColor("#ffffff"));
                canvas.drawText(b[i], xPos, yPos, paint);
            }else {
                canvas.drawText(b[i], xPos, yPos, paint);//开始绘制每个字母
            }

            paint.reset();//绘制完一个字母需要重置一下画笔对象
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {//重写view的触摸事件分发方法
        final int action = event.getAction();
        final float y = event.getY();//由于只涉及到Y轴坐标,只获取y坐标
        final int oldChoose = choose;//oldChoose用于记录上一次点击字母所在字母数组中的下标
        final int c = (int) (y / getHeight() * b.length);//得到点击或触摸的位置从而确定对应点击或触摸的字母所在字母数组中的下标
        switch (action) {
            case MotionEvent.ACTION_DOWN://监听按下事件
                if (oldChoose != c && listener != null) {//如果此次点击的字母数组下标不等于上一次的且已经注册了监听事件的,
                    if (c >= 0 && c <= b.length) {//并且点击得到数组下标在字母数组范围内的，我们就将此时的字母回调出去
                        listener.onTouchingLetterChanged(b[c]);//我们就将此时的对应在字母数组中的字母回调出去
                        choose = c;//并且更新当前选中的字母下标存储在choose变量中
                        invalidate();//最后通知canvas重新绘制
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE://监听移动事件,因为按下的时候已经把背景showBkg设置true,这里就不需要重新设置,其他操作与按下的事件一致
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c <= b.length) {
                        listener.onTouchingLetterChanged(b[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP://监听手指抬起的动作
                choose = -1;//此时记录下标的变量也需要重置
//                invalidate();//并且重绘整个view
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
    /**
     * 注册自定义监听器
     * */
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener listener) {
        this.listener = listener;
    }
    /**
     * 定义一个接口,用于回调出点击后的字母,显示在弹出的字母对话框中
     * */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }
}
