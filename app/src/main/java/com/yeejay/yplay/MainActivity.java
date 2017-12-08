package com.yeejay.yplay;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.friend.FragmentFriend;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.message.FragmentMessage;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.model.PushNotifyRespond;
import com.yeejay.yplay.utils.PushUtil;
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
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements HuaweiApiClient.ConnectionCallbacks,
        HuaweiApiClient.OnConnectionFailedListener {

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
    }

    //右一
    @OnClick(R.id.main_nav_bar_right)
    public void rightButton(View view) {
        viewPager.setCurrentItem(2);
    }

    //左二
    @OnClick(R.id.main_nav_bar_left2)
    public void leftButton2(View view) {
        viewPager.setCurrentItem(0);
    }


    //中间
    @OnClick(R.id.main_nav_center2)
    public void ceneterButton2(View view) {
        viewPager.setCurrentItem(1);
    }

    //右二
    @OnClick(R.id.main_nav_right2)
    public void rightButton2(View view) {
        viewPager.setCurrentItem(2);
    }

    private static final String TAG = "MainActivity";

    FragmentAdapter frgAdapter;
    FragmentFriend fragmentFriend;
    FragmentAnswer fragmentAnswer;
    FragmentMessage fragmentMessage;
    private HuaweiApiClient huaWeiClient;
    ImSessionDao imSessionDao;
    MyInfoDao myInfoDao;

    private int mColor;
    private boolean isNewFeeds = false;

    private DbHelper dbHelper;
    private int insertFriendInfoNum = 0;
    private int mPageNum = 1;

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public boolean isNewFeeds() {
        return isNewFeeds;
    }

    public void setNewFeeds(boolean newFeeds) {
        isNewFeeds = newFeeds;
    }

    BroadcastReceiver messageBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int brType = intent.getIntExtra("broadcast_type", 0);
            if (1 == brType) {   //消息广播
                setMessageIcon();
            } else if (3 == brType) { //加好友
                Log.i(TAG, "onReceive: 加好友");
                setFriendCount();
            } else if (5 == brType) { //动态
                setFeedIcon();
                setNewFeeds(true);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();

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
            public void onPageScrollStateChanged(int i) {}
        });

        IntentFilter filter = new IntentFilter("messageService");
        registerReceiver(messageBr, filter);

        //获取签名
        getImSignature();
        //获取加好友的人数
        getAddFreindCount();
        setMessageIcon();
