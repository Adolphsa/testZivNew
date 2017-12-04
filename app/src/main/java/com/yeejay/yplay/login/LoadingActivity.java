package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoadingActivity extends BaseActivity implements TIMCallBack {

    private static final String TAG = "LoadingActivity";
    private static final int LOGIN_CODE = 100;
    private static final int NETWORK_CODE = 101;

    private int uin;
    private String token;
    private int ver;


    private final int REQUEST_PHONE_PERMISSIONS = 0;

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
        final List<String> permissionsList = new ArrayList<>();
        getWindow().setStatusBarColor(getResources().getColor(R.color.loading_color));

        clearNotification();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
//            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionsList.size() == 0){
//                init();
//            }else{
//                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                        REQUEST_PHONE_PERMISSIONS);
//            }
//        }else{
//            init();
//        }

        init();
    }

    private void init() {

        uin = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_UIN, (int) 0);
        token = (String) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_TOKEN, (String) "");
        ver = (int) SharePreferenceUtil.get(LoadingActivity.this, YPlayConstant.YPLAY_VER, (int) 0);

        System.out.println("token---" + token);

        if (uin == 0 || TextUtils.isEmpty(token) || ver == 0) {
            handler.sendEmptyMessageDelayed(LOGIN_CODE, 1000);
        } else {
            handler.sendEmptyMessageDelayed(NETWORK_CODE, 1000);
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PHONE_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    init();
//                } else {
//                    Toast.makeText(this, getString(R.string.need_permission),Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

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
