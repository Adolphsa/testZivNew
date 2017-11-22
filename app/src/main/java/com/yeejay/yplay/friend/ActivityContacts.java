package com.yeejay.yplay.friend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.ContactsAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
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

public class ActivityContacts extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.aafd_list_view)
    ListView aafdListView;
    @BindView(R.id.aafd_ptf_refresh)
    PullToRefreshLayout aafdPtfRefresh;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    ContactsAdapter contactsAdapter;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activty_add_fiends_detail);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContacts.this,true);

        layoutTitle.setText("通讯录好友");
        mDataList = new ArrayList<>();
        getRecommends(1,mPageNum);
        loadMore();
    }

    private void initContactListView(final List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList){

        contactsAdapter = new ContactsAdapter(ActivityContacts.this,
                new ContactsAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");

            }
        }, new ContactsAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button)v;
                button.setBackgroundResource(R.drawable.feeds_status_apply);
                button.setEnabled(false);
               //邀请好友
                addFriend(tempList.get((int) button.getTag()).getUin());
            }
        },tempList);
        aafdListView.setAdapter(contactsAdapter);
    }

    //加载更多
    private void loadMore(){
        aafdPtfRefresh.setCanRefresh(false);
        aafdPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                mPageNum++;
                System.out.println("pageNum---" + mPageNum);
                getRecommends(1,mPageNum);
            }
        });
    }

    //拉取通讯录好友
    private void getRecommends(final int type,int pageNum) {

        Map<String, Object> recommendsMap = new HashMap<>();
        recommendsMap.put("type", type);
        recommendsMap.put("pageNum", pageNum);
        recommendsMap.put("uin", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_UIN, 0));
        recommendsMap.put("token", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        recommendsMap.put("ver", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getSchoolmates(recommendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
                        System.out.println("通讯录好友---" + getRecommendsRespond.toString());
                        if (getRecommendsRespond.getCode() == 0) {
                            List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList =
                                    getRecommendsRespond.getPayload().getFriends();
                            mDataList.addAll(tempList);
                            initContactListView(mDataList);
                        }
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("拉取通讯录好友异常---" + e.getMessage());
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //添加好友
    private void addFriend(int toUin) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("uin", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_VER, 0));
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
                        System.out.println("通讯录加好友请求---" + addFriendRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("通讯录加好友请求异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //删除好友
    private void removeFriend(int toUin) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("toUin", toUin);
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityContacts.this, YPlayConstant.YPLAY_VER, 0));
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
                        System.out.println("删除好友---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("删除好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
