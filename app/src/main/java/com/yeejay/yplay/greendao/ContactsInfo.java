package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 通讯录联系人资料
 * Created by Administrator on 2017/12/12.
 */

@Entity
public class ContactsInfo {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String phone;
    @Generated(hash = 322479528)
    public ContactsInfo(Long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
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


}
