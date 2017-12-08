package com.yeejay.yplay.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import org.greenrobot.greendao.query.DeleteQuery;

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

public class ActivityFriendsInfo extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
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

    //钻石
    @BindView(R.id.diamond_tv_num)
    TextView diamondTvNum;
    @BindView(R.id.diamond_list)
    MesureListView amiDiamondListView;
    @BindView(R.id.diamond_line)
    View amiDiamonLine;
    @BindView(R.id.diamond_null_img)
    ImageView amiDiamondNullImg;

    @BindView(R.id.friend_arrows)
    ImageView arrowsImg;

    int friendUin;
    private DbHelper dbHelper;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.remove_friend)
    public void removeFriend(View view) {
        if (NetWorkUtil.isNetWorkAvailable(ActivityFriendsInfo.this)){
            showNormalDialog();
        }else {
            Toast.makeText(ActivityFriendsInfo.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.lui_header_img)
    public void test(View view) {
        testToast();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_info2);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.play_color2));
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color2));
        layoutSetting.setVisibility(View.GONE);
        arrowsImg.setVisibility(View.GONE);

        dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_my_friend);
        TextView tv = (TextView) rl.findViewById(R.id.friend_tv1);
        tv.setText("好友");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String friendName = bundle.getString("yplay_friend_name");
            friendUin = bundle.getInt("yplay_friend_uin");

            layoutTitle.setText(friendName);
            layoutTitle.setTextColor(getResources().getColor(R.color.white));
            layoutTitleBack.setImageResource(R.drawable.back_white);

            getFriendInfo(friendUin);
        }
        getUserDiamondInfo(friendUin, 1, 10);
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

            //更新朋友数据
            FriendInfo friendInfo = dbHelper.queryFriendInfo(friendUin);
            if (friendInfo == null){
                dbHelper.insertFriendInfo(new FriendInfo(null,infoBean.getUin(),infoBean.getNickName(),infoBean.getHeadImgUrl(),infoBean.getGender(),infoBean.getGrade(),infoBean.getSchoolId(),infoBean.getSchoolType(),infoBean.getSchoolName(),infoBean.getTs()));
            }else {
                friendInfo.setFriendUin(infoBean.getUin());
                friendInfo.setFriendName(infoBean.getNickName());
                friendInfo.setFriendHeadUrl(infoBean.getHeadImgUrl());
                friendInfo.setFriendGender(infoBean.getGender());
                friendInfo.setFriendGrade(infoBean.getGrade());
                friendInfo.setFriendSchoolId(infoBean.getSchoolId());
                friendInfo.setFriendSchoolType(infoBean.getSchoolType());
                friendInfo.setFriendSchoolName(infoBean.getSchoolName());
                friendInfo.setTs(infoBean.getTs());
                dbHelper.updateFriendInfo(friendInfo);
            }

        }
    }


    //初始化钻石
    private void initDiamondList(UsersDiamondInfoRespond.PayloadBean payloadBean) {


        int total = payloadBean.getTotal();
        if (total > 0) {
            amiDiamondNullImg.setVisibility(View.GONE);
            amiDiamonLine.setVisibility(View.GONE);
        }
        if (total >= 3) {
            amiDiamonLine.setVisibility(View.VISIBLE);
        }
        final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = payloadBean.getStats();
        amiDiamondListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return tempList.size() >= 3 ? 3 : tempList.size();
            }

            @Override
            public Object getItem(int position) {
                return tempList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean;
                if (convertView == null) {
                    convertView = View.inflate(ActivityFriendsInfo.this, R.layout.item_user_info_diamond, null);
                    holder = new ViewHolder();
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.item_diamond_top_img);
                    holder.itemAmiImg2 = (ImageView) convertView.findViewById(R.id.item_diamond_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.item_diamond_text);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                statsBean = tempList.get(position);
                if (position == 0) {
                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top1));
                } else if (position == 1) {
                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top2));
                } else if (position == 2) {
                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top3));
                } else {
                    holder.itemAmiImg.setImageDrawable(getDrawable(R.drawable.diamond_top3));
                }

                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(ActivityFriendsInfo.this).load(url).into(holder.itemAmiImg2);
                } else {
                    holder.itemAmiImg2.setImageDrawable(getDrawable(R.drawable.diamond));
                }
                holder.itemAmiText.setText(statsBean.getQtext());
                return convertView;
            }
        });

    }

    private static class ViewHolder {
        ImageView itemAmiImg;
        ImageView itemAmiImg2;
        TextView itemAmiText;
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

                            if (payloadBean != null && payloadBean.getStats() != null) {

                                initDiamondList(payloadBean);
                                List<UsersDiamondInfoRespond.PayloadBean.StatsBean> statsBeanList =
                                        usersDiamondInfoRespond.getPayload().getStats();
//                                if (statsBeanList.size() >= 1){
//                                    initDiamondDetail(statsBeanList.get(0),null,null);
//                                }else if (statsBeanList.size() >= 2){
//                                    initDiamondDetail(statsBeanList.get(0),statsBeanList.get(1),null);
//                                }if (statsBeanList.size() >= 3){
//                                    initDiamondDetail(statsBeanList.get(0),statsBeanList.get(2),statsBeanList.get(3));
//                                }
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
                        deleteFriendInfo();
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

    //删除好友资料
    private void deleteFriendInfo() {

        DaoFriendFeedsDao friendFeedsDao = YplayApplication.getInstance()
                .getDaoSession()
                .getDaoFriendFeedsDao();

        DeleteQuery deleteQuery = friendFeedsDao.queryBuilder()
                .where(DaoFriendFeedsDao.Properties.FriendUin.eq(friendUin))
                .buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();

        //删除好友列表中的好友
        FriendInfo friendInfo =  dbHelper.queryFriendInfo(friendUin);
        dbHelper.deleteFriendInfo(friendInfo);

        finish();
    }


    private void testToast() {

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.TOP, 0, 0);
        TextView view = new TextView(this);
        view.setText("我是测试oast");
        view.setBackgroundColor(getResources().getColor(R.color.feeds_title_color));
        toast.setView(view);
        toast.show();
    }

}



