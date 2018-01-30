package com.yeejay.yplay.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.friend.ActivityAddFiendsDetail;
import com.yeejay.yplay.friend.AddFriends;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.UnReadMsgCountRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tangxiaolv.com.library.EffectiveShapeView;

public class ActivityMyInfo extends BaseActivity {

    private static final String TAG = "ActivityMyInfo";

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
    public void myDiamonds() {
        startActivity(new Intent(ActivityMyInfo.this, ActivityAllDiamond.class));
    }

    //我的好友
    @OnClick(R.id.ami_include_my_friends)
    public void myFriends() {
        startActivity(new Intent(ActivityMyInfo.this, ActivityMyFriends.class));
    }

    //好友请求
    @OnClick(R.id.ami_include_friend_request)
    public void friendsRequest() {
        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null) {
            myInfo.setAddFriendNum(0);
            myInfoDao.update(myInfo);
        }
        startActivity(new Intent(ActivityMyInfo.this, ActivityAddFiendsDetail.class));
    }

    //添加好友
    @OnClick(R.id.ami_include_add_friend)
    public void addFriends() {
        System.out.println("添加好友");
        startActivity(new Intent(ActivityMyInfo.this, AddFriends.class));
    }

    List<FriendsListRespond.PayloadBean.FriendsBean> mDataList;

    TextView friendRequestNum;
    TextView friendNumTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.play_color2));
        initTitle();
        initFriendsList();

        mDataList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新我的资料
        getMyInfo();
        getAddFriendMessageCount();
    }

    //初始化标题
    private void initTitle() {
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color2));
        layoutSetting.setVisibility(View.VISIBLE);
        myTvTitle.setVisibility(View.GONE);
    }

    //初始化我的资料
    private void initMyInfo(UserInfoResponde.PayloadBean.InfoBean infoBean) {

        if (infoBean != null) {
            String url = infoBean.getHeadImgUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(ActivityMyInfo.this).load(url)
                        .resizeDimen(R.dimen.lui_header_img_width, R.dimen.lui_header_img_height)
                        .centerCrop()
                        .into(amiItemHeaderImg);
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


    //初始化我的好友列表
    private void initFriendsList() {

        //我的好友
        friendNumTv = (TextView) myFriendsRl.findViewById(R.id.friend_tv_num);

        //好友请求
        friendRequestNum = (TextView) friendRequestRl.findViewById(R.id.friend_tv_num);
    }

    //获取自己的资料
    private void getMyInfo() {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_MY_INFO_URL;
        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        myInfoMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myInfoMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, myInfoMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                UserInfoResponde userInfoResponde = GsonUtil.GsonToBean(result, UserInfoResponde.class);
                if (userInfoResponde.getCode() == 0){
                    initMyInfo(userInfoResponde.getPayload().getInfo());
                    String phoneNumber = userInfoResponde.getPayload().getInfo().getPhone();
                    SharePreferenceUtil.put(ActivityMyInfo.this, YPlayConstant.YPLAY_PHONE_NUMBER, phoneNumber);
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });

    }

    //获取未读的消息数
    private void getAddFriendMessageCount() {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_GET_UNREAD_MESSAGE_COUNT_URL;
        Map<String, Object> unreadFriendMsgCountMap = new HashMap<>();
        unreadFriendMsgCountMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        unreadFriendMsgCountMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        unreadFriendMsgCountMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, unreadFriendMsgCountMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 未读好友消息---" + result);
                UnReadMsgCountRespond unReadMsgCountRespond = GsonUtil.GsonToBean(result, UnReadMsgCountRespond.class);
                if (unReadMsgCountRespond.getCode() == 0) {
                    int count = unReadMsgCountRespond.getPayload().getCnt();
                    if (count == 0) {
                        friendRequestNum.setVisibility(View.GONE);
                    } else {
                        friendRequestNum.setVisibility(View.VISIBLE);
                        friendRequestNum.setText(String.valueOf(count));
                        friendRequestNum.setBackground(getDrawable(R.drawable.shape_friend_request_background));
                        friendRequestNum.setTextColor(getResources().getColor(R.color.white));
                    }

                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });
    }
}
