package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户资料
 * Created by Administrator on 2017/11/3.
 */

public class UserInfoResponde implements Parcelable {

    /**
     * code : 0
     * msg : succ
     * payload : {"info":{"uin":100004,"userName":"tt","phone":"13480821913","nickName":"TT","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":2,"grade":0,"schoolId":3,"schoolType":2,"schoolName":"深圳中学","country":"中国","province":"广东","city":"深圳","ts":0,"gemCnt":124,"friendCnt":0}}
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
        return "UserInfoResponde{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        /**
         * info : {"uin":100004,"userName":"tt","phone":"13480821913","nickName":"TT","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","gender":2,"grade":0,"schoolId":3,"schoolType":2,"schoolName":"深圳中学","country":"中国","province":"广东","city":"深圳","ts":0,"gemCnt":124,"friendCnt":0}
         */

        private InfoBean info;

        private int status;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "info=" + info +
                    ", status=" + status +
                    '}';
        }

        public static class InfoBean implements Parcelable {
            /**
             * uin : 100004
             * userName : tt
             * phone : 13480821913
             * nickName : TT
             * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png
             * gender : 2
             * age : 10
             * grade : 0
             * schoolId : 3
             * schoolType : 2
             * schoolName : 深圳中学
             * country : 中国
             * province : 广东
             * city : 深圳
             * ts : 0
             * gemCnt : 124
             * friendCnt : 0
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
            private int gemCnt;
            private int friendCnt;

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

            public int getGemCnt() {
                return gemCnt;
            }

            public void setGemCnt(int gemCnt) {
                this.gemCnt = gemCnt;
            }

            public int getFriendCnt() {
                return friendCnt;
            }

            public void setFriendCnt(int friendCnt) {
                this.friendCnt = friendCnt;
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
                        ", grade=" + grade +
                        ", schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", schoolName='" + schoolName + '\'' +
                        ", country='" + country + '\'' +
                        ", province='" + province + '\'' +
                        ", city='" + city + '\'' +
                        ", ts=" + ts +
                        ", gemCnt=" + gemCnt +
                        ", friendCnt=" + friendCnt +
                        '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.uin);
                dest.writeString(this.userName);
                dest.writeString(this.phone);
                dest.writeString(this.nickName);
                dest.writeString(this.headImgUrl);
                dest.writeInt(this.gender);
                dest.writeInt(this.age);
                dest.writeInt(this.grade);
                dest.writeInt(this.schoolId);
                dest.writeInt(this.schoolType);
                dest.writeString(this.schoolName);
                dest.writeString(this.country);
                dest.writeString(this.province);
                dest.writeString(this.city);
                dest.writeInt(this.ts);
                dest.writeInt(this.gemCnt);
                dest.writeInt(this.friendCnt);
            }

            public InfoBean() {
            }

            protected InfoBean(Parcel in) {
                this.uin = in.readInt();
                this.userName = in.readString();
                this.phone = in.readString();
                this.nickName = in.readString();
                this.headImgUrl = in.readString();
                this.gender = in.readInt();
                this.age = in.readInt();
                this.grade = in.readInt();
                this.schoolId = in.readInt();
                this.schoolType = in.readInt();
                this.schoolName = in.readString();
                this.country = in.readString();
                this.province = in.readString();
                this.city = in.readString();
                this.ts = in.readInt();
                this.gemCnt = in.readInt();
                this.friendCnt = in.readInt();
            }

            public static final Creator<InfoBean> CREATOR = new Creator<InfoBean>() {
                @Override
                public InfoBean createFromParcel(Parcel source) {
                    return new InfoBean(source);
                }

                @Override
                public InfoBean[] newArray(int size) {
                    return new InfoBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.info, flags);
            dest.writeInt(this.status);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.info = in.readParcelable(InfoBean.class.getClassLoader());
            this.status = in.readInt();
        }

        public static final Creator<PayloadBean> CREATOR = new Creator<PayloadBean>() {
            @Override
            public PayloadBean createFromParcel(Parcel source) {
                return new PayloadBean(source);
            }

            @Override
            public PayloadBean[] newArray(int size) {
                return new PayloadBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeParcelable(this.payload, flags);
    }

    public UserInfoResponde() {
    }

    protected UserInfoResponde(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserInfoResponde> CREATOR = new Parcelable.Creator<UserInfoResponde>() {
        @Override
        public UserInfoResponde createFromParcel(Parcel source) {
            return new UserInfoResponde(source);
        }

        @Override
        public UserInfoResponde[] newArray(int size) {
            return new UserInfoResponde[size];
        }
    };
}
