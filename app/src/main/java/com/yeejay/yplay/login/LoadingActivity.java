package com.yeejay.yplay.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoadingActivity extends AppCompatActivity {

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
                    getMyInfo(uin, token, ver);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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

}
