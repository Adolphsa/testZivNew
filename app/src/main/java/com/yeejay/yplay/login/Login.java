package com.yeejay.yplay.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.BuildConfig;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CountDownTimer;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ContactsInfo;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
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

public class Login extends BaseActivity {

    private static final String TAG = "Login";

    //    @BindView(R.id.login_scroll_view)
//    ScrollView loginScrollView;
//    @BindView(R.id.login_root_view)
//    LinearLayout rootView;
    @BindView(R.id.login_edt_number)
    EditText mEdtPhoneNumber;
    @BindView(R.id.login_edt_auth_code)
    EditText mEdtAuthCode;
    @BindView(R.id.login_get_auth_code)
    Button mBtnAuthCode;
    @BindView(R.id.login_btn_next)
    Button mBtnNext;
    @BindView(R.id.test_tv1)
    TextView testTv1;
    @BindView(R.id.test_tv2)
    TextView testTv2;


    @OnClick(R.id.login_edt_number)
    public void loginPhone() {
    }

    @OnClick(R.id.login_edt_auth_code)
    public void loginAuthCode() {
    }

    //用户协议和隐私政策
    @OnClick(R.id.test_tv2)
    public void userPrivacy() {
        startActivity(new Intent(Login.this, UserPrivacy.class));
    }

    MyInfoDao myInfoDao;

    List<ContactsInfo> mContactsList;
    boolean uuidIsNull = false;
    long uuid;
    private int uin;
    private int ver;
    private String token;

    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {  //按钮倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnAuthCode.setTextColor(getResources().getColor(R.color.black50));
            mBtnAuthCode.setText("发送" + millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            mBtnAuthCode.setEnabled(true);
            mBtnAuthCode.setText(R.string.login_get_auth_code);
            mBtnAuthCode.setTextColor(getResources().getColor(R.color.black));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.loading_color));

        //删掉权限页面临时保存的数据
        SharePreferenceUtil.remove(Login.this, "temp_lat");
        SharePreferenceUtil.remove(Login.this, "temp_lon");
        SharePreferenceUtil.remove(Login.this, "temp_book");
        SharePreferenceUtil.remove(Login.this, "temp_location");

        mContactsList = new ArrayList<>();
        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        testTv2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        uuid = System.currentTimeMillis();

        Log.i(TAG, "onCreate: unique code---" + BaseUtils.getUniquePsuedoID());

