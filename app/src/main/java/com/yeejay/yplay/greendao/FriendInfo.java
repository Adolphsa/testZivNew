package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 朋友资料
 * Created by Administrator on 2017/12/8.
 */

@Entity
public class FriendInfo {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    private int friendUin;
    private String friendName;
    private  String friendHeadUrl;
    private int friendGender;
    private int friendGrade;
    private int friendSchoolId;
    private int friendSchoolType;
    private String friendSchoolName;
    private int ts;
    private String sortKey;
    private String myselfUin;

    @Generated(hash = 1861697744)
    public FriendInfo(Long id, int friendUin, String friendName,
            String friendHeadUrl, int friendGender, int friendGrade,
            int friendSchoolId, int friendSchoolType, String friendSchoolName,
            int ts, String sortKey, String myselfUin) {
        this.id = id;
        this.friendUin = friendUin;
        this.friendName = friendName;
        this.friendHeadUrl = friendHeadUrl;
        this.friendGender = friendGender;
        this.friendGrade = friendGrade;
        this.friendSchoolId = friendSchoolId;
        this.friendSchoolType = friendSchoolType;
        this.friendSchoolName = friendSchoolName;
        this.ts = ts;
        this.sortKey = sortKey;
        this.myselfUin = myselfUin;
    }
    @Generated(hash = 459681999)
    public FriendInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getFriendUin() {
        return this.friendUin;
    }
    public void setFriendUin(int friendUin) {
        this.friendUin = friendUin;
    }
    public String getFriendName() {
        return this.friendName;
    }
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
    public String getFriendHeadUrl() {
        return this.friendHeadUrl;
    }
    public void setFriendHeadUrl(String friendHeadUrl) {
        this.friendHeadUrl = friendHeadUrl;
    }
    public int getFriendGender() {
        return this.friendGender;
    }
    public void setFriendGender(int friendGender) {
        this.friendGender = friendGender;
    }
    public int getFriendGrade() {
        return this.friendGrade;
    }
    public void setFriendGrade(int friendGrade) {
        this.friendGrade = friendGrade;
    }
    public int getFriendSchoolId() {
        return this.friendSchoolId;
    }
    public void setFriendSchoolId(int friendSchoolId) {
        this.friendSchoolId = friendSchoolId;
    }
    public int getFriendSchoolType() {
        return this.friendSchoolType;
    }
    public void setFriendSchoolType(int friendSchoolType) {
        this.friendSchoolType = friendSchoolType;
    }
    public String getFriendSchoolName() {
        return this.friendSchoolName;
    }
    public void setFriendSchoolName(String friendSchoolName) {
        this.friendSchoolName = friendSchoolName;
    }
    public int getTs() {
        return this.ts;
    }
    public void setTs(int ts) {
        this.ts = ts;
    }
    public String getSortKey() {
        return this.sortKey;
    }
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
    public String getMyselfUin() {
        return this.myselfUin;
    }
    public void setMyselfUin(String myselfUin) {
        this.myselfUin = myselfUin;
    }
    



}


