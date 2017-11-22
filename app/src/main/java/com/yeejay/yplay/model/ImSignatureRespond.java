package com.yeejay.yplay.model;

/**
 * Im签名返回
 *
 * Created by Administrator on 2017/11/22.
 */

public class ImSignatureRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"sig":"eJxlz1tPgzAUwPF3PkXDq0ZLgSEme2gmc3TeuhFvLw2jZVYZt3YTNH53J2ps4nn9-U9OzrsFALCTi*VRmmXVttRM97WwwSmwoX34h3UtOUs1c1v*D0VXy1awNNeiHdDxfR9BaDaSi1LLXP4WEDqeY7jiL2w48s3eftsb*QEyE7ke8DKikzhKVEjabl6Qg6eAnOCH7WJxvLxTGE9nq2uaTtIGncnQjaoCSyzK7DUn84beX3XFKuHnO5LHcZ-1s1yVj5v1FL51Nw29DZ-peGyc1HIjfj5yHNcbBa5v6E60SlblECC4T5ALv8a2PqxPrlddAA__","expireAt":1526034735}
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
        return "ImSignatureRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * sig : eJxlz1tPgzAUwPF3PkXDq0ZLgSEme2gmc3TeuhFvLw2jZVYZt3YTNH53J2ps4nn9-U9OzrsFALCTi*VRmmXVttRM97WwwSmwoX34h3UtOUs1c1v*D0VXy1awNNeiHdDxfR9BaDaSi1LLXP4WEDqeY7jiL2w48s3eftsb*QEyE7ke8DKikzhKVEjabl6Qg6eAnOCH7WJxvLxTGE9nq2uaTtIGncnQjaoCSyzK7DUn84beX3XFKuHnO5LHcZ-1s1yVj5v1FL51Nw29DZ-peGyc1HIjfj5yHNcbBa5v6E60SlblECC4T5ALv8a2PqxPrlddAA__
         * expireAt : 1526034735
         */

        private String sig;
        private int expireAt;

        public String getSig() {
            return sig;
        }

        public void setSig(String sig) {
            this.sig = sig;
        }

        public int getExpireAt() {
            return expireAt;
        }

        public void setExpireAt(int expireAt) {
            this.expireAt = expireAt;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "sig='" + sig + '\'' +
                    ", expireAt=" + expireAt +
                    '}';
        }
    }
}
