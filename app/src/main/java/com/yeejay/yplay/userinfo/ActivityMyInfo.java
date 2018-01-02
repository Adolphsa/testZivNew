package com.yeejay.yplay.userinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.RecommendFriendForNullAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.friend.ActivityAddFiendsDetail;
import com.yeejay.yplay.friend.AddFriends;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.model.UnReadMsgCountRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
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
import tangxiaolv.com.library.EffectiveShapeView;

public class ActivityMyInfo extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton myTvBack;
    @BindView(R.id.layout_title2)
    TextView myTvTitle;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.layout_title_rl)
    RelativeLayout layoutTitleRl;

    //我的资料
    @BindView(R.id.lui_header_img)
    EffectiveShapeView amiItemHeaderImg;
    @BindView(R.id.personal_nick_name)
    TextView personalNickName;
    @BindView(R.id.personal_gender)
    ImageView personalGender;
    @BindView(R.id.personal_user_name)
    TextView personalUserName;
    @BindView(R.id.personal_school)
    TextView personalSchool;
    @BindView(R.id.personal_grade)
    TextView personalGrade;

    //钻石
//    @BindView(R.id.diamond_tv_num)
//    TextView amiTvDiamondNumber;
//    @BindView(R.id.diamond_list)
//    MesureListView amiDiamondListView;
//    @BindView(R.id.diamond_line)
//    View amiDiamonLine;
//    @BindView(R.id.diamond_null_img)
//    ImageView amiDiamondNullImg;
    @BindView(R.id.diamond_tv_num)
    TextView amiTvDiamondNumber;
    @BindView(R.id.ami_include_my_diammonds)
    RelativeLayout amiDiamonds;


    //好友相关
    @BindView(R.id.ami_include_my_friends)
    RelativeLayout myFriendsRl;
    @BindView(R.id.ami_include_friend_request)
    RelativeLayout friendRequestRl;
    @BindView(R.id.ami_include_add_friend)
    RelativeLayout addFriendsRl;

    //扩列开启
//    @BindView(R.id.ami_view2)
//    View amiView2;
//    @BindView(R.id.frf_no_more_show)
//    ImageButton frfNoMoreShow;
//    @BindView(R.id.frf_see_more)
//    TextView frfSeeMore;
//    @BindView(R.id.diamond_expansion)
//    RelativeLayout diamondExpansionRl;
//    @BindView(R.id.frf_list_view)
//    ListView diamondExpansionListView;

    //返回
    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    //设置
    @OnClick(R.id.layout_setting)
    public void setting(View view) {
        startActivity(new Intent(ActivityMyInfo.this, ActivitySetting.class));
    }

    //钻石
    @OnClick(R.id.ami_include_my_diammonds)
    public void myDiamonds(){
        startActivity(new Intent(ActivityMyInfo.this,ActivityAllDiamond.class));
    }

    //我的好友
    @OnClick(R.id.ami_include_my_friends)
    public void myFriends(){
        startActivity(new Intent(ActivityMyInfo.this,ActivityMyFriends.class));
    }

    //好友请求
    @OnClick(R.id.ami_include_friend_request)
    public void friendsRequest(){
        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null){
            myInfo.setAddFriendNum(0);
            myInfoDao.update(myInfo);
        }
        startActivity(new Intent(ActivityMyInfo.this,ActivityAddFiendsDetail.class));
    }

    //添加好友
    @OnClick(R.id.ami_include_add_friend)
    public void addFriends(){
        System.out.println("添加好友");
        startActivity(new Intent(ActivityMyInfo.this,AddFriends.class));
    }


    //不再显示
//    @OnClick(R.id.frf_no_more_show)
//    public void diamondNoMoreShow(View v) {
//
//        diamondExpansionRl.setVisibility(View.GONE);
//        //将数据库中的值变为1
//        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
//        int uin = (int) SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, (int) 0);
//        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
//                .build().unique();
//        if (myInfo != null) {
//            myInfo.setIsNoMoreShow2(1);
//            myInfoDao.update(myInfo);
//        }
//    }

    //查看更多
