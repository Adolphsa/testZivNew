package com.yeejay.yplay.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.customview.CountDownTimer;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Login extends AppCompatActivity {

    EditText mEdtPhoneNumber;
    EditText mEdtAuthCode;
    Button mBtnAuthCode;
    Button mBtnNext;
    Button mBtnBack;

    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {  //按钮倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnAuthCode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            mBtnAuthCode.setEnabled(true);
            mBtnAuthCode.setText(R.string.login_get_auth_code);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtPhoneNumber = (EditText) findViewById(R.id.login_edt_number);
        mEdtAuthCode = (EditText) findViewById(R.id.login_edt_auth_code);
        mBtnAuthCode = (Button) findViewById(R.id.login_get_auth_code);
        mBtnNext = (Button) findViewById(R.id.login_btn_next);
        mBtnBack = (Button) findViewById(R.id.layout_title_back);
        mBtnBack.setVisibility(View.INVISIBLE);

        if ((long)SharePreferenceUtil.get(Login.this,YPlayConstant.YPLAY_UUID,(long)0) == 0){
            System.out.println("第一次为零");
            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_UUID,System.currentTimeMillis());
        }


        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //监听手机号输入栏的变化
        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    mBtnAuthCode.setEnabled(true);
                } else {
                    mBtnAuthCode.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        //监听验证码输入栏的变化
        mEdtAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mBtnNext.setEnabled(true);
                } else {
                    mBtnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //获取验证码按钮
        mBtnAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtil.isNetWorkAvailable(Login.this)){
                    countDownTimer.start();
                    mBtnAuthCode.setEnabled(false);
                    sendSms(mEdtPhoneNumber.getText().toString());
                }else {
                    Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //下一步
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtil.isNetWorkAvailable(Login.this)){
                    long uuid = (long)SharePreferenceUtil.get(Login.this,YPlayConstant.YPLAY_UUID,(long)0);
                    System.out.println("uuid---" + uuid);
                    login(mEdtPhoneNumber.getText().toString(), mEdtAuthCode.getText().toString(),uuid);
                }else {
                    Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //跳转逻辑判断
    private void jumpToWhere(LoginRespond.PayloadBean.InfoBean infoBean){
        //判断年龄
        int age = infoBean.getAge();
        System.out.println("年龄---" + age);
        if (age == 0){
            startActivity(new Intent(Login.this,LoginAge.class));
        }
        //判断年级
        int grade = infoBean.getGrade();
        if (grade == 0){
            startActivity(new Intent(Login.this,LoginAuthorization.class));
        }
        //判断学校信息
        int schoolId = infoBean.getSchoolId();
        if (schoolId == 0){
            startActivity(new Intent(Login.this,LoginAuthorization.class));
        }
        //判断性别
        int gender = infoBean.getGender();
        if (gender == 0){
            startActivity(new Intent(Login.this,ChoiceSex.class));
        }
        //判断基本信息
        String name = infoBean.getNickName();
        if (TextUtils.isEmpty(name)){
            startActivity(new Intent(Login.this,UserInfo.class));
        }

        startActivity(new Intent(Login.this, MainActivity.class));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白处隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Login.this.getCurrentFocus() != null) {
                if (Login.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(Login.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    //获取验证码
    private void sendSms(String phoneNumber) {

        YPlayApiManger.getInstance().getZivApiService()
                .sendMessage(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("发送短信返回---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("发送短信返回错误---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //登录
    private void login(String phoneNumber, String code, long uuid) {

        YPlayApiManger.getInstance().getZivApiService()
                .login(phoneNumber, code, uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull LoginRespond loginRespond) {
                        System.out.println("登录返回---" + loginRespond.toString());
                        if (loginRespond.getCode() == 0) {
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_UIN, loginRespond.getPayload().getUin());
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_TOKEN, loginRespond.getPayload().getToken());
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_VER, loginRespond.getPayload().getVer());

                            if (loginRespond.getPayload().getIsNewUser() == 1){
                                startActivity(new Intent(Login.this, LoginAge.class));
                            }else {
                                //保存数据
                                saveData(loginRespond.getPayload().getInfo());
                                //逻辑跳转
                                jumpToWhere(loginRespond.getPayload().getInfo());
                            }
                        } else {
                            Toast.makeText(Login.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("登录返回错误---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void saveData(LoginRespond.PayloadBean.InfoBean infoBean){
        //年级
        SharePreferenceUtil.put(Login.this,YPlayConstant.TEMP_GRADE,infoBean.getGrade());
        //学校
        SharePreferenceUtil.put(Login.this,YPlayConstant.TEMP_SCHOOL_ID,infoBean.getSchoolId());
        //性别
        SharePreferenceUtil.put(Login.this,YPlayConstant.TEMP_GENDER,infoBean.getGender());
        //姓名
        SharePreferenceUtil.put(Login.this,YPlayConstant.TEMP_NICK_NAME,infoBean.getNickName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
