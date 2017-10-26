package com.yeejay.yplay.answer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.ListButtonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityInviteFriend extends AppCompatActivity implements AdapterView.OnItemClickListener,
        ListButtonAdapter.LbCallback{

    @BindView(R.id.aif_back)
    ImageButton aifBack;
    @BindView(R.id.aif_tv_search_view)
    TextView aifTvSearchView;
    @BindView(R.id.aif_list_view)
    ListView aifListView;

    List<String> nameList;
    ListButtonAdapter lbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.aif_back)
    public void back(View view){
        finish();
    }

    @OnClick(R.id.aif_tv_search_view)
    public void tvSearch(View view){
        startActivity(new Intent(ActivityInviteFriend.this,ActivityInvite.class));
    }

    private void init() {
        nameList = new ArrayList<>();
        nameList.add("尼古拉斯赵四");
        nameList.add("尼古拉斯赵");
        nameList.add("尼古拉斯");
        nameList.add("尼古拉");

        lbAdapter = new ListButtonAdapter(this,nameList,this);
        aifListView.setAdapter(lbAdapter);
        aifListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("omItemClick---" + position);
    }

    @Override
    public void click(View v) {
        System.out.println("第" + (Integer)v.getTag() + "行被点击，内容是"
                + nameList.get((Integer) v.getTag()));
        Button button = (Button)v;
        button.setText("已邀请");
    }
}
