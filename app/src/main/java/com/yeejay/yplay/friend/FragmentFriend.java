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

    List<FriendFeedsRespond.PayloadBean.FeedsBean> feedsBeanList;
    FriendFeedsRespond.PayloadBean.FeedsBean mFeedsBean;

    DaoFriendFeedsDao mDaoFriendFeedsDao;
    DaoFriendFeeds mDaoFriendFeeds;
    //List<DaoFriendFeeds> dataBasefeedsList;
    List<DaoFriendFeeds> mDataList;
    boolean isRefresh = false;
    boolean isAddMore = false;
    boolean isFirst = true;

    int refreshOffser = 0;
    int addMoreOffset = 0;

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
                System.out.println("搞事情---" + position);
                mFeedsBean = feedsBeanList.get(position);
                Intent intent = new Intent(getActivity(), ActivityFriendsInfo.class);
                intent.putExtra("yplay_friend_header_img", mFeedsBean.getFriendHeadImgUrl());
                intent.putExtra("yplay_friend_school_name", mFeedsBean.getVoteFromSchoolName());
                intent.putExtra("yplay_friend_name", mFeedsBean.getFriendNickName());
                intent.putExtra("yplay_friend_school_grade",
                        FriendFeedsUtil.schoolType(mFeedsBean.getVoteFromSchoolType(),
                                mFeedsBean.getVoteFromGrade()));

                //将被点击的item设置为已读
                mDaoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                        .where(DaoFriendFeedsDao.Properties.Ts.eq(mFeedsBean.getTs()))
                        .build().unique();
                if (!mDaoFriendFeeds.getIsReaded())
                    mDaoFriendFeeds.setIsReaded(true);
                mDaoFriendFeedsDao.update(mDaoFriendFeeds);

                startActivity(intent);
            }
        });

        feedsAdapter = new FriendFeedsAdapter(getActivity(),
                mDataList,
                mDaoFriendFeedsDao);
        ffSwipeRecyclerView.setAdapter(feedsAdapter);
        long aa = System.currentTimeMillis();
        System.out.println("aa---" + aa);
        getFriendFeeds(aa, 10);
        ffPtfRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                long ts = System.currentTimeMillis();
                System.out.println("顶部刷新--" + ts);

                refreshOffser = 0;
                getFriendFeeds(ts, 10);
            }

            @Override
            public void loadMore() {
                refreshOffser++;
                long ts = mDataList.get(mDataList.size()-1).getTs();
                System.out.println("向下翻页----" + refreshOffser + ts);
                getFriendFeeds(ts, 10);
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于onResume()方法
            System.out.println("feeds可见");

        } else {
            // 相当于onpause()方法
            System.out.println("feeds不可见");
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
                            feedsBeanList = friendFeedsRespond.getPayload().getFeeds();
                            if (feedsBeanList != null && feedsBeanList.size() > 0) {

                                for (int i = 0; i < feedsBeanList.size(); i++) {
                                    //插入到数据库
                                    insertFeedsToDataBase(feedsBeanList.get(i));
                                }

                                makeSureFeeds(feedsBeanList.get(feedsBeanList.size() - 1).getTs(),
                                        feedsBeanList.get(0).getTs());
                            }
                        }

                        if (0 == refreshOffser) {
                            mDataList.clear();
                        }

                        List<DaoFriendFeeds> refreshList = refreshQuery(refreshOffser);
                        System.out.println("刷新----refreshOffser---" + refreshOffser);
                        System.out.println("刷新----refreshList---" + refreshList.size());

                        mDataList.addAll(refreshList);
                        feedsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取好友动态异常---" + e.getMessage());
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }
                });
    }


    //第一次加载
    private List<DaoFriendFeeds> firstQuery() {
//        System.out.println("addMoreOffset---" + addMoreOffset);
        List<DaoFriendFeeds> tempDataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
                .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                .limit(10)
                .list();
        System.out.println("第一次查询到加载的个数---" + tempDataBasefeedsList.size());
        return tempDataBasefeedsList;
    }

    //刷新查询
    private List<DaoFriendFeeds> refreshQuery(int refreshOffser) {

        List<DaoFriendFeeds> tempDataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
                .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                .offset(refreshOffser * 10)
                .limit(10)
                .list();
        System.out.println("刷新的个数---" + tempDataBasefeedsList.size());
        return tempDataBasefeedsList;
    }

    //加载更多
    private List<DaoFriendFeeds> addMoreQuery(int addMoreOffset) {
        System.out.println("addMoreOffset---" + addMoreOffset);
        List<DaoFriendFeeds> tempDataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
//                .where(DaoFriendFeedsDao.Properties.Ts.lt(mDataList.get(mDataList.size() - 1).getTs()))
                .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                .offset(addMoreOffset * 10)
                .limit(10)
                .list();
        System.out.println("加载更多的个数---" + tempDataBasefeedsList.size());
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
