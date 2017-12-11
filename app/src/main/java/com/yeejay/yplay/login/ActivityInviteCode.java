package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityInviteCode extends BaseActivity {

    private static final String TAG = "ActivityInviteCode";

    @BindView(R.id.aic_back)
    ImageButton aicBack;
    @BindView(R.id.aic_code_input)
    EditText aicCodeInput;
    @BindView(R.id.aic_code_invalid)
    TextView aicCodeInvalid;
    @BindView(R.id.aic_enter)
    Button aicEnter;

    @OnClick(R.id.aic_back)
    public void back(){
        finish();
    }

    @OnClick(R.id.aic_enter)    //下一步
    public void aicEnter(){
        Log.i(TAG, "aicEnter: 下一步");
        String inviteCode =  aicCodeInput.getText().toString();
        if (TextUtils.isEmpty(inviteCode)) return;

        if (inviteCode.length() <= 0){
            Toast.makeText(ActivityInviteCode.this,"邀请码为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetWorkUtil.isNetWorkAvailable(ActivityInviteCode.this)){
            checkInviteCode(phoneNumber,inviteCode);
        }

    }

    String phoneNumber;
    private int uin;
    private String token;
    private int ver;
    private String nickName;
    MyInfoDao myInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.invite_background));
        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            phoneNumber = bundle.getString("phone_number");
            uin = bundle.getInt("uin");
            token = bundle.getString("token");
            ver = bundle.getInt("ver");
            nickName = bundle.getString("nick_name");
        }

        aicCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0){
                    aicCodeInvalid.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    //检验邀请码
    private void checkInviteCode(String phone,String inviteCode){
        Map<String, Object> inviteCodeMap = new HashMap<>();
        inviteCodeMap.put("phone", phone);
        inviteCodeMap.put("inviteCode", inviteCode);

        YPlayApiManger.getInstance().getZivApiService()
                .checkInviteCode(inviteCodeMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRespond baseRespond) {
                        Log.i(TAG, "onNext: baseRespond---" + baseRespond);
                        if (baseRespond.getCode() == 0){

                            SharePreferenceUtil.put(ActivityInviteCode.this, YPlayConstant.YPLAY_UIN, uin);
                            SharePreferenceUtil.put(ActivityInviteCode.this, YPlayConstant.YPLAY_TOKEN, token);
                            SharePreferenceUtil.put(ActivityInviteCode.this, YPlayConstant.YPLAY_VER, ver);
                            SharePreferenceUtil.put(ActivityInviteCode.this,YPlayConstant.YPLAY_USER_NAME,nickName);
                            insertUin(uin);

                            getMyInfo();
                        }else {
                            aicCodeInvalid.setVisibility(View.VISIBLE);
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


    //获取自己的资料
    private void getMyInfo() {

        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", uin);
        myInfoMap.put("token", token);
        myInfoMap.put("ver", ver);

        Log.i(TAG, "getMyInfo: uin---" + uin + ",token---" + token + ",ver---" + ver);

        YPlayApiManger.getInstance().getZivApiService()
                .getMyInfo(myInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull UserInfoResponde userInfoResponde) {
                        Log.i(TAG, "onNext: userInfoResponde---" + userInfoResponde.toString());

                        if (userInfoResponde.getCode() == 0) {
                            UserInfoResponde.PayloadBean.InfoBean infoBean = userInfoResponde.getPayload().getInfo();

                            if (infoBean.getAge() == 0 ||
                                    infoBean.getGrade() == 0 ||
                                    infoBean.getSchoolId() == 0 ||
                                    infoBean.getGender() == 0 ||
                                    TextUtils.isEmpty(infoBean.getNickName())) {
                                startActivity(new Intent(ActivityInviteCode.this, LoginAge.class));

                            } else {
                                startActivity(new Intent(ActivityInviteCode.this, MainActivity.class));
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onNext: userInfoResponde---" + e.toString());
                    }

                    @Override
                    public void onComplete() {}
                });
    }


    //插入uin到数据库
    private void insertUin(int tempUin){

        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(tempUin))
                .build().unique();

        if (myInfo == null){
            MyInfo insert = new MyInfo(null,tempUin,0,0,0,0);
            myInfoDao.insert(insert);
            System.out.println("插入数据库");
        }
    }

}
