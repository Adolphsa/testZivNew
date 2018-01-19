package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XJG on 2018/1/17.
 */

public class SubmitQueryDetailRespond implements Parcelable {
    /**
     * code : 0
     * msg : succ
     * payload : {"total":17,"infos":[{"uin":100238,"nickName":"","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","votedCnt":2},{"uin":100290,"nickName":"李琦","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1516152111344.png","gender":2,"grade":1,"schoolId":78629,"schoolType":3,"schoolName":"中国地质大学（武汉）","votedCnt":2},{"uin":100203,"nickName":"董事长","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1513133633600.png","gender":2,"grade":1,"schoolId":78806,"schoolType":3,"schoolName":"深圳大学","votedCnt":1}]}
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
        return "SubmitQueryDetailRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        /**
         * total : 17
         * infos : [{"uin":100238,"nickName":"","headImgUrl":"","gender":0,"grade":0,"schoolId":0,"schoolType":0,"schoolName":"","votedCnt":2},{"uin":100290,"nickName":"李琦","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1516152111344.png","gender":2,"grade":1,"schoolId":78629,"schoolType":3,"schoolName":"中国地质大学（武汉）","votedCnt":2},{"uin":100203,"nickName":"董事长","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1513133633600.png","gender":2,"grade":1,"schoolId":78806,"schoolType":3,"schoolName":"深圳大学","votedCnt":1}]
         */

        private int total;
        private List<InfosBean> infos;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<InfosBean> getInfos() {
            return infos;
        }

        public void setInfos(List<InfosBean> infos) {
            this.infos = infos;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "total=" + total +
                    ", infos=" + infos +
                    '}';
        }

        public static class InfosBean implements Parcelable {
            /**
             * uin : 100238
             * nickName :
             * headImgUrl :
             * gender : 0
             * grade : 0
             * schoolId : 0
             * schoolType : 0
             * schoolName :
             * votedCnt : 2
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int gender;
            private int grade;
            private int schoolId;
            private int schoolType;
            private String schoolName;
            private int votedCnt;

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

            public int getVotedCnt() {
                return votedCnt;
            }

            public void setVotedCnt(int votedCnt) {
                this.votedCnt = votedCnt;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.uin);
                dest.writeString(this.nickName);
                dest.writeString(this.headImgUrl);
                dest.writeInt(this.gender);
                dest.writeInt(this.grade);
                dest.writeInt(this.schoolId);
                dest.writeInt(this.schoolType);
                dest.writeString(this.schoolName);
                dest.writeInt(this.votedCnt);
            }

            public InfosBean() {
            }

            protected InfosBean(Parcel in) {
                this.uin = in.readInt();
                this.nickName = in.readString();
                this.headImgUrl = in.readString();
                this.gender = in.readInt();
                this.grade = in.readInt();
                this.schoolId = in.readInt();
                this.schoolType = in.readInt();
                this.schoolName = in.readString();
                this.votedCnt = in.readInt();
            }

            public static final Creator<InfosBean> CREATOR = new Creator<InfosBean>() {
                @Override
                public InfosBean createFromParcel(Parcel source) {
                    return new InfosBean(source);
                }

                @Override
                public InfosBean[] newArray(int size) {
                    return new InfosBean[size];
                }
            };

            @Override
            public String toString() {
                return "InfosBean{" +
                        "uin=" + uin +
                        ", nickName='" + nickName + '\'' +
                        ", headImgUrl='" + headImgUrl + '\'' +
                        ", gender=" + gender +
                        ", grade=" + grade +
                        ", schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", schoolName='" + schoolName + '\'' +
                        ", votedCnt=" + votedCnt +
                        '}';
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
            dest.writeList(this.infos);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.total = in.readInt();
            this.infos = new ArrayList<InfosBean>();
            in.readList(this.infos, InfosBean.class.getClassLoader());
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

    public SubmitQueryDetailRespond() {
    }

    protected SubmitQueryDetailRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SubmitQueryDetailRespond> CREATOR = new Parcelable.Creator<SubmitQueryDetailRespond>() {
        @Override
        public SubmitQueryDetailRespond createFromParcel(Parcel source) {
            return new SubmitQueryDetailRespond(source);
        }

        @Override
        public SubmitQueryDetailRespond[] newArray(int size) {
            return new SubmitQueryDetailRespond[size];
        }
    };
}
