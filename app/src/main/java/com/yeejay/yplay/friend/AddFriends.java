package com.yeejay.yplay.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.AddFriendsAdapter;
import com.yeejay.yplay.adapter.ScoolmateAdapter;
import com.yeejay.yplay.adapter.ScoolmateAdapter2;
import com.yeejay.yplay.adapter.ScoolmateAdapter3;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetAddFriendMsgs;
import com.yeejay.yplay.model.GetRecommendAll;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

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

public class AddFriends extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.searchView)
    TextView searchView;
    @BindView(R.id.af_lv_add_friends_list_view)
    MesureListView afLvAddFriends;
    @BindView(R.id.af_lv_book_friends_list)
    MesureListView afLvBookFriendsList;
    @BindView(R.id.af_lv_school_classmate)
    MesureListView afLvSchoolClassmate;
    @BindView(R.id.af_lv_wait_invite)
    MesureListView afLvWaitInvite;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.searchView)
    public void clickSearch(View view){
        startActivity(new Intent(AddFriends.this,ActivitySearchFriends.class));
    }
    @OnClick(R.id.af_btn_add_contacts)
    public void btnAddContacts() {
        //通讯录好友
        System.out.println("通讯录好友");
        startActivity(new Intent(AddFriends.this,ActivityContacts.class));
    }
    @OnClick(R.id.af_btn_add_schoolmate)
    public void btnAddSchool() {
        //同校好友
        System.out.println("同校好友");
        startActivity(new Intent(AddFriends.this,ActivitySchoolmate.class));
    }
    @OnClick(R.id.af_btn_wait_invite)
    public void btnWaitInvite() {
        //等待邀请
        System.out.println("等待邀请");
        startActivity(new Intent(AddFriends.this,ActivityWaitInvite.class));
    }
    List<GetAddFriendMsgs.PayloadBean.MsgsBean> friendsList;
    AddFriendsAdapter addFriendsAdapter;
    ScoolmateAdapter bookFriendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(AddFriends.this,true);
        layoutTitle.setText("添加好友");

        //getAddFriendmsgs();
        getAllTypeRecommends();
    }

