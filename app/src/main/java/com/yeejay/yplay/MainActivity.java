package com.yeejay.yplay;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
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
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.adapter.FragmentAdapter;
import com.yeejay.yplay.answer.FragmentAnswer;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.friend.FragmentFriend;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.message.FragmentMessage;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.model.PushNotifyRespond;
import com.yeejay.yplay.model.UpdateContactsRespond;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.PushUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.io.File;
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
    @BindView(R.id.mainnav_background)
    RelativeLayout mainBottonBackground;

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
    private static final int REQUEST_CODE_PERMISSION_SINGLE_CONTACTS = 101;

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

    private int mPageNum = 1;
    private int mPageSize = 10;

    boolean numberBookAuthoritySuccess = false;
    ContactsInfoDao contactsInfoDao;

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

        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            boolean uuidIsNull = bundle.getBoolean("uuid_is_null");
//            if (uuidIsNull) {
//                getNumberBookAuthority();
//            }
//        }

        getNumberBookAuthority();

        dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();

        initMainView();
        addFragment();
        viewPager.setAdapter(frgAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                System.out.println("当前为第" + i + "页");
                if (i == 0) {
                    mainBottonBackground.setVisibility(View.VISIBLE);
                    feedsFragmentStatus();
                } else if (i == 1) {
                    mainBottonBackground.setVisibility(View.INVISIBLE);
                    playFragmentStatus();
                } else if (i == 2) {
                    mainBottonBackground.setVisibility(View.VISIBLE);
                    messageFragmentStatus();
                }

                setMessageIcon();

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
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

        //申请下读写内部SD卡的权限，然后创建记录日志的文件夹，路径为/storage/emulated/0/yplay/logs
        if (Build.VERSION.SDK_INT >= 23 && MainActivity.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            MainActivity.this.requestPermissions(new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        //创建记录日志的文件夹，路径为/storage/emulated/0/yplay/logs
        String dirStr = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "yplay" + File.separator + "logs";
        File file = new File(dirStr);
        Log.i(TAG, "logs file dirStr = " + dirStr);
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
            Log.i(TAG, "logs file created!");
        }
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i(TAG, "onKeyDown: 返回键");
//            moveTaskToBack(true);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;//不执行父类点击事件
        }

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
                            if (myInfo != null) {

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
        if (mPageNum == 1) {
            dbHelper.deleteFriendInfoAll();
        }
        Log.i(TAG, "getMyFriendsList: 493");
        Map<String, Object> myFriendsMap = new HashMap<>();

        myFriendsMap.put("pageNum", mPageNum);
        myFriendsMap.put("pageSize", mPageSize);
        myFriendsMap.put("uin", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_UIN, 0));
        myFriendsMap.put("token", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myFriendsMap.put("ver", SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_VER, 0));
        Log.i(TAG, "getMyFriendsList: 501");
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
                        if (tempList == null || tempList.size() == 0) {
                            return;
                        }

                        FriendInfo dataBaseFriendInfo;
//                        insertFriendInfoNum += tempList.size();
                        for (FriendsListRespond.PayloadBean.FriendsBean friendInfo : tempList) {
                            dataBaseFriendInfo = dbHelper.queryFriendInfo(friendInfo.getUin());
                            if (dataBaseFriendInfo == null) {
//                                Log.i(TAG, "onNext: insertFriendInfo--" + dataBaseFriendInfo);
                                dbHelper.insertFriendInfo(dbHelper.NetworkFriendInfo2DbFriendInfo(friendInfo));
                            } else {
                                Log.i(TAG, "onNext: updateFriendInfo---" + dataBaseFriendInfo);

                                dbHelper.updateFriendInfo(dataBaseFriendInfo, friendInfo);
                            }
                        }
                        if ((mPageNum * mPageSize) >= total) {
                            return;
                        } else {
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
                        Log.i(TAG, "onComplete: 554");
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
        if (huaWeiClient != null) {
            huaWeiClient.disconnect();
        }

        unregisterReceiver(messageBr);
    }

    //设置消息icon的点亮
    public void setMessageIcon() {

        int uin = (int) SharePreferenceUtil.get(MainActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);

        List<ImSession> imSessionList = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.Uin.eq(uin))
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
        } else {
            Log.i(TAG, "setMessageIcon: ");
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
    public void setFriendCount() {
        fragmentAnswer.setFriendCount();
        fragmentFriend.setFriendCount();
        fragmentMessage.setFriendCount();
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

    //获取通讯录权限
    private void getNumberBookAuthority() {

        AndPermission.with(MainActivity.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_CONTACTS)
                .permission(Permission.CONTACTS)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .start();
    }

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    Log.i(TAG, "onSucceed: 通讯录权限成功");
                    getContacts();
                    break;
            }

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    Log.i(TAG, "onFailed: 通讯录权限失败");
                    getContacts();
                    break;
            }

            if (numberBookAuthoritySuccess) {
                Log.i(TAG, "onFailed: 读到通讯录权限了numberBookAuthoritySuccess---" + numberBookAuthoritySuccess);
            } else {
                if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                    if (requestCode == REQUEST_CODE_PERMISSION_SINGLE_CONTACTS) {
                        AndPermission.defaultSettingDialog(MainActivity.this, 400).show();
                    }

                }
            }
        }
    };

    private void getContacts() {

        if (Build.VERSION.SDK_INT >= 23
                && MainActivity.this.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {

            ContentResolver mContentResolver = getContentResolver();
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            Uri dataUri = Uri.parse("content://com.android.contacts/data");

            int counter = 0;

            //如果有权限count就++
            if (!TextUtils.isEmpty(uri.toString())) {
                counter++;
            }

            String id;
            String contactName;
            String contactNumber;
            String contactSortKey;
            List<com.yeejay.yplay.greendao.ContactsInfo> currentContactsList = new ArrayList<>();

            Cursor cursor = mContentResolver.query(uri, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {

                id = cursor.getString(cursor.getColumnIndex("_id"));
                contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                contactSortKey = cursor.getString(cursor.getColumnIndex("phonebook_label"));

                Cursor dataCursor = mContentResolver.query(dataUri, null, "raw_contact_id= ?", new String[]{id}, null);
                while (dataCursor != null && dataCursor.moveToNext()) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if (type.equals("vnd.android.cursor.item/phone_v2")) {//如果得到的mimeType类型为手机号码类型才去接收
                        contactNumber = dataCursor.getString(dataCursor.getColumnIndex("data1"));//获取手机号码
                        String filterContactNumber = BaseUtils.filterUnNumber(contactNumber);
                        com.yeejay.yplay.greendao.ContactsInfo contactsInfo = new com.yeejay.yplay.greendao.ContactsInfo(null, contactName, filterContactNumber, null, 1, contactSortKey, null, null);
                        currentContactsList.add(contactsInfo);
                    }
                }
                dataCursor.close();
                counter++;
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

            if (counter > 0) {
                numberBookAuthoritySuccess = true;
                //比较本地通讯录的变更
                compareContacts(currentContactsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //比较本地通讯录的变更
    private void compareContacts(List<com.yeejay.yplay.greendao.ContactsInfo> currentContactsList) {

        Log.i(TAG, "compareContacts: 比较通讯录的变更");
        List<com.yeejay.yplay.greendao.ContactsInfo> localContactsList = contactsInfoDao.loadAll();
        int localSize = localContactsList.size();   //旧通讯录的size
        int currentSize = currentContactsList.size();   //新通讯库的size

        Map<String, Integer> map = new HashMap<>(localSize + currentSize);
        for (com.yeejay.yplay.greendao.ContactsInfo contactsInfo : localContactsList) {
            map.put(contactsInfo.getOrgPhone(), 1);
        }
        for (com.yeejay.yplay.greendao.ContactsInfo contactsInfo : currentContactsList) {
            Integer cc = map.get(contactsInfo.getOrgPhone());
            if (cc != null) {
                map.put(contactsInfo.getOrgPhone(), ++cc);
                continue;
            }
            map.put(contactsInfo.getOrgPhone(), 3);
        }

        List<String> deleteList = new ArrayList<>();
        List<String> addList = new ArrayList<>();

        //  1 删除  2 不变   3 新增
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {             //删除的记录
                deleteList.add(entry.getKey());
            } else if (entry.getValue() == 3) {       //新增的记录
                addList.add(entry.getKey());
            }
        }

        List<com.yeejay.yplay.model.ContactsInfo> deleteContactsList = new ArrayList<>();
        List<com.yeejay.yplay.model.ContactsInfo> addContactsList = new ArrayList<>();

        if (deleteList.size() > 0) {
            int hadleType = 1;
            for (String str : deleteList) {
                Log.i(TAG, "compareContacts: 要删除元素---" + str);
                ContactsInfo contactsInfo = contactsInfoDao.queryBuilder()
                        .where(ContactsInfoDao.Properties.OrgPhone.eq(str))
                        .build().unique();
                if (contactsInfo != null) {
                    contactsInfoDao.delete(contactsInfo);
                    deleteContactsList.add(new com.yeejay.yplay.model.ContactsInfo(contactsInfo.getName(), contactsInfo.getOrgPhone()));
                }
            }

            dealBySubList(deleteContactsList, 100, hadleType);
        }

        if (addList.size() > 0) {
            int hadleType = 3;
            for (String str : addList) {
                Log.i(TAG, "compareContacts: 要增加元素---" + str);
                for (int i = 0; i < currentSize; i++) {
                    com.yeejay.yplay.greendao.ContactsInfo contactsInfo = currentContactsList.get(i);
                    if (str.equals(contactsInfo.getOrgPhone())) {
                        //插入数据库
                        com.yeejay.yplay.greendao.ContactsInfo tempContacts = contactsInfoDao.queryBuilder()
                                .where(ContactsInfoDao.Properties.OrgPhone.eq(contactsInfo.getOrgPhone()))
                                .build().unique();
                        if (tempContacts == null) {
                            contactsInfoDao.insert(new ContactsInfo(null, contactsInfo.getName(), contactsInfo.getOrgPhone(), null, 1, contactsInfo.getSortKey(), null, null));
                        }
                        //加入到addList
                        addContactsList.add(new com.yeejay.yplay.model.ContactsInfo(contactsInfo.getName(), contactsInfo.getOrgPhone()));
                        Log.i(TAG, "compareContacts: addContactsInfo---" + contactsInfo.getName() + "---" + contactsInfo.getOrgPhone());
                    }
                }
            }

            //更新通讯录
            dealBySubList(addContactsList, 100, hadleType);
        }


    }

    /**
     * 通过list的     subList(int fromIndex, int toIndex)方法实现
     *
     * @param sourList   源list
     * @param batchCount 分组条数
     */
    private void dealBySubList(List<com.yeejay.yplay.model.ContactsInfo> sourList, int batchCount, int handleType) {
        int sourListSize = sourList.size();
        Log.i(TAG, "dealBySubList: sourListSize---" + sourListSize);
        int subCount = sourListSize % batchCount == 0 ? sourListSize / batchCount : sourListSize / batchCount + 1;
        Log.i(TAG, "dealBySubList: 循环上传的次数---" + subCount);
        int startIndext = 0;
        int stopIndext = 0;
        for (int i = 0; i < subCount; i++) {
            stopIndext = (i == subCount - 1) ? stopIndext + sourListSize % batchCount : stopIndext + batchCount;
            List<com.yeejay.yplay.model.ContactsInfo> tempList = new ArrayList<>(sourList.subList(startIndext, stopIndext));
            startIndext = stopIndext;
            if (handleType == 1) {       //删除
                deleteContacts(tempList);
            } else if (handleType == 3) { //增加
                updateContacts(tempList);
            }

        }
    }

    //更新通讯录
    private void updateContacts(List<com.yeejay.yplay.model.ContactsInfo> contactsInfoList) {

        Map<String, Object> contactsMap = new HashMap<>();
        String contactString = GsonUtil.GsonString(contactsInfoList);
        String encodedString = Base64.encodeToString(contactString.getBytes(), Base64.DEFAULT);
        contactsMap.put("data", encodedString);
        contactsMap.put("uin", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, 0));
        contactsMap.put("token", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contactsMap.put("ver", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .updateContacts(contactsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateContactsRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull UpdateContactsRespond baseRespond) {

                        if (baseRespond.getCode() == 0) {
                            Log.i(TAG, "onNext: baseRespond---" + baseRespond.toString());
                            List<UpdateContactsRespond.PayloadBean.InfosBean> infoList = baseRespond.getPayload().getInfos();
                            updateSuccessHandle(infoList);

                        } else {
                            Log.i(TAG, "onNext: 更新通讯录失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.i(TAG, "onError:更新通讯录失败---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //删除通讯录记录
    private void deleteContacts(List<com.yeejay.yplay.model.ContactsInfo> contactsInfoList) {

        Map<String, Object> contactsMap = new HashMap<>();
        String contactString = GsonUtil.GsonString(contactsInfoList);
        String encodedString = Base64.encodeToString(contactString.getBytes(), Base64.DEFAULT);
        contactsMap.put("data", encodedString);
        contactsMap.put("uin", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, 0));
        contactsMap.put("token", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contactsMap.put("ver", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .removeContacts(contactsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateContactsRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull UpdateContactsRespond baseRespond) {

                        if (baseRespond.getCode() == 0) {
                            int cnt = baseRespond.getPayload().getCnt();
                            Log.i(TAG, "onNext: 删除记录的条数---" + cnt);

                        } else {
                            Log.i(TAG, "onNext: 删除通讯录失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.i(TAG, "onError:删除通讯录失败---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //上传通讯录成功后更新数据库数据
    private void updateSuccessHandle(List<UpdateContactsRespond.PayloadBean.InfosBean> infoList) {
        for (UpdateContactsRespond.PayloadBean.InfosBean infosBean : infoList) {
            ContactsInfo contactsInfo = contactsInfoDao.queryBuilder()
                    .where(ContactsInfoDao.Properties.OrgPhone.eq(infosBean.getOrgPhone()))
                    .build().unique();
            if (contactsInfo != null) {
                contactsInfo.setPhone(infosBean.getPhone());
                contactsInfo.setUin(infosBean.getUin());
                if (!TextUtils.isEmpty(infosBean.getNickName())){
                    contactsInfo.setNiclName(infosBean.getNickName());
                }
                if (!TextUtils.isEmpty(infosBean.getHeadImgUrl())){
                    contactsInfo.setHeadImgUrl(infosBean.getHeadImgUrl());
                }
                contactsInfoDao.update(contactsInfo);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 400: { // 这个400就是上面defineSettingDialog()的第二个参数。
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                getContacts();
                break;
            }
        }
    }

}
