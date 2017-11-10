package com.yeejay.yplay.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
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
import tangxiaolv.com.library.EffectiveShapeView;

import static com.yeejay.yplay.utils.FriendFeedsUtil.schoolType;

public class ActivityFriendsInfo extends AppCompatActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.lui_name)
    TextView luiName;
    @BindView(R.id.lui_gender)
    ImageView luiGender;
    @BindView(R.id.lui_user_name)
    TextView luiUserName;
    @BindView(R.id.lui_school_name)
    TextView luiSchoolName;
    @BindView(R.id.lui_grade)
    TextView luiGrade;
    @BindView(R.id.lui_header_img)
    EffectiveShapeView luiHeaderImg;
    @BindView(R.id.layout_title_rl)
    RelativeLayout layoutTitleRl;

    @BindView(R.id.friend_tv_num)
    TextView friendTvNum;
    @BindView(R.id.diamond_tv_num)
    TextView diamondTvNum;

    int friendUin;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private EffectiveShapeView diamondDetailImg1;
    private TextView diamondDetailTv1;
    private EffectiveShapeView diamondDetailImg2;
    private TextView diamondDetailTv2;
    private EffectiveShapeView diamondDetailImg3;
    private TextView diamondDetailTv3;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.remove_friend)
    public void removeFriend(View view) {
        showNormalDialog();
    }

