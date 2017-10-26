package com.yeejay.yplay.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.yeejay.yplay.R;
import com.yeejay.yplay.customview.CountDownTimer;

public class Login extends AppCompatActivity {

    EditText mEdtPhoneNumber;
    EditText mEdtAuthCode;
    Button mBtnAuthCode;
    Button mBtnNext;
    Button mBtnBack;

    CountDownTimer countDownTimer = new CountDownTimer(60000,1000) {  //按钮倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnAuthCode.setText(millisUntilFinished/1000 + "秒");
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

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //监听手机号输入栏的变化
        mEdtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("手机输入框的字符---" + s + "数量---" + count);
                if (s.length() == 11){
                    mBtnAuthCode.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //监听验证码输入栏的变化
        mEdtAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBtnAuthCode.isEnabled() && s.length() > 0){
                    mBtnNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.start();
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,LoginAuthorization.class));
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
