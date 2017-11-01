package com.yeejay.yplay.model;

import java.util.List;

/**
 * 拉取添加好友消息的返回
 * Created by Administrator on 2017/11/1.
 */

public class GetAddFriendMsgs {

    /**
     * code : 0
     * msg : succ
     * payload : {"total":3,"msgs":[{"msgId":6,"uin":100001,"fromUin":100007,"fromNickName":"蒙大顺","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","fromGender":2,"fromGrade":1,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":0,"ts":1509451163},{"msgId":5,"uin":100001,"fromUin":100000,"fromNickName":"+frankshi+","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/13145710821.png","fromGender":1,"fromGrade":2,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":1,"ts":1509450537},{"msgId":1,"uin":100001,"fromUin":100000,"fromNickName":"+frankshi+","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/13145710821.png","fromGender":1,"fromGrade":2,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":1,"ts":1509192351}]}
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
        return "GetAddFriendMsgs{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * total : 3
         * msgs : [{"msgId":6,"uin":100001,"fromUin":100007,"fromNickName":"蒙大顺","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png","fromGender":2,"fromGrade":1,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":0,"ts":1509451163},{"msgId":5,"uin":100001,"fromUin":100000,"fromNickName":"+frankshi+","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/13145710821.png","fromGender":1,"fromGrade":2,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":1,"ts":1509450537},{"msgId":1,"uin":100001,"fromUin":100000,"fromNickName":"+frankshi+","fromHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/13145710821.png","fromGender":1,"fromGrade":2,"schoolId":1,"schoolName":"南山第二外国语学校","schoolType":1,"status":1,"ts":1509192351}]
         */

        private int total;
        private List<MsgsBean> msgs;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<MsgsBean> getMsgs() {
            return msgs;
        }

        public void setMsgs(List<MsgsBean> msgs) {
            this.msgs = msgs;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "total=" + total +
                    ", msgs=" + msgs +
                    '}';
        }

        public static class MsgsBean {
            /**
             * msgId : 6
             * uin : 100001
             * fromUin : 100007
             * fromNickName : 蒙大顺
             * fromHeadImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509192590102.png
             * fromGender : 2
             * fromGrade : 1
             * schoolId : 1
             * schoolName : 南山第二外国语学校
             * schoolType : 1
             * status : 0
             * ts : 1509451163
             */

            private int msgId;
            private int uin;
            private int fromUin;
            private String fromNickName;
            private String fromHeadImgUrl;
            private int fromGender;
            private int fromGrade;
            private int schoolId;
            private String schoolName;
            private int schoolType;
            private int status;
            private int ts;

            public int getMsgId() {
                return msgId;
            }

            public void setMsgId(int msgId) {
                this.msgId = msgId;
            }

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

            public int getFromUin() {
                return fromUin;
            }

            public void setFromUin(int fromUin) {
                this.fromUin = fromUin;
            }

            public String getFromNickName() {
                return fromNickName;
            }

            public void setFromNickName(String fromNickName) {
                this.fromNickName = fromNickName;
            }

            public String getFromHeadImgUrl() {
                return fromHeadImgUrl;
            }

            public void setFromHeadImgUrl(String fromHeadImgUrl) {
                this.fromHeadImgUrl = fromHeadImgUrl;
            }

            public int getFromGender() {
                return fromGender;
            }

            public void setFromGender(int fromGender) {
                this.fromGender = fromGender;
            }

            public int getFromGrade() {
                return fromGrade;
            }

            public void setFromGrade(int fromGrade) {
                this.fromGrade = fromGrade;
            }

            public int getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(int schoolId) {
                this.schoolId = schoolId;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
            }

            public int getSchoolType() {
                return schoolType;
            }

            public void setSchoolType(int schoolType) {
                this.schoolType = schoolType;
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

            @Override
            public String toString() {
                return "MsgsBean{" +
                        "msgId=" + msgId +
                        ", uin=" + uin +
                        ", fromUin=" + fromUin +
                        ", fromNickName='" + fromNickName + '\'' +
                        ", fromHeadImgUrl='" + fromHeadImgUrl + '\'' +
                        ", fromGender=" + fromGender +
                        ", fromGrade=" + fromGrade +
                        ", schoolId=" + schoolId +
                        ", schoolName='" + schoolName + '\'' +
                        ", schoolType=" + schoolType +
                        ", status=" + status +
                        ", ts=" + ts +
                        '}';
            }
        }
    }
}