//        setFeedIcon();

        getMyFriendsList();
        Log.i(TAG, "onCreate: mainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearNotification();
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

    //获取请求加好友的人数
    public void getAddFreindCount() {

        Map<String, Object> tempMap = new HashMap<>();
        final int uin = (int) SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);
        tempMap.put("uin", uin);
        tempMap.put("token", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        tempMap.put("ver", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .getNewNotify(tempMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PushNotifyRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PushNotifyRespond pushNotifyRespond) {
                        Log.i(TAG, "onNext: pushNotifyRespond---" + pushNotifyRespond.toString());
                        if (0 == pushNotifyRespond.getCode()) {
                            int newCount = pushNotifyRespond.getPayload().getNewAddFriendMsgCnt();
                            MyInfo myInfo = myInfoDao.queryBuilder()
                                    .where(MyInfoDao.Properties.Uin.eq(uin))
                                    .build().unique();
                            if (myInfo != null){

                                int addFriendNum = myInfo.getAddFriendNum();
                                Log.i(TAG, "onNext: addFriendNum---" + addFriendNum);
                                if (addFriendNum != newCount)
                                addFriendNum = newCount;

                                myInfo.setAddFriendNum(addFriendNum);
                                myInfoDao.update(myInfo);
                                setFriendCount();
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

    //插入我的好友列表到数据库
    private void getMyFriendsList() {

        Log.i(TAG, "getMyFriendsList---mPageNum=" + mPageNum);
        if (mPageNum == 1){
            dbHelper.deleteFriendInfoAll();
        }

        Map<String, Object> myFriendsMap = new HashMap<>();
        myFriendsMap.put("pageNum", mPageNum);
        myFriendsMap.put("uin", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_UIN, 0));
        myFriendsMap.put("token", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myFriendsMap.put("ver", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getMyFriendsList(myFriendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendsListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull FriendsListRespond friendsListRespond) {
                        Log.i(TAG, "onNext: friendsListRespond---" + friendsListRespond.toString());
                        if (friendsListRespond.getCode() != 0) {
                            return;
                        }
                        int total = friendsListRespond.getPayload().getTotal();
                        List<FriendsListRespond.PayloadBean.FriendsBean> tempList
                                = friendsListRespond.getPayload().getFriends();
                        if (tempList == null || tempList.size() == 0){
                            return;
                        }

                        FriendInfo dataBaseFriendInfo;
                        insertFriendInfoNum += tempList.size();
                        for (FriendsListRespond.PayloadBean.FriendsBean friendInfo : tempList) {
                            dataBaseFriendInfo = dbHelper.queryFriendInfo(friendInfo.getUin());
                            if (dataBaseFriendInfo == null){
                                Log.i(TAG, "onNext: insertFriendInfo--" + dataBaseFriendInfo);
                                dbHelper.insertFriendInfo(dbHelper.NetworkFriendInfo2DbFriendInfo(friendInfo));
                            }else {
                                Log.i(TAG, "onNext: updateFriendInfo---" + dataBaseFriendInfo);

                                dbHelper.updateFriendInfo(dataBaseFriendInfo,friendInfo);
                            }
                        }
                        if (insertFriendInfoNum >= total){
                            return;
                        }else {
                            mPageNum++;
                            getMyFriendsList();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                    }
                });
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
    protected void onDestroy() {
        super.onDestroy();
        if (huaWeiClient != null){
            huaWeiClient.disconnect();
        }

        unregisterReceiver(messageBr);
    }

    //设置消息icon的点亮
    public void setMessageIcon() {

        List<ImSession> imSessionList = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.UnreadMsgNum.gt(0))
                .build().list();

        if (imSessionList != null && imSessionList.size() > 0) {
            Log.i(TAG, "setMessageIcon: 消息ICON点亮---" + imSessionList.size());
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
            mainNavBarRight.setTextColor(getResources().getColor(R.color.white));

            Drawable nav_up2 = getResources().getDrawable(R.drawable.message_no);
            nav_up2.setBounds(0, 0, nav_up2.getMinimumWidth(), nav_up2.getMinimumHeight());
            mainNavRight2.setCompoundDrawables(null, nav_up2, null, null);

        }
    }

    //动态图标点亮
    public void setFeedIcon() {

        Log.i(TAG, "setFeedIcon: 动态图标点亮");
        Drawable nav_up = getResources().getDrawable(R.drawable.feeds_icon_yes);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mainNavBarLeft.setCompoundDrawables(null, nav_up, null, null);
        mainNavBarLeft.setTextColor(getResources().getColor(R.color.feeds_title_color));

        Drawable nav_up2 = getResources().getDrawable(R.drawable.feeds_icon_yes);
        nav_up2.setBounds(0, 0, nav_up2.getMinimumWidth(), nav_up2.getMinimumHeight());
        mainNavBarLeft2.setCompoundDrawables(null, nav_up2, null, null);
    }

    //动态图标清除
    public void setFeedClear() {
        Log.i(TAG, "setFeedClear: 动态图标清除");
        Drawable nav_up = getResources().getDrawable(R.drawable.feeds_icon_no);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mainNavBarLeft.setCompoundDrawables(null, nav_up, null, null);
        mainNavBarLeft.setTextColor(getResources().getColor(R.color.white));

        Drawable nav_up2 = getResources().getDrawable(R.drawable.feeds_icon_no);
        nav_up2.setBounds(0, 0, nav_up2.getMinimumWidth(), nav_up2.getMinimumHeight());
        mainNavBarLeft2.setCompoundDrawables(null, nav_up2, null, null);
    }


    //设置加好友的个数
    public void setFriendCount(){
        fragmentAnswer.setFriendCount();
        fragmentFriend.setFriendCount();
        fragmentFriend.setFriendCount();
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());

        //设置华为角标为0
        PushUtil.setHuaWeiBadgenumber(0);
        PushUtil.resetPushNum();
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
