package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 记录通讯录中已邀请的好友
 * Created by Adolph on 2018/1/23.
 */

@Entity
public class ContactInvite {

    @Id(autoincrement = true)
    private Long id;
    private String uin;
    private String friendPhone;
    @Generated(hash = 1980305164)
    public ContactInvite(Long id, String uin, String friendPhone) {
        this.id = id;
        this.uin = uin;
        this.friendPhone = friendPhone;
    }
    @Generated(hash = 1588141107)
    public ContactInvite() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUin() {
        return this.uin;
    }
    public void setUin(String uin) {
        this.uin = uin;
    }
    public String getFriendPhone() {
        return this.friendPhone;
    }
    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }
}
