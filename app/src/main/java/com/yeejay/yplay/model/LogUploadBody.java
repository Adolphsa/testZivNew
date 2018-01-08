package com.yeejay.yplay.model;

/**
 * Log上传body
 * Created by xjg on 2018/01/08.
 */

public class LogUploadBody {

    String op;
    byte[] filecontent;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public byte[] getFilecontent() {
        return filecontent;
    }

    public void setFilecontent(byte[] filecontent) {
        this.filecontent = filecontent;
    }
}