//    @OnClick(R.id.frf_see_more)
//    public void DiamondseeMore(View v) {
//        startActivity(new Intent(ActivityMyInfo.this, AddFriends.class));
//    }

    List<FriendsListRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;

    TextView friendRequestNum;
    TextView friendNumTv;

    RecommendFriendForNullAdapter recommendFriendForNullAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.play_color2));
        initTitle();
        initFriendsList();

        int uin = (int) SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0);
        getUserDiamondInfo(uin, 1);
        mDataList = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyInfo();
        getAddFriendMessageCount();
        System.out.println("我的资料resume");
        recommendFriends();
    }

    //初始化标题
    private void initTitle() {
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color2));
        layoutSetting.setVisibility(View.VISIBLE);
//        myTvBack.setImageResource(R.drawable.back_white);
//        myTvTitle.setText("我的");
//        myTvTitle.setTextColor(getResources().getColor(R.color.white));
        myTvTitle.setVisibility(View.GONE);
    }

    //初始化我的资料
    private void initMyInfo(UserInfoResponde.PayloadBean.InfoBean infoBean) {

        if (infoBean != null) {
            String url = infoBean.getHeadImgUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(ActivityMyInfo.this).load(url).into(amiItemHeaderImg);
            }

            personalSchool.setText(infoBean.getSchoolName());
            personalNickName.setText(infoBean.getNickName());
            personalUserName.setText(infoBean.getUserName());
            personalGrade.setText(FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade()));
            if (infoBean.getGender() == 1) {
                personalGender.setVisibility(View.VISIBLE);
                personalGender.setImageDrawable(getDrawable(R.drawable.feeds_boy));
            } else {
                personalGender.setVisibility(View.VISIBLE);
                personalGender.setImageDrawable(getDrawable(R.drawable.feeds_girl));
            }

            //钻石数量
            int diamondNum = infoBean.getGemCnt();
            amiTvDiamondNumber.setText(String.valueOf(diamondNum));

            //好友数量
            int friendsNum = infoBean.getFriendCnt();
            friendNumTv.setText(String.valueOf(friendsNum));
        }

    }

    //初始化钻石
    private void initDiamondList(UsersDiamondInfoRespond.PayloadBean payloadBean) {

//        final View footView = View.inflate(ActivityMyInfo.this, R.layout.item_af_listview_foot, null);
//        int total = payloadBean.getTotal();
//        if (total > 0) {
//            amiDiamondNullImg.setVisibility(View.GONE);
//            amiDiamonLine.setVisibility(View.GONE);
//        }
//        if (total >= 3) {
//            TextView countTv = (TextView) footView.findViewById(R.id.af_foot_tv1);
//            countTv.setText("查看全部");
//            amiDiamonLine.setVisibility(View.VISIBLE);
//            if (!footView.isShown()){
//                amiDiamondListView.addFooterView(footView);
//            }
//        }
//        final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = payloadBean.getStats();
//        amiDiamondListView.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return tempList.size() >= 3 ? 3 : tempList.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return tempList.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder holder;
//                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean;
//                if (convertView == null){
//                    convertView = View.inflate(ActivityMyInfo.this, R.layout.item_user_info_diamond, null);
//                    holder = new ViewHolder();
//                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.item_diamond_top_img);
//                    holder.itemAmiImg2 = (ImageView) convertView.findViewById(R.id.item_diamond_img);
//                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.item_diamond_text);
//                    convertView.setTag(holder);
//                }else {
//                    holder = (ViewHolder) convertView.getTag();
//                }
//                statsBean = tempList.get(position);
//                if (position == 0){
//                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top1));
//                } else if (position == 1) {
//                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top2));
//                } else if (position == 2) {
//                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top3));
//                }else {
//                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top3));
//                }
//                System.out.println("钻石position---" + position);
//                String url = statsBean.getQiconUrl();
//                if (!TextUtils.isEmpty(url)) {
//                    Picasso.with(ActivityMyInfo.this).load(url).into(holder.itemAmiImg2);
//                } else {
//                    holder.itemAmiImg2.setVisibility(View.GONE);
//                }
//                holder.itemAmiText.setText(statsBean.getQtext());
//                return convertView;
//            }
//        });
//        amiDiamondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 3) {
//                    startActivity(new Intent(ActivityMyInfo.this, ActivityAllDiamond.class));
//                }
//            }
//        });
    }

    //初始化我的好友列表
    private void initFriendsList() {

        //我的好友
        friendNumTv = (TextView) myFriendsRl.findViewById(R.id.friend_tv_num);

        //好友请求
        //ImageView friendRequestImg = (ImageView) friendRequestRl.findViewById(R.id.friend_iv1);
        //TextView friendRequestTv = (TextView) friendRequestRl.findViewById(R.id.friend_tv1);
        friendRequestNum = (TextView) friendRequestRl.findViewById(R.id.friend_tv_num);

        //friendRequestImg.setImageDrawable(getDrawable(R.drawable.my_info_friend_request));
        //friendRequestTv.setText("好友请求");

        //添加好友
        //ImageView addFriendImg = (ImageView) addFriendsRl.findViewById(R.id.friend_iv1);
        //TextView addFriendTv = (TextView) addFriendsRl.findViewById(R.id.friend_tv1);
        //TextView addFriendNum = (TextView) addFriendsRl.findViewById(R.id.friend_tv_num);

        //addFriendImg.setImageDrawable(getDrawable(R.drawable.my_info_add_friend));
        //addFriendTv.setText("添加好友");
        //addFriendNum.setVisibility(View.GONE);
    }

    //扩列开启相关
    private void initRecommendList(final List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList) {

        //从数据库读取值来设定开启扩列的显示与否
        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
//        if (myInfo != null && myInfo.getIsNoMoreShow2() == 1) {
//            diamondExpansionRl.setVisibility(View.GONE);
//        }

        recommendFriendForNullAdapter = new RecommendFriendForNullAdapter(ActivityMyInfo.this,
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
                            button.setBackgroundResource(R.drawable.already_apply);
                            int uin = friendsBean.getUin();
                            addFriend(uin);
                        }
                    }
                },
                tempList,2);
