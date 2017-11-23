package com.yeejay.yplay.model;

/**
 * DataType为2的情况
 * Created by Administrator on 2017/11/24.
 */

public class MsgContent2 {

    private String content;

    private SenderInfoBean senderinfo;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SenderInfoBean getSenderinfo() {
        return senderinfo;
    }

    public void setSenderinfo(SenderInfoBean senderinfo) {
        this.senderinfo = senderinfo;
    }

    @Override
    public String toString() {
        return "MsgContent2{" +
                "content='" + content + '\'' +
                ", senderinfo=" + senderinfo +
                '}';
    }

    public static class SenderInfoBean {
        /**
         * uin : 100032
         * userName : frankshi
         * phone : 13590457127
         * nickName : ��
         * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1510168943467.png
         * gender : 2
         * age : 15
         * grade : 1
         * schoolId : 54400
         * schoolType : 2
         * schoolName : 南头中学
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
            return "SenderInfoBean{" +
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
