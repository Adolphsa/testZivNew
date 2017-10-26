package com.yeejay.yplay.model;

/**
 * 学校信息类
 * Created by Administrator on 2017/10/25.
 */

public class SchoolInfoBean {

    private String schoolName;
    private String schoolNumber;
    private String schoolAddress;
    private String isJoin;

    public SchoolInfoBean(String schoolName,String schoolNumber,String schoolAddress,String isJoin){
        this.schoolName = schoolName;
        this.schoolNumber = schoolNumber;
        this.schoolAddress = schoolAddress;
        this.isJoin = isJoin;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(String schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(String isJoin) {
        this.isJoin = isJoin;
    }
}
