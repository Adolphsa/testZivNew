package com.yeejay.yplay.friend;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.FriendFeedsAdapter;
import com.yeejay.yplay.base.BaseFragment;

import butterknife.BindView;

/**
 * 好友
 * Created by Administrator on 2017/10/26.
 */

public class FragmentFriend extends BaseFragment {

    @BindView(R.id.frg_title)
    TextView frgTitle;
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;
    @BindView(R.id.ff_swipe_recycler_view)
    SwipeMenuRecyclerView ffSwipeRecyclerView;

    FriendFeedsAdapter feedsAdapter;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        frgTitle.setText("好友");

        feedsAdapter = new FriendFeedsAdapter(getContext());
        ffSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ffSwipeRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                System.out.println("搞事情---" + position);
            }
        });
        ffSwipeRecyclerView.setAdapter(feedsAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
