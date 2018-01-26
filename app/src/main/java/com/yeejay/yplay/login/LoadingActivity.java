package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends BaseActivity implements TIMCallBack {

    private static final String TAG = "LoadingActivity";
    private static final int LOGIN_CODE = 100;
    private static final int NETWORK_CODE = 101;

    private int uin;
    private String token;
    private int ver;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_CODE:
                    startActivity(new Intent(LoadingActivity.this, Login.class));
                    break;
                case NETWORK_CODE:
                    if (NetWorkUtil.isNetWorkAvailable(LoadingActivity.this)) {
                        getMyInfo(uin, token, ver);
                    } else {
                        Toast.makeText(LoadingActivity.this, R.string.base_no_internet, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getWindow().setStatusBarColor(getResources().getColor(R.color.loading_color));

        clearNotification();
        init();
    }

    private void init() {

        uin = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);
        token = (String) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_TOKEN, (String) "");
        ver = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_VER, (int) 0);

        Log.i(TAG, "init: token---" + token);

        if (uin == 0 || TextUtils.isEmpty(token) || ver == 0) {
            handler.sendEmptyMessageDelayed(LOGIN_CODE, 500);
        } else {
            handler.sendEmptyMessageDelayed(NETWORK_CODE, 500);
        }

    }

    //获取自己的资料
    private void getMyInfo(int uin, String token, int ver) {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_MY_INFO_URL;
        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", uin);
        myInfoMap.put("token", token);
        myInfoMap.put("ver", ver);

        WnsAsyncHttp.wnsRequest(url, myInfoMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 我的资料---" + result);
                handleMyInfoRespond(result);
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });

    }

    //处理我的资料返回
    private void handleMyInfoRespond(String result) {

        UserInfoResponde userInfoResponde = GsonUtil.GsonToBean(result, UserInfoResponde.class);
        if (userInfoResponde.getCode() == 0) {
            UserInfoResponde.PayloadBean.InfoBean infoBean = userInfoResponde.getPayload().getInfo();
            SharePreferenceUtil.put(LoadingActivity.this,
                    YPlayConstant.YPLAY_USER_NAME, userInfoResponde.getPayload().getInfo().getUserName());
            SharePreferenceUtil.put(LoadingActivity.this,
                    YPlayConstant.YPLAY_NICK_NAME, userInfoResponde.getPayload().getInfo().getNickName());

            if (infoBean.getAge() == 0 ||
                    infoBean.getGrade() == 0 ||
                    infoBean.getSchoolId() == 0 ||
                    infoBean.getGender() == 0 ||
                    TextUtils.isEmpty(infoBean.getNickName())) {
                startActivity(new Intent(LoadingActivity.this, Login.class));

            } else {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            }

        } else if (userInfoResponde.getCode() == 11002) {
            startActivity(new Intent(LoadingActivity.this, Login.class));
        }
    }


    @Override
    public void onError(int i, String s) {
        Log.i(TAG, "onError: im回调错误 " + s);
    }

    @Override
    public void onSuccess() {
        Log.i(TAG, "onError: im回调成功");
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
