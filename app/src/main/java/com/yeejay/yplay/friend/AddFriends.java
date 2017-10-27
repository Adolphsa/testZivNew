package com.yeejay.yplay.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.AddFriendsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFriends extends AppCompatActivity implements AdapterView.OnItemClickListener{

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.af_lv_add_friends_list_view)
    ListView afLvAddFriends;
    @BindView(R.id.af_lv_book_friends_list)
    ListView afLvBookFriendsList;
    @BindView(R.id.af_lv_friends_friends)
    ListView afLvFriendsFriends;
    @BindView(R.id.af_lv_school_classmate)
    ListView afLvSchoolClassmate;
    @BindView(R.id.af_lv_wait_invite)
    ListView afLvWaitInvite;

    @OnClick(R.id.layout_title_back)
    public void back(View view) {
        finish();
    }

    List<String> friendsList;
    List<String> bookFriendsList;
    AddFriendsAdapter addFriendsAdapter;
    AddFriendsAdapter bookFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        layoutTitle.setText("加好友");



        initAddFriendsListView();
        initbooKFriendsList();
    }

    //加好友请求列表
    private void initAddFriendsListView(){
        friendsList = new ArrayList<>();
        friendsList.add("张飞");
        friendsList.add("关羽");
        friendsList.add("赵云");
        friendsList.add("吕布");

        final  TextView AddFriendsHeaderView = (TextView) View.inflate(AddFriends.this,R.layout.item_af_listview_header,null);
        final View AddFriendsFootView = View.inflate(AddFriends.this,R.layout.item_af_listview_foot,null);
        afLvAddFriends.addHeaderView(AddFriendsHeaderView);
        afLvAddFriends.addFooterView(AddFriendsFootView);
        addFriendsAdapter = new AddFriendsAdapter(AddFriends.this, new AddFriendsAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");
                Button button = (Button) v;
                button.setVisibility(View.INVISIBLE);
                if (friendsList.size() > 0) {
                    friendsList.remove((int) v.getTag());
                    addFriendsAdapter.notifyDataSetChanged();
                }
                if (friendsList.size() < 3 ){
                    afLvAddFriends.removeFooterView(AddFriendsFootView);
                }
                if (friendsList.size() <= 0){
                    afLvAddFriends.removeHeaderView(AddFriendsHeaderView);
                }
            }
        }, new AddFriendsAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button)v;
                button.setText("接受");
            }
        }, friendsList);
        afLvAddFriends.setAdapter(addFriendsAdapter);
        afLvAddFriends.setOnItemClickListener(this);
    }

    //通讯录好友
    private void initbooKFriendsList(){
        bookFriendsList = new ArrayList<>();
        bookFriendsList.add("鲁班");
        bookFriendsList.add("哪吒");
        final  TextView booKFriendsHeaderView = (TextView) View.inflate(AddFriends.this,R.layout.item_af_listview_header,null);
        final View booKFriendsFootView = View.inflate(AddFriends.this,R.layout.item_af_listview_foot,null);
        booKFriendsHeaderView.setText("通讯录好友");
        afLvBookFriendsList.addHeaderView(booKFriendsHeaderView);
        if (bookFriendsList.size() > 3){
            afLvBookFriendsList.addFooterView(booKFriendsFootView);
        }
        bookFriendsAdapter = new AddFriendsAdapter(AddFriends.this, new AddFriendsAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");
                Button button = (Button) v;
                button.setVisibility(View.INVISIBLE);
                if (bookFriendsList.size() > 0) {
                    bookFriendsList.remove((int) v.getTag());
                    bookFriendsAdapter.notifyDataSetChanged();
                }
                if (bookFriendsList.size() < 3 ){
                    afLvBookFriendsList.removeFooterView(booKFriendsFootView);
                }
                if (bookFriendsList.size() <= 0){

                    afLvBookFriendsList.removeHeaderView(booKFriendsHeaderView);
                }

            }
        }, new AddFriendsAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button)v;
                button.setText("接受");
            }
        }, bookFriendsList);
        afLvBookFriendsList.setAdapter(bookFriendsAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("position---" + position);
        if (position == 4){
            System.out.println("啦啦啦");
            startActivity(new Intent(AddFriends.this,ActivityAddFiendsDetail.class));
        }
    }

}
