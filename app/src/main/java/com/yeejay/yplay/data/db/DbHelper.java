package com.yeejay.yplay.data.db;

import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.FriendsListRespond;

import java.util.List;

/**
 * 数据库帮助
 * Created by Administrator on 2017/12/5.
 */

public interface DbHelper {

    //friendInfo
    long insertFriendInfo(FriendInfo friendInfo);
    FriendInfo NetworkFriendInfo2DbFriendInfo(FriendsListRespond.PayloadBean.FriendsBean friendInfo);
    FriendInfo queryFriendInfo(int friendUin, int myselfUin);
    void updateFriendInfo(FriendInfo friendInfo);
    void updateFriendInfo(FriendInfo dataBaseFriendInfo, FriendsListRespond.PayloadBean.FriendsBean friendInfo);
    void deleteFriendInfo(FriendInfo friendInfo);
    void deleteFriendInfoAll();
    List<FriendInfo> getAllFriends();

}
