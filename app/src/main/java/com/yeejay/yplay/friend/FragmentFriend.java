package com.yeejay.yplay.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.FriendFeedsAdapter;
import com.yeejay.yplay.adapter.RecommendFriendForNullAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.customview.CardBigDialog;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.customview.MyLinearLayoutManager;
import com.yeejay.yplay.greendao.DaoFriendFeeds;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.greendao.FriendInfoDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendFeedsMakesureRespond;
import com.yeejay.yplay.model.FriendFeedsRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 好友
 * Created by Administrator on 2017/10/26.
 */

public class FragmentFriend extends BaseFragment implements FriendFeedsAdapter.OnRecycleImageListener {

    @BindView(R.id.ff_user_info)
    ImageButton ffUserInfo;
    @BindView(R.id.ff_message_count)
    TextView addFriendTextView;
    @BindView(R.id.ff_swipe_recycler_view)
    RecyclerView ffSwipeRecyclerView;
    @BindView(R.id.ff_ptf)
    PullToRefreshLayout ffPtfRefreshLayout;

    @BindView(R.id.frans_frf_layout)
    LinearLayout fransFrfLayout;

    private static final String TAG = "FragmentFriend";
    public static final int FEED_REQUEST_CODE = 0x111;

    FriendFeedsAdapter feedsAdapter;
    DaoFriendFeedsDao mDaoFriendFeedsDao;
    FriendInfoDao friendInfoDao;
    List<DaoFriendFeeds> mDataList = new ArrayList<>();
    MainActivity mainActivity;
    MyLinearLayoutManager linearLayoutMgr;
    DefaultItemDecoration defaultItemDecoration;

    int refreshOffset = 0;

