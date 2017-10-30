package com.yeejay.yplay.model;

/**
 * 通讯录返回
 * Created by Administrator on 2017/10/29.
 */

public class ContactsRespond {

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
    public String toString() {
        return "ContactsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }
}
