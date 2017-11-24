package com.yeejay.yplay.model;

import java.util.List;

/**
 * 会话消息类型为1
 * Created by Administrator on 2017/11/24.
 */

public class MsgContent1 {

    /**
     * QuestionInfo : {"qid":12637,"qtext":"汽车品牌专家","qiconUrl":"http://yplay-1253229355.image.myqcloud.com/qicon/4.png","status":0,"ts":1509940385}
     * options : [{"uin":100035,"nickName":"蒙萌","beSelCnt":0},{"uin":100141,"nickName":"卡卡","beSelCnt":0},{"uin":100034,"nickName":"dyj_哇哦","beSelCnt":0},{"uin":100062,"nickName":"advice","beSelCnt":0}]
     * selIndex : 2
     * senderInfo : {"uin":100032,"userName":"frankshi","phone":"13590457127","nickName":"��","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1510168943467.png","gender":2,"age":15,"grade":1,"schoolId":54400,"schoolType":2,"schoolName":"南头中学","country":"中国","province":"广东","city":"深圳市","ts":0}
     */

    private QuestionInfoBean QuestionInfo;
    private int selIndex;
    private SenderInfoBean senderInfo;
    private List<OptionsBean> options;
    private ReceiverInfoBean receiverInfo;

    public QuestionInfoBean getQuestionInfo() {
        return QuestionInfo;
    }

    public void setQuestionInfo(QuestionInfoBean QuestionInfo) {
        this.QuestionInfo = QuestionInfo;
    }

    public int getSelIndex() {
        return selIndex;
    }

    public void setSelIndex(int selIndex) {
        this.selIndex = selIndex;
    }

    public SenderInfoBean getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(SenderInfoBean senderInfo) {
        this.senderInfo = senderInfo;
    }

    public List<OptionsBean> getOptions() {
        return options;
    }

    public void setOptions(List<OptionsBean> options) {
        this.options = options;
    }

    public ReceiverInfoBean getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(ReceiverInfoBean receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "QuestionInfo=" + QuestionInfo +
                ", selIndex=" + selIndex +
                ", senderInfo=" + senderInfo +
                ", options=" + options +
                '}';
    }

    public static class QuestionInfoBean {
        /**
         * qid : 12637
         * qtext : 汽车品牌专家
         * qiconUrl : http://yplay-1253229355.image.myqcloud.com/qicon/4.png
         * status : 0
         * ts : 1509940385
         */

        private int qid;
        private String qtext;
        private String qiconUrl;
        private int status;
        private int ts;

        public int getQid() {
            return qid;
        }

        public void setQid(int qid) {
            this.qid = qid;
        }

        public String getQtext() {
            return qtext;
        }

        public void setQtext(String qtext) {
            this.qtext = qtext;
        }

        public String getQiconUrl() {
            return qiconUrl;
        }

        public void setQiconUrl(String qiconUrl) {
            this.qiconUrl = qiconUrl;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getTs() {
            return ts;
        }

        public void setTs(int ts) {
            this.ts = ts;
        }

        @Override
        public String toString() {
            return "QuestionInfoBean{" +
                    "qid=" + qid +
                    ", qtext='" + qtext + '\'' +
                    ", qiconUrl='" + qiconUrl + '\'' +
                    ", status=" + status +
                    ", ts=" + ts +
                    '}';
        }
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

    public static class OptionsBean {
        /**
         * uin : 100035
         * nickName : 蒙萌
         * beSelCnt : 0
         */

        private int uin;
        private String nickName;
        private int beSelCnt;

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

        public int getBeSelCnt() {
            return beSelCnt;
        }

        public void setBeSelCnt(int beSelCnt) {
            this.beSelCnt = beSelCnt;
        }
    }

    public static class ReceiverInfoBean {
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