//    @OnClick(R.id.lui_header_img)
//    public void test(View view) {
//        testToast();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_info2);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.play_color2));
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color2));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String friendName = bundle.getString("yplay_friend_name");
            friendUin = bundle.getInt("yplay_friend_uin");

            layoutTitle.setText(friendName);
            layoutTitle.setTextColor(getResources().getColor(R.color.white));
            layoutTitleBack.setBackgroundResource(R.drawable.back_white);

            getFriendInfo(friendUin);
        }

        initDiamondTop();

        getUserDiamondInfo(friendUin,1,10);
    }

    //初始化用户资料
    private void initUserInfo(UserInfoResponde.PayloadBean.InfoBean infoBean) {

        if (infoBean != null) {
            String url = infoBean.getHeadImgUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(this).load(url).into(luiHeaderImg);
            }
            luiName.setText(infoBean.getNickName());
            luiUserName.setText(infoBean.getUserName());
            luiGrade.setText(schoolType(infoBean.getSchoolType(), infoBean.getGrade()));
            if (infoBean.getGender() == 1) {
                luiGender.setVisibility(View.VISIBLE);
                luiGender.setImageDrawable(getDrawable(R.drawable.feeds_boy));
            } else {
                luiGender.setVisibility(View.VISIBLE);
                luiGender.setImageDrawable(getDrawable(R.drawable.feeds_girl));
            }
            luiSchoolName.setText(infoBean.getSchoolName());
            friendTvNum.setText(infoBean.getFriendCnt() + "");
            diamondTvNum.setText(infoBean.getGemCnt() + "");
        }
    }

    //初始化钻石top
    private void initDiamondTop(){

        rl1 = (RelativeLayout) findViewById(R.id.diamond_detail1);
        TextView diamondDetailTop1 = (TextView) rl1.findViewById(R.id.diamond_detail_top);
        diamondDetailImg1 = (EffectiveShapeView) rl1.findViewById(R.id.diamond_detail_img);
        diamondDetailTv1 = (TextView) rl1.findViewById(R.id.diamond_detail_tv);
        diamondDetailTop1.setText("Top1");

        rl2 = (RelativeLayout) findViewById(R.id.diamond_detail2);
        TextView diamondDetailTop2 = (TextView) rl2.findViewById(R.id.diamond_detail_top);
        diamondDetailImg2 = (EffectiveShapeView) rl2.findViewById(R.id.diamond_detail_img);
        diamondDetailTv2 = (TextView) rl2.findViewById(R.id.diamond_detail_tv);
        diamondDetailTop2.setText("Top2");

        rl3 = (RelativeLayout) findViewById(R.id.diamond_detail3);
        TextView diamondDetailTop3 = (TextView) rl3.findViewById(R.id.diamond_detail_top);
        diamondDetailImg3 = (EffectiveShapeView) rl3.findViewById(R.id.diamond_detail_img);
        diamondDetailTv3 = (TextView) rl3.findViewById(R.id.diamond_detail_tv);
        diamondDetailTop3.setText("Top3");

    }

    //初始化钻石详情
    private void initDiamondDetail(UsersDiamondInfoRespond.PayloadBean.StatsBean  statsBean1,
                                   UsersDiamondInfoRespond.PayloadBean.StatsBean  statsBean2,
                                   UsersDiamondInfoRespond.PayloadBean.StatsBean  statsBean3){


        if (statsBean1 != null){
            String url1 = statsBean1.getQiconUrl();
            if (!TextUtils.isEmpty(url1)){
                Picasso.with(ActivityFriendsInfo.this).load(url1).into(diamondDetailImg1);
            }
            diamondDetailTv1.setText(statsBean1.getQtext());
        }
        if (statsBean2 != null){
            String url2 = statsBean2.getQiconUrl();
            if (!TextUtils.isEmpty(url2)){
                Picasso.with(ActivityFriendsInfo.this).load(url2).into(diamondDetailImg2);
            }
            diamondDetailTv2.setText(statsBean2.getQtext());
        }
        if (statsBean3 != null){
            String url3 = statsBean3.getQiconUrl();
            if (!TextUtils.isEmpty(url3)){
                Picasso.with(ActivityFriendsInfo.this).load(url3).into(diamondDetailImg3);
            }
            diamondDetailTv3.setText(statsBean3.getQtext());
        }
    }

    private void showNormalDialog() {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ActivityFriendsInfo.this);
        normalDialog.setMessage("解除好友关系后，你也会在对方的好友列表中消失");
        normalDialog.setPositiveButton("解除关系",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFriend(friendUin);
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


    //查询朋友的信息
    private void getFriendInfo(int friendUin) {
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_VER, 0));
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
                            UserInfoResponde.PayloadBean.InfoBean infoBean =
                                    userInfoResponde.getPayload().getInfo();
                            initUserInfo(infoBean);
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


    //获取钻石信息
    private void getUserDiamondInfo(int userUin, int pageNum, int pageSize) {

        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin", userUin);
        diamondInfoMap.put("pageNum", pageNum);
        diamondInfoMap.put("pageSize", pageSize);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_VER, 0));
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
                            UsersDiamondInfoRespond.PayloadBean payloadBean = usersDiamondInfoRespond.getPayload();

                            if (payloadBean != null && payloadBean.getStats() != null){
                                List<UsersDiamondInfoRespond.PayloadBean.StatsBean> statsBeanList =
                                        usersDiamondInfoRespond.getPayload().getStats();
                                if (statsBeanList.size() >= 1){
                                    initDiamondDetail(statsBeanList.get(0),null,null);
                                }else if (statsBeanList.size() >= 2){
                                    initDiamondDetail(statsBeanList.get(0),statsBeanList.get(1),null);
                                }if (statsBeanList.size() >= 3){
                                    initDiamondDetail(statsBeanList.get(0),statsBeanList.get(2),statsBeanList.get(3));
                                }
                            }

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

    //删除好友
    private void removeFriend(int toUin) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("toUin", toUin);
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_VER, 0));
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


//    private void testToast() {
//
//        Toast toast = new Toast(this);
//        toast.setGravity(Gravity.TOP, 0, 0);
//        TextView view = new TextView(this);
//        view.setText("我是测试oast");
//        view.setBackgroundColor(getResources().getColor(R.color.feeds_title_color));
//        toast.setView(view);
//        toast.show();
//    }

}



