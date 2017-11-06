package com.yeejay.yplay.model;

/**
 * 登录返回
 * Created by Administrator on 2017/10/29.
 */

public class LoginRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"uin":100008,"token":"oZ9vc6nmkfC4yPqF/PdikvI9oP9wN7e5uZABqGVtjjTg5KMlcXHk/QsDfw8n35GwjRD7rArOlOpqyR8zhb00yay80oiG/VEELvfq1s/LkShu4fa+3Tew7zhM7OsJAp8KDxKikHrDjsjpxvfnaPWfUiDPQaMnLnbtIBd6pODeltXINzF5Kg==","ver":1,"isNewUser":0,"info":{"uin":100008,"userName":"adolph","phone":"13480995624","nickName":"拉卡","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509953589095.jpg","gender":2,"grade":3,"schoolId":54404,"schoolType":1,"schoolName":"荔香中学","country":"中国","province":"广东","city":"深圳市","ts":0}}
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
        return "LoginRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * uin : 100008
         * token : oZ9vc6nmkfC4yPqF/PdikvI9oP9wN7e5uZABqGVtjjTg5KMlcXHk/QsDfw8n35GwjRD7rArOlOpqyR8zhb00yay80oiG/VEELvfq1s/LkShu4fa+3Tew7zhM7OsJAp8KDxKikHrDjsjpxvfnaPWfUiDPQaMnLnbtIBd6pODeltXINzF5Kg==
         * ver : 1
         * isNewUser : 0
         * info : {"uin":100008,"userName":"adolph","phone":"13480995624","nickName":"拉卡","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509953589095.jpg","gender":2,"grade":3,"schoolId":54404,"schoolType":1,"schoolName":"荔香中学","country":"中国","province":"广东","city":"深圳市","ts":0}
         */

        private int uin;
        private String token;
        private int ver;
        private int isNewUser;
        private InfoBean info;

        public int getUin() {
            return uin;
        }

        public void setUin(int uin) {
            this.uin = uin;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getVer() {
            return ver;
        }

        public void setVer(int ver) {
            this.ver = ver;
        }

        public int getIsNewUser() {
            return isNewUser;
        }

        public void setIsNewUser(int isNewUser) {
            this.isNewUser = isNewUser;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "uin=" + uin +
                    ", token='" + token + '\'' +
                    ", ver=" + ver +
                    ", isNewUser=" + isNewUser +
                    ", info=" + info +
                    '}';
        }

        public static class InfoBean {
            /**
             * uin : 100008
             * userName : adolph
             * phone : 13480995624
             * nickName : 拉卡
             * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509953589095.jpg
             * gender : 2
             * age : 0
             * grade : 3
             * schoolId : 54404
             * schoolType : 1
             * schoolName : 荔香中学
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

            public void setAge(int age) {
                this.age = age;
            }

            public int getAge() {
                return age;
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
                return "InfoBean{" +
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
