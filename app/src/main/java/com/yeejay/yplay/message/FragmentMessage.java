package com.yeejay.yplay.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.MessageAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.userinfo.ActivityMyInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 消息
 * Created by Administrator on 2017/10/26.
 */

public class FragmentMessage extends BaseFragment{

    @BindView(R.id.message_title)
    RelativeLayout messageTitle;
    @BindView(R.id.frg_title)
    TextView frgTitle;
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;
    @BindView(R.id.frg_edit)
    ImageButton frgEdit;
    @BindView(R.id.message_recyclerView)
    SwipeMenuRecyclerView messageRecyclerView;

    @OnClick(R.id.frg_user_info)
    public void userInfo(View view){
        System.out.println("跳转到我的资料");
        startActivity(new Intent(getActivity(), ActivityMyInfo.class));
    }

    int dataOffset = 0;
    MessageAdapter messageAdapter;
    List<ImSession> mDataList;
    List<ImSession> imSessionList;
    ImSessionDao imSessionDao;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        messageTitle.setBackgroundColor(getResources().getColor(R.color.message_title_color));
        frgTitle.setText("消息");

        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        mDataList = new ArrayList<>();


        initMessageView();
    }

    private void initMessageView(){
        messageAdapter = new MessageAdapter(getActivity(),mDataList);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        messageRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //跳转到聊天页面
                Intent intent = new Intent(getActivity(),ActivityNnonymityReply.class);
                ImSession imSession = mDataList.get(position);
                String sessionId = imSession.getSessionId();
                int status = imSession.getStatus();
                String sender = imSession.getLastSender();
                String msgContent = imSession.getMsgContent();
                intent.putExtra("yplay_sessionId",sessionId);
                intent.putExtra("yplay_session_status",status);
                intent.putExtra("yplay_sender",sender);
                intent.putExtra("yplay_msg_content",msgContent);

                System.out.println("message----sessionId---" + sessionId);
                startActivity(intent);
            }
        });

        messageRecyclerView.setAdapter(messageAdapter);
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser){
            System.out.println("FragmentMessage---消息可见");
            frgEdit.setVisibility(View.GONE);
            updateUi();
        }
    }

    //从数据库中查找会话列表
    private List<ImSession> queryDatabaseForImsession(){

        return imSessionDao.queryBuilder()
                .orderDesc(ImSessionDao.Properties.MsgTs)
                .offset(dataOffset * 10)
                .limit(10)
                .list();
    }

    //更新UI
    private void updateUi(){

        mDataList.clear();

        List<ImSession> tempList = queryDatabaseForImsession();
        if (tempList == null){
            System.out.println("数据库中没有查到数据");

        }else{
            System.out.println("查询到的列表长度---" + tempList.size());
            mDataList.addAll(tempList);
            messageAdapter.notifyDataSetChanged();
        }

    }
}