//    //加好友请求列表
//    private void initAddFriendsListView(int total,
//                                        List<GetAddFriendMsgs.PayloadBean.MsgsBean> tempList) {
//        friendsList = new ArrayList<>();
//
//        if (tempList.size() == 1) {
//            friendsList.add(tempList.get(0));
//        }
//        if (tempList.size() == 2) {
//            friendsList.add(tempList.get(0));
//            friendsList.add(tempList.get(1));
//        }
//        if (tempList.size() >= 3) {
//            friendsList.add(tempList.get(0));
//            friendsList.add(tempList.get(1));
//            friendsList.add(tempList.get(2));
//        }
//
//        final TextView AddFriendsHeaderView = (TextView) View.inflate(AddFriends.this, R.layout.item_af_listview_header, null);
//        final View AddFriendsFootView = View.inflate(AddFriends.this, R.layout.item_af_listview_foot, null);
//        TextView footTextSeeMore = (TextView) AddFriendsFootView.findViewById(R.id.af_foot_tv1);
//        afLvAddFriends.addHeaderView(AddFriendsHeaderView);
//        if (total > 3) {
//            afLvAddFriends.addFooterView(AddFriendsFootView);
//            footTextSeeMore.setText("查看更多" + (total - 3));
//        }
//        addFriendsAdapter = new AddFriendsAdapter(AddFriends.this, new AddFriendsAdapter.hideCallback() {
//            @Override
//            public void hideClick(View v) {
//                System.out.println("隐藏按钮被点击");
//                Button button = (Button) v;
//                //删除好友
//                removeFriend(friendsList.get((int) button.getTag()).getUin());
//                button.setVisibility(View.INVISIBLE);
//                if (friendsList.size() > 0) {
//                    friendsList.remove((int) v.getTag());
//                    addFriendsAdapter.notifyDataSetChanged();
//                }
//                if (friendsList.size() < 3) {
//                    afLvAddFriends.removeFooterView(AddFriendsFootView);
//                }
//                if (friendsList.size() <= 0) {
//                    afLvAddFriends.removeHeaderView(AddFriendsHeaderView);
//                }
//            }
//        }, new AddFriendsAdapter.acceptCallback() {
//            @Override
//            public void acceptClick(View v) {
//                System.out.println("接受按钮被点击");
//                Button button = (Button) v;
//                //接受加好友的请求
//                accepeAddFreind(friendsList.get((int) button.getTag()).getMsgId());
//                button.setText("已添加");
//                button.setEnabled(false);
//            }
//        }, friendsList);
//        afLvAddFriends.setAdapter(addFriendsAdapter);
//        afLvAddFriends.setOnItemClickListener(this);
//    }

    //通讯录好友
    private void initbooKFriendsList(final List<GetRecommendAll.PayloadBean.InfoBean.FriendsFromAddrBookBean> tempList) {

        final TextView booKFriendsHeaderView = (TextView) View.inflate(AddFriends.this, R.layout.item_af_listview_header, null);
        if (tempList.size() > 0) {
            afLvBookFriendsList.addHeaderView(booKFriendsHeaderView);
            booKFriendsHeaderView.setText("通讯录好友");
        }
        bookFriendsAdapter = new ScoolmateAdapter(AddFriends.this, new ScoolmateAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");

            }
        }, new ScoolmateAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("通讯录好友---加好友被点击");
                Button button = (Button) v;
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.feeds_status_apply);
                int uin = tempList.get((int) v.getTag()).getUin();
                addFriend(uin);

            }
        }, tempList);
        afLvBookFriendsList.setAdapter(bookFriendsAdapter);

    }

    //同校好友
    private void initbooKFriendsList3(final List<GetRecommendAll.PayloadBean.InfoBean.FriendsFromSameSchoolBean> tempList) {

        final TextView booKFriendsHeaderView = (TextView) View.inflate(AddFriends.this, R.layout.item_af_listview_header, null);
        if (tempList.size() > 0) {
            afLvSchoolClassmate.addHeaderView(booKFriendsHeaderView);
            booKFriendsHeaderView.setText("同校好友");
        }
        ScoolmateAdapter3 bookFriendsAdapter3 = new ScoolmateAdapter3(AddFriends.this,
                new ScoolmateAdapter3.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");

            }
        }, new ScoolmateAdapter3.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("同校好友---加好友被点击");
                Button button = (Button) v;
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.feeds_status_apply);
                int uin = tempList.get((int) v.getTag()).getUin();
                addFriend(uin);
            }
        }, tempList);
        afLvSchoolClassmate.setAdapter(bookFriendsAdapter3);

    }

    //等待邀请
    private void initbooKFriendsList2(final List<GetRecommendAll.PayloadBean.InfoBean.FriendsFromNotRegisterBean> tempList) {

        final TextView booKFriendsHeaderView = (TextView) View.inflate(AddFriends.this, R.layout.item_af_listview_header, null);
        if (tempList.size() > 0) {
            afLvWaitInvite.addHeaderView(booKFriendsHeaderView);
            booKFriendsHeaderView.setText("等待邀请");
        }

        ScoolmateAdapter2 bookFriendsAdapter2 =
            new ScoolmateAdapter2(AddFriends.this, new ScoolmateAdapter2.hideCallback() {
            @Override
            public void hideClick(View v) {
                System.out.println("隐藏按钮被点击");

            }
        }, new ScoolmateAdapter2.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("等待邀请加好友被点击");
                Button button = (Button) v;
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.play_invite_yes);
                //短信邀请
                String phone = GsonUtil.GsonString(tempList.get((int) v.getTag()).getPhone());
                String base64phone = Base64.encodeToString(phone.getBytes(), Base64.DEFAULT);
                invitefriendsbysms(base64phone);
            }
        }, tempList);
        afLvWaitInvite.setAdapter(bookFriendsAdapter2);

    }
