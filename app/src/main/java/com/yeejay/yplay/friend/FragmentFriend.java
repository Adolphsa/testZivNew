package com.yeejay.yplay.friend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;

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
import com.yeejay.yplay.model.FriendFeedsRespond;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.BaseUtils;
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
    @BindView(R.id.ff_refresh_layout)
    SwipeRefreshLayout ffRefreshLayout;

    FriendFeedsAdapter feedsAdapter;

    List<FriendFeedsRespond.PayloadBean.FeedsBean> feedsBeanList;
    FriendFeedsRespond.PayloadBean.FeedsBean mFeedsBean;

    DaoFriendFeedsDao mDaoFriendFeedsDao;
    DaoFriendFeeds mDaoFriendFeeds;
    List<DaoFriendFeeds> dataBasefeedsList;
    List<DaoFriendFeeds> mDataList;
    int listSize = 1;

    @OnClick(R.id.ff_imgbtn_add_friends)
    public void ffAddFriends(View view){
        startActivity(new Intent(getActivity(),AddFriends.class));
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
                Intent intent = new Intent(getActivity(),ActivityFriendsInfo.class);
                intent.putExtra("yplay_friend_header_img",mFeedsBean.getFriendHeadImgUrl());
                intent.putExtra("yplay_friend_school_name",mFeedsBean.getVoteFromSchoolName());
                intent.putExtra("yplay_friend_name",mFeedsBean.getFriendNickName());
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


        ffSwipeRecyclerView.useDefaultLoadMore();
        ffSwipeRecyclerView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                //加载更多
                System.out.println("上拉加载更多");
                loadMore();
            }
        });

        ffRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("下拉刷新");
                getFriendFeeds(BaseUtils.getCurrentDayTimeMillis(),10);
            }
        });

        //拉取数据
        getFriendFeeds(BaseUtils.getCurrentDayTimeMillis(),10);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于onResume()方法
            System.out.println("feeds可见");
            listSize = 1;
        } else {
            // 相当于onpause()方法
            System.out.println("feeds不可见");
        }
    }

    private void jumpToUserInfo(){
        ffUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });
    }

    //上拉加载更多数据
    private void loadMore(){
        if (feedsBeanList != null && feedsBeanList.size() > 0){
            long smallTs = feedsBeanList.get(feedsBeanList.size()-1).getTs();
            listSize++;
            getFriendFeeds(smallTs,10);
            ffSwipeRecyclerView.loadMoreFinish(false, false);
        }
    }


    //获取好友动态
    private void getFriendFeeds(long ts, int cnt){

        Map<String,Object> friendFeedsMap = new HashMap<>();
        friendFeedsMap.put("ts",ts);
        friendFeedsMap.put("cnt",cnt);
        friendFeedsMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN,0));
        System.out.println("UIN---" + SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN,0));
        friendFeedsMap.put("token",SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN,"yplay"));
        friendFeedsMap.put("ver",SharePreferenceUtil.get(getActivity(),YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .getFriendFeeds(friendFeedsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendFeedsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onNext(@NonNull FriendFeedsRespond friendFeedsRespond) {
                        System.out.println("获取好友动态---" + friendFeedsRespond.toString());
                        if (friendFeedsRespond.getCode() == 0){
                            feedsBeanList = friendFeedsRespond.getPayload().getFeeds();
                            if (feedsBeanList != null && feedsBeanList.size() > 0){
//
                                for (int i=0; i<feedsBeanList.size(); i++){
                                    //插入到数据库
                                    insertFeedsToDataBase(feedsBeanList.get(i));
                                }
                                dataBasefeedsList = mDaoFriendFeedsDao.queryBuilder()
                                        .orderDesc(DaoFriendFeedsDao.Properties.Ts)
                                        .limit(10)
                                        .list();
                                if (listSize == 1){
                                    System.out.println("listSize---为1");
                                    feedsAdapter = new FriendFeedsAdapter(getActivity(),dataBasefeedsList,mDaoFriendFeedsDao);
                                    ffSwipeRecyclerView.setAdapter(feedsAdapter);
                                    mDataList.addAll(dataBasefeedsList);
                                    ffSwipeRecyclerView.loadMoreFinish(false, true);
                                }else {
                                    System.out.println("listSize---" + listSize);
                                    mDataList.addAll(dataBasefeedsList);
                                    feedsAdapter.notifyDataSetChanged();
                                    System.out.println("mDataList的大小---" + mDataList.size());
                                    feedsAdapter.notifyItemRangeInserted(mDataList.size() - dataBasefeedsList.size(), dataBasefeedsList.size());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取好友动态异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ffRefreshLayout.setRefreshing(false);

                    }
                });
    }

    //插入数据到数据库
    private void insertFeedsToDataBase(FriendFeedsRespond.PayloadBean.FeedsBean feedsBean){

        DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.Ts.eq(feedsBean.getTs()))
                .build().unique();
        if (daoFriendFeeds == null){
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
