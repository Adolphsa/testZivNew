package com.yeejay.yplay.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.customview.CountDownTimer;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ContactsInfo;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.model.UserInfoResponde;
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

public class Login extends AppCompatActivity {

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


    @OnClick(R.id.login_edt_number)
    public void loginPhone(){
    }
    @OnClick(R.id.login_edt_auth_code)
    public void loginAuthCode(){
    }

    MyInfoDao myInfoDao;


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

    boolean numberBookAuthoritySuccess = false;
    boolean addressAuthoritySuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.feeds_title_color));

        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();

        if ((long) SharePreferenceUtil.get(Login.this, YPlayConstant.YPLAY_UUID, (long) 0) == 0) {
            System.out.println("第一次为零");
            SharePreferenceUtil.put(Login.this, YPlayConstant.YPLAY_UUID, System.currentTimeMillis());
        }

        //监听手机号输入栏的变化
        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                    if (!TextUtils.isEmpty(authCode) && authCode.length() == 4){
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                    System.out.println("uuid---" + uuid);
                    login(mEdtPhoneNumber.getText().toString(), mEdtAuthCode.getText().toString(), uuid);
                } else {
                    Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //KeyWordUtils.pullKeywordTop(this,rootView.getId(),testTv1.getId(),loginScrollView.getId());
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    //跳转逻辑判断
    private void jumpToWhere(int tempAge, int tempGrade, int tempSchoolId, int tempGender, String tempNickName) {
        //判断年龄
        int age = tempAge;
        int grade = tempGrade;
        int schoolId = tempSchoolId;
        int gender = tempGender;
        String name = tempNickName;

        System.out.println("年龄---" + age);
        if (age == 0 || grade == 0 || schoolId == 0 || gender == 0 || TextUtils.isEmpty(name)) {
            startActivity(new Intent(Login.this, LoginAge.class));
            //重新登录检查权限
            return;
        }

        //年龄
        if (age == 0 ) {
            startActivity(new Intent(Login.this, LoginAge.class));
            return;
        }

        //权限
        getContacts();
        getLonLat();
        if (!addressAuthoritySuccess || !numberBookAuthoritySuccess){
            startActivity(new Intent(Login.this,LoginAuthorization.class));
            return;
        }

        //判断年级
        if (grade == 0){
            startActivity(new Intent(Login.this,ClassList.class));
            return;
        }

        //判断学校信息
        if (schoolId == 0){
            startActivity(new Intent(Login.this,ClassList.class));
            return;
        }
        //判断性别
        if (gender == 0){
            startActivity(new Intent(Login.this,ChoiceSex.class));
            return;
        }
        //判断基本信息
        if (TextUtils.isEmpty(name)){
            startActivity(new Intent(Login.this,UserInfo.class));
            return;
        }

        startActivity(new Intent(Login.this, MainActivity.class));
    }


    private void getContacts() {
        List<ContactsInfo> mContactsList = new ArrayList<ContactsInfo>();
        if (Build.VERSION.SDK_INT >= 23
                && Login.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && Login.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            System.out.println("contactUri---" + contactUri);
            if (contactUri != null) {
                numberBookAuthoritySuccess = true;
                System.out.println("通讯录权限申请成功");

            }
            Cursor cursor = getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            //String contactSortKey;
            //int contactId;
            while (cursor != null && cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                //contactSortKey =getSortkey(cursor.getString(1));
                ContactsInfo contactsInfo = new ContactsInfo(contactName, contactNumber);
                if (contactName != null)
                    mContactsList.add(contactsInfo);
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

            if (mContactsList.size() > 0) {
                ContactsInfo testContactInfo = mContactsList.get(0);
                System.out.println("姓名---" + testContactInfo.getName() + "号码---" + testContactInfo.getNumber());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    String mProvider;//位置提供器
    LocationManager mLocationManager;//位置服务
    Location mLocation;

    //获取当前经纬度
    private void getLonLat() {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mProvider = judgeProvider(mLocationManager);
        if (Build.VERSION.SDK_INT >= 23 &&
                Login.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                Login.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null) {
            addressAuthoritySuccess = true;
            System.out.println("当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
        }
    }

    //判断是否有可用的内容提供者
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(Login.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
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

                            insertUin(loginRespond.getPayload());

                            if (loginRespond.getPayload().getIsNewUser() == 1) {
                                startActivity(new Intent(Login.this, LoginAge.class));
                            } else {
                                //逻辑跳转
                                jumpToWhere(loginRespond.getPayload().getInfo().getAge(),
                                        loginRespond.getPayload().getInfo().getGrade(),
                                        loginRespond.getPayload().getInfo().getSchoolId(),
                                        loginRespond.getPayload().getInfo().getGender(),
                                        loginRespond.getPayload().getInfo().getNickName()
                                        );
                                //startActivity(new Intent(Login.this, LoginAge.class));
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

    //插入uin到数据库
    private void insertUin(LoginRespond.PayloadBean payloadBean){


        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(payloadBean.getUin()))
                .build().unique();

        if (myInfo == null){
            MyInfo insert = new MyInfo(null,payloadBean.getUin(),0);
            myInfoDao.insert(insert);
            System.out.println("插入数据库");
        }
    }


    //获取自己的资料
    private void getMyInfo(int uin,String token,int ver){

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
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull UserInfoResponde userInfoResponde) {
                        System.out.println("获取自己的资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0){
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
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
