package com.yeejay.yplay.model;

/**
 * 图片上传body
 * Created by Administrator on 2017/10/30.
 */

public class ImageUploadBody {

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
