package com.yeejay.yplay.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.WheelView;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
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

public class LoginAge extends BaseActivity {

    @BindView(R.id.la_back)
    ImageButton laBack;
    @BindView(R.id.la_tv_age)
    TextView mAgeView;
    @BindView(R.id.la_btn_quick_start)
    Button mQuickStartBtn;
    @BindView(R.id.la_wheel_view)
    WheelView mWheelView;

    @OnClick(R.id.la_back)
    public void back(View view) {
        finish();
    }

    Handler mHandler = new Handler();
    ArrayList<String> ageList;
    String mAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_age);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.message_title_color));

        ageList = new ArrayList<String>();
        //添加年龄数据
        for (int i = 0; i < 112; i++) {
            ageList.add(i + " ");
        }

        mWheelView.setOffset(1);
        mWheelView.setItems(ageList);
        mWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, final String item) {
                Log.d("LoginAge", "selectedIndex: " + selectedIndex + ", item: " + item);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAgeView.setText(item);
                        mAgeView.setTextColor(getResources().getColor(R.color.black));
                        mAge = item;
                        System.out.println("mAge---" + mAge);

                        mQuickStartBtn.setEnabled(true);
                        mQuickStartBtn.setTextColor(getResources().getColor(R.color.white));
                    }
                });
            }
        });

        mQuickStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(mAge.trim()) < 12) {
                    //年龄未达到要求
                    showNormalDialog();
                } else if (Integer.parseInt(mAge.trim()) >= 12 && Integer.parseInt(mAge.trim()) < 24) {
                    //允许进入
                    settingAge(Integer.parseInt(mAge.trim()));

                } else {
                    //年龄未达到要求
                    showNormalDialog();
                }
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showNormalDialog() {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoginAge.this);
        normalDialog.setTitle(R.string.la_age_invail);
        normalDialog.setPositiveButton(R.string.la_i_know,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    //设置年龄
    private void settingAge(int age) {

        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("age", age);
        nameMap.put("uin", SharePreferenceUtil.get(LoginAge.this, YPlayConstant.YPLAY_UIN, 0));
        nameMap.put("token", SharePreferenceUtil.get(LoginAge.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        nameMap.put("ver", SharePreferenceUtil.get(LoginAge.this, YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .settingName(nameMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("设置年龄---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0) {
                            startActivity(new Intent(LoginAge.this, LoginAuthorization.class));
                            //jumpToWhere();
                        } else {
                            Toast.makeText(LoginAge.this, "设置年龄失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("设置年龄异常---" + e.getMessage());
                        Toast.makeText(LoginAge.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void jumpToWhere() {
        //判断年级
        int grade = (int) SharePreferenceUtil.get(LoginAge.this, YPlayConstant.TEMP_GRADE, 0);
        if (grade == 0) {
            startActivity(new Intent(LoginAge.this, LoginAuthorization.class));
            return;
        }
        //判断学校信息
        int schoolId = (int) SharePreferenceUtil.get(LoginAge.this, YPlayConstant.TEMP_SCHOOL_ID, 0);
        if (schoolId == 0) {
            startActivity(new Intent(LoginAge.this, LoginAuthorization.class));
            return;
        }
        //判断性别
        int gender = (int) SharePreferenceUtil.get(LoginAge.this, YPlayConstant.TEMP_GENDER, 0);
        if (gender == 0) {
            startActivity(new Intent(LoginAge.this, ChoiceSex.class));
            return;
        }
        //判断基本信息
        String name = (String) SharePreferenceUtil.get(LoginAge.this, YPlayConstant.TEMP_NICK_NAME, "yplay");
        if (TextUtils.isEmpty(name) || name.equals("yplay")) {
            startActivity(new Intent(LoginAge.this, UserInfo.class));
            return;
        }

        startActivity(new Intent(LoginAge.this, MainActivity.class));
    }

}
