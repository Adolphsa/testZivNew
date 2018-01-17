package com.yeejay.yplay.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.FriendsDetailAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CardRequestFredDialog;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetAddFriendMsgs;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
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

public class ActivityAddFiendsDetail extends BaseActivity {

    private static final String TAG = "ActivityAddFiendsDetail";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.aafd_list_view)
    RecyclerView aafdListView;
    @BindView(R.id.aafd_ptf_refresh)
    PullToRefreshLayout aafdPtfRefresh;
    @BindView(R.id.emptyview)
    View emptyView;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    private LoadMoreView loadMoreView;
    FriendsDetailAdapter friendsDetailAdapter;
    int mPageNum = 1;
    List<GetAddFriendMsgs.PayloadBean.MsgsBean> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activty_add_fiends_detail);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityAddFiendsDetail.this, true);

        mDataList = new ArrayList<>();
        layoutTitle.setText("好友请求");

        initAdapter();

        getAddFriendmsgs(mPageNum);
        loadMore();
    }

    private void initAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityAddFiendsDetail.this);
        aafdListView.setLayoutManager(linearLayoutManager);

        friendsDetailAdapter = new FriendsDetailAdapter(ActivityAddFiendsDetail.this,
                new FriendsDetailAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {
                        System.out.println("隐藏按钮被点击");
                        Button button = (Button) v;
                        if (NetWorkUtil.isNetWorkAvailable(ActivityAddFiendsDetail.this)){
                            //隐藏按钮点击后不做接受好友处理；
                            //accepeAddFreind(tempList.get((int) button.getTag()).getMsgId(),
                            //        1,
                            //        tempList.get((int) button.getTag()));
                            button.setVisibility(View.INVISIBLE);
                            if (mDataList.size() > 0) {
                                System.out.println("tempList---" + mDataList.size() + "----" + (int) v.getTag());
                                mDataList.remove((int) v.getTag());
                                friendsDetailAdapter.notifyDataSetChanged();
                            }
                        }else {
                            Toast.makeText(ActivityAddFiendsDetail.this,"网络异常",Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new FriendsDetailAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button) v;
                View parent = (View)v.getParent();
                if (parent != null) {
                    Button cancelBtn = (Button)parent.findViewById(R.id.af_btn_hide);
                    if (cancelBtn != null) {
                        cancelBtn.setVisibility(View.GONE);
                    }
                }

                if (NetWorkUtil.isNetWorkAvailable(ActivityAddFiendsDetail.this)){
                    button.setBackgroundResource(R.drawable.be_as_friends);
                    button.setEnabled(false);
                    //接受加好友的请求
                    accepeAddFreind(mDataList.get((int) button.getTag()).getMsgId(),
                            0,
                            mDataList.get((int) button.getTag()));
                }else {
                    Toast.makeText(ActivityAddFiendsDetail.this,"网络异常",Toast.LENGTH_SHORT).show();
                }

            }
        }, mDataList);

