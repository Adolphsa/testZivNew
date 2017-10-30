package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 某个问题拉取候选者
 * Created by Administrator on 2017/10/30.
 */

public class QuestionCandidateRespond implements Parcelable {


    /**
     * code : 0
     * msg : succ
     * payload : {"options":[{"uin":100007,"nickName":"蒙萌","beSelCnt":0},{"uin":100002,"nickName":"胡小说","beSelCnt":0},{"uin":100001,"nickName":"鱼干","beSelCnt":0},{"uin":100004,"nickName":"TT","beSelCnt":0}]}
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
        return "QuestionCandidateRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        private List<OptionsBean> options;

        public List<OptionsBean> getOptions() {
            return options;
        }

        public void setOptions(List<OptionsBean> options) {
            this.options = options;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "options=" + options +
                    '}';
        }

        public static class OptionsBean implements Parcelable {
            /**
             * uin : 100007
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

            @Override
            public String toString() {
                return "OptionsBean{" +
                        "uin=" + uin +
                        ", nickName='" + nickName + '\'' +
                        ", beSelCnt=" + beSelCnt +
                        '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.uin);
                dest.writeString(this.nickName);
                dest.writeInt(this.beSelCnt);
            }

            public OptionsBean() {
            }

            protected OptionsBean(Parcel in) {
                this.uin = in.readInt();
                this.nickName = in.readString();
                this.beSelCnt = in.readInt();
            }

            public static final Creator<OptionsBean> CREATOR = new Creator<OptionsBean>() {
                @Override
                public OptionsBean createFromParcel(Parcel source) {
                    return new OptionsBean(source);
                }

                @Override
                public OptionsBean[] newArray(int size) {
                    return new OptionsBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(this.options);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.options = new ArrayList<OptionsBean>();
            in.readList(this.options, OptionsBean.class.getClassLoader());
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

    public QuestionCandidateRespond() {
    }

    protected QuestionCandidateRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<QuestionCandidateRespond> CREATOR = new Parcelable.Creator<QuestionCandidateRespond>() {
        @Override
        public QuestionCandidateRespond createFromParcel(Parcel source) {
            return new QuestionCandidateRespond(source);
        }

        @Override
        public QuestionCandidateRespond[] newArray(int size) {
            return new QuestionCandidateRespond[size];
        }
    };
}
