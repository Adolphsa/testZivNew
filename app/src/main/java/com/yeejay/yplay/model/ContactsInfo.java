package com.yeejay.yplay.model;

/**
 * 通讯录联系人
 * Created by Administrator on 2017/10/25.
 */

public class ContactsInfo {

    private String name;
    private String phone;

    public ContactsInfo(String name, String phone){
        setName(name);
        setPhone(phone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
