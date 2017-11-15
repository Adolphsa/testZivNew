package com.yeejay.yplay.model;

import java.util.List;

/**
 * 拉取好友列表
 * Created by Administrator on 2017/11/2.
 */

public class GetRecommendsRespond {
    /**
     * code : 0
     * msg : succ
     * payload : {"total":273,"friends":[{"uin":0,"nickName":"秦波","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13001000165","status":4},{"uin":0,"nickName":"腾飞","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13011842435","status":4},{"uin":0,"nickName":"朱三哥温州","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13017800370","status":4},{"uin":0,"nickName":"刘郁强","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13156090909","status":4},{"uin":0,"nickName":"面试者孙宾","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13168771430","status":4},{"uin":0,"nickName":"王葳","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13186095680","status":4},{"uin":0,"nickName":"杨朋涛","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13201619105","status":4},{"uin":0,"nickName":"老爸","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13237291629","status":4},{"uin":0,"nickName":"唐孟松","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13249068253","status":4},{"uin":0,"nickName":"合租李志勇","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13260289660","status":4}]}
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
        return "GetRecommendsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * total : 273
         * friends : [{"uin":0,"nickName":"秦波","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13001000165","status":4},{"uin":0,"nickName":"腾飞","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13011842435","status":4},{"uin":0,"nickName":"朱三哥温州","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13017800370","status":4},{"uin":0,"nickName":"刘郁强","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13156090909","status":4},{"uin":0,"nickName":"面试者孙宾","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13168771430","status":4},{"uin":0,"nickName":"王葳","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13186095680","status":4},{"uin":0,"nickName":"杨朋涛","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13201619105","status":4},{"uin":0,"nickName":"老爸","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13237291629","status":4},{"uin":0,"nickName":"唐孟松","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13249068253","status":4},{"uin":0,"nickName":"合租李志勇","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13260289660","status":4}]
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
             * uin : 0
             * nickName : 秦波
             * headImgUrl :
             * gender : 0
             * grade : 0
             * schoolId : 0
             * schoolType : 0
             * schoolName :
             * phone : 13001000165
             * recommendType : 1
             * recommendDesc ："同校好友"
             * status : 4
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int gender;
            private int grade;
            private int schoolId;
            private int schoolType;
            private String schoolName;
            private String phone;
            private int recommendType;
            private String recommendDesc;
            private int status;

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

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public void setRecommendType(int recommendType) {
                this.recommendType = recommendType;
            }

            public int getRecommendType() {
                return recommendType;
            }

            public void setRecommendDesc(String recommendDesc) {
                this.recommendDesc = recommendDesc;
            }

            public String getRecommendDesc() {
                return recommendDesc;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            @Override
            public String  toString() {
                return "FriendsBean{" +
                        "uin=" + uin +
                        ", nickName='" + nickName + '\'' +
                        ", headImgUrl='" + headImgUrl + '\'' +
                        ", gender=" + gender +
                        ", grade=" + grade +
                        ", schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", schoolName='" + schoolName + '\'' +
                        ", phone='" + phone + '\'' +
                        ", recommendType=" + recommendType +
                        ", recommendDesc='" + recommendDesc + '\'' +
                        ", status=" + status +
                        '}';
            }
        }
    }
}
