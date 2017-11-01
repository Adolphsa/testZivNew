package com.yeejay.yplay.model;

/**
 * 发送加好友的返回
 * Created by Administrator on 2017/11/1.
 */

public class AddFriendRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"msgId":8}
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
        return "AddFriendRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * msgId : 8
         */

        private int msgId;

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "msgId=" + msgId +
                    '}';
        }
    }
}