//    //拉取添加好友消息
//    private void getAddFriendmsgs() {
//        Map<String, Object> getAddFriendmsgsMap = new HashMap<>();
//        getAddFriendmsgsMap.put("updateLastReadMsgId", 0);
//        getAddFriendmsgsMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
//        getAddFriendmsgsMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
//        getAddFriendmsgsMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
//        YPlayApiManger.getInstance().getZivApiService()
//                .getAddFriendMsg(getAddFriendmsgsMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<GetAddFriendMsgs>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull GetAddFriendMsgs getAddFriendMsgs) {
//                        System.out.println("拉取添加好友消息---" + getAddFriendMsgs.toString());
//                        if (getAddFriendMsgs.getCode() == 0) {
//                            List<GetAddFriendMsgs.PayloadBean.MsgsBean> tempList
//                                    = getAddFriendMsgs.getPayload().getMsgs();
//                            int total = getAddFriendMsgs.getPayload().getTotal();
//                            initAddFriendsListView(total, tempList);
//
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        System.out.println("拉取添加好友消息异常---" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    //接受好友请求
    private void accepeAddFreind(int msgId) {
        Map<String, Object> accepeAddFreindMap = new HashMap<>();
        accepeAddFreindMap.put("msgId", msgId);
        accepeAddFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        accepeAddFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        accepeAddFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //发送加好友的请求
    private void addFriend(int toUin) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //删除好友
    private void removeFriend(int toUin) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("toUin", toUin);
        removeFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {
        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("friends", friends);
        removeFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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



    //拉取所有类型的的好友
    private void getAllTypeRecommends(){
        Map<String, Object> allRecommendsMap = new HashMap<>();
        allRecommendsMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        allRecommendsMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        allRecommendsMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getAllRecommends(allRecommendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendAll>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GetRecommendAll getRecommendAll) {
                        System.out.println("获取所有类型的数据---" + getRecommendAll.toString());
                        //通讯录好友1
                        initbooKFriendsList(getRecommendAll.getPayload().getInfo().getFriendsFromAddrBook());
                        //同校好友3
                        initbooKFriendsList3(getRecommendAll.getPayload().getInfo().getFriendsFromSameSchool());
                        //等待邀请2
                        initbooKFriendsList2(getRecommendAll.getPayload().getInfo().getFriendsFromNotRegister());

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("获取所有类型的数据---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


//    //拉取同校/通讯录好友
//    private void getRecommends(final int type) {
//        System.out.println("type---" + type);
//        Map<String, Object> recommendsMap = new HashMap<>();
//        recommendsMap.put("type", type);
//        recommendsMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
//        recommendsMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
//        recommendsMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
//        YPlayApiManger.getInstance().getZivApiService()
//                .getSchoolmates(recommendsMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<GetRecommendsRespond>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
//                        if (getRecommendsRespond.getCode() == 0) {
//                            if (type == 1) {
//                                System.out.println("通讯录好友---" + getRecommendsRespond.toString());
//                                initbooKFriendsList(type,
//                                        getRecommendsRespond.getPayload().getTotal(),
//                                        afLvBookFriendsList,
//                                        getRecommendsRespond.getPayload().getFriends());
//                            }else if (type == 2) {
//                                System.out.println("等待邀请---" + getRecommendsRespond.toString());
//                                initbooKFriendsList(type,
//                                        getRecommendsRespond.getPayload().getTotal(),
//                                        afLvWaitInvite,
//                                        getRecommendsRespond.getPayload().getFriends());
//                            } else if (type == 3) {
//                                System.out.println("同校好友---" + getRecommendsRespond.toString());
//                                initbooKFriendsList(type,
//                                        getRecommendsRespond.getPayload().getTotal(),
//                                        afLvSchoolClassmate,
//                                        getRecommendsRespond.getPayload().getFriends());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        System.out.println("拉取好友异常---" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("position---" + position);
        if (position == 4) {
            System.out.println("啦啦啦");
            startActivity(new Intent(AddFriends.this, ActivityAddFiendsDetail.class));
        }
    }

}
