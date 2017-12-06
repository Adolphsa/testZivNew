package com.yeejay.yplay.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.MessageAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.MessageUpdateUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 消息
 * Created by Administrator on 2017/10/26.
 */

public class FragmentMessage extends BaseFragment implements MessageUpdateUtil.SessionUpdateListener{

    private static final String TAG = "FragmentMessage";

    @BindView(R.id.message_title)
    RelativeLayout messageTitle;
    @BindView(R.id.frg_title)
    TextView frgTitle;
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;
    @BindView(R.id.frg_message_count)
    TextView addFriendCount;
    @BindView(R.id.frg_edit)
    ImageButton frgEdit;
    @BindView(R.id.message_recyclerView)
    SwipeMenuRecyclerView messageRecyclerView;
    @BindView(R.id.message_refresh_view)
    PullToRefreshLayout messageRefreshView;

    @OnClick(R.id.frg_user_info)
    public void userInfo(View view){
        System.out.println("跳转到我的资料");
        startActivity(new Intent(getActivity(), ActivityMyInfo.class));
    }

    int dataOffset = 0;
    MessageAdapter messageAdapter;
    List<ImSession> mDataList;
    ImSessionDao imSessionDao;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        messageTitle.setBackgroundColor(getResources().getColor(R.color.message_title_color));
        frgTitle.setText("消息");
        frgTitle.setTextColor(getResources().getColor(R.color.white));

        MessageUpdateUtil.getMsgUpdateInstance().setSessionUpdateListener(this);
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        mDataList = new ArrayList<>();

        initMessageView();
    }

    private void initMessageView(){

        messageAdapter = new MessageAdapter(getActivity(),mDataList);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecyclerView.addItemDecoration(new DefaultItemDecoration(getResources().getColor(R.color.divider_color2)));

        messageRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //跳转到聊天页面
                ImSession imSession = mDataList.get(position);
                int status = imSession.getStatus();
                String sender = imSession.getLastSender();
                String sessionId = imSession.getSessionId();
                String msgContent = imSession.getMsgContent();
                String nickName = imSession.getNickName();
                imSession.setUnreadMsgNum(0);
                imSessionDao.update(imSession);
                int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);

                Intent intent = new Intent();
                intent.putExtra("yplay_sessionId",sessionId);
                intent.putExtra("yplay_session_status",status);
                intent.putExtra("yplay_sender",sender);
                intent.putExtra("yplay_msg_content",msgContent);
                intent.putExtra("yplay_nick_name",nickName);

                Log.d(TAG, "sessionId: " + sessionId);

                if (status == 0){
                    intent.setClass(getActivity(),ActivityNnonymityReply.class);
                }else if (status == 1 && !TextUtils.isEmpty(sender) && sender.equals(String.valueOf(uin))){
                    intent.setClass(getActivity(),ActivityNnonymityReply.class);
                }else if (status == 1 && !TextUtils.isEmpty(sender) && !sender.equals(String.valueOf(uin))){
                    intent.setClass(getActivity(),ActivityChatWindow.class);
                }else if (status == 2){
                    intent.setClass(getActivity(),ActivityChatWindow.class);
                }

                System.out.println("message----sessionId---" + sessionId);
                startActivity(intent);
            }
        });

        messageRecyclerView.setAdapter(messageAdapter);

        messageRefreshView.setCanRefresh(false);
        messageRefreshView.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                dataOffset++;
                List<ImSession> tempImSessinList = queryDatabaseForImsession();
                if (tempImSessinList != null && tempImSessinList.size() > 0){
                    mDataList.addAll(tempImSessinList);
                    messageAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getActivity(),"没有更多数据了",Toast.LENGTH_LONG).show();
                }
                messageRefreshView.finishLoadMore();
            }
        });
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser){
            System.out.println("FragmentMessage---消息可见");
            frgEdit.setVisibility(View.GONE);
            dataOffset = 0;
            updateUi();

            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.setMessageClear();

            setFriendCount();
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
//            System.out.println("查询到的列表长度---" + tempList.size());
            mDataList.addAll(tempList);
            messageAdapter.notifyDataSetChanged();
            if (messageRecyclerView != null){
                messageRecyclerView.scrollToPosition(0);
                messageRecyclerView.loadMoreFinish(false, true);
            }

        }

    }

    @Override
    public void onSessionUpdate(ImSession imSession) {
        String sessionId =  imSession.getSessionId();
        Log.d(TAG, "会话列表更新了onSessionUpdate: sessionId---" + sessionId);

        updateUi();
    }

    public void setFriendCount(){

        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null){
            int addFriendNum = myInfo.getAddFriendNum();
            if (addFriendNum == 0){
                addFriendCount.setText("");
            }else {
                addFriendCount.setText(addFriendNum + "");
            }

        }
    }

}
