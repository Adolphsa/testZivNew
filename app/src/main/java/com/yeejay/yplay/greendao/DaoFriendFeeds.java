package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 好友动态数据库
 * Created by Administrator on 2017/10/31.
 */

@Entity
public class DaoFriendFeeds {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    private long ts;
    private int uin;
    private int voteRecordId;
    private int friendUin;
    private String friendNickName;
    private int friendGender;
    private String friendHeadImgUrl;
    private int qid;
    private String qtext;
    private String qiconUrl;
    private int voteFromUin;
    private int voteFromGender;
    private int voteFromSchoolId;
    private int voteFromSchoolType;
    private String voteFromSchoolName;
    private int voteFromGrade;
    private boolean isReaded;
    @Generated(hash = 2021960420)
    public DaoFriendFeeds(Long id, long ts, int uin, int voteRecordId,
            int friendUin, String friendNickName, int friendGender,
            String friendHeadImgUrl, int qid, String qtext, String qiconUrl,
            int voteFromUin, int voteFromGender, int voteFromSchoolId,
            int voteFromSchoolType, String voteFromSchoolName, int voteFromGrade,
            boolean isReaded) {
        this.id = id;
        this.ts = ts;
        this.uin = uin;
        this.voteRecordId = voteRecordId;
        this.friendUin = friendUin;
        this.friendNickName = friendNickName;
        this.friendGender = friendGender;
        this.friendHeadImgUrl = friendHeadImgUrl;
        this.qid = qid;
        this.qtext = qtext;
        this.qiconUrl = qiconUrl;
        this.voteFromUin = voteFromUin;
        this.voteFromGender = voteFromGender;
        this.voteFromSchoolId = voteFromSchoolId;
        this.voteFromSchoolType = voteFromSchoolType;
        this.voteFromSchoolName = voteFromSchoolName;
        this.voteFromGrade = voteFromGrade;
        this.isReaded = isReaded;
    }
    @Generated(hash = 1623157914)
    public DaoFriendFeeds() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTs() {
        return this.ts;
    }
    public void setTs(long ts) {
        this.ts = ts;
    }
    public int getVoteRecordId() {
        return this.voteRecordId;
    }
    public void setVoteRecordId(int voteRecordId) {
        this.voteRecordId = voteRecordId;
    }
    public int getFriendUin() {
        return this.friendUin;
    }
    public void setFriendUin(int friendUin) {
        this.friendUin = friendUin;
    }
    public String getFriendNickName() {
        return this.friendNickName;
    }
    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }
    public int getFriendGender() {
        return this.friendGender;
    }
    public void setFriendGender(int friendGender) {
        this.friendGender = friendGender;
    }
    public String getFriendHeadImgUrl() {
        return this.friendHeadImgUrl;
    }
    public void setFriendHeadImgUrl(String friendHeadImgUrl) {
        this.friendHeadImgUrl = friendHeadImgUrl;
    }
    public int getQid() {
        return this.qid;
    }
    public void setQid(int qid) {
        this.qid = qid;
    }
    public String getQtext() {
        return this.qtext;
    }
    public void setQtext(String qtext) {
        this.qtext = qtext;
    }
    public String getQiconUrl() {
        return this.qiconUrl;
    }
    public void setQiconUrl(String qiconUrl) {
        this.qiconUrl = qiconUrl;
    }
    public int getVoteFromUin() {
        return this.voteFromUin;
    }
    public void setVoteFromUin(int voteFromUin) {
        this.voteFromUin = voteFromUin;
    }
    public int getVoteFromGender() {
        return this.voteFromGender;
    }
    public void setVoteFromGender(int voteFromGender) {
        this.voteFromGender = voteFromGender;
    }
    public int getVoteFromSchoolId() {
        return this.voteFromSchoolId;
    }
    public void setVoteFromSchoolId(int voteFromSchoolId) {
        this.voteFromSchoolId = voteFromSchoolId;
    }
    public int getVoteFromSchoolType() {
        return this.voteFromSchoolType;
    }
    public void setVoteFromSchoolType(int voteFromSchoolType) {
        this.voteFromSchoolType = voteFromSchoolType;
    }
    public String getVoteFromSchoolName() {
        return this.voteFromSchoolName;
    }
    public void setVoteFromSchoolName(String voteFromSchoolName) {
        this.voteFromSchoolName = voteFromSchoolName;
    }
    public int getVoteFromGrade() {
        return this.voteFromGrade;
    }
    public void setVoteFromGrade(int voteFromGrade) {
        this.voteFromGrade = voteFromGrade;
    }
    public boolean getIsReaded() {
        return this.isReaded;
    }
    public void setIsReaded(boolean isReaded) {
        this.isReaded = isReaded;
    }
    public int getUin() {
        return this.uin;
    }
    public void setUin(int uin) {
        this.uin = uin;
    }
}
