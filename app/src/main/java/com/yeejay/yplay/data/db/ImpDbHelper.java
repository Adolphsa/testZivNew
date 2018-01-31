package com.yeejay.yplay.data.db;

import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.DaoSession;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.FriendInfoDao;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.List;

/**
 * 数据库实现类
 * Created by Administrator on 2017/12/8.
 */

public class ImpDbHelper implements DbHelper{

    private static final String TAG = "ImpDbHelper";

    private DaoSession mDaoSession;

    public ImpDbHelper(DaoSession daoSession){
        this.mDaoSession = daoSession;
    }

    //----------friendInfo-------------------
    @Override
    public long insertFriendInfo(FriendInfo friendInfo) {
        return mDaoSession.getFriendInfoDao().insert(friendInfo);
    }

    @Override
    public FriendInfo NetworkFriendInfo2DbFriendInfo(FriendsListRespond.PayloadBean.FriendsBean friendInfo) {

        String nickName = friendInfo.getNickName();
        return new FriendInfo(null,
                friendInfo.getUin(),
                friendInfo.getNickName(),
                friendInfo.getHeadImgUrl(),
                friendInfo.getGender(),
                friendInfo.getGrade(),
                friendInfo.getSchoolId(),
                friendInfo.getSchoolType(),
                friendInfo.getSchoolName(),
                friendInfo.getTs(),
                BaseUtils.getSortKey(nickName),
                String.valueOf(SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, 0)));
    }

    @Override
    public FriendInfo queryFriendInfo(int friendUin, int myselfUin) {
        return mDaoSession.getFriendInfoDao().queryBuilder()
                .where(FriendInfoDao.Properties.MyselfUin.eq(myselfUin))
                .where(FriendInfoDao.Properties.FriendUin.eq(friendUin))
                .build().unique();
    }

    @Override
    public void updateFriendInfo(FriendInfo friendInfo) {
        mDaoSession.getFriendInfoDao().update(friendInfo);
    }

    @Override
    public void updateFriendInfo(FriendInfo dataBaseFriendInfo, FriendsListRespond.PayloadBean.FriendsBean friendInfo) {
        dataBaseFriendInfo.setFriendUin(friendInfo.getUin());
        dataBaseFriendInfo.setFriendName(friendInfo.getNickName());
        dataBaseFriendInfo.setFriendHeadUrl(friendInfo.getHeadImgUrl());
        dataBaseFriendInfo.setFriendGender(friendInfo.getGender());
        dataBaseFriendInfo.setFriendGrade(friendInfo.getGrade());
        dataBaseFriendInfo.setFriendSchoolId(friendInfo.getSchoolId());
        dataBaseFriendInfo.setFriendSchoolType(friendInfo.getSchoolType());
        dataBaseFriendInfo.setFriendSchoolName(friendInfo.getSchoolName());
        dataBaseFriendInfo.setTs(friendInfo.getTs());
        mDaoSession.getFriendInfoDao().update(dataBaseFriendInfo);
    }

    @Override
    public void deleteFriendInfo(FriendInfo friendInfo) {
        mDaoSession.getFriendInfoDao().delete(friendInfo);
    }

    @Override
    public void deleteFriendInfoAll() {
        mDaoSession.getFriendInfoDao().deleteAll();
    }

    @Override
    public List<FriendInfo> getAllFriends() {
        return mDaoSession.getFriendInfoDao().loadAll();
    }
}
