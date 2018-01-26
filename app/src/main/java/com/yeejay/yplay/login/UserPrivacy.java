package com.yeejay.yplay.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserPrivacy extends AppCompatActivity {

    private static final String privacyUrl = "https://yplay.vivacampus.com/static/agreement";

    @BindView(R.id.aup_webview)
    WebView aupWebview;
    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.layout_title_rl)
    RelativeLayout layoutTitleRl;

    @OnClick(R.id.layout_title_back2)
    public void back(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_privacy);
        ButterKnife.bind(this);

        layoutTitleBack2.setImageResource(R.drawable.white_back);
        layoutTitle2.setText("用户协议");
        layoutTitle2.setTextColor(getResources().getColor(R.color.white));
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.grey));

        aupWebview.loadUrl(privacyUrl);
    }
}
