package com.yeejay.yplay.model;

import java.util.List;

/**
 * 获取我的好友列表
 * Created by Administrator on 2017/11/3.
 */

public class FriendsListRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"total":2,"friends":[{"uin":100002,"nickName":"胡小说","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":1,"grade":3,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校"},{"uin":100007,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","gender":2,"grade":1,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校"}]}
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
        return "FriendsListRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * total : 2
         * friends : [{"uin":100002,"nickName":"胡小说","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":1,"grade":3,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校"},{"uin":100007,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","gender":2,"grade":1,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校"}]
         */

        private int total;
        private List<FriendsBean> friends;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<FriendsBean> getFriends() {
            return friends;
        }

        public void setFriends(List<FriendsBean> friends) {
            this.friends = friends;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "total=" + total +
                    ", friends=" + friends +
                    '}';
        }

        public static class FriendsBean {
            /**
             * uin : 100002
             * nickName : 胡小说
             * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png
             * gender : 1
             * grade : 3
             * schoolId : 1
             * schoolType : 1
             * schoolName : 南山第二外国语学校
             * int ts : 好友的时间
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int gender;
            private int grade;
            private int schoolId;
            private int schoolType;
            private String schoolName;
            private int ts;

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

            public int getTs() {
                return ts;
            }

            public void setTs(int ts) {
                this.ts = ts;
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
                        ", ts=" + ts +
                        '}';
            }
        }
    }
}
