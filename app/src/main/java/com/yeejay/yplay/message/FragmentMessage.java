package com.yeejay.yplay.message;

import android.os.Bundle;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseFragment;

import butterknife.BindView;

/**
 * 消息
 * Created by Administrator on 2017/10/26.
 */

public class FragmentMessage extends BaseFragment{

    @BindView(R.id.frg_title)
    TextView frgTitle;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        frgTitle.setText("消息");
    }
}
