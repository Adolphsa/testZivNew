package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * 通讯录联系人资料
 * Created by Administrator on 2017/12/12.
 */

@Entity
public class ContactsInfo {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    @Index(unique = true)
    private String orgPhone;
    private String phone;
    private int uin;
    private String sortKey;
    private String nickName;
    private String headImgUrl;

    @Generated(hash = 2042693928)
    public ContactsInfo(Long id, String name, String orgPhone, String phone,
            int uin, String sortKey, String nickName, String headImgUrl) {
        this.id = id;
        this.name = name;
        this.orgPhone = orgPhone;
        this.phone = phone;
        this.uin = uin;
        this.sortKey = sortKey;
        this.nickName = nickName;
        this.headImgUrl = headImgUrl;
    }
    @Generated(hash = 9726432)
    public ContactsInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getOrgPhone() {
        return this.orgPhone;
    }
    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }
    public int getUin() {
        return this.uin;
    }
    public void setUin(int uin) {
        this.uin = uin;
    }
    public String getSortKey() {
        return this.sortKey;
    }
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getHeadImgUrl() {
        return this.headImgUrl;
    }
    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }


}
