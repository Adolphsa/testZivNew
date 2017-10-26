package com.yeejay.yplay.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.customview.WheelView;

import java.util.ArrayList;

public class LoginAge extends AppCompatActivity {

    WheelView mWheelView;
    Handler mHandler = new Handler();
    ArrayList<String> ageList;
    String mAge;

    TextView mAgeView;
    LinearLayout mLinearLayout;
    Button mQuickStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_age);

        mWheelView = (WheelView) findViewById(R.id.la_wheel_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.la_ll_privacy);
        mAgeView = (TextView) findViewById(R.id.la_tv_age);
        mQuickStartBtn = (Button) findViewById(R.id.la_btn_quick_start);


        ageList = new ArrayList<String>();
        //添加年龄数据
        for (int i = 0; i < 112; i++){
            ageList.add(i +" ");
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
                        mAge = item;
                        System.out.println("mAge---" + mAge);
                        if (mLinearLayout.isShown()){
                            mLinearLayout.setVisibility(View.INVISIBLE);
                            mQuickStartBtn.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });

        mQuickStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(mAge.trim()) < 12){
                    //年龄未达到要求
                    showNormalDialog();
                }else if (Integer.parseInt(mAge.trim()) >= 12 && Integer.parseInt(mAge.trim()) < 24){
                    //允许进入
                    startActivity(new Intent(LoginAge.this,Login.class));
                }else {
                    //年龄未达到要求
                    showNormalDialog();
                }
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showNormalDialog(){

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

}
