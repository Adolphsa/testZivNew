package com.yeejay.yplay.userinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
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

public class ActivityMyInfo extends AppCompatActivity {

    @BindView(R.id.my_tv_back)
    Button myTvBack;
    @BindView(R.id.my_tv_title)
    TextView myTvTitle;
    @BindView(R.id.my_ib_logo)
    ImageButton myIbLogo;
    @BindView(R.id.ami_tv_school_name)
    TextView amiTvSchoolName;
    @BindView(R.id.ami_item_header_img)
    EffectiveShapeView amiItemHeaderImg;
    @BindView(R.id.ami_tv_name)
    TextView amiTvName;
    @BindView(R.id.ami_tv_name2)
    TextView amiTvName2;
    @BindView(R.id.ami_small_img)
    ImageView amiSmallImg;
    @BindView(R.id.ami_tv_is_graduate)
    TextView amiTvIsGraduate;
    @BindView(R.id.ami_btn_setting)
    ImageButton amiBtnSetting;
    @BindView(R.id.ami_info_friends_number)
    TextView amiTvFriendsNumber;
    @BindView(R.id.ami_info_diamond_number)
    TextView amiTvDiamondNumber;
    @BindView(R.id.ami_diamond_list_view)
    ListView amiDiamondListView;
    @BindView(R.id.ami_friends_list_view)
    ListView amiFriendsListView;
    @BindView(R.id.ami_ptf_refresh)
    PullToRefreshLayout amiPtfRefresh;
    @BindView(R.id.ami_tv_friend_num)
    TextView amiFriendNum;

    @OnClick(R.id.my_tv_back)
    public void back(View view) {
        finish();
    }

    //logo
    @OnClick(R.id.my_ib_logo)
    public void logo(View view){
        startActivity(new Intent(ActivityMyInfo.this,ActivityAboutOur.class));
    }

    //设置
    @OnClick(R.id.ami_btn_setting)
    public void setting(View view){
        startActivity(new Intent(ActivityMyInfo.this,ActivitySetting.class));
    }

    List<FriendsListRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);

        mDataList = new ArrayList<>();
        int uin = (int)SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0);

        getMyInfo();
        getUserDiamondInfo(uin,1);
        getMyFriendsList(mPageNum);
    }

    //初始化我的资料
    private void initMyInfo(UserInfoResponde.PayloadBean.InfoBean infoBean ){
        if (infoBean != null){
            amiTvSchoolName.setText(infoBean.getSchoolName());
            String url = infoBean.getHeadImgUrl();
            if (!TextUtils.isEmpty(url)){
                Picasso.with(ActivityMyInfo.this).load(url).resize(80,80).into(amiItemHeaderImg);
            }
            amiTvName.setText(infoBean.getNickName());
            amiTvName2.setText(infoBean.getUserName());
            amiTvIsGraduate.setText(FriendFeedsUtil.schoolType(infoBean.getSchoolType(),infoBean.getGrade()));
            StringBuilder str1 = new StringBuilder(String.valueOf(infoBean.getFriendCnt()));
            str1.append("好友");
            amiTvFriendsNumber.setText(str1);
            StringBuilder str2 = new StringBuilder(String.valueOf(infoBean.getGemCnt()));
            str2.append("钻石");
           amiTvDiamondNumber.setText(str2);
        }

    }

    //初始化钻石
    private void initDiamondList(UsersDiamondInfoRespond.PayloadBean payloadBean){

        View footView = View.inflate(ActivityMyInfo.this,R.layout.item_af_listview_foot,null);
        int total = payloadBean.getTotal();
        if (total > 3){
            TextView countTv = (TextView) footView.findViewById(R.id.af_foot_tv1);
            countTv.setText("查看更多" + (total-3));
            amiDiamondListView.addFooterView(footView);
        }
        final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = payloadBean.getStats();
        amiDiamondListView.setAdapter(new BaseAdapter() {
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
                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean;
                if (convertView == null){
                    convertView = View.inflate(ActivityMyInfo.this,R.layout.item_afi,null);
                    holder = new ViewHolder();
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                statsBean = tempList.get(position);
                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(ActivityMyInfo.this).load(url).resize(35,35).into(holder.itemAmiImg);
                }
                holder.itemAmiText.setText(statsBean.getQtext());
                return convertView;
            }
        });
        amiDiamondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    startActivity(new Intent(ActivityMyInfo.this,ActivityAllDiamond.class));
                }
            }
        });
    }

    //初始化我的好友列表
    private void initFriendsList(final List<FriendsListRespond.PayloadBean.FriendsBean> tempList){

        amiFriendsListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return tempList.size();
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
                viewHolderFriends holder;
                if (convertView == null){
                    convertView = View.inflate(ActivityMyInfo.this,R.layout.item_all_diamond,null);
                    holder = new viewHolderFriends();
                    holder.itemAadImg = (EffectiveShapeView) convertView.findViewById(R.id.item_aad_img);
                    holder.itemAadText1 = (TextView) convertView.findViewById(R.id.item_aad_text1);
                    holder.itemAadText2 = (TextView) convertView.findViewById(R.id.item_aad_text2);
                    convertView.setTag(holder);
                }else {
                    holder = (viewHolderFriends) convertView.getTag();
                }
                FriendsListRespond.PayloadBean.FriendsBean friendsBean = tempList.get(position);
                String url = friendsBean.getHeadImgUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(ActivityMyInfo.this).load(url).resize(50,50).into(holder.itemAadImg);
                }
                holder.itemAadText1.setText(friendsBean.getNickName());
                return convertView;
            }
        });
        amiFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showNormalDialog();
            }
        });
        loadMore();
    }

    //加载更多
    private void loadMore(){
        amiPtfRefresh.setCanRefresh(false);
        amiPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                mPageNum++;
                System.out.println("mPageNum---" + mPageNum);
                getMyFriendsList(mPageNum);
            }
        });

    }

    //显示对话框
    private void showNormalDialog(){
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

    private static class ViewHolder{
        ImageView itemAmiImg;
        TextView itemAmiText;
    }

    private static class viewHolderFriends{
        EffectiveShapeView itemAadImg;
        TextView itemAadText1;
        TextView itemAadText2;
    }

    //获取自己的资料
    private void getMyInfo(){

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
                        if (userInfoResponde.getCode() == 0){
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
    private void getUserDiamondInfo(int userUin,int pageNum){
        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin",userUin);
        diamondInfoMap.put("pageNum",pageNum);
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
                        if (usersDiamondInfoRespond.getCode() == 0){
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


    //获取好友列表
    private void getMyFriendsList(int pageNum){

        Map<String, Object> myFriendsMap = new HashMap<>();
        myFriendsMap.put("pageNum",pageNum);
        myFriendsMap.put("uin", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_UIN, 0));
        myFriendsMap.put("token", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myFriendsMap.put("ver", SharePreferenceUtil.get(ActivityMyInfo.this, YPlayConstant.YPLAY_VER, 0));
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
                        if (friendsListRespond.getCode() == 0){
                            mDataList.addAll(friendsListRespond.getPayload().getFriends());
                            if (mDataList.size() > 0){
                                initFriendsList(mDataList);
                                int friendCount = friendsListRespond.getPayload().getTotal();
                                System.out.println("朋友---" + friendCount);
                                amiFriendNum.setText("朋友" + friendCount);
                            }

                        }

                        amiPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取我的好友列表异常---" + e.getMessage());
                        amiPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
