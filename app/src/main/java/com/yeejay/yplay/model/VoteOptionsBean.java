package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 投票请求参数bean
 * Created by Administrator on 2017/10/30.
 */

public class VoteOptionsBean implements Parcelable {

    int uin;
    String nickName;

    public VoteOptionsBean(int uin, String name){
        this.uin = uin;
        this.nickName = name;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public String getName() {
        return nickName;
    }

    public void setName(String name) {
        this.nickName = name;
    }


    @Override
    public String toString() {
        return "VoteOptionsBean{" +
                "uin=" + uin +
                ", name='" + nickName + '\'' +
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
    }

    public VoteOptionsBean() {
    }

    protected VoteOptionsBean(Parcel in) {
        this.uin = in.readInt();
        this.nickName = in.readString();
    }

    public static final Parcelable.Creator<VoteOptionsBean> CREATOR = new Parcelable.Creator<VoteOptionsBean>() {
        @Override
        public VoteOptionsBean createFromParcel(Parcel source) {
            return new VoteOptionsBean(source);
        }

        @Override
        public VoteOptionsBean[] newArray(int size) {
            return new VoteOptionsBean[size];
        }
    };
}
