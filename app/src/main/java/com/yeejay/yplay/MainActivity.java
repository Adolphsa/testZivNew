package com.yeejay.yplay;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yeejay.yplay.adapter.FragmentAdapter;
import com.yeejay.yplay.answer.FragmentAnswer;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.friend.FragmentFriend;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.message.FragmentMessage;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.utils.MessageUpdateUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements HuaweiApiClient.ConnectionCallbacks,
        HuaweiApiClient.OnConnectionFailedListener, MessageUpdateUtil.MessageUpdateListener {

    @BindView(R.id.main_view_pager)
    ViewPager viewPager;
    @BindView(R.id.main_nav_bar_left)
    Button mainNavBarLeft;
    @BindView(R.id.main_nav_bar_right) //下面有字的
            Button mainNavBarRight;
    @BindView(R.id.main_nav_bar_rl)
    RelativeLayout mainNavBarRl;

    @BindView(R.id.main_nav_bar_left2)
    Button mainNavBarLeft2;
    @BindView(R.id.main_nav_center2)
    Button mainNavCenter2;
    @BindView(R.id.main_nav_right2)
    Button mainNavRight2;
    @BindView(R.id.mainnav_bar2)
    RelativeLayout mainnavBar2;

    //左一
    @OnClick(R.id.main_nav_bar_left)
    public void leftButton(View view) {
        viewPager.setCurrentItem(0);
//        feedsFragmentStatus();
    }

    //右一
    @OnClick(R.id.main_nav_bar_right)
    public void rightButton(View view) {
        viewPager.setCurrentItem(2);
//        messageFragmentStatus();
    }

    //左二
    @OnClick(R.id.main_nav_bar_left2)
    public void leftButton2(View view) {
        viewPager.setCurrentItem(0);
//        feedsFragmentStatus();
    }


    //中间
    @OnClick(R.id.main_nav_center2)
    public void ceneterButton2(View view) {
        viewPager.setCurrentItem(1);
//        playFragmentStatus();
    }

    //右二
    @OnClick(R.id.main_nav_right2)
    public void rightButton2(View view) {
        viewPager.setCurrentItem(2);
//        messageFragmentStatus();
    }

    private static final String TAG = "MainActivity";

    FragmentAdapter frgAdapter;
    FragmentFriend fragmentFriend;
    FragmentAnswer fragmentAnswer;
    FragmentMessage fragmentMessage;
    private HuaweiApiClient huaWeiClient;
    ImSessionDao imSessionDao;

    private int mColor;

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        MessageUpdateUtil.getMsgUpdateInstance().setMessageUpdateListener(this);

        initMainView();
        addFragment();
        viewPager.setAdapter(frgAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                System.out.println("当前为第" + i + "页");
                if (i == 0) {
                    feedsFragmentStatus();
                } else if (i == 1) {
                    playFragmentStatus();
                } else if (i == 2) {
                    messageFragmentStatus();
                }

                setMessageClear();

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setMessageIcon();

        //获取签名
        getImSignature();
    }

    private void addFragment() {
        //构造适配器
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragmentFriend = new FragmentFriend();
        fragmentAnswer = new FragmentAnswer();
        fragmentMessage = new FragmentMessage();
        fragments.add(fragmentFriend);
        fragments.add(fragmentAnswer);
        fragments.add(fragmentMessage);
        frgAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
    }

    private void initMainView() {
        mainNavBarRl.setVisibility(View.VISIBLE);
        mainnavBar2.setVisibility(View.GONE);

    }

    //动态
    private void feedsFragmentStatus() {
        System.out.println("动态---MainActivity");
        mainNavBarRl.setVisibility(View.INVISIBLE);
        mainnavBar2.setVisibility(View.VISIBLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.feeds_title_color));
    }

    //答题
    private void playFragmentStatus() {
        System.out.println("答题---MainActivity");
        mainNavBarRl.setVisibility(View.VISIBLE);
        mainnavBar2.setVisibility(View.INVISIBLE);
        getWindow().setStatusBarColor(getResources().getColor(mColor));

    }

    //消息
    private void messageFragmentStatus() {
        System.out.println("消息---MainActivity");
        mainNavBarRl.setVisibility(View.INVISIBLE);
        mainnavBar2.setVisibility(View.VISIBLE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.message_title_color));
    }

    //设置状态栏的颜色
    public void setBottomColor(int color) {
        getWindow().setStatusBarColor(getResources().getColor(color));
        mColor = color;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    //获取im签名
    private void getImSignature() {

        final Map<String, Object> imMap = new HashMap<>();
        final int uin = (int) SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);
        imMap.put("uin", uin);
        imMap.put("token", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        imMap.put("ver", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_VER, 0));
        imMap.put("identifier", uin);


        YPlayApiManger.getInstance().getZivApiService()
                .getImSignature(imMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImSignatureRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImSignatureRespond imSignatureRespond) {
                        if (imSignatureRespond.getCode() == 0) {
                            String imSig = imSignatureRespond.getPayload().getSig();
                            System.out.println("im签名---" + imSig);
                            if (!TextUtils.isEmpty(imSig)) {
                                imLogin(String.valueOf(uin), imSig);
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * im登录
     *
     * @param identifier 用户账号
     * @param imSig      用户签名
     */
    private void imLogin(final String identifier, final String imSig) {

        System.out.println("mainactivity---identifier" + identifier +
                ",imSig---" + imSig);

        TIMManager.getInstance().login(String.valueOf(identifier),
                imSig,
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        System.out.println("登录错误---" + s + ",错误码---" + i);
                        YPlayConstant.IM_ERROR_CODE = 6208;

                    }

                    @Override
                    public void onSuccess() {
                        System.out.println("mainactivity---登录成功");
                        initMiAndHuaPush();
                    }
                });
    }

    //初始化小米和华为推送
    private void initMiAndHuaPush() {

        //注册小米和华为推送
        String deviceMan = android.os.Build.MANUFACTURER;
        if (deviceMan.equals("Xiaomi") && shouldMiInit()) {
            MiPushClient.registerPush(getApplicationContext(),
                    YPlayConstant.MI_PUSH_APP_ID,
                    YPlayConstant.MI_PUSH_APP_KEY);
        } else if (deviceMan.equals("HUAWEI")) {

            Log.i(TAG, "initMiAndHuaPush: ");
            huaWeiClient = new HuaweiApiClient.Builder(this)
                    .addApi(HuaweiPush.PUSH_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            huaWeiClient.connect();
        }

    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "HuaweiApiClient 连接成功");
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (!this.isDestroyed() && !this.isFinishing()) {
            huaWeiClient.connect();
        }
        Log.i(TAG, "HuaweiApiClient 连接断开");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());
    }

    //获取华为push token
    private void getTokenAsyn() {
        if (!huaWeiClient.isConnected()) {
            Log.i(TAG, "获取token失败，原因：HuaweiApiClient未连接");
            huaWeiClient.connect();
            return;
        }

        Log.i(TAG, "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaWeiClient);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
                Log.i(TAG, "onResult: 华为token---" + result.toString());
            }
        });
    }

    @Override
    public void onMessageUpdate(ImMsg imMsg) {
        Log.i(TAG, "onMessageUpdate: 有消息更新");
        setMessageIcon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        huaWeiClient.disconnect();
    }

    //设置消息图标的点亮
    public void setMessageIcon() {
        Log.i(TAG, "setMessageClear: 消息ICON点亮");

        List<ImSession> imSessionList = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.UnreadMsgNum.gt(0))
                .build().list();
        if (imSessionList != null && imSessionList.size() > 0) {
            Drawable nav_up = getResources().getDrawable(R.drawable.message_yes);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            mainNavBarRight.setCompoundDrawables(null, nav_up, null, null);
            mainNavBarRight.setTextColor(getResources().getColor(R.color.message_title_color));


            Drawable nav_up2 = getResources().getDrawable(R.drawable.message_yes);
            nav_up2.setBounds(0, 0, nav_up2.getMinimumWidth(), nav_up2.getMinimumHeight());
            mainNavRight2.setCompoundDrawables(null, nav_up2, null, null);
        }
    }

    //设置消息icon变暗
    public void setMessageClear() {

        Log.i(TAG, "setMessageClear: 消息ICON清除");


        List<ImSession> imSessionList = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.UnreadMsgNum.gt(0))
                .build().list();
        if (imSessionList == null || imSessionList.size() == 0) {
            Log.i(TAG, "setMessageClear: 为空");
            Drawable nav_up = getResources().getDrawable(R.drawable.message_no);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            mainNavBarRight.setCompoundDrawables(null, nav_up, null, null);
            mainNavBarRight.setTextColor(getResources().getColor(R.color.text_color_gray));


            Drawable nav_up2 = getResources().getDrawable(R.drawable.message_no);
            nav_up2.setBounds(0, 0, nav_up2.getMinimumWidth(), nav_up2.getMinimumHeight());
            mainNavRight2.setCompoundDrawables(null, nav_up2, null, null);

        }
    }


    /*
    //拉取离线会话消息
    private void getOfflineMsgs(){

        System.out.println("获取离线消息");
        List<TIMConversation> offlineList = TIMManagerExt.getInstance().getConversationList();

        for (TIMConversation timCon : offlineList) {

            TIMConversationExt conExt = new TIMConversationExt(timCon);
            conExt.getMessage(YPlayConstant.YPLAY_OFFINE_MSG_COUNT,
                    null,
                    new TIMValueCallBack<List<TIMMessage>>() {
                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onSuccess(List<TIMMessage> timMessages) {
                            System.out.println("IM登录成功，拉取离线消息");
                            ImConfig.getImInstance().updateSession(timMessages);
                        }
                    });

            conExt.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    System.out.println("设置会话已读错误---" + s);
                }

                @Override
                public void onSuccess() {
                    System.out.println("设置会话已读成功");
                }
            });


        }



    }*/

}
