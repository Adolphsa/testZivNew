package com.yeejay.yplay.model;

/**
 * 投票请求参数bean
 * Created by Administrator on 2017/10/30.
 */

public class VoteOptionsBean {

    int uin;
    String nickName;
    int beSelCnt;

    public VoteOptionsBean(int uin, String name,int beSelCnt){
        this.uin = uin;
        this.nickName = name;
        this.beSelCnt = beSelCnt;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String name) {
        this.nickName = name;
    }

    public void setBeSelCnt(int beSelCnt) {
        this.beSelCnt = beSelCnt;
    }

    public int getBeSelCnt() {
        return beSelCnt;
    }

    @Override
    public String toString() {
        return "VoteOptionsBean{" +
                "uin=" + uin +
                ", nickName='" + nickName + '\'' +
                ", beSelCnt=" + beSelCnt +
                '}';
    }
}
