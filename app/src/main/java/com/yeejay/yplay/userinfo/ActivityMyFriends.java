package com.yeejay.yplay.userinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.MyFriendsAdapter;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.SideView;
import com.yeejay.yplay.friend.ActivityFriendsInfo;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.FriendInfoDao;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMyFriends extends BaseActivity implements MyFriendsAdapter.OnGetAlphaIndexerAndSectionsListener{
    private static final String TAG = "ActivityMyFriends";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.amf_friend_null)
    ImageView friendNull;
    @BindView(R.id.amf_list_view)
    ListView amfListView;

    @BindView(R.id.amf_side_veiw)
    SideView amfSideView;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    private List<FriendInfo> mDataList;
    private FriendInfoDao friendInfoDao;
    private Map<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private List<String> sections;// 存放存在的汉语拼音首字母
    private MyFriendsAdapter mMyFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityMyFriends.this, true);

        friendInfoDao = YplayApplication.getInstance().getDaoSession().getFriendInfoDao();

        layoutTitle.setText(R.string.my_friends);
        mDataList = new ArrayList<>();
        initDataFirst();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //调到其它页面再返回本页面时，重新从数据库中获取一次好友信息，保证好友信息是最新的；
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //保存listview当前滚动到的位置，下次进入listview界面时需要滚动到该位置;
        View v = amfListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        SharedPreferences sharedPrefFriends = YplayApplication.getContext().
                getSharedPreferences("friend_list_pos",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editorsettings = sharedPrefFriends.edit();
        editorsettings.putInt("pos", amfListView.getFirstVisiblePosition());
        editorsettings.putInt("top", top);
        editorsettings.commit();
    }

    private void initDataFirst() {
        String uin = String.valueOf(SharePreferenceUtil.get(ActivityMyFriends.this, YPlayConstant.YPLAY_UIN, 0));
        mDataList = friendInfoDao.queryBuilder()
                .where(FriendInfoDao.Properties.MyselfUin.eq(uin))
                .orderAsc(FriendInfoDao.Properties.SortKey)
                .list();

        if (mDataList != null && mDataList.size() > 0){
            friendNull.setVisibility(View.GONE);
            amfSideView.setVisibility(View.VISIBLE);
            mMyFriendsAdapter = new MyFriendsAdapter(this, mDataList);
            mMyFriendsAdapter.setOnGetAlphaIndeserAndSectionListener(this);
            amfListView.setAdapter(mMyFriendsAdapter);

            amfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mDataList != null && mDataList.size() > 0){
                        Intent intent = new Intent(ActivityMyFriends.this, ActivityFriendsInfo.class);
                        intent.putExtra("yplay_friend_name", mDataList.get(position).getFriendName());
                        intent.putExtra("yplay_friend_uin",mDataList.get(position).getFriendUin());
                        startActivity(intent);
                    }
                }
            });

            SharedPreferences sharedPrefFriendsInfo = YplayApplication.getContext().
                    getSharedPreferences("friend_list_pos", Context.MODE_PRIVATE);
            int pos = sharedPrefFriendsInfo.getInt("pos", 0);
            int top = sharedPrefFriendsInfo.getInt("top", 0);
            LogUtils.getInstance().debug(TAG, " , pos = " + pos + " , top = " + top);
            amfListView.setSelectionFromTop(pos, top);

            amfSideView.setOnTouchingLetterChangedListener(new FriendSideListViewListener());
        }else {
            friendNull.setVisibility(View.VISIBLE);
            amfSideView.setVisibility(View.GONE);
        }
    }

    private void initData(){
        View v = amfListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        //防止数据有更新，因此重新从数据库中去一遍数据，通知adapter更新列表；
        String uin = String.valueOf(SharePreferenceUtil.get(ActivityMyFriends.this, YPlayConstant.YPLAY_UIN, 0));
        mDataList.clear();
        List<FriendInfo> tempList = friendInfoDao.queryBuilder()
                    .where(FriendInfoDao.Properties.MyselfUin.eq(uin))
                    .orderAsc(FriendInfoDao.Properties.SortKey)
                    .list();
        mDataList.addAll(tempList);

        mMyFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void getAlphaIndexerAndSectionsListner(Map<String, Integer> alphaIndexer, List<String> sections) {
        this.alphaIndexer = alphaIndexer;
        this.sections = sections;
    }

    /**
     * 字母列表点击滑动监听器事件
     */
    private class FriendSideListViewListener implements SideView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            if (alphaIndexer.get(s) != null) {//判断当前选中的字母是否存在集合中
                int position = alphaIndexer.get(s);//如果存在集合中则取出集合中该字母对应所在的位置,再利用对应的setSelection，就可以实现点击选中相应字母，然后联系人就会定位到相应的位置
                amfListView.setSelection(position);
            }
        }

    }
}
