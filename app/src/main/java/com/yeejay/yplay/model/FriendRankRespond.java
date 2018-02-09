package com.yeejay.yplay.model;

import java.util.List;

/**
 * 好友排行返回
 * Created by Adolph on 2018/2/7.
 */

public class FriendRankRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"Uin":100339,"nickName":"p","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg","schoolName":"育才二中","schoolType":1,"grade":2,"cnt":1,"DiamondSet":[10],"FriendUin":100244,"friendNickName":"芳草萋萋鹦鹉洲","friendHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1516588209072.png","friendSchoolName":"育才二中","friendSchoolType":1,"friendGrade":3,"friendCnt":1,"friendDiamondSet":[84]}
     */

    private int code;
    private String msg;
    private PayloadBean payload;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PayloadBean getPayload() {
        return payload;
    }

    public void setPayload(PayloadBean payload) {
        this.payload = payload;
    }

    public static class PayloadBean {
        /**
         * Uin : 100339
         * nickName : p
         * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg
         * schoolName : 育才二中
         * schoolType : 1
         * grade : 2
         * cnt : 1
         * DiamondSet : [10]
         * FriendUin : 100244
         * friendNickName : 芳草萋萋鹦鹉洲
         * friendHeadImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1516588209072.png
         * friendSchoolName : 育才二中
         * friendSchoolType : 1
         * friendGrade : 3
         * friendCnt : 1
         * friendDiamondSet : [84]
         */

        private int Uin;
        private String nickName;
        private String headImgUrl;
        private String schoolName;
        private int schoolType;
        private int grade;
        private int cnt;
        private int FriendUin;
        private String friendNickName;
        private String friendHeadImgUrl;
        private String friendSchoolName;
        private int friendSchoolType;
        private int friendGrade;
        private int friendCnt;
        private List<Integer> DiamondSet;
        private List<Integer> friendDiamondSet;

        public int getUin() {
            return Uin;
        }

        public void setUin(int Uin) {
            this.Uin = Uin;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getSchoolType() {
            return schoolType;
        }

        public void setSchoolType(int schoolType) {
            this.schoolType = schoolType;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public int getFriendUin() {
            return FriendUin;
        }

        public void setFriendUin(int FriendUin) {
            this.FriendUin = FriendUin;
        }

        public String getFriendNickName() {
            return friendNickName;
        }

        public void setFriendNickName(String friendNickName) {
            this.friendNickName = friendNickName;
        }

        public String getFriendHeadImgUrl() {
            return friendHeadImgUrl;
        }

        public void setFriendHeadImgUrl(String friendHeadImgUrl) {
            this.friendHeadImgUrl = friendHeadImgUrl;
        }

        public String getFriendSchoolName() {
            return friendSchoolName;
        }

        public void setFriendSchoolName(String friendSchoolName) {
            this.friendSchoolName = friendSchoolName;
        }

        public int getFriendSchoolType() {
            return friendSchoolType;
        }

        public void setFriendSchoolType(int friendSchoolType) {
            this.friendSchoolType = friendSchoolType;
        }

        public int getFriendGrade() {
            return friendGrade;
        }

        public void setFriendGrade(int friendGrade) {
            this.friendGrade = friendGrade;
        }

        public int getFriendCnt() {
            return friendCnt;
        }

        public void setFriendCnt(int friendCnt) {
            this.friendCnt = friendCnt;
        }

        public List<Integer> getDiamondSet() {
            return DiamondSet;
        }

        public void setDiamondSet(List<Integer> DiamondSet) {
            this.DiamondSet = DiamondSet;
        }

        public List<Integer> getFriendDiamondSet() {
            return friendDiamondSet;
        }

        public void setFriendDiamondSet(List<Integer> friendDiamondSet) {
            this.friendDiamondSet = friendDiamondSet;
        }
    }
}