        if ((long) SharePreferenceUtil.get(Login.this, YPlayConstant.YPLAY_UUID, (long) 0) == 0) {
            uuidIsNull = true;
            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_UUID, uuid);
            Log.i(TAG, "onCreate: uuid---" + uuid);
        }

        //监听手机号输入栏的变化
        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 11) {
                    System.out.println("手机号码的长度---" + s.toString());
                    mBtnAuthCode.setEnabled(true);
                    mBtnAuthCode.setTextColor(getResources().getColor(R.color.black));
                    mBtnAuthCode.setText("发验证码");
                    countDownTimer.cancel();

                    //判断验证码的长度
                    String authCode = mEdtAuthCode.getText().toString();
                    if (!TextUtils.isEmpty(authCode) && authCode.length() == 4) {
                        mBtnNext.setEnabled(true);
                        mBtnNext.setTextColor(getResources().getColor(R.color.white));
                    }

                } else {
                    mBtnAuthCode.setEnabled(false);
                    mBtnAuthCode.setTextColor(getResources().getColor(R.color.black50));

                    mBtnNext.setEnabled(false);
                    mBtnNext.setTextColor(getResources().getColor(R.color.text_color_gray2));
                }
            }


        });
        //监听验证码输入栏的变化
        mEdtAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4 && mEdtPhoneNumber.getText().toString().length() == 11) {
                    mBtnNext.setEnabled(true);
                    mBtnNext.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mBtnNext.setEnabled(false);
                    mBtnNext.setTextColor(getResources().getColor(R.color.text_color_gray2));
                }
            }
        });

        //获取验证码按钮
        mBtnAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtil.isNetWorkAvailable(Login.this)) {
                    countDownTimer.start();
                    mBtnAuthCode.setEnabled(false);
                    sendSms(mEdtPhoneNumber.getText().toString());
                } else {
                    Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //下一步
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtil.isNetWorkAvailable(Login.this)) {
                    long uuid = (long) SharePreferenceUtil.get(Login.this, YPlayConstant.YPLAY_UUID, (long) 0);
                    String os = getOsTyep();
                    String appVersion = getVersion();
                    Log.i(TAG, "onClick: uuid---" + uuid + " , device name = " + android.os.Build.MODEL
                            + " , os = " + os + " , appVersion = " + appVersion);
                    login(mEdtPhoneNumber.getText().toString(), mEdtAuthCode.getText().toString(), uuid,
                            android.os.Build.MODEL, os, appVersion);
                } else {
                    Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private String getVersion() {
        return  BuildConfig.VERSION_NAME +
                "_" + BuildConfig.BUILD_TIMESTAMP + "_beta";
    }

    private String getOsTyep() {
        String sdk = android.os.Build.VERSION.SDK;
        if(TextUtils.isEmpty(sdk)) {
            return "android_" + android.os.Build.VERSION.RELEASE;
        }
        return "android_" + android.os.Build.VERSION.RELEASE + "_" + sdk;
    }

    //跳转逻辑判断
    private void jumpToWhere(int tempAge, int tempGrade, int tempSchoolId, int tempGender, String tempNickName) {
        //判断年龄
//        int age = tempAge;
//        int grade = tempGrade;
//        int schoolId = tempSchoolId;
//        int gender = tempGender;
//        String name = tempNickName;

        System.out.println("年龄---" + tempAge);
        if (tempAge == 0 ||
                tempGrade == 0 ||
                tempSchoolId == 0 ||
                tempGender == 0 ||
                TextUtils.isEmpty(tempNickName)) {
            startActivity(new Intent(Login.this, LoginAge.class));
            //重新登录检查权限
            return;
        }

        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("uuid_is_null", true);
        startActivity(intent);

//        //年龄
//        if (age == 0 ) {
//            startActivity(new Intent(Login.this, LoginAge.class));
//            return;
//        }
//
//        //权限
//        getContacts();
//        getLonLat();
//        if (!addressAuthoritySuccess || !numberBookAuthoritySuccess){
//            startActivity(new Intent(Login.this,LoginAuthorization.class));
//            return;
//        }
//
//        //判断年级
//        if (grade == 0){
//            startActivity(new Intent(Login.this,ClassList.class));
//            return;
//        }
//
//        //判断学校信息
//        if (schoolId == 0){
//            startActivity(new Intent(Login.this,ClassList.class));
//            return;
//        }
//        //判断性别
//        if (gender == 0){
//            startActivity(new Intent(Login.this,ChoiceSex.class));
//            return;
//        }
//        //判断基本信息
//        if (TextUtils.isEmpty(name)){
//            startActivity(new Intent(Login.this,UserInfo.class));
//            return;
//        }


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
                    public void onSubscribe(@NonNull Disposable d) {
                    }

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

    //插入uin到数据库
    private void insertUin(LoginRespond.PayloadBean payloadBean) {

        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(payloadBean.getUin()))
                .build().unique();

        if (myInfo == null) {
            MyInfo insert = new MyInfo(null, payloadBean.getUin(), 0, 0, 0, 0, 0);
            myInfoDao.insert(insert);
            System.out.println("插入数据库");
        }
    }


    //登录
    private void login(final String phoneNumber, String code, long uuid, String deviceName, String os, String appVersion) {
        YPlayApiManger.getInstance().getZivApiService()
                .login(phoneNumber, code, uuid, deviceName, os, appVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }
                    @Override
                    public void onNext(@NonNull LoginRespond loginRespond) {
                        System.out.println("登录返回---" + loginRespond.toString());
                        if (loginRespond.getCode() == 0) {
                            int hasCheckInviteCode = loginRespond.getPayload().getHasCheckInviteCode();
                            uin = loginRespond.getPayload().getUin();
                            ver = loginRespond.getPayload().getVer();
                            token = loginRespond.getPayload().getToken();
                            if (hasCheckInviteCode == 0) { //0表示邀请码验证未通过
                                Intent intent = new Intent(Login.this, ActivityInviteCode.class);
                                intent.putExtra("phone_number", phoneNumber);
                                intent.putExtra("uin", uin);
                                intent.putExtra("ver", ver);
                                intent.putExtra("token", token);
                                intent.putExtra("nick_name", loginRespond.getPayload().getInfo().getUserName());
                                startActivity(intent);
                                return;
                            }
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_UIN, uin);
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_TOKEN, token);
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_VER, ver);
                            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_USER_NAME, loginRespond.getPayload().getInfo().getUserName());
                            insertUin(loginRespond.getPayload());
                            if (loginRespond.getPayload().getIsNewUser() == 1) {
                                startActivity(new Intent(Login.this, LoginAge.class));
                            } else {
                                //逻辑跳转
//                                jumpToWhere(loginRespond.getPayload().getInfo().getAge(),
//                                        loginRespond.getPayload().getInfo().getGrade(),
//                                        loginRespond.getPayload().getInfo().getSchoolId(),
//                                        loginRespond.getPayload().getInfo().getGender(),
//                                        loginRespond.getPayload().getInfo().getNickName()
//                                );
                                startActivity(new Intent(Login.this, LoginAge.class));
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
                            jumpToWhere(userInfoResponde.getPayload().getInfo().getAge(),
                                    userInfoResponde.getPayload().getInfo().getGrade(),
                                    userInfoResponde.getPayload().getInfo().getSchoolId(),
                                    userInfoResponde.getPayload().getInfo().getGender(),
                                    userInfoResponde.getPayload().getInfo().getNickName()
                            );
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
