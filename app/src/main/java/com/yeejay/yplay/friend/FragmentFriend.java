package com.yeejay.yplay.friend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.FriendFeedsAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.greendao.DaoFriendFeeds;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.model.FriendFeedsMakesureRespond;
import com.yeejay.yplay.model.FriendFeedsRespond;
import com.yeejay.yplay.model.UnReadMsgCountRespond;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.ff_ptf)
    PullToRefreshLayout ffPtfRefreshLayout;

    FriendFeedsAdapter feedsAdapter;
    DaoFriendFeedsDao mDaoFriendFeedsDao;
    List<DaoFriendFeeds> mDataList;

    int refreshOffset = 0;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //加好友
    @OnClick(R.id.ff_imgbtn_add_friends)
    public void ffAddFriends(View view) {
        startActivity(new Intent(getActivity(), AddFriends.class));
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        //跳转到我的资料
        jumpToUserInfo();
        mDataList = new ArrayList<>();

        mDaoFriendFeedsDao = YplayApplication.getInstance().getDaoSession().getDaoFriendFeedsDao();

        ffSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ffSwipeRecyclerView.addItemDecoration(new DefaultItemDecoration(Color.GRAY));

        ffSwipeRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                System.out.println("被点击的item---" + position);
                DaoFriendFeeds tempFeeds = mDataList.get(position);
                Intent intent = new Intent(getActivity(), ActivityFriendsInfo.class);
                intent.putExtra("yplay_friend_header_img", tempFeeds.getFriendHeadImgUrl());
                intent.putExtra("yplay_friend_school_name", tempFeeds.getVoteFromSchoolName());
                intent.putExtra("yplay_friend_name", tempFeeds.getFriendNickName());
                intent.putExtra("yplay_friend_school_grade",
                        FriendFeedsUtil.schoolType(tempFeeds.getVoteFromSchoolType(),
                                tempFeeds.getVoteFromGrade()));

                //将被点击的item设置为已读
                DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                        .where(DaoFriendFeedsDao.Properties.Ts.eq(tempFeeds.getTs()))
                        .build().unique();
                daoFriendFeeds.setIsReaded(true);
                mDaoFriendFeedsDao.update(daoFriendFeeds);
                startActivity(intent);
            }
        });

        feedsAdapter = new FriendFeedsAdapter(getActivity(),
                mDataList,
                mDaoFriendFeedsDao);
        ffSwipeRecyclerView.setAdapter(feedsAdapter);

        ffPtfRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                long ts = System.currentTimeMillis();
                System.out.println("顶部刷新--" + ts);

                refreshOffset = 0;
                getFriendFeeds(ts, 10);
            }

            @Override
            public void loadMore() {
                if (mDataList.size() > 0) {
                    refreshOffset++;
                    long ts = mDataList.get(mDataList.size() - 1).getTs();
                    System.out.println("向下翻页----" + refreshOffset + ts);
                    getFriendFeeds(ts, 10);
                } else {
                    System.out.println("无数据");
                    ffPtfRefreshLayout.finishLoadMore();
                }

            }
        });

    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser) {
            System.out.println("FragmentFriend---可见");
            long ts = System.currentTimeMillis();
            System.out.println("ts---" + ts);
            getFriendFeeds(ts, 10);
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
    public void getFriendFeeds(long ts, int cnt) {
        System.out.println("拉取好友动态----" + ts);
        Map<String, Object> friendFeedsMap = new HashMap<>();
        friendFeedsMap.put("ts", ts);
        friendFeedsMap.put("cnt", cnt);
        friendFeedsMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        System.out.println("UIN---" + SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        friendFeedsMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendFeedsMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .getFriendFeeds(friendFeedsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendFeedsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull FriendFeedsRespond friendFeedsRespond) {
                        System.out.println("获取好友动态---" + friendFeedsRespond.toString());
                        if (friendFeedsRespond.getCode() == 0) {
                            List<FriendFeedsRespond.PayloadBean.FeedsBean> feedsBeanList = friendFeedsRespond.getPayload().getFeeds();
                            if (feedsBeanList != null && feedsBeanList.size() > 0) {

                                for (int i = 0; i < feedsBeanList.size(); i++) {
                                    //插入到数据库
                                    insertFeedsToDataBase(feedsBeanList.get(i));
                                }

                                makeSureFeeds(feedsBeanList.get(feedsBeanList.size() - 1).getTs(),
                                        feedsBeanList.get(0).getTs());
                            }
                        }
                        //从数据库取数据更新到UI
                        updateUiData();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取好友动态异常---" + e.getMessage());
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                        //从数据库取数据更新到UI
                        updateUiData();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("获取好友动态完成---onComplete");
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }
                });
    }


    //更新UI数据
    private void updateUiData() {

        if (0 == refreshOffset) {
            mDataList.clear();
        }
        List<DaoFriendFeeds> refreshList = refreshQuery(refreshOffset);
        System.out.println("刷新----refreshOffser---" + refreshOffset);
        System.out.println("刷新----refreshList---" + refreshList.size());

        mDataList.addAll(refreshList);
        feedsAdapter.notifyDataSetChanged();
    }

    //数据查询
    private List<DaoFriendFeeds> refreshQuery(int refreshOffset) {

        List<DaoFriendFeeds> tempDataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
                .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                .offset(refreshOffset * 10)
                .limit(10)
                .list();
        System.out.println("查询到到的个数---" + tempDataBasefeedsList.size());
        return tempDataBasefeedsList;
    }

    //插入数据到数据库
    private void insertFeedsToDataBase(FriendFeedsRespond.PayloadBean.FeedsBean feedsBean) {

        DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.Ts.eq(feedsBean.getTs()))
                .build().unique();
        if (daoFriendFeeds == null) {
            System.out.println("插入数据库");
            DaoFriendFeeds insert = new DaoFriendFeeds(null,
                    feedsBean.getTs(),
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
        System.out.println("收到好友动态确认");
        Map<String, Object> makeSureFeedsMap = new HashMap<>();
        makeSureFeedsMap.put("minTs", minTs);
        makeSureFeedsMap.put("maxTs", maxTs);
        makeSureFeedsMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        makeSureFeedsMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        makeSureFeedsMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .makeSureFeeds(makeSureFeedsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendFeedsMakesureRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull FriendFeedsMakesureRespond friendFeedsMakesureRespond) {
                        if (friendFeedsMakesureRespond.getCode() == 0) {
                            System.out.println("确认收到数据---" + friendFeedsMakesureRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("确认收到数据异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getAddFriendMessageCount() {

        Map<String, Object> unreadFriendMsgCountMap = new HashMap<>();
        unreadFriendMsgCountMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        unreadFriendMsgCountMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        unreadFriendMsgCountMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUnreadMessageCount(unreadFriendMsgCountMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnReadMsgCountRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UnReadMsgCountRespond unReadMsgCountRespond) {
                        System.out.println("未读好友消息---" + unReadMsgCountRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("未读好友消息数异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
