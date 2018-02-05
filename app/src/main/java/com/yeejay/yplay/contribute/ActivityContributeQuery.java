package com.yeejay.yplay.contribute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.FragmentAdapter;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.SubmitQueryListRespond;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.StatuBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityContributeQuery extends BaseActivity {
    private static final String TAG = "ActivityContributeQuery";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutBack;
    @BindView(R.id.indicator)
    ImageView indicatorView;
    @BindView(R.id.contribute_query_view_pager)
    ViewPager viewPager;
    @BindView(R.id.online_tab)
    TextView onlineTab;
    @BindView(R.id.offline_tab)
    TextView offlineTab;
    @BindView(R.id.layout_tab)
    LinearLayout layoutTab;
    @BindView(R.id.contribute_new)
    ImageView contributeNew;

    @OnClick(R.id.layout_title_back2)
    public void toBack() {
        //页面返回时需要回退到投稿编辑界面;
        setResult(4);

        finish();
    }

    @OnClick(R.id.online_tab)
    public void clickOnlineTab() {
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.offline_tab)
    public void clickOfflineTab() {
        viewPager.setCurrentItem(1);
    }

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentAdapter frgAdapter;
    private FragmentConOnline fragOnline;
    private FragmentConOffline fragOffline;
    private int mIndex;
    DisplayMetrics dm;
//    private int mIndicatorOffSet;
//    private int mIndicatorWidth;
//    private int mPageOffset;
    Animation anima;

    private BroadcastReceiver mContributeBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra("contribute_flag", 0);
            LogUtils.getInstance().debug(TAG + " , mContributeBr, flag = " + String.valueOf(flag));
            if (1 == flag) { //表示有新的投稿消息;
                contributeNew.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_query);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.contribute_color));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContributeQuery.this, true);

        registerBr();
        //initView();
        initFragmentAndViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBr();
    }

    private void registerBr() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yeejay.br.contribute");
        registerReceiver(mContributeBr, intentFilter);
    }

    private void unregisterBr() {
        unregisterReceiver(mContributeBr);
    }

    /*
     * 用户设置图片指示器的初始位置；
     */
//    private void initView() {
//        DisplayMetrics dm=new DisplayMetrics();
//        // 把屏幕尺寸信息赋值给DisplayMetrics dm，注意不是set
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        // 屏幕宽度
//        int screenWidth = dm.widthPixels;
//
//        int offset = (screenWidth / 2 - indicatorView.getWidth())/2;
//        int offsetDp = offset / (int)dm.density;
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorView
//                .getLayoutParams();// 获取当前横线的布局参数
//        params.leftMargin = offsetDp;// 设置左边距
//        indicatorView.setLayoutParams(params);// 重新给横线指示器设置布局参数
//    }

    private void initFragmentAndViewPager() {
        dm = new DisplayMetrics();
        // 把屏幕尺寸信息赋值给DisplayMetrics dm，注意不是set
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //构造适配器
        fragOnline = new FragmentConOnline();
        fragOffline = new FragmentConOffline();
        fragments.add(fragOnline);
        fragments.add(fragOffline);

        frgAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(frgAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int i1) {
/*                int len = (int) (indicatorView.getWidth() * positionOffset) + position
                        * indicatorView.getWidth() + 30;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorView
                        .getLayoutParams();// 获取当前横线的布局参数
                params.leftMargin = len;// 设置左边距

                indicatorView.setLayoutParams(params);// 重新给横线设置布局参数*/
            }

            @Override
            public void onPageSelected(int i) {
                // 屏幕宽度
                int screenWidth = dm.widthPixels;//像素宽度
                int screenWidthDp = screenWidth / (int)dm.density;//dp宽度

                //计算偏移量
                int offset = (screenWidth / 2 - indicatorView.getWidth())/2;

                //相邻页面的偏移量
                int pageOffset = offset * 2 + indicatorView.getWidth();

                anima = new TranslateAnimation(mIndex * pageOffset + offset,
                        i * pageOffset + offset,0,0);

                mIndex = i; //当前页跟着变
                anima.setFillAfter(true); // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
                anima.setDuration(200);// 动画持续时间0.2秒
                indicatorView.startAnimation(anima);

                if (i == 0) {
                    //nothing to do;
                } else if (i == 1) {
                    contributeNew.setVisibility(View.INVISIBLE);

                    //将已上线列表中的item的高亮背景去掉;
                    removeHighLightBackgroundOfOnlineList();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        //目的是为了进入该页面时，指示器图片的初始位置可以处在正确的位置;
        viewPager.setCurrentItem(0);
    }

    private void removeHighLightBackgroundOfOnlineList() {
        if (fragOnline.mReviewedList != null) {
            SubmitQueryListRespond.PayloadBean.ContributesBean bean;
            for(int i = 0; i < fragOnline.mReviewedList.size(); i++) {
                bean = fragOnline.mReviewedList.get(i);
                if(bean != null && bean.getFlag() == 1) {
                    bean.setFlag(-1);
                    if (fragOnline.reviewedAdapter != null) {
                        fragOnline.reviewedAdapter.notifyItemChanged(i);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
        //按导航键返回时，需要回退到投稿编辑界面;
        setResult(4);

        super.onBackPressed();
    }
}