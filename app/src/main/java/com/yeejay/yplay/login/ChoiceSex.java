package com.yeejay.yplay.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
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

public class ChoiceSex extends AppCompatActivity {

    int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_sex);

        Button btnBack = (Button) findViewById(R.id.layout_title_back);
        TextView title = (TextView) findViewById(R.id.layout_title);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("选择你的性别");

        Button boyBtn = (Button) findViewById(R.id.cs_btn_boy);
        Button girlBtn = (Button) findViewById(R.id.cs_btn_girl);

        boyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = 1;
                if (NetWorkUtil.isNetWorkAvailable(ChoiceSex.this)){
                    choiceSex(gender);
                }else {
                    Toast.makeText(ChoiceSex.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

        girlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = 2;
                if (NetWorkUtil.isNetWorkAvailable(ChoiceSex.this)){
                    choiceSex(gender);
                }else {
                    Toast.makeText(ChoiceSex.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //选择性别
    private void choiceSex(int gender){

        Map<String,Object> sexMap = new HashMap<>();
        System.out.println("性别---" + gender);
        sexMap.put("gender",gender);
        sexMap.put("uin", SharePreferenceUtil.get(ChoiceSex.this, YPlayConstant.YPLAY_UIN,0));
        sexMap.put("token",SharePreferenceUtil.get(ChoiceSex.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        sexMap.put("ver",SharePreferenceUtil.get(ChoiceSex.this,YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .choiceSex(sexMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        if (baseRespond.getCode() == 0){
                            System.out.println("选择性别成功---" + baseRespond.toString());
                            startActivity(new Intent(ChoiceSex.this,UserInfo.class));
                        }else {
                            System.out.println("选择性别失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("选择性别异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
