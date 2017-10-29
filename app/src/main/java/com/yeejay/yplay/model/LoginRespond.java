package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登录返回
 * Created by Administrator on 2017/10/29.
 */

public class LoginRespond implements Parcelable {


    /**
     * code : 0
     * msg : succ
     * payload : {"uin":100008,"token":"T/8JXhHRn/p/gmKbB6PAltejPSJfLATKAuSuZPK+7Nosuyo3TD3qfIYLQKOWf2GsYiEpaHiOQkY2y3yhaWFTzkYee5wcmUYlDhaRU/7rs3K3WzZytpvzuE2aPd9XwtlqdyuwboPDFM5sccm7riU//qV5g0qy1vFU4aat+KDF52e1HXiGqjEt2DtHY1vRxF9pP55dtajHwfAkW/v/K2oGNEWfDuo=","ver":1,"isNewUser":1}
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

    public static class PayloadBean implements Parcelable {
        /**
         * uin : 100008
         * token : T/8JXhHRn/p/gmKbB6PAltejPSJfLATKAuSuZPK+7Nosuyo3TD3qfIYLQKOWf2GsYiEpaHiOQkY2y3yhaWFTzkYee5wcmUYlDhaRU/7rs3K3WzZytpvzuE2aPd9XwtlqdyuwboPDFM5sccm7riU//qV5g0qy1vFU4aat+KDF52e1HXiGqjEt2DtHY1vRxF9pP55dtajHwfAkW/v/K2oGNEWfDuo=
         * ver : 1
         * isNewUser : 1
         */

        private int uin;
        private String token;
        private int ver;
        private int isNewUser;

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.uin);
            dest.writeString(this.token);
            dest.writeInt(this.ver);
            dest.writeInt(this.isNewUser);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.uin = in.readInt();
            this.token = in.readString();
            this.ver = in.readInt();
            this.isNewUser = in.readInt();
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

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "uin=" + uin +
                    ", token='" + token + '\'' +
                    ", ver=" + ver +
                    ", isNewUser=" + isNewUser +
                    '}';
        }
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

    public LoginRespond() {
    }

    protected LoginRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoginRespond> CREATOR = new Parcelable.Creator<LoginRespond>() {
        @Override
        public LoginRespond createFromParcel(Parcel source) {
            return new LoginRespond(source);
        }

        @Override
        public LoginRespond[] newArray(int size) {
            return new LoginRespond[size];
        }
    };

    @Override
    public String toString() {
        return "LoginRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }
}
