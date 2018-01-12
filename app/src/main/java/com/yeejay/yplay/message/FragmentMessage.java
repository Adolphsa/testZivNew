package com.yeejay.yplay.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.customview.UpRefreshView;
import com.yeejay.yplay.greendao.FriendInfoDao;
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

public class FragmentMessage extends BaseFragment implements MessageUpdateUtil.SessionUpdateListener {

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
    @BindView(R.id.message_null)
    ImageView messageNull;

    @OnClick(R.id.frg_user_info)
    public void userInfo(View view) {
        System.out.println("跳转到我的资料");
        startActivity(new Intent(getActivity(), ActivityMyInfo.class));
    }

    int dataOffset = 0;
    int uin;
    MessageAdapter messageAdapter;
    List<ImSession> mDataList = new ArrayList<>();
    ImSessionDao imSessionDao;
    FriendInfoDao friendInfoDao;
    private LoadMoreView loadMoreView;
    private UpRefreshView upRefreshView;
    private RelativeLayout rlRefreshLayout;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        messageTitle.setBackgroundColor(getResources().getColor(R.color.message_title_color));
        frgTitle.setText("消息");
        frgTitle.setTextColor(getResources().getColor(R.color.white));

        uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);

        MessageUpdateUtil.getMsgUpdateInstance().setSessionUpdateListener(this);
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        friendInfoDao = YplayApplication.getInstance().getDaoSession().getFriendInfoDao();

        initMessageView();
    }

    private void loadMoreData() {

        Log.d("msg", "begin loadMoreData, dataOffset--" + dataOffset);

        List<ImSession> tempImSessinList = queryDatabaseForImsession(dataOffset++);

        if (tempImSessinList != null && tempImSessinList.size() > 0) {

            for (int i = 0; i < tempImSessinList.size(); ++i) {
                Log.d("msg", "queryFromDatabase index " + i + ", info--" + tempImSessinList.get(i).getSessionId() + ",content--" + tempImSessinList.get(i).getMsgContent());

                boolean find = false;
                String curSessionId = tempImSessinList.get(i).getSessionId();

                for(int j = 0; j < mDataList.size(); j++){
                    if(curSessionId.equals(mDataList.get(j).getSessionId())){
                        find = true;
                        break;
                    }
                }

                if(!find){
                    mDataList.add(tempImSessinList.get(i));
                }
            }

            //mDataList.addAll(tempImSessinList);

            Log.d("msg", "loadMoreData, allDataSize--" + mDataList.size() + " fromDataBase size--" + tempImSessinList.size());

            //底部刷新的时候才需要将新加载的页面外露一部分
            //dataOffset == 1表示顶部更新，>1表示底部更新
            if (dataOffset > 1) {
                if (tempImSessinList.size() >= 2) {
                    Log.d("msg", "smoothScroll to position---" + (mDataList.size() - tempImSessinList.size() + 1) + "  mDatalist.size()---" + mDataList.size());
                    messageRecyclerView.smoothScrollToPosition(mDataList.size() - tempImSessinList.size() + 1);
                } else if (tempImSessinList.size() == 1) {
                    Log.d("msg", "smoothScroll to position---" + (mDataList.size() - tempImSessinList.size()) + "  mDatalist.size()---" + mDataList.size());
                    messageRecyclerView.smoothScrollToPosition(mDataList.size() - tempImSessinList.size());
                }
            }
            messageAdapter.notifyDataSetChanged();

        } else {
            dataOffset--;
            //Toast.makeText(getActivity(),"没有更多数据了",Toast.LENGTH_LONG).show();
            //没有更多数据了
            loadMoreView.noData(true);

            Log.d("msg", "loadMoreData, fromDbSize 0, allDataSize--" + mDataList.size());
        }
        messageRefreshView.finishLoadMore();
    }

    private void initMessageView() {

        if (messageAdapter == null) {
            messageAdapter = new MessageAdapter(getActivity(), mDataList, friendInfoDao);
        }

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
                View tmp = (View) itemView.findViewById(R.id.msg_item_new_msg);
                if (tmp != null) {
                    tmp.setVisibility(View.GONE);
                }

                imSessionDao.update(imSession);
                int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);

                Intent intent = new Intent();
                intent.putExtra("yplay_sessionId", sessionId);
                intent.putExtra("yplay_session_status", status);
                intent.putExtra("yplay_sender", sender);
                intent.putExtra("yplay_msg_content", msgContent);
                intent.putExtra("yplay_nick_name", nickName);

                Log.d(TAG, "sessionId: " + sessionId);

                if (status == 0) {
                    intent.setClass(getActivity(), ActivityNnonymityReply.class);
                } else if (status == 1 && !TextUtils.isEmpty(sender) && sender.equals(String.valueOf(uin))) {
                    intent.setClass(getActivity(), ActivityChatWindow.class);
                } else if (status == 1 && !TextUtils.isEmpty(sender) && !sender.equals(String.valueOf(uin))) {
                    intent.setClass(getActivity(), ActivityChatWindow.class);
                } else if (status == 2) {
                    intent.setClass(getActivity(), ActivityChatWindow.class);
                }

                System.out.println("message----sessionId---" + sessionId);
                startActivity(intent);
            }
        });

        messageRecyclerView.setAdapter(messageAdapter);

        if (loadMoreView == null) {
            loadMoreView = new LoadMoreView(YplayApplication.getContext());
        }
        if (upRefreshView == null) {
            upRefreshView = new UpRefreshView(getActivity());
            rlRefreshLayout = (RelativeLayout) upRefreshView.findViewById(R.id.anim_up_background);
            rlRefreshLayout.setBackgroundColor(getActivity().getResources().
                    getColor(R.color.message_title_color));
        }

        messageRefreshView.setFooterView(loadMoreView);
        messageRefreshView.setHeaderView(upRefreshView);
        messageRefreshView.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                messageRefreshView.finishRefresh();
            }

            @Override
            public void loadMore() {
                loadMoreData();
                //messageRefreshView.finishLoadMore();
            }
        });
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser) {

            System.out.println("FragmentMessage---消息可见" + "--dataOffset--" + dataOffset);
            frgEdit.setVisibility(View.GONE);

            if (dataOffset == 0) {
                loadMoreData();
            } else {
                System.out.println("FragmentMessage---消息可见" + "--allDataSize--" + mDataList.size());
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setMessageIcon();

            setFriendCount();

            updateUi();
        }
    }

    //从数据库中查找会话列表
    private List<ImSession> queryDatabaseForImsession(int pageNumber) {

        Log.i(TAG, "queryDatabaseForImsession: uin---" + uin + " ---pageNum---" + pageNumber);

        if (pageNumber < 0) {
            return null;
        }

        long ts = 3000000000000L;
        if (mDataList.size() > 0) {
            ts = mDataList.get(mDataList.size() - 1).getMsgTs();
        }

        return imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.Uin.eq(uin))
                .where(ImSessionDao.Properties.MsgTs.lt(ts))
                .orderDesc(ImSessionDao.Properties.MsgTs)
                .limit(10)
                .list();
    }

    //更新UI 判断是否数据为空
    private void updateUi() {

        if (messageNull != null) {
            if (mDataList.size() > 0) {
                messageNull.setVisibility(View.GONE);
            } else {
                messageNull.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onSessionUpdate(ImSession imSession) {
        String sessionId = imSession.getSessionId();
        Log.d(TAG, "会话列表更新了onSessionUpdate: sessionId---" + sessionId + ", uin--" + imSession.getUin());

        if (uin != imSession.getUin()) {
            return;
        }

        int idx = 0;
        boolean find = false;

        for (; idx < mDataList.size(); ++idx) {
            if (mDataList.get(idx).getSessionId() == sessionId) {
                find = true;
                break;
            }
        }

        if (find) {
            Log.d(TAG, "会话列表更新了onSessionUpdate: sessionId已经存在回话列表中--" + sessionId);
            mDataList.remove(idx);
            mDataList.add(0, imSession);
        } else {
            Log.d(TAG, "会话列表更新了onSessionUpdate: sessionId是新回话--" + sessionId);
            mDataList.add(0, imSession);
            if (messageNull.isShown()) {
                messageNull.setVisibility(View.GONE);
            }
        }
        messageAdapter.notifyDataSetChanged();
    }

    public void setFriendCount() {

        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null) {
            int addFriendNum = myInfo.getAddFriendNum();
            if (addFriendNum == 0) {
                if (addFriendCount != null) {
                    addFriendCount.setText("");
                }

            } else {
                if (addFriendCount != null) {
                    addFriendCount.setText(addFriendNum + "");
                }

            }

        }
    }

}