//        aafdListView.setEmptyView(emptyView);
        aafdListView.setAdapter(friendsDetailAdapter);

        friendsDetailAdapter.addRecycleItemListener(new FriendsDetailAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {

                int position = (int)o;

                int userUin = mDataList.get(position).getFromUin();
                if (NetWorkUtil.isNetWorkAvailable(ActivityAddFiendsDetail.this)) {
                    getFriendInfo(userUin, v);
                } else {
                    Toast.makeText(ActivityAddFiendsDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        aafdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//            }
//        });
    }

    private void initFriendsDetailListView(final List<GetAddFriendMsgs.PayloadBean.MsgsBean> tempList) {

        if (mDataList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.GONE);
        }

        friendsDetailAdapter.notifyDataSetChanged();
        //拉到第二页数据时，自动向上滚动两个item高度（如果第二页只有一个数据的话，则只滚动一个item高度）
        //ListView需要先调用notifyDataSetChanged()再滚动
        if (tempList.size() >= 2) {
            aafdListView.smoothScrollToPosition(mDataList.size() - tempList.size() + 1);
        } else if (tempList.size() == 1) {
            aafdListView.smoothScrollToPosition(mDataList.size() - tempList.size());
        }
    }

    private void loadMore(){
        aafdPtfRefresh.setCanRefresh(false);
        loadMoreView = new LoadMoreView(this);
        aafdPtfRefresh.setFooterView(loadMoreView);
        aafdPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {}

            @Override
            public void loadMore() {
                mPageNum++;
                getAddFriendmsgs(mPageNum);
            }
        });
    }

    //查询发好友请求之人的信息
    private void getFriendInfo(int friendUin, View view) {
        final View friendItemView = view;
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUserInfo(friendMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(UserInfoResponde userInfoResponde) {
                        System.out.println("获取朋友资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0) {
                            UserInfoResponde.PayloadBean.InfoBean infoBean =
                                    userInfoResponde.getPayload().getInfo();
                            int status = userInfoResponde.getPayload().getStatus();
                            if (status == 1) {
                                Intent intent = new Intent(ActivityAddFiendsDetail.this, ActivityFriendsInfo.class);
                                intent.putExtra("yplay_friend_name", infoBean.getNickName());
                                intent.putExtra("yplay_friend_uin", infoBean.getUin());
                                System.out.println("朋友的uin---" + infoBean.getUin());
                                startActivity(intent);
                            } else {
                                showCardDialog(userInfoResponde.getPayload(), friendItemView);
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
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean, View view) {

        final View friendItemView = view;
        final Button freiendIcon = (Button) friendItemView.findViewById(R.id.af_btn_accept2);
        final Button hideIcon = (Button) friendItemView.findViewById(R.id.af_btn_hide);
        final CardRequestFredDialog cardDialog = new CardRequestFredDialog(ActivityAddFiendsDetail.this, R.style.CustomDialog,
                payloadBean);

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView button = (ImageView) v;
                if (NetWorkUtil.isNetWorkAvailable(ActivityAddFiendsDetail.this)) {
                    button.setImageResource(R.drawable.peer_be_as_friends);
                    //除了更新朋友选项卡信息中的按钮状态外，还要更新外部对应的好友请求列表item的按钮状态；
                    if (friendItemView != null) {

                        if (freiendIcon != null) {
                            freiendIcon.setBackgroundResource(R.drawable.be_as_friends);
                            accepeAddFreind(mDataList.get((int)freiendIcon.getTag()).getMsgId(),
                                    0,
                                    mDataList.get((int)freiendIcon.getTag()));
                        }
                        if (hideIcon != null) {
                            hideIcon.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(ActivityAddFiendsDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardDialog.show();
    }

    //拉取添加好友消息
    private void getAddFriendmsgs(int pageNum) {

        Map<String, Object> getAddFriendmsgsMap = new HashMap<>();
        getAddFriendmsgsMap.put("updateLastReadMsgId", 0);
        getAddFriendmsgsMap.put("pageNum",pageNum);
        getAddFriendmsgsMap.put("uin", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_UIN, 0));
        getAddFriendmsgsMap.put("token", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        getAddFriendmsgsMap.put("ver", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getAddFriendMsg(getAddFriendmsgsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetAddFriendMsgs>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull GetAddFriendMsgs getAddFriendMsgs) {
                        System.out.println("拉取添加好友消息---" + getAddFriendMsgs.toString());
                        if (getAddFriendMsgs.getCode() == 0) {
                            List<GetAddFriendMsgs.PayloadBean.MsgsBean> tempList
                                    = getAddFriendMsgs.getPayload().getMsgs();
                            if (tempList != null && tempList.size() > 0) {
                                mDataList.addAll(tempList);
                                initFriendsDetailListView(tempList);
                            } else {
                                //不能拉到数据了
                                loadMoreView.noData();
                            }

                        } else {
                            //如果服务器返回失败
                            aafdListView.setAdapter(null);
                        }
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("拉取添加好友消息异常---" + e.getMessage());
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //接受好友请求
    private void accepeAddFreind(int msgId, int act, final GetAddFriendMsgs.PayloadBean.MsgsBean msgsBean) {

        Map<String, Object> accepeAddFreindMap = new HashMap<>();
        accepeAddFreindMap.put("msgId", msgId);
        accepeAddFreindMap.put("act",act);
        accepeAddFreindMap.put("uin", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_UIN, 0));
        accepeAddFreindMap.put("token", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        accepeAddFreindMap.put("ver", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .acceptAddFriend(accepeAddFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("接受好友请求---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0){
                            Toast.makeText(ActivityAddFiendsDetail.this,"接受成功",Toast.LENGTH_SHORT).show();
                            DbHelper dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
                            dbHelper.insertFriendInfo(new FriendInfo(null,
                                    msgsBean.getFromUin(),
                                    msgsBean.getFromNickName(),
                                    msgsBean.getFromHeadImgUrl(),
                                    msgsBean.getFromGender(),
                                    msgsBean.getFromGrade(),
                                    msgsBean.getSchoolId(),
                                    msgsBean.getSchoolType(),
                                    msgsBean.getSchoolName(),
                                    msgsBean.getTs(),
                                    BaseUtils.getSortKey(msgsBean.getFromNickName()),
                                    String.valueOf(SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, 0))));
                            Log.i(TAG, "onNext: friendUin---" + msgsBean.getFromUin());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("接受好友请求异常---" + e.getMessage());
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
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityAddFiendsDetail.this, YPlayConstant.YPLAY_VER, 0));
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
