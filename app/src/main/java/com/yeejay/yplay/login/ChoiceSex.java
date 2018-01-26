package com.yeejay.yplay.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.HashMap;
import java.util.Map;

public class ChoiceSex extends BaseActivity {

    private static final String TAG = "ChoiceSex";

    int gender;
    int isActivitySetting;

    Button boyBtn;
    Button girlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_sex);

        getWindow().setStatusBarColor(getResources().getColor(R.color.play_color4));

        ImageButton btnBack = (ImageButton) findViewById(R.id.cs_back);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isActivitySetting = bundle.getInt("activity_setting");
            Log.i(TAG, "isActivitySetting---" + isActivitySetting);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boyBtn = (Button) findViewById(R.id.cs_btn_boy);
        girlBtn = (Button) findViewById(R.id.cs_btn_girl);

        boyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = 1;
                if (NetWorkUtil.isNetWorkAvailable(ChoiceSex.this)) {
                    choiceSex(gender);
                    Drawable navBoy = getResources().getDrawable(R.drawable.boy_unselect);
                    navBoy.setBounds(0, 0, navBoy.getMinimumWidth(), navBoy.getMinimumHeight());
                    boyBtn.setCompoundDrawables(null, navBoy, null, null);

                    Drawable navGirl = getResources().getDrawable(R.drawable.girl_select);
                    navGirl.setBounds(0, 0, navGirl.getMinimumWidth(), navGirl.getMinimumHeight());
                    girlBtn.setCompoundDrawables(null, navGirl, null, null);
                } else {
                    Toast.makeText(ChoiceSex.this, R.string.base_no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        girlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = 2;
                if (NetWorkUtil.isNetWorkAvailable(ChoiceSex.this)) {
                    choiceSex(gender);

                    Drawable navBoy = getResources().getDrawable(R.drawable.boy_select);
                    navBoy.setBounds(0, 0, navBoy.getMinimumWidth(), navBoy.getMinimumHeight());
                    boyBtn.setCompoundDrawables(null, navBoy, null, null);

                    Drawable navGirl = getResources().getDrawable(R.drawable.girl_unselect);
                    navGirl.setBounds(0, 0, navGirl.getMinimumWidth(), navGirl.getMinimumHeight());
                    girlBtn.setCompoundDrawables(null, navGirl, null, null);
                } else {
                    Toast.makeText(ChoiceSex.this, R.string.base_no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //选择性别
    private void choiceSex(final int gender){

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_SET_AGE_URL;
        Map<String,Object> sexMap = new HashMap<>();
        LogUtils.getInstance().error("gender {}", gender);
        sexMap.put("gender",gender);
        sexMap.put("uin", SharePreferenceUtil.get(ChoiceSex.this, YPlayConstant.YPLAY_UIN,0));
        sexMap.put("token",SharePreferenceUtil.get(ChoiceSex.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        sexMap.put("ver",SharePreferenceUtil.get(ChoiceSex.this,YPlayConstant.YPLAY_VER,0));

        WnsAsyncHttp.wnsRequest(url, sexMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 选择性别---" + result);
                BaseRespond baseRespond = GsonUtil.GsonToBean(result,BaseRespond.class);
                if (baseRespond.getCode() == 0){

                    if (isActivitySetting == 1){
                        Intent intent = new Intent();
                        String str = gender == 1 ? "男" : "女";
                        intent.putExtra("activity_setting_gender",str);
                        ChoiceSex.this.setResult(201,intent);
                        ChoiceSex.this.finish();
                    }else {
                        startActivity(new Intent(ChoiceSex.this,UserInfo.class));
                        //jumpToWhere();
                    }

                }else {
                    LogUtils.getInstance().error("choice gender fail {}", baseRespond.toString());
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });
    }

}
