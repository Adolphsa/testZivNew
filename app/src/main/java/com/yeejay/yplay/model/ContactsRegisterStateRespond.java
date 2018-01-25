package com.yeejay.yplay.model;

import java.util.List;

/**
 * 查询通讯录注册状态的返回
 * Created by Adolph on 2018/1/23.
 */

public class ContactsRegisterStateRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"infos":[{"uin":100328,"userName":"frankshi","phone":"13590457127","nickName":"frankshi","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1514887797855.png","gender":2,"age":19,"grade":2,"schoolId":54400,"schoolType":1,"schoolName":"学府中学","country":"中国","province":"广东","city":"深圳市","ts":0}]}
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
        return "ContactsRegisterStateRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        private List<InfosBean> infos;

        public List<InfosBean> getInfos() {
            return infos;
        }

        public void setInfos(List<InfosBean> infos) {
            this.infos = infos;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "infos=" + infos +
                    '}';
        }

        public static class InfosBean {
            /**
             * uin : 100328
             * userName : frankshi
             * phone : 13590457127
             * nickName : frankshi
             * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1514887797855.png
             * gender : 2
             * age : 19
             * grade : 2
             * schoolId : 54400
             * schoolType : 1
             * schoolName : 学府中学
             * country : 中国
             * province : 广东
             * city : 深圳市
             * ts : 0
             */

            private int uin;
            private String userName;
            private String phone;
            private String nickName;
            private String headImgUrl;
            private int gender;
            private int age;
            private int grade;
            private int schoolId;
            private int schoolType;
            private String schoolName;
            private String country;
            private String province;
            private String city;
            private int ts;

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
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

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
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

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public int getTs() {
                return ts;
            }

            public void setTs(int ts) {
                this.ts = ts;
            }

            @Override
            public String toString() {
                return "InfosBean{" +
                        "uin=" + uin +
                        ", userName='" + userName + '\'' +
                        ", phone='" + phone + '\'' +
                        ", nickName='" + nickName + '\'' +
                        ", headImgUrl='" + headImgUrl + '\'' +
                        ", gender=" + gender +
                        ", age=" + age +
                        ", grade=" + grade +
                        ", schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", schoolName='" + schoolName + '\'' +
                        ", country='" + country + '\'' +
                        ", province='" + province + '\'' +
                        ", city='" + city + '\'' +
                        ", ts=" + ts +
                        '}';
            }
        }
    }
}
