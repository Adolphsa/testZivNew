package com.yeejay.yplay.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.MessageAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.userinfo.ActivityMyInfo;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 消息
 * Created by Administrator on 2017/10/26.
 */

public class FragmentMessage extends BaseFragment{

    @BindView(R.id.frg_title)
    TextView frgTitle;
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;
    @BindView(R.id.message_recyclerView)
    SwipeMenuRecyclerView messageRecyclerView;

    @OnClick(R.id.frg_user_info)
    public void userInfo(View view){
        System.out.println("跳转到我的资料");
        startActivity(new Intent(getActivity(), ActivityMyInfo.class));
    }

    MessageAdapter messageAdapter;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        frgTitle.setText("消息");
        initMessageView();
    }

    private void initMessageView(){
        messageAdapter = new MessageAdapter(getActivity());
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        messageRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //跳转到聊天页面
                System.out.println("啦啦啦" + position);
                startActivity(new Intent(getActivity(),ActivityChatWindow.class));
            }
        });
        messageRecyclerView.setAdapter(messageAdapter);
    }
}
