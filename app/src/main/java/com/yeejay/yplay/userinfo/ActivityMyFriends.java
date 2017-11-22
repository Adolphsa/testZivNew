package com.yeejay.yplay.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
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
import tangxiaolv.com.library.EffectiveShapeView;

public class ActivityMyFriends extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.amf_list_view)
    ListView amfListView;
    @BindView(R.id.amf_ptf_refresh)
    PullToRefreshLayout amfPtfRefresh;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    List<FriendsListRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityMyFriends.this, true);

        layoutTitle.setText("我的好友");
        mDataList = new ArrayList<>();

        loadMore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataList.clear();

        System.out.println("我的好友resume" + mDataList.size());
        getMyFriendsList(mPageNum);
    }

    private void initMyFriendsList(final List<FriendsListRespond.PayloadBean.FriendsBean> tempList) {

        amfListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return tempList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder;
                if (convertView == null){
                    convertView = View.inflate(ActivityMyFriends.this,R.layout.item_my_friend,null);
                    holder = new ViewHolder();
                    holder.itemMyFriendImg = (EffectiveShapeView) convertView.findViewById(R.id.item_my_friend_img);
                    holder.itemMyFriendName = (TextView) convertView.findViewById(R.id.item_my_friend_name);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String url = tempList.get(position).getHeadImgUrl();
                holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(ActivityMyFriends.this).load(url).into(holder.itemMyFriendImg);
                }else {
                    holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
                }
                String name = tempList.get(position).getNickName();
                holder.itemMyFriendName.setText(name);
                return convertView;
            }
        });

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

    private static class ViewHolder{
        EffectiveShapeView itemMyFriendImg;
        TextView itemMyFriendName;
    }

    private void loadMore() {

        amfPtfRefresh.setCanRefresh(false);
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
                            mDataList.addAll(tempList);
                            initMyFriendsList(mDataList);
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