//        diamondExpansionListView.setAdapter(recommendFriendForNullAdapter);
    }

    //加载更多
//    private void loadMore(){
//        amiPtfRefresh.setCanRefresh(false);
//        amiPtfRefresh.setRefreshListener(new BaseRefreshListener() {
//            @Override
//            public void refresh() {
//
//            }
//
//            @Override
//            public void loadMore() {
//                mPageNum++;
//                System.out.println("mPageNum---" + mPageNum);
//                getMyFriendsList(mPageNum);
//            }
//        });
//
//    }

    //显示对话框
    private void showNormalDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ActivityMyInfo.this);
        normalDialog.setMessage("解除好友关系后，你也会在对方的好友列表中消失");
        normalDialog.setPositiveButton("解除关系",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private static class ViewHolder {
        ImageView itemAmiImg;
        ImageView itemAmiImg2;
        TextView itemAmiText;
    }


    //获取自己的资料
    private void getMyInfo() {

        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        myInfoMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myInfoMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getMyInfo(myInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull UserInfoResponde userInfoResponde) {
                        System.out.println("获取自己的资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0) {
                            initMyInfo(userInfoResponde.getPayload().getInfo());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取自己的资料异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取钻石信息
    private void getUserDiamondInfo(int userUin, int pageNum) {

        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin", userUin);
        diamondInfoMap.put("pageNum", pageNum);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUsersDamonInfo(diamondInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UsersDiamondInfoRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull UsersDiamondInfoRespond usersDiamondInfoRespond) {
                        System.out.println("获取用户钻石信息---" + usersDiamondInfoRespond.toString());
                        if (usersDiamondInfoRespond.getCode() == 0) {
                            initDiamondList(usersDiamondInfoRespond.getPayload());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取用户钻石信息异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //获取未读的消息数
    private void getAddFriendMessageCount() {

        Map<String, Object> unreadFriendMsgCountMap = new HashMap<>();
        unreadFriendMsgCountMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        unreadFriendMsgCountMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        unreadFriendMsgCountMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
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
                        if (unReadMsgCountRespond.getCode() == 0) {
                            int count = unReadMsgCountRespond.getPayload().getCnt();
                            if (count == 0){
                                friendRequestNum.setVisibility(View.GONE);
                            }else {
                                friendRequestNum.setVisibility(View.VISIBLE);
                                friendRequestNum.setText(String.valueOf(count));
                                friendRequestNum.setBackground(getDrawable(R.drawable.shape_friend_request_background));
                                friendRequestNum.setTextColor(getResources().getColor(R.color.white));
                            }

                        }

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


    //获取推荐好友信息
    private void recommendFriends() {

        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        tempMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        tempMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
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
//                                diamondExpansionRl.setVisibility(View.GONE);
//                                amiView2.setVisibility(View.GONE);
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
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
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
        addFreindMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
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
}
