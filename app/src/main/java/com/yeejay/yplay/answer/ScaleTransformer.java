package com.yeejay.yplay.answer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

/**
 * pager缩放相关处理
 * Created by Adolph on 2018/2/6.
 */

public class ScaleTransformer implements ViewPager.PageTransformer{

    private Context context;
    private float elevation;

    public ScaleTransformer(Context context) {
        this.context = context;
        elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, context.getResources().getDisplayMetrics());
    }

    @Override
    public void transformPage(View page, float position) {

//        if (position < -1 || position > 1) {
//
//        } else {
//            if (position < 0) {
//                ((CardView) page).setCardElevation((1 + position) * elevation);
//            } else {
//                ((CardView) page).setCardElevation((1 - position) * elevation);
//            }
//        }

    }

}
