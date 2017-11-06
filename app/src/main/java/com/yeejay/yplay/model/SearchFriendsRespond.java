package com.yeejay.yplay.model;

import java.util.List;

/**
 * 搜索好友返回
 * Created by Administrator on 2017/11/6.
 */

public class SearchFriendsRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"friends":[{"uin":100008,"nickName":"拉卡","headImgUrl":"1509953589095.jpg","gender":2,"grade":3,"schoolId":54404,"schoolType":1,"schoolName":"荔香中学"}]}
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

    @Override
    public String toString() {
        return "SearchFriendsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        private List<FriendsBean> friends;

        public List<FriendsBean> getFriends() {
            return friends;
        }

        public void setFriends(List<FriendsBean> friends) {
            this.friends = friends;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "friends=" + friends +
                    '}';
        }

        public static class FriendsBean {
            /**
             * uin : 100008
             * nickName : 拉卡
             * headImgUrl : 1509953589095.jpg
             * gender : 2
             * grade : 3
             * schoolId : 54404
             * schoolType : 1
             * schoolName : 荔香中学
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int gender;
            private int grade;
            private int schoolId;
            private int schoolType;
            private String schoolName;

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
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

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public int getGrade() {
                return grade;
            }

            public void setGrade(int grade) {
                this.grade = grade;
            }

            public int getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(int schoolId) {
                this.schoolId = schoolId;
            }

            public int getSchoolType() {
                return schoolType;
            }

            public void setSchoolType(int schoolType) {
                this.schoolType = schoolType;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
            }

            @Override
            public String toString() {
                return "FriendsBean{" +
                        "uin=" + uin +
                        ", nickName='" + nickName + '\'' +
                        ", headImgUrl='" + headImgUrl + '\'' +
                        ", gender=" + gender +
                        ", grade=" + grade +
                        ", schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", schoolName='" + schoolName + '\'' +
                        '}';
            }
        }
    }
}
