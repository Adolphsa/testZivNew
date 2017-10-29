package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 发送短信返回
 * Created by Administrator on 2017/10/29.
 */

public class SendSmsRespond implements Parcelable {

    /**
     * code : 0
     * msg : succ
     * payload : null
     */

    private int code;
    private String msg;
    private Object payload;

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

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeParcelable((Parcelable) this.payload, flags);
    }

    public SendSmsRespond() {
    }

    protected SendSmsRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<SendSmsRespond> CREATOR = new Parcelable.Creator<SendSmsRespond>() {
        @Override
        public SendSmsRespond createFromParcel(Parcel source) {
            return new SendSmsRespond(source);
        }

        @Override
        public SendSmsRespond[] newArray(int size) {
            return new SendSmsRespond[size];
        }
    };

    @Override
    public String toString() {
        return "SendSmsResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }
}
