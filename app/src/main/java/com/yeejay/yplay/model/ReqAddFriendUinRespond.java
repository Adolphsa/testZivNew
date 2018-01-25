package com.yeejay.yplay.model;

import java.util.List;

/**
 * 获取已点击加好友按钮的列表
 * Created by Adolph on 2018/1/23.
 */

public class ReqAddFriendUinRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"uins":[100437]}
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
        return "ReqAddFriendUinRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        private List<Integer> uins;

        public List<Integer> getUins() {
            return uins;
        }

        public void setUins(List<Integer> uins) {
            this.uins = uins;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "uins=" + uins +
                    '}';
        }
    }
}
