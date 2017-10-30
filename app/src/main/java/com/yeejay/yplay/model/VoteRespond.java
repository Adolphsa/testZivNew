package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 投票返回
 * Created by Administrator on 2017/10/30.
 */

public class VoteRespond implements Parcelable {

    /**
     * code : 0
     * msg : succ
     * payload : {"voteRecordId":157}
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
        return "VoteRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        /**
         * voteRecordId : 157
         */

        private int voteRecordId;

        public int getVoteRecordId() {
            return voteRecordId;
        }

        public void setVoteRecordId(int voteRecordId) {
            this.voteRecordId = voteRecordId;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "voteRecordId=" + voteRecordId +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.voteRecordId);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.voteRecordId = in.readInt();
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

    public VoteRespond() {
    }

    protected VoteRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<VoteRespond> CREATOR = new Parcelable.Creator<VoteRespond>() {
        @Override
        public VoteRespond createFromParcel(Parcel source) {
            return new VoteRespond(source);
        }

        @Override
        public VoteRespond[] newArray(int size) {
            return new VoteRespond[size];
        }
    };
}
