package com.yeejay.yplay.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.FriendFeedsAdapter;
import com.yeejay.yplay.adapter.RecommendFriendForNullAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.customview.CardDialog;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.customview.MyLinearLayoutManager;
import com.yeejay.yplay.customview.UpRefreshView;
import com.yeejay.yplay.greendao.DaoFriendFeeds;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.greendao.FriendInfo;
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
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 好友
 * Created by Administrator on 2017/10/26.
 */

public class FragmentFriend extends BaseFragment implements FriendFeedsAdapter.OnRecycleImageListener{

    @BindView(R.id.ff_user_info)
    ImageButton ffUserInfo;
    @BindView(R.id.ff_message_count)
    TextView addFriendTextView;
    @BindView(R.id.ff_swipe_recycler_view)
    SwipeMenuRecyclerView ffSwipeRecyclerView;
    @BindView(R.id.ff_ptf)
    PullToRefreshLayout ffPtfRefreshLayout;

    @BindView(R.id.frans_frf_layout)
    LinearLayout fransFrfLayout;

    private static final String TAG = "FragmentFriend";

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
    private UpRefreshView upRefreshView;
    private RelativeLayout rlRefreshLayout;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_friend;
    }

    private void resetData(){
         mDataList.clear();
         refreshOffset = 0;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        Log.i(TAG, "initAllMembersView: ");

        //跳转到我的资料
        jumpToUserInfo();

        mainActivity = (MainActivity) getActivity();

        mDaoFriendFeedsDao = YplayApplication.getInstance().getDaoSession().getDaoFriendFeedsDao();
        friendInfoDao = YplayApplication.getInstance().getDaoSession().getFriendInfoDao();

        linearLayoutMgr = new MyLinearLayoutManager(getActivity());
        ffSwipeRecyclerView.setLayoutManager(linearLayoutMgr);

        defaultItemDecoration = new DefaultItemDecoration(getResources().getColor(R.color.divider_color2));
        ffSwipeRecyclerView.addItemDecoration(defaultItemDecoration);

        if(feedsAdapter == null) {
            feedsAdapter = new FriendFeedsAdapter(getActivity(),
                    mDataList,
                    friendInfoDao);
        }
        ffSwipeRecyclerView.setAdapter(feedsAdapter);
        feedsAdapter.addRecycleImageListener(this);

        if(loadMoreView == null) {
            loadMoreView = new LoadMoreView(getActivity());
        }
        if(upRefreshView == null) {
            upRefreshView = new UpRefreshView(getActivity());
            rlRefreshLayout = (RelativeLayout) upRefreshView.findViewById(R.id.anim_up_background);
            rlRefreshLayout.setBackgroundColor(mainActivity.getResources().
                    getColor(R.color.feeds_title_color));
        }
        ffPtfRefreshLayout.setFooterView(loadMoreView);
        ffPtfRefreshLayout.setHeaderView(upRefreshView);
        ffPtfRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                long ts = System.currentTimeMillis();
                System.out.println("顶部刷新--" + ts);

                resetData();

                //拉取新数据
                getFriendFeeds(ts, 10, false);

            }

            @Override
            public void loadMore() {
                System.out.println("底部刷新--");

                if(0 == mDataList.size()){
                    updateUiData();
                }else {
                    //取当前最后一个feed的ts去服务器拉数据 并插入到本地数据库
                    DaoFriendFeeds feed = mDataList.get(mDataList.size()-1);
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
        if (isVisibleToUser) {

            //判断当前的view是否已经滑动到顶部，如果是则需要自动更新 pos=0表示在顶部
            int pos = -1;
            if(linearLayoutMgr != null){
                pos = linearLayoutMgr.findFirstCompletelyVisibleItemPosition();
            }
            System.out.println("FragmentFriend---可见" + "  refreshOffset-- " + refreshOffset + " feed数组大小--" + mDataList.size()
                   + " , 在顶部 = " + pos);

            //refreshOffset = 0 表示第一次进入动态页面
            //refreshOffset = 1 表示拉取过一次进入动态页面
            if ( refreshOffset <= 1) {

                //pos = 0 表示拉取过并且处于顶端
                //refreshOffset = 0 表示从来没有数据，这时需要去刷新看看有没有新数据
                if(pos == 0 || refreshOffset == 0){
                    resetData();
                    long ts = System.currentTimeMillis();
                    Log.i(TAG, "scrollview 在顶部，需要更新数据: ts---" + ts);
                    getFriendFeeds(ts, 10, false);
                }
            }

            setFriendCount();
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
                                Log.d("feed","拉取好友动态----" + " retCnt--" + feedsBeanList.size());
                                for (int i = 0; i < feedsBeanList.size(); i++) {
                                    //插入到数据库
                                    insertFeedsToDataBase(feedsBeanList.get(i));
                                }

                                makeSureFeeds(feedsBeanList.get(feedsBeanList.size() - 1).getTs(),
                                        feedsBeanList.get(0).getTs());
                            }

                            Log.d("feed","从数据库查询动态----" + " pagenum--" + refreshOffset);

                            List<DaoFriendFeeds> refreshList = refreshQuery(refreshOffset++);
                            if(refreshList.size() == 0){
                                //已经是最后一页了没有数据了，页码不应该一直增加
                                refreshOffset--;
                                //todo提示加载完成了没有更多数据了
                                loadMoreView.noData();
                                Log.d("feed","从数据库查询动态返回为空----" + " pagenum--" + refreshOffset);
                            } else {
                                //不是最后一页，在拉取数据后，RecycleView自动向上滑动一个item高度；
                                mDataList.addAll(refreshList);
                                if (isLoadMore) {
                                    //是底部刷新时，才需要向上自动滚动2个item高度；
                                    if(refreshList.size() >= 2) {
                                        Log.d("feed","smoothScroll to position---" + (mDataList.size()-refreshList.size()+1) + "  mDatalist.size()---" + mDataList.size());
                                        ffSwipeRecyclerView.smoothScrollToPosition(mDataList.size()-refreshList.size()+1);
                                    } else if (refreshList.size() == 1) {
                                        Log.d("feed","smoothScroll to position---" + (mDataList.size()-refreshList.size()) + "  mDatalist.size()---" + mDataList.size());
                                        ffSwipeRecyclerView.smoothScrollToPosition(mDataList.size()-refreshList.size());
                                    }
                                }

                                feedsAdapter.notifyDataSetChanged();
                            }

                            Log.d("feed","当前feeds总数目----" + mDataList.size() + "当前页码数--" + (refreshOffset-1));

                            //判断是否扩列开启
                            updateUiData();

                            //顶部刷新清理点亮标志
                            if ( !isLoadMore ) {
                                mainActivity.setNewFeeds(false);
                                mainActivity.setFeedClear();
                            }

                        }else{
                            //todo失败的处理
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取好友动态异常---" + e.getMessage());
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("获取好友动态完成---onComplete");
                        ffPtfRefreshLayout.finishRefresh();
                        ffPtfRefreshLayout.finishLoadMore();
                    }
                });
    }


    //扩列开启界面
    private void updateUiData() {
           if(mDataList.size()==0) {
               System.out.println("无动态");
               fransFrfLayout.setVisibility(View.VISIBLE);
               ffPtfRefreshLayout.setVisibility(View.GONE);
               initRecommentFriends();
           }else{
            System.out.println("有动态");
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
                .offset(refreshOffset * 10)
                .limit(10)
                .list();
        System.out.println("查询到到的个数---" + tempDataBasefeedsList.size());
        return tempDataBasefeedsList;
    }

    //插入数据到数据库
    private void insertFeedsToDataBase(FriendFeedsRespond.PayloadBean.FeedsBean feedsBean) {

        int uin = (int) SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, (int) 0);

        DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.Ts.eq(feedsBean.getTs()))
                .build().unique();
        if (daoFriendFeeds == null) {
            System.out.println("插入数据库");
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

//    private void getAddFriendMessageCount() {
//
//        Map<String, Object> unreadFriendMsgCountMap = new HashMap<>();
//        unreadFriendMsgCountMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
//        unreadFriendMsgCountMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
//        unreadFriendMsgCountMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
//        YPlayApiManger.getInstance().getZivApiService()
//                .getUnreadMessageCount(unreadFriendMsgCountMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<UnReadMsgCountRespond>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull UnReadMsgCountRespond unReadMsgCountRespond) {
//                        System.out.println("未读好友消息---" + unReadMsgCountRespond.toString());
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        System.out.println("未读好友消息数异常---" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

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
                    System.out.println("添加好友");
                    startActivity(new Intent(getActivity(), AddFriends.class));
                }
            });

            noMoreShowTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("不再显示");
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
                    System.out.println("刷新");
                    long ts = System.currentTimeMillis();
                    getFriendFeeds(ts, 10, false);
                    recommendPullView.finishRefresh();
                }

                @Override
                public void loadMore() {
                    System.out.println("加载更多");
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

        recommendFriendForNullAdapter = new RecommendFriendForNullAdapter(getActivity(),
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
                            System.out.println("邀请的电话---" + phone);
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
        YPlayApiManger.getInstance().getZivApiService()
                .recommendFriendsForNull(tempMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
                        System.out.println("推荐好友---" + getRecommendsRespond.toString());
                        if (getRecommendsRespond.getCode() == 0) {

                            if (getRecommendsRespond.getPayload().getFriends() != null
                                    && getRecommendsRespond.getPayload().getFriends().size() > 0) {
                                initRecommendList(getRecommendsRespond.getPayload().getFriends());
                            } else {
                                rl.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("推荐好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("friends", friends);
        removeFreindMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .removeFriend(removeFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("短信邀请好友---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("短信邀请好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //发送加好友的请求
    private void addFriend(int toUin) {

        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .addFriend(addFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddFriendRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AddFriendRespond addFriendRespond) {
                        System.out.println("发送加好友请求---" + addFriendRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("发送加好友请求异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //查询我和好友的关系
    private void getFriendInfo(int friendUin, final DaoFriendFeeds tempFeeds) {
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUserInfo(friendMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserInfoResponde userInfoResponde) {
                        System.out.println("获取朋友资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0) {
                            int status = userInfoResponde.getPayload().getStatus();

                            if (status == 0) {//非好友
                                showCardDialog(userInfoResponde.getPayload());
                            } else if (status == 1) {//好友
                                Intent intent = new Intent(getActivity(), ActivityFriendsInfo.class);
                                intent.putExtra("yplay_friend_name", tempFeeds.getFriendNickName());
                                intent.putExtra("yplay_friend_uin", tempFeeds.getFriendUin());
                                System.out.println("朋友的uin---" + tempFeeds.getFriendUin());
                                //将被点击的item设置为已读
                                DaoFriendFeeds daoFriendFeeds = mDaoFriendFeedsDao.queryBuilder()
                                        .where(DaoFriendFeedsDao.Properties.Ts.eq(tempFeeds.getTs()))
                                        .build().unique();
                                daoFriendFeeds.setIsReaded(true);
                                mDaoFriendFeedsDao.update(daoFriendFeeds);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("获取朋友资料异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //显示名片
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean) {

        final UserInfoResponde.PayloadBean.InfoBean infoBean = payloadBean.getInfo();
        int status = payloadBean.getStatus();

        final CardDialog cardDialog = new CardDialog(getActivity(), R.style.CustomDialog);
        cardDialog.setCardImgStr(infoBean.getHeadImgUrl());
        cardDialog.setCardDiamondCountStr("钻石 " + String.valueOf(infoBean.getGemCnt()));
        cardDialog.setCardNameStr(infoBean.getNickName());
        cardDialog.setCardSchoolNameStr(infoBean.getSchoolName());
        cardDialog.setCardGradeStr(FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade()));

        if (status == 0) {
            cardDialog.setButtonImg(R.drawable.green_add_friend);
        } else if (status == 2) {
            cardDialog.setButtonImg(R.drawable.already_apply);
        }

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (NetWorkUtil.isNetWorkAvailable(getActivity())) {
                    button.setImageResource(R.drawable.already_apply);
                    addFriend(infoBean.getUin());
                } else {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                }


            }
        });
        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                button.setImageResource(R.drawable.already_apply);
            }
        });
        cardDialog.setCarDialogRlListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("哈哈哈");
                cardDialog.dismiss();
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
                if (addFriendTextView != null){
                    addFriendTextView.setText("");
                }

            } else {
                if (addFriendTextView != null){
                    addFriendTextView.setText(addFriendNum + "");
                }

            }

        }
    }


    @Override
    public void OnRecycleImageClick(View v, Object o) {
        switch (v.getId()){
            case R.id.ff_item_header_img:

                int position = (int)o;
                DaoFriendFeeds tempFeeds = mDataList.get(position);
                Log.i(TAG, "OnRecycleImageClick: 头像被点击---" + position);
                //查询关系之后再跳转
                getFriendInfo(tempFeeds.getFriendUin(), tempFeeds);
                break;
        }
    }
}
