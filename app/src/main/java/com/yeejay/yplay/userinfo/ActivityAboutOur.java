package com.yeejay.yplay.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAboutOur extends BaseActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;

    @OnClick(R.id.layout_title_back)
    public void back(View view){finish();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_our);
        ButterKnife.bind(this);

        layoutTitle.setText("关于我们");
    }
}
