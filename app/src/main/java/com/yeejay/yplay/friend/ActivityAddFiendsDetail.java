package com.yeejay.yplay.friend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.FriendsDetailAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAddFiendsDetail extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.aafd_searchView)
    SearchView aafdSearchView;
    @BindView(R.id.aafd_list_view)
    ListView aafdListView;

    @OnClick(R.id.layout_title_back)
    public void back(View view) {
        finish();
    }

    List<String> dataList;
    FriendsDetailAdapter friendsDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activty_add_fiends_detail);
        ButterKnife.bind(this);

        layoutTitle.setText("加好友请求");
        initFriendsDetailListView();
    }

    private void initFriendsDetailListView(){
        dataList = new ArrayList<>();
        dataList.add("a");
        dataList.add("b");
        dataList.add("c");
        dataList.add("d");
        dataList.add("e");

        friendsDetailAdapter = new FriendsDetailAdapter(ActivityAddFiendsDetail.this,
                new FriendsDetailAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");
                Button button = (Button) v;
                button.setVisibility(View.INVISIBLE);
                if (dataList.size() > 0) {
                    dataList.remove((int) v.getTag());
                    friendsDetailAdapter.notifyDataSetChanged();
                }
            }
        }, new FriendsDetailAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button)v;
                button.setText("接受");
            }
        },dataList);
        aafdListView.setAdapter(friendsDetailAdapter);
    }
}
