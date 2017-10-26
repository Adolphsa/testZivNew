package com.yeejay.yplay.answer;

import android.os.Bundle;
import android.widget.TextView;

import com.yeejay.yplay.R;

import com.yeejay.yplay.base.BaseFragment;
import butterknife.BindView;

/**
 * 答题
 * Created by Administrator on 2017/10/26.
 */

public class FragmentAnswer extends BaseFragment{

    @BindView(R.id.frg_tv_title)
    TextView testView;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        testView.setText("我是修改后的答题");
    }
}
