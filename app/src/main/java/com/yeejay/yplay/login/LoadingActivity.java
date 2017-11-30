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

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.tencent.imsdk.TIMCallBack;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoadingActivity extends BaseActivity implements TIMCallBack,
        HuaweiApiClient.ConnectionCallbacks,
        HuaweiApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoadingActivity";
    private static final int LOGIN_CODE = 100;
    private static final int NETWORK_CODE = 101;

    private int uin;
    private String token;
    private int ver;

    private HuaweiApiClient huaWeiClient;

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
                        Toast.makeText(LoadingActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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

        uin = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);
        token = (String) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_TOKEN, (String) "");
        ver = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_VER, (int) 0);

        System.out.println("token---" + token);

        if (uin == 0 || TextUtils.isEmpty(token) || ver == 0) {
            handler.sendEmptyMessageDelayed(LOGIN_CODE, 1000);
        } else {
            handler.sendEmptyMessageDelayed(NETWORK_CODE, 1000);
        }

        huaWeiClient = new HuaweiApiClient.Builder(this)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        huaWeiClient.connect();
    }

    //获取自己的资料
    private void getMyInfo(int uin, String token, int ver) {

        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", uin);
        myInfoMap.put("token", token);
        myInfoMap.put("ver", ver);
        YPlayApiManger.getInstance().getZivApiService()
                .getMyInfo(myInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UserInfoResponde userInfoResponde) {
                        System.out.println("获取自己的资料---" + userInfoResponde.toString());
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
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取自己的资料异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onError(int i, String s) {
        System.out.println("im回调错误---" + s);
    }

    @Override
    public void onSuccess() {
        System.out.println("im回调成功---");
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

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification(){
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        huaWeiClient.disconnect();
    }


    private void getTokenAsyn() {
        if(!huaWeiClient.isConnected()) {
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

}
