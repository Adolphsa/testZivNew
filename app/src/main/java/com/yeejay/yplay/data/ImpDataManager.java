package com.yeejay.yplay.data;

import android.content.Context;

import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.network.ApiHelper;
import com.yeejay.yplay.data.prefs.PreferencesHelper;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.FriendsListRespond;

import java.util.List;

/**
 * DataManager实现类
 * Created by Administrator on 2017/12/5.
 */

public class ImpDataManager implements DataManager{

    private Context mContext;
    private DbHelper mDbHelper;
    private ApiHelper mApiHelper;
    private PreferencesHelper mPreferencesHelper;

    public ImpDataManager(Context context,
                          DbHelper dbHelper,
                          ApiHelper apiHelper,
                          PreferencesHelper preferencesHelper){
        mContext = context;
        mDbHelper = dbHelper;
        mApiHelper = apiHelper;
        mPreferencesHelper = preferencesHelper;
    }

    //-----------------------DbHelper-----------------------------
    @Override
    public long insertFriendInfo(FriendInfo friendInfo) {
        return mDbHelper.insertFriendInfo(friendInfo);
    }

    @Override
    public FriendInfo NetworkFriendInfo2DbFriendInfo(FriendsListRespond.PayloadBean.FriendsBean friendInfo) {
        return mDbHelper.NetworkFriendInfo2DbFriendInfo(friendInfo);
    }

    @Override
    public FriendInfo queryFriendInfo(int friendUin, int myselfUin) {
        return mDbHelper.queryFriendInfo(friendUin,myselfUin);
    }

    @Override
    public void updateFriendInfo(FriendInfo friendInfo) {
        mDbHelper.updateFriendInfo(friendInfo);
    }

    @Override
    public void updateFriendInfo(FriendInfo dataBaseFriendInfo, FriendsListRespond.PayloadBean.FriendsBean friendInfo) {
        mDbHelper.updateFriendInfo(dataBaseFriendInfo,friendInfo);
    }

    @Override
    public void deleteFriendInfo(FriendInfo friendInfo) {
        mDbHelper.deleteFriendInfo(friendInfo);
    }

    @Override
    public void deleteFriendInfoAll() {
        mDbHelper.deleteFriendInfoAll();
    }

    @Override
    public List<FriendInfo> getAllFriends() {
        return mDbHelper.getAllFriends();
    }
}