    private RelativeLayout rl;
    private LoadMoreView loadMoreView;
//    private UpRefreshView upRefreshView;
    private RelativeLayout rlRefreshLayout;
    private String isRemoveFriend;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_friend;
    }

    private void resetData() {
        mDataList.clear();
        refreshOffset = 0;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        LogUtils.getInstance().debug("initAllMembersView:");

        //跳转到我的资料
        jumpToUserInfo();

        mainActivity = (MainActivity) getActivity();

        mDaoFriendFeedsDao = YplayApplication.getInstance().getDaoSession().getDaoFriendFeedsDao();
        friendInfoDao = YplayApplication.getInstance().getDaoSession().getFriendInfoDao();

        linearLayoutMgr = new MyLinearLayoutManager(getActivity());
        ffSwipeRecyclerView.setLayoutManager(linearLayoutMgr);

        defaultItemDecoration = new DefaultItemDecoration(getResources().getColor(R.color.divider_color2));
        ffSwipeRecyclerView.addItemDecoration(defaultItemDecoration);

        if (feedsAdapter == null) {
            feedsAdapter = new FriendFeedsAdapter(YplayApplication.getContext(),
                    mDataList,
                    friendInfoDao);
        }
        ffSwipeRecyclerView.setAdapter(feedsAdapter);
        feedsAdapter.addRecycleImageListener(this);

        if (loadMoreView == null) {
            loadMoreView = new LoadMoreView(YplayApplication.getContext());
        }
/*        if (upRefreshView == null) {
            upRefreshView = new UpRefreshView(getActivity());
            rlRefreshLayout = (RelativeLayout) upRefreshView.findViewById(R.id.anim_up_background);
            rlRefreshLayout.setBackgroundColor(mainActivity.getResources().
                    getColor(R.color.feeds_title_color));
        }*/
        ffPtfRefreshLayout.setFooterView(loadMoreView);
        //ffPtfRefreshLayout.setHeaderView(upRefreshView);
        ffPtfRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                long ts = System.currentTimeMillis();
                LogUtils.getInstance().debug("顶部刷新, ts = {}", ts);

                if (0 == mDataList.size()) {
                    updateUiData();
                } else {
                    resetData();
                    //拉取新数据
                    getFriendFeeds(ts, 20, false);
                }

            }

            @Override
            public void loadMore() {
                LogUtils.getInstance().debug("底部刷新");

                if (0 == mDataList.size()) {
                    updateUiData();
                } else {
                    //取当前最后一个feed的ts去服务器拉数据 并插入到本地数据库
                    DaoFriendFeeds feed = mDataList.get(mDataList.size() - 1);
                    long ts = feed.getTs();
                    getFriendFeeds(ts, 10, true);
                }

                ffPtfRefreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);

        Log.i(TAG, "onVisibilityChangedToUser: ");
        if (isVisibleToUser) {

            //判断当前的view是否已经滑动到顶部，如果是则需要自动更新 pos=0表示在顶部
            int pos = -1;
            if (linearLayoutMgr != null) {
                pos = linearLayoutMgr.findFirstCompletelyVisibleItemPosition();
            }
            LogUtils.getInstance().debug("FragmentFriend---可见，refreshOffset = {}, feed数组大小 = {}, 在顶部 = {}",
                    refreshOffset, mDataList.size(), pos);

            //refreshOffset = 0 表示第一次进入动态页面
            //refreshOffset = 1 表示拉取过一次进入动态页面
            if (refreshOffset <= 1) {

                if (!TextUtils.isEmpty(isRemoveFriend) && isRemoveFriend.equals("yes"))
                    return;

                //pos = 0 表示拉取过并且处于顶端
                //refreshOffset = 0 表示从来没有数据，这时需要去刷新看看有没有新数据
                if (pos == 0 || refreshOffset == 0) {
                    resetData();
                    long ts = System.currentTimeMillis();
                    LogUtils.getInstance().debug("scrollview 在顶部，需要更新数据: ts = {}", ts);
                    getFriendFeeds(ts, 10, false);
                }
            }

            setFriendCount();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == FEED_REQUEST_CODE && data != null){
            isRemoveFriend = data.getStringExtra("is_remove_friend");
            Log.i(TAG, "onActivityResult: isRemoveFriend---" + isRemoveFriend);
        }
    }

    private void jumpToUserInfo() {
        ffUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });
    }

    //获取好友动态
    public void getFriendFeeds(long ts, int cnt, final boolean isLoadMore) {
        LogUtils.getInstance().debug("{}, ts = {}", "拉取好友动态", ts);
        Map<String, Object> friendFeedsMap = new HashMap<>();
        friendFeedsMap.put("ts", ts);
        friendFeedsMap.put("cnt", cnt);
        friendFeedsMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        System.out.println("UIN---" + SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        friendFeedsMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendFeedsMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETFEEDS_URL, friendFeedsMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetFriendFeedsResponse(result, isLoadMore);

                        LogUtils.getInstance().debug("onComplete() {}", "获取好友动态完成");
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onTimeOut() {

                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("onError() {}", "获取好友动态异常");
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }
                });

    }

    private void handleGetFriendFeedsResponse(String result, final boolean isLoadMore) {
        FriendFeedsRespond friendFeedsRespond = GsonUtil.GsonToBean(result, FriendFeedsRespond.class);
        LogUtils.getInstance().debug("获取好友动态: {}", friendFeedsRespond.toString());
        if (friendFeedsRespond.getCode() == 0) {

            List<FriendFeedsRespond.PayloadBean.FeedsBean> feedsBeanList = friendFeedsRespond.getPayload().getFeeds();
            if (feedsBeanList != null && feedsBeanList.size() > 0) {
                LogUtils.getInstance().debug("拉取好友动态, retCnt = {}", feedsBeanList.size());
                for (int i = 0; i < feedsBeanList.size(); i++) {
                    //插入到数据库
                    insertFeedsToDataBase(feedsBeanList.get(i));
                }

                makeSureFeeds(feedsBeanList.get(feedsBeanList.size() - 1).getTs(),
                        feedsBeanList.get(0).getTs());
            }

            LogUtils.getInstance().debug("从数据库查询动态, pagenum = {}", refreshOffset);

            List<DaoFriendFeeds> refreshList = refreshQuery(refreshOffset++);
            if (refreshList.size() == 0) {
                //已经是最后一页了没有数据了，页码不应该一直增加
                refreshOffset--;
                //todo提示加载完成了没有更多数据了
                loadMoreView.noData();
                LogUtils.getInstance().debug("从数据库查询动态返回为空, pagenum = {}", refreshOffset);
            } else {
                //不是最后一页，在拉取数据后，RecycleView自动向上滑动一个item高度；
                mDataList.addAll(refreshList);
                if (isLoadMore) {
                    //是底部刷新时，才需要向上自动滚动2个item高度；
                    if (refreshList.size() >= 2) {
                        LogUtils.getInstance().debug("smoothScroll to position = {}, mDatalist.size() = {}",
                                mDataList.size() - refreshList.size() + 1, mDataList.size());
                        ffSwipeRecyclerView.smoothScrollToPosition(mDataList.size() - refreshList.size() + 1);
                    } else if (refreshList.size() == 1) {
                        LogUtils.getInstance().debug("smoothScroll to position = {}, mDatalist.size() = {}",
                                mDataList.size() - refreshList.size(), mDataList.size());
                        ffSwipeRecyclerView.smoothScrollToPosition(mDataList.size() - refreshList.size());
                    }
                }

                feedsAdapter.notifyDataSetChanged();
            }

            LogUtils.getInstance().debug("当前feeds总数目 = {}, 当前页码数 = {}",
                    mDataList.size(), refreshOffset - 1);

            //判断是否扩列开启
            updateUiData();

            //顶部刷新清理点亮标志
            if (!isLoadMore) {
                mainActivity.setNewFeeds(false);
                mainActivity.setFeedClear();
            }

        }
    }

    //扩列开启界面
    private void updateUiData() {
        if (mDataList.size() == 0) {
            LogUtils.getInstance().debug("无动态");
            fransFrfLayout.setVisibility(View.VISIBLE);
            ffPtfRefreshLayout.setVisibility(View.GONE);
            initRecommentFriends();
        } else {
            LogUtils.getInstance().debug("有动态");
            fransFrfLayout.setVisibility(View.GONE);
            ffPtfRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    //数据查询
    private List<DaoFriendFeeds> refreshQuery(int refreshOffset) {
        int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);
        List<DaoFriendFeeds> tempDataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.Uin.eq(uin))
                .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                .offset(refreshOffset * 20)
                .limit(20)
                .list();
        LogUtils.getInstance().debug("查询到到的个数 = {}", tempDataBasefeedsList.size());
        return tempDataBasefeedsList;
    }

    //插入数据到数据库
    private void insertFeedsToDataBase(FriendFeedsRespond.PayloadBean.FeedsBean feedsBean) {

        int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);

        DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.Ts.eq(feedsBean.getTs()))
                .build().unique();
        if (daoFriendFeeds == null) {
            LogUtils.getInstance().debug("插入数据库");
            DaoFriendFeeds insert = new DaoFriendFeeds(null,
                    feedsBean.getTs(),
                    uin,
                    feedsBean.getVoteRecordId(),
                    feedsBean.getFriendUin(),
                    feedsBean.getFriendNickName(),
                    feedsBean.getFriendGender(),
                    feedsBean.getFriendHeadImgUrl(),
                    feedsBean.getQid(),
                    feedsBean.getQtext(),
                    feedsBean.getQiconUrl(),
                    feedsBean.getVoteFromUin(),
                    feedsBean.getVoteFromGender(),
                    feedsBean.getVoteFromSchoolId(),
                    feedsBean.getVoteFromSchoolType(),
                    feedsBean.getVoteFromSchoolName(),
                    feedsBean.getVoteFromGrade(),
                    false
            );
            mDaoFriendFeedsDao.insert(insert);
        }
    }

    //收到好友动态数据确认
    private void makeSureFeeds(long minTs, long maxTs) {
        LogUtils.getInstance().debug("收到好友动态确认");
        Map<String, Object> makeSureFeedsMap = new HashMap<>();
        makeSureFeedsMap.put("minTs", minTs);
        makeSureFeedsMap.put("maxTs", maxTs);
        makeSureFeedsMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        makeSureFeedsMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        makeSureFeedsMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_ACKFEEDS, makeSureFeedsMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleMakeSureFeeds(result);
                    }

                    @Override
                    public void onTimeOut() {

                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("确认收到数据异常");
                    }
                });
    }

    private void handleMakeSureFeeds(String result) {
        FriendFeedsMakesureRespond friendFeedsMakesureRespond = GsonUtil.GsonToBean(result,
                FriendFeedsMakesureRespond.class);
        LogUtils.getInstance().debug("确认收到数据: {}", friendFeedsMakesureRespond.toString());
    }

    RecommendFriendForNullAdapter recommendFriendForNullAdapter;
    ListView rfListView;

    //初始化推荐好友界面
    private void initRecommentFriends() {

        if (fransFrfLayout.isShown()) {
            rfListView = (ListView) fransFrfLayout.findViewById(R.id.frf_list_view);
            Button addFriend = (Button) fransFrfLayout.findViewById(R.id.frf_btn_add_friend);
            ImageButton noMoreShowTv = (ImageButton) fransFrfLayout.findViewById(R.id.frf_no_more_show);
            TextView seeMore = (TextView) fransFrfLayout.findViewById(R.id.frf_see_more);
            rl = (RelativeLayout) fransFrfLayout.findViewById(R.id.frf_recommend_rl);
            final PullToRefreshLayout recommendPullView = (PullToRefreshLayout) fransFrfLayout.findViewById(R.id.frf_recommend_pull_view);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.getInstance().debug("添加好友");
                    startActivity(new Intent(getActivity(), AddFriends.class));
                }
            });

            noMoreShowTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.getInstance().debug("不再显示");
                    rl.setVisibility(View.INVISIBLE);

                    SharePreferenceUtil.put(getActivity(), YPlayConstant.YPLAY_NO_MORE_SHOW, true);

                    //将数据库中的值变为1
                    MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
                    int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);
                    MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                            .build().unique();
                    if (myInfo != null) {
                        myInfo.setIsNoMoreShow(1);
                        myInfoDao.update(myInfo);
                    }

                }
            });

            //查看更多
            seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AddFriends.class));
                }
            });

            recommendPullView.setRefreshListener(new BaseRefreshListener() {
                @Override
                public void refresh() {
                    LogUtils.getInstance().debug("刷新");
                    long ts = System.currentTimeMillis();
                    getFriendFeeds(ts, 20, false);
                    recommendPullView.finishRefresh();
                }

                @Override
                public void loadMore() {
                    LogUtils.getInstance().debug("加载更多");
                    recommendPullView.finishLoadMore();

                }
            });
            MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
            int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);
            MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                    .build().unique();
            if (myInfo != null && myInfo.getIsNoMoreShow() == 1) {
                rl.setVisibility(View.GONE);
            }

            recommendFriendsForNull();
        }
    }

    private void initRecommendList(final List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList) {

        recommendFriendForNullAdapter = new RecommendFriendForNullAdapter(YplayApplication.getContext(),
                new RecommendFriendForNullAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {

                    }
                },
                new RecommendFriendForNullAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        Button button = (Button) v;
                        GetRecommendsRespond.PayloadBean.FriendsBean friendsBean = tempList.get((int) v.getTag());
                        int recommendType = friendsBean.getRecommendType();
                        if (recommendType == 2) {
                            button.setBackgroundResource(R.drawable.play_invite_yes);
                            //邀请
                            String phone = GsonUtil.GsonString(friendsBean.getPhone());
                            LogUtils.getInstance().debug("邀请的电话: {}", phone);
                            String base64phone = Base64.encodeToString(phone.getBytes(), Base64.DEFAULT);
                            invitefriendsbysms(base64phone);
                        } else if (recommendType == 1 || recommendType == 3 || recommendType == 4) {
                            button.setBackgroundResource(R.drawable.btn_alread_applt);
                            int uin = friendsBean.getUin();
                            addFriend(uin);
                        }
                    }
                },
                tempList, 1);
        rfListView.setAdapter(recommendFriendForNullAdapter);
    }

    //获取推荐好友信息
    private void recommendFriendsForNull() {
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        tempMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        tempMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETRANDOMRECOMMENDS, tempMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleRecommendFriendsForNullResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("推荐好友异常");
                    }
                });
    }

    private void handleRecommendFriendsForNullResponse(String result) {
        GetRecommendsRespond getRecommendsRespond = GsonUtil.GsonToBean(result, GetRecommendsRespond.class);
        LogUtils.getInstance().debug("推荐好友: {}", getRecommendsRespond.toString());
        if (getRecommendsRespond.getCode() == 0) {
            if (getRecommendsRespond.getPayload().getFriends() != null
                    && getRecommendsRespond.getPayload().getFriends().size() > 0) {
                initRecommendList(getRecommendsRespond.getPayload().getFriends());
            } else {
                rl.setVisibility(View.GONE);
            }
        }
    }

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {
        Map<String, Object> inviteFreindMap = new HashMap<>();
        inviteFreindMap.put("friends", friends);
        inviteFreindMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        inviteFreindMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        inviteFreindMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_INVITEFRIENDSBYSMS_URL, inviteFreindMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleInviteFriendsBySmsResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void handleInviteFriendsBySmsResponse(String result) {
        BaseRespond baseRespond = GsonUtil.GsonToBean(result, BaseRespond.class);
        LogUtils.getInstance().debug("短信邀请好友: {}", baseRespond.toString());
    }

    //发送加好友的请求
    private void addFriend(int toUin) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_ADDFRIEND, addFreindMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleAddFriendResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("发送加好友请求异常");
                    }
                });
    }

    private void handleAddFriendResponse(String result) {
        AddFriendRespond addFriendRespond = GsonUtil.GsonToBean(result, AddFriendRespond.class);
        LogUtils.getInstance().debug("发送加好友请求: {}", addFriendRespond.toString());
    }

    //查询我和好友的关系
    private void getFriendInfo(int friendUin, final DaoFriendFeeds tempFeeds) {
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETUSERPROFILE, friendMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetFriendInfoResponse(result, tempFeeds);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("获取朋友资料异常");
                    }
                });
    }

    private void handleGetFriendInfoResponse(String result, final DaoFriendFeeds tempFeeds) {
        UserInfoResponde userInfoResponde = GsonUtil.GsonToBean(result, UserInfoResponde.class);
        LogUtils.getInstance().debug("获取朋友资料: {}", userInfoResponde.toString());
        if (userInfoResponde.getCode() == 0) {
            int status = userInfoResponde.getPayload().getStatus();

            if (status == 0) {//非好友
                showCardDialog(userInfoResponde.getPayload());
            } else if (status == 1) {//好友
                Intent intent = new Intent(getActivity(), ActivityFriendsInfo.class);
                intent.putExtra("yplay_friend_name", tempFeeds.getFriendNickName());
                intent.putExtra("yplay_friend_uin", tempFeeds.getFriendUin());
                LogUtils.getInstance().debug("朋友的uin = {}", tempFeeds.getFriendUin());
                //将被点击的item设置为已读
                DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                        .where(DaoFriendFeedsDao.Properties.Ts.eq(tempFeeds.getTs()))
                        .build().unique();
                daoFriendFeeds.setIsReaded(true);
                mDaoFriendFeedsDao.update(daoFriendFeeds);
                startActivityForResult(intent,FEED_REQUEST_CODE);
            }
        }
    }

    //显示名片
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean) {

        final UserInfoResponde.PayloadBean.InfoBean infoBean = payloadBean.getInfo();

        final CardBigDialog cardDialog = new CardBigDialog(getActivity(), R.style.CustomDialog,
                payloadBean);

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView button = (TextView) v;
                if (NetWorkUtil.isNetWorkAvailable(getActivity())) {
                    button.setBackgroundResource(R.drawable.shape_friend_card_add_selected_bg);
                    button.setTextColor(getResources().getColor(R.color.text_color_gray2));
                    button.setEnabled(false);

                    addFriend(infoBean.getUin());
                } else {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardDialog.show();
    }


    //设置添加好友的数量
    public void setFriendCount() {

        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null) {
            int addFriendNum = myInfo.getAddFriendNum();
            if (addFriendNum == 0) {
                if (addFriendTextView != null) {
                    addFriendTextView.setText("");
                }

            } else {
                if (addFriendTextView != null) {
                    addFriendTextView.setText(String.valueOf(addFriendNum));
                }

            }

        }
    }


    @Override
    public void OnRecycleImageClick(View v, Object o) {
        switch (v.getId()) {
            case R.id.ff_item_header_img:

                int position = (int) o;
                DaoFriendFeeds tempFeeds = mDataList.get(position);
                LogUtils.getInstance().debug("OnRecycleImageClick: 头像被点击, position = {}", position);
                //查询关系之后再跳转
                getFriendInfo(tempFeeds.getFriendUin(), tempFeeds);
                break;
        }
    }
}
