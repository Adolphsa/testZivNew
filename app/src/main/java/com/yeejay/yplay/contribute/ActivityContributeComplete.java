package com.yeejay.yplay.contribute;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityContributeComplete extends BaseActivity {
    private static final String TAG = "ActivityContributeComplete";

    @BindView(R.id.con_submit_complete)
    Button contributeComplete;
    @BindView(R.id.con_query_contribute)
    Button queryContribute;

    @OnClick(R.id.con_submit_complete)
    public void submitComplete() {
        setResult(3);
        finish();
    }

    @OnClick(R.id.con_query_contribute)
    public void queryContribute() {
        startActivityForResult(new Intent(ActivityContributeComplete.this,
                ActivityContributeQuery.class), 4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_complete);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContributeComplete.this, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == 4) {
            setResult(4);
            finish();
        }
    }
}