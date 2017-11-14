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

    @Generated(hash = 1701828999)
    public MyInfo(Long id, int uin, int isNoMoreShow) {
        this.id = id;
        this.uin = uin;
        this.isNoMoreShow = isNoMoreShow;
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
}
