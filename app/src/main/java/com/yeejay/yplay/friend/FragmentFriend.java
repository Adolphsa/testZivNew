package com.yeejay.yplay.friend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.FriendFeedsAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.userinfo.ActivityMyInfo;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 好友
 * Created by Administrator on 2017/10/26.
 */

public class FragmentFriend extends BaseFragment {

    @BindView(R.id.ff_user_info)
    ImageButton ffUserInfo;
    @BindView(R.id.ff_imgbtn_add_friends)
    ImageButton ffAddFriends;
    @BindView(R.id.ff_swipe_recycler_view)
    SwipeMenuRecyclerView ffSwipeRecyclerView;

    FriendFeedsAdapter feedsAdapter;

    @OnClick(R.id.ff_imgbtn_add_friends)
    public void ffAddFriends(View view){
        startActivity(new Intent(getContext(),AddFriends.class));
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        //跳转到我的资料
        jumpToUserInfo();

        feedsAdapter = new FriendFeedsAdapter(getContext());
        ffSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ffSwipeRecyclerView.addItemDecoration(new DefaultItemDecoration(Color.GRAY));

        ffSwipeRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                System.out.println("搞事情---" + position);
                startActivity(new Intent(getContext(),ActivityFriendsInfo.class));
            }
        });
        ffSwipeRecyclerView.setAdapter(feedsAdapter);
    }


    private void jumpToUserInfo(){
        ffUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ActivityMyInfo.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
