package com.yeejay.yplay.model;

import java.util.List;

/**
 * ⼀一次拉取多个类型的好友推荐列列表
 * Created by Administrator on 2017/11/8.
 */

public class GetRecommendAll {

    /**
     * code : 0
     * msg : succ
     * payload : {"info":{"totalFromAddrBook":5,"friendsFromAddrBook":[{"uin":100004,"nickName":"TT","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":2,"grade":0,"schoolId":3,"schoolType":2,"schoolName":"深圳中学","phone":"","status":0,"recommendType":1},{"uin":100007,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","gender":1,"grade":1,"schoolId":54406,"schoolType":1,"schoolName":"育才二中","phone":"","status":0,"recommendType":1},{"uin":100010,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509643347138.jpg","gender":1,"grade":1,"schoolId":54400,"schoolType":1,"schoolName":"学府中学","phone":"","status":0,"recommendType":1}],"totalFromSameSchool":1,"friendsFromSameSchool":[{"uin":100001,"nickName":"鱼干","headImgUrl":"1509122247953.png","gender":2,"grade":0,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校","phone":"","status":0,"recommendType":3}],"totalFromNotRegister":272,"friendsFromNotRegister":[{"uin":0,"nickName":"秦波","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13001000165","status":4,"recommendType":2},{"uin":0,"nickName":"腾飞","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13011842435","status":4,"recommendType":2},{"uin":0,"nickName":"朱三哥温州","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13017800370","status":4,"recommendType":2}]}}
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
        return "GetRecommendAll{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * info : {"totalFromAddrBook":5,"friendsFromAddrBook":[{"uin":100004,"nickName":"TT","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":2,"grade":0,"schoolId":3,"schoolType":2,"schoolName":"深圳中学","phone":"","status":0,"recommendType":1},{"uin":100007,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","gender":1,"grade":1,"schoolId":54406,"schoolType":1,"schoolName":"育才二中","phone":"","status":0,"recommendType":1},{"uin":100010,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509643347138.jpg","gender":1,"grade":1,"schoolId":54400,"schoolType":1,"schoolName":"学府中学","phone":"","status":0,"recommendType":1}],"totalFromSameSchool":1,"friendsFromSameSchool":[{"uin":100001,"nickName":"鱼干","headImgUrl":"1509122247953.png","gender":2,"grade":0,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校","phone":"","status":0,"recommendType":3}],"totalFromNotRegister":272,"friendsFromNotRegister":[{"uin":0,"nickName":"秦波","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13001000165","status":4,"recommendType":2},{"uin":0,"nickName":"腾飞","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13011842435","status":4,"recommendType":2},{"uin":0,"nickName":"朱三哥温州","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13017800370","status":4,"recommendType":2}]}
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "info=" + info +
                    '}';
        }

        public static class InfoBean {
            /**
             * totalFromAddrBook : 5
             * friendsFromAddrBook : [{"uin":100004,"nickName":"TT","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":2,"grade":0,"schoolId":3,"schoolType":2,"schoolName":"深圳中学","phone":"","status":0,"recommendType":1},{"uin":100007,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","gender":1,"grade":1,"schoolId":54406,"schoolType":1,"schoolName":"育才二中","phone":"","status":0,"recommendType":1},{"uin":100010,"nickName":"蒙大顺","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509643347138.jpg","gender":1,"grade":1,"schoolId":54400,"schoolType":1,"schoolName":"学府中学","phone":"","status":0,"recommendType":1}]
             * totalFromSameSchool : 1
             * friendsFromSameSchool : [{"uin":100001,"nickName":"鱼干","headImgUrl":"1509122247953.png","gender":2,"grade":0,"schoolId":1,"schoolType":1,"schoolName":"南山第二外国语学校","phone":"","status":0,"recommendType":3}]
             * totalFromNotRegister : 272
             * friendsFromNotRegister : [{"uin":0,"nickName":"秦波","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13001000165","status":4,"recommendType":2},{"uin":0,"nickName":"腾飞","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13011842435","status":4,"recommendType":2},{"uin":0,"nickName":"朱三哥温州","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","phone":"13017800370","status":4,"recommendType":2}]
             */

            private int totalFromAddrBook;
            private int totalFromSameSchool;
            private int totalFromNotRegister;
            private List<FriendsFromAddrBookBean> friendsFromAddrBook;
            private List<FriendsFromSameSchoolBean> friendsFromSameSchool;
            private List<FriendsFromNotRegisterBean> friendsFromNotRegister;

            public int getTotalFromAddrBook() {
                return totalFromAddrBook;
            }

            public void setTotalFromAddrBook(int totalFromAddrBook) {
                this.totalFromAddrBook = totalFromAddrBook;
            }

            public int getTotalFromSameSchool() {
                return totalFromSameSchool;
            }

            public void setTotalFromSameSchool(int totalFromSameSchool) {
                this.totalFromSameSchool = totalFromSameSchool;
            }

