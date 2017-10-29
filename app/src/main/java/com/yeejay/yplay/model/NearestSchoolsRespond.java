package com.yeejay.yplay.model;

import java.util.List;

/**
 * 最近学校列表
 * Created by Administrator on 2017/10/29.
 */

public class NearestSchoolsRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"schools":[{"schoolId":1,"schoolType":1,"school":"南山第二外国语学校","country":"中国","province":"广东","city":"深圳","latitude":22.52033,"longitude":113.93923,"status":0,"ts":1508901982,"memberCnt":2},{"schoolId":2,"schoolType":1,"school":"育才二中","country":"中国","province":"广东","city":"深圳","latitude":22.496393,"longitude":113.929768,"status":0,"ts":1508901982,"memberCnt":0}]}
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
        return "NearestSchoolsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        private List<SchoolsBean> schools;

        public List<SchoolsBean> getSchools() {
            return schools;
        }

        public void setSchools(List<SchoolsBean> schools) {
            this.schools = schools;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "schools=" + schools +
                    '}';
        }

        public static class SchoolsBean {
            /**
             * schoolId : 1
             * schoolType : 1
             * school : 南山第二外国语学校
             * country : 中国
             * province : 广东
             * city : 深圳
             * latitude : 22.52033
             * longitude : 113.93923
             * status : 0
             * ts : 1508901982
             * memberCnt : 2
             */

            private int schoolId;
            private int schoolType;
            private String school;
            private String country;
            private String province;
            private String city;
            private double latitude;
            private double longitude;
            private int status;
            private int ts;
            private int memberCnt;

            public int getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(int schoolId) {
                this.schoolId = schoolId;
            }

            public int getSchoolType() {
                return schoolType;
            }

            public void setSchoolType(int schoolType) {
                this.schoolType = schoolType;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getTs() {
                return ts;
            }

            public void setTs(int ts) {
                this.ts = ts;
            }

            public int getMemberCnt() {
                return memberCnt;
            }

            public void setMemberCnt(int memberCnt) {
                this.memberCnt = memberCnt;
            }

            @Override
            public String toString() {
                return "SchoolsBean{" +
                        "schoolId=" + schoolId +
                        ", schoolType=" + schoolType +
                        ", school='" + school + '\'' +
                        ", country='" + country + '\'' +
                        ", province='" + province + '\'' +
                        ", city='" + city + '\'' +
                        ", latitude=" + latitude +
                        ", longitude=" + longitude +
                        ", status=" + status +
                        ", ts=" + ts +
                        ", memberCnt=" + memberCnt +
                        '}';
            }
        }
    }
}
