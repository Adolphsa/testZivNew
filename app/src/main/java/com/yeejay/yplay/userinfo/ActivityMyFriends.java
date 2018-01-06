package com.yeejay.yplay.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.MyFriendsAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.friend.ActivityFriendsInfo;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityMyFriends extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.amf_friend_null)
    ImageView friendNull;
    @BindView(R.id.amf_list_view)
    ListView amfListView;
    @BindView(R.id.amf_ptf_refresh)
    PullToRefreshLayout amfPtfRefresh;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    private LoadMoreView loadMoreView;
    List<FriendsListRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;
    private MyFriendsAdapter mMyFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityMyFriends.this, true);

        layoutTitle.setText(R.string.my_friends);
        mDataList = new ArrayList<>();

        initAdapter();

        getMyFriendsList(mPageNum);
        loadMore();
    }

    private void initAdapter() {
        mMyFriendsAdapter = new MyFriendsAdapter(this, mDataList);
        amfListView.setAdapter(mMyFriendsAdapter);

        amfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDataList != null && mDataList.size() > 0){
                    Intent intent = new Intent(ActivityMyFriends.this, ActivityFriendsInfo.class);
                    intent.putExtra("yplay_friend_name", mDataList.get(position).getNickName());
                    intent.putExtra("yplay_friend_uin",mDataList.get(position).getUin());
                    startActivity(intent);
                }
            }
        });
    }

    private void initMyFriendsList(final List<FriendsListRespond.PayloadBean.FriendsBean> tempList) {
        if (tempList.size() > 0){
            friendNull.setVisibility(View.GONE);
        }else {
            friendNull.setVisibility(View.VISIBLE);
        }

        mMyFriendsAdapter.notifyDataSetChanged();
        //拉到第二页数据时，自动向上滚动两个item高度（如果第二页只有一个数据的话，则只滚动一个item高度）
        //ListView需要先调用notifyDataSetChanged()再滚动
        if (tempList.size() >= 2) {
            amfListView.smoothScrollToPosition(mDataList.size() - tempList.size() + 1);
        } else if (tempList.size() == 1) {
            amfListView.smoothScrollToPosition(mDataList.size() - tempList.size());
        }
    }


    private void loadMore() {
        amfPtfRefresh.setCanRefresh(false);
        loadMoreView = new LoadMoreView(ActivityMyFriends.this);
        amfPtfRefresh.setFooterView(loadMoreView);
        amfPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                System.out.println("加载更多");
                mPageNum++;
                getMyFriendsList(mPageNum);
            }
        });
    }


    //获取好友列表
    private void getMyFriendsList(int pageNum) {

        Map<String, Object> myFriendsMap = new HashMap<>();
        myFriendsMap.put("pageNum", pageNum);
        myFriendsMap.put("uin", SharePreferenceUtil.get(ActivityMyFriends.this, YPlayConstant.YPLAY_UIN, 0));
        myFriendsMap.put("token", SharePreferenceUtil.get(ActivityMyFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myFriendsMap.put("ver", SharePreferenceUtil.get(ActivityMyFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getMyFriendsList(myFriendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendsListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull FriendsListRespond friendsListRespond) {
                        System.out.println("获取我的好友列表---" + friendsListRespond.toString());
                        if (friendsListRespond.getCode() == 0) {
                            List<FriendsListRespond.PayloadBean.FriendsBean> tempList
                                    = friendsListRespond.getPayload().getFriends();
                            if(tempList.size() > 0) {
                                mDataList.addAll(tempList);
                                initMyFriendsList(tempList);
                            } else {
                                System.out.println("数据加载完毕");
                                loadMoreView.noData();
                            }
                        }
                        amfPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取我的好友列表异常---" + e.getMessage());
                        amfPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