            public int getTotalFromNotRegister() {
                return totalFromNotRegister;
            }

            public void setTotalFromNotRegister(int totalFromNotRegister) {
                this.totalFromNotRegister = totalFromNotRegister;
            }

            public List<FriendsFromAddrBookBean> getFriendsFromAddrBook() {
                return friendsFromAddrBook;
            }

            public void setFriendsFromAddrBook(List<FriendsFromAddrBookBean> friendsFromAddrBook) {
                this.friendsFromAddrBook = friendsFromAddrBook;
            }

            public List<FriendsFromSameSchoolBean> getFriendsFromSameSchool() {
                return friendsFromSameSchool;
            }

            public void setFriendsFromSameSchool(List<FriendsFromSameSchoolBean> friendsFromSameSchool) {
                this.friendsFromSameSchool = friendsFromSameSchool;
            }

            public List<FriendsFromNotRegisterBean> getFriendsFromNotRegister() {
                return friendsFromNotRegister;
            }

            public void setFriendsFromNotRegister(List<FriendsFromNotRegisterBean> friendsFromNotRegister) {
                this.friendsFromNotRegister = friendsFromNotRegister;
            }

            @Override
            public String toString() {
                return "InfoBean{" +
                        "totalFromAddrBook=" + totalFromAddrBook +
                        ", totalFromSameSchool=" + totalFromSameSchool +
                        ", totalFromNotRegister=" + totalFromNotRegister +
                        ", friendsFromAddrBook=" + friendsFromAddrBook +
                        ", friendsFromSameSchool=" + friendsFromSameSchool +
                        ", friendsFromNotRegister=" + friendsFromNotRegister +
                        '}';
            }

            public static class FriendsFromAddrBookBean {
                /**
                 * uin : 100004
                 * nickName : TT
                 * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png
                 * gender : 2
                 * grade : 0
                 * schoolId : 3
                 * schoolType : 2
                 * schoolName : 深圳中学
                 * phone :
                 * status : 0
                 * recommendType : 1
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
                private int status;
                private int recommendType;

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

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public int getRecommendType() {
                    return recommendType;
                }

                public void setRecommendType(int recommendType) {
                    this.recommendType = recommendType;
                }

                @Override
                public String toString() {
                    return "FriendsFromAddrBookBean{" +
                            "uin=" + uin +
                            ", nickName='" + nickName + '\'' +
                            ", headImgUrl='" + headImgUrl + '\'' +
                            ", gender=" + gender +
                            ", grade=" + grade +
                            ", schoolId=" + schoolId +
                            ", schoolType=" + schoolType +
                            ", schoolName='" + schoolName + '\'' +
                            ", phone='" + phone + '\'' +
                            ", status=" + status +
                            ", recommendType=" + recommendType +
                            '}';
                }
            }

            public static class FriendsFromSameSchoolBean {
                /**
                 * uin : 100001
                 * nickName : 鱼干
                 * headImgUrl : 1509122247953.png
                 * gender : 2
                 * grade : 0
                 * schoolId : 1
                 * schoolType : 1
                 * schoolName : 南山第二外国语学校
                 * phone :
                 * status : 0
                 * recommendType : 3
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
                private int status;
                private int recommendType;

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

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public int getRecommendType() {
                    return recommendType;
                }

                public void setRecommendType(int recommendType) {
                    this.recommendType = recommendType;
                }

                @Override
                public String toString() {
                    return "FriendsFromSameSchoolBean{" +
                            "uin=" + uin +
                            ", nickName='" + nickName + '\'' +
                            ", headImgUrl='" + headImgUrl + '\'' +
                            ", gender=" + gender +
                            ", grade=" + grade +
                            ", schoolId=" + schoolId +
                            ", schoolType=" + schoolType +
                            ", schoolName='" + schoolName + '\'' +
                            ", phone='" + phone + '\'' +
                            ", status=" + status +
                            ", recommendType=" + recommendType +
                            '}';
                }
            }

            public static class FriendsFromNotRegisterBean {
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
                 * status : 4
                 * recommendType : 2
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
                private int status;
                private int recommendType;

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

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public int getRecommendType() {
                    return recommendType;
                }

                public void setRecommendType(int recommendType) {
                    this.recommendType = recommendType;
                }

                @Override
                public String toString() {
                    return "FriendsFromNotRegisterBean{" +
                            "uin=" + uin +
                            ", nickName='" + nickName + '\'' +
                            ", headImgUrl='" + headImgUrl + '\'' +
                            ", gender=" + gender +
                            ", grade=" + grade +
                            ", schoolId=" + schoolId +
                            ", schoolType=" + schoolType +
                            ", schoolName='" + schoolName + '\'' +
                            ", phone='" + phone + '\'' +
                            ", status=" + status +
                            ", recommendType=" + recommendType +
                            '}';
                }
            }
        }
    }
}
