package com.yeejay.yplay.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.RecyclerImageView;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

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

public class ActivityFriendsInfo extends BaseActivity {

    private static final String TAG = "ActivityFriendsInfo";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.personal_nick_name)
    TextView luiName;
    @BindView(R.id.personal_gender)
    ImageView luiGender;
    @BindView(R.id.personal_user_name)
    TextView luiUserName;
    @BindView(R.id.personal_school)
    TextView luiSchoolName;
//    @BindView(R.id.personal_grade)
//    TextView luiGrade;
    @BindView(R.id.lui_header_img)
    EffectiveShapeView luiHeaderImg;
    @BindView(R.id.apf_title)
    RelativeLayout layoutTitleRl;

    @BindView(R.id.lui_friend_count)
    TextView friendTvNum;

    //钻石
    @BindView(R.id.lui_diamond_count)
    TextView diamondTvNum;
    @BindView(R.id.diamond_tv_num)
    TextView diamondTvNum2;
    @BindView(R.id.friend_tv1)
    TextView diamondTvTitle;

    @BindView(R.id.friend_arrows)
    ImageView friendArrows;
    @BindView(R.id.lpid_diamond_list)
    ListView amiDiamondListView;

    int friendUin;
    int myselfUin;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_friends_info);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.myinfo_end_color));
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.myinfo_end_color));
        layoutSetting.setVisibility(View.GONE);

        myselfUin = (int) SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, (int) 0);
        dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            friendUin = bundle.getInt("yplay_friend_uin");
            layoutTitle.setVisibility(View.GONE);
            layoutTitleBack.setImageResource(R.drawable.back_white);

            getFriendInfo(friendUin);
        }
        getUserDiamondInfo(friendUin, 1, 3);
        diamondTvNum2.setVisibility(View.GONE);
        friendArrows.setVisibility(View.GONE);
        diamondTvTitle.setText("TOP");
    }

    //初始化用户资料
    private void initUserInfo(UserInfoResponde.PayloadBean.InfoBean infoBean) {

        if (infoBean != null) {
            String url = infoBean.getHeadImgUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(this).load(url).resizeDimen(R.dimen.lui_header_img_width,
                        R.dimen.lui_header_img_height).
                        memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(luiHeaderImg);
            }
            luiName.setText(infoBean.getNickName());
            luiUserName.setText(infoBean.getUserName());

            if (infoBean.getGender() == 1) {
                luiGender.setVisibility(View.VISIBLE);
                luiGender.setImageDrawable(getDrawable(R.drawable.myinfo_sex_boy));
            } else {
                luiGender.setVisibility(View.VISIBLE);
                luiGender.setImageDrawable(getDrawable(R.drawable.myinfo_sex_girl));
            }
            luiSchoolName.setText(infoBean.getSchoolName() + " • " + FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade()));
            friendTvNum.setText(String.valueOf(infoBean.getFriendCnt()));
            diamondTvNum.setText(String.valueOf(infoBean.getGemCnt()));

            //更新朋友数据
            FriendInfo friendInfo = dbHelper.queryFriendInfo(friendUin,myselfUin);
            if (friendInfo == null){
                dbHelper.insertFriendInfo(new FriendInfo(null,infoBean.getUin(),
                        infoBean.getNickName(),
                        infoBean.getHeadImgUrl(),
                        infoBean.getGender(),
                        infoBean.getGrade(),
                        infoBean.getSchoolId(),
                        infoBean.getSchoolType(),
                        infoBean.getSchoolName(),
                        infoBean.getTs(),
                        BaseUtils.getSortKey(infoBean.getNickName()),
                        String.valueOf(SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, 0))));
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
                friendInfo.setSortKey(BaseUtils.getSortKey(infoBean.getNickName()));
                dbHelper.updateFriendInfo(friendInfo);
            }

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

        String url = YPlayConstant.BASE_URL + YPlayConstant.API_GETUSERPROFILE;
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(url, friendMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 获取朋友的资料---" + result);
                UserInfoResponde userInfoResponde = GsonUtil.GsonToBean(result, UserInfoResponde.class);
                if (userInfoResponde.getCode() == 0) {
                    UserInfoResponde.PayloadBean.InfoBean infoBean =
                            userInfoResponde.getPayload().getInfo();
                    initUserInfo(infoBean);

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

    //获取钻石信息
    private void getUserDiamondInfo(int userUin,int pageNum, int pageSize) {

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_GET_DIAMOND_URL;
        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin",userUin);
        diamondInfoMap.put("pageNum",pageNum);
        diamondInfoMap.put("pageSize",pageSize);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(ActivityFriendsInfo.this, YPlayConstant.YPLAY_VER, 0));
        WnsAsyncHttp.wnsRequest(url, diamondInfoMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 用户钻石信息---" + result);
                handleDiamondRespond(result);
            }

            @Override
            public void onTimeOut() {}

            @Override
            public void onError() {

            }
        });
    }

    //处理钻石列表数据返回
    private void handleDiamondRespond(String result){

        UsersDiamondInfoRespond usersDiamondInfoRespond = GsonUtil.GsonToBean(result, UsersDiamondInfoRespond.class);
        if (usersDiamondInfoRespond.getCode() == 0){
            List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = usersDiamondInfoRespond.getPayload().getStats();
            Log.i(TAG, "handleDiamondRespond: list---" + tempList.size());
            if (tempList != null && tempList.size() > 0){
                initDiamondAdapter(tempList);
            }

        }

    }

    //初始化钻石适配器
    private void initDiamondAdapter(final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList){

        BaseAdapter diamondAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return tempList.size() > 3 ? 3 : tempList.size();
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
                if (convertView == null) {
                    convertView = View.inflate(ActivityFriendsInfo.this, R.layout.item_diamond_new, null);
                    holder = new ViewHolder();
                    holder.itemAmiIndex = (ImageView) convertView.findViewById(R.id.afi_item_index);
                    holder.itemAmiImg = (RecyclerImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean = tempList.get(position);

                if (position == 0){
                    holder.itemAmiIndex.setImageResource(R.drawable.diamond_top1);
                }else if (position == 1){
                    holder.itemAmiIndex.setImageResource(R.drawable.diamond_top2);
                }else if (position == 2){
                    holder.itemAmiIndex.setImageResource(R.drawable.diamond_top3);
                }

                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(ActivityFriendsInfo.this).load(url).resizeDimen(R.dimen.item_diamonds_list_img_width,
                            R.dimen.item_diamonds_list_img_height).into(holder.itemAmiImg);
                }else {
                    holder.itemAmiImg.setImageResource(R.drawable.diamond_null);
                }
                holder.itemAmiText.setText(statsBean.getQtext());
                return convertView;
            }
        };

        amiDiamondListView.setAdapter(diamondAdapter);

    }

    private static class ViewHolder {
        ImageView itemAmiIndex;
        TextView itemAmiText;
        RecyclerImageView itemAmiImg;
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
                .where(DaoFriendFeedsDao.Properties.Uin.eq(myselfUin))
                .where(DaoFriendFeedsDao.Properties.FriendUin.eq(friendUin))
                .buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();

        Log.i(TAG, "deleteFriendInfo: friend uin---" + friendUin);

        //删除好友列表中的好友
        FriendInfo friendInfo =  dbHelper.queryFriendInfo(friendUin,myselfUin);
        dbHelper.deleteFriendInfo(friendInfo);
        //删除好友动态

        Intent i = new Intent();
        i.putExtra("is_remove_friend","yes");
        setResult(FragmentFriend.FEED_REQUEST_CODE,i);
        finish();
    }

}



