package com.yeejay.yplay.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.yeejay.yplay.R;

/**
 * 进度条按钮
 * Created by Administrator on 2017/10/30.
 */

public class ProgressButton extends android.support.v7.widget.AppCompatButton {

    //public static final int TYPE_FILL = 0;
    //public static final int TYPE_STROKE = 1;

    private Paint mPaint = new Paint();
    private int mProgress;
    //private int currentType = TYPE_FILL;
    private int buttonColor = R.color.white;

    public int getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public ProgressButton(Context context) {
        super(context);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

            mPaint.setColor(getContext().getResources().getColor(buttonColor));
            mPaint.setAntiAlias(true);
            //mPaint.setAlpha(128);
            //mPaint.setStrokeWidth(1.0f);
            Rect rect = new Rect();
            canvas.getClipBounds(rect);
            rect.left += getPaddingLeft();
            rect.top += getPaddingTop();
            rect.right = (rect.left - getPaddingLeft()) + (mProgress * getWidth() / 100) - getPaddingRight();
            rect.bottom -= getPaddingBottom();
            canvas.drawRoundRect(new RectF(rect), 8.0f, 8.0f, mPaint);

        super.onDraw(canvas);
    }

    public void updateProgress(int progress) {
        if(progress >= 0 && progress <= 100) {
            mProgress = progress;
            invalidate();
        } else if(progress < 0) {
            mProgress = 0;
            invalidate();
        } else if(progress > 100) {
            mProgress = 100;
            invalidate();
        }
    }

//    public void setType(int type) {
//        if(type == TYPE_FILL || type == TYPE_STROKE)
//            currentType = type;
//        else
//            currentType = TYPE_FILL;
//    }

}
