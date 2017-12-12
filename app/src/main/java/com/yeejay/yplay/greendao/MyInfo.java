package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 我的资料
 * Created by Administrator on 2017/11/15.
 */

@Entity
public class MyInfo {

    @Id(autoincrement = true)
    private Long id;

    private int uin;
    private int isNoMoreShow;
    private int isNoMoreShow2;
    private int isShowInviteDialogInfo;
    private int addFriendNum;
    private int isInviteTipShow;

    @Generated(hash = 1493227262)
    public MyInfo(Long id, int uin, int isNoMoreShow, int isNoMoreShow2,
            int isShowInviteDialogInfo, int addFriendNum, int isInviteTipShow) {
        this.id = id;
        this.uin = uin;
        this.isNoMoreShow = isNoMoreShow;
        this.isNoMoreShow2 = isNoMoreShow2;
        this.isShowInviteDialogInfo = isShowInviteDialogInfo;
        this.addFriendNum = addFriendNum;
        this.isInviteTipShow = isInviteTipShow;
    }
    @Generated(hash = 980896312)
    public MyInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getUin() {
        return this.uin;
    }
    public void setUin(int uin) {
        this.uin = uin;
    }
    public int getIsNoMoreShow() {
        return this.isNoMoreShow;
    }
    public void setIsNoMoreShow(int isNoMoreShow) {
        this.isNoMoreShow = isNoMoreShow;
    }
    public int getIsNoMoreShow2() {
        return this.isNoMoreShow2;
    }
    public void setIsNoMoreShow2(int isNoMoreShow2) {
        this.isNoMoreShow2 = isNoMoreShow2;
    }
    public int getIsShowInviteDialogInfo() {
        return this.isShowInviteDialogInfo;
    }
    public void setIsShowInviteDialogInfo(int isShowInviteDialogInfo) {
        this.isShowInviteDialogInfo = isShowInviteDialogInfo;
    }
    public int getAddFriendNum() {
        return this.addFriendNum;
    }
    public void setAddFriendNum(int addFriendNum) {
        this.addFriendNum = addFriendNum;
    }
    public int getIsInviteTipShow() {
        return this.isInviteTipShow;
    }
    public void setIsInviteTipShow(int isInviteTipShow) {
        this.isInviteTipShow = isInviteTipShow;
    }
}
