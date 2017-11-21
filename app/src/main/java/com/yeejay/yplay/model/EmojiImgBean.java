package com.yeejay.yplay.model;

/**
 *
 * Created by Administrator on 2017/11/21.
 */

public class EmojiImgBean extends SelecteBean{

    private String fileName;
    private int resId;

    public EmojiImgBean(int resId) {
        this.resId = resId;
    }

    public EmojiImgBean(String fileName, int resId) {
        this.fileName = fileName;
        this.resId = resId;
    }

    public EmojiImgBean(String fileName, int resId, boolean isSelected) {
        this.fileName = fileName;
        this.resId = resId;
        setSelected(isSelected);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
