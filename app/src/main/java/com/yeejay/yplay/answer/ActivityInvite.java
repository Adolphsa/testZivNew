package com.yeejay.yplay.answer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.ListButtonAdapter;
import com.yeejay.yplay.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityInvite extends BaseActivity implements AdapterView.OnItemClickListener,ListButtonAdapter.LbCallback{


    @BindView(R.id.ai_back)
    ImageButton aiBack;
    @BindView(R.id.ai_searchView)
    SearchView aiSearchView;
    @BindView(R.id.ai_list_view)
    ListView aiListView;

    List<String> nameList;

    @OnClick(R.id.ai_back)
    public void aiBack(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        nameList = new ArrayList<>();
        nameList.add("尼古拉斯金");
        nameList.add("古拉斯木");
        nameList.add("古斯水");
        nameList.add("尼拉");

        aiListView.setAdapter(new ListButtonAdapter(this,nameList,this));
        aiListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void click(View v) {
        System.out.println("第" + (Integer)v.getTag() + "行被点击，内容是" + nameList.get((Integer) v.getTag()));
        Button button = (Button)v;
        button.setText("已邀请");
    }
}
