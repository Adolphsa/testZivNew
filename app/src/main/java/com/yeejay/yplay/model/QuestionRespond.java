package com.yeejay.yplay.model;

import java.util.List;

/**
 * 单个问题的返回
 * Created by Administrator on 2017/12/8.
 */

public class QuestionRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"freezeStatus":0,"freezeTs":0,"nowTs":1512743749,"freezeDuration":60,"total":15,"index":1,"question":{"qid":12253,"qtext":"笑点比较低","qiconUrl":"http://yplay-1253229355.image.myqcloud.com/qicon/87.png","optionGender":0,"replyGender":0,"status":0,"ts":1509940385},"options":[{"uin":100033,"nickName":"蒙小萌MIX2","beSelCnt":1},{"uin":100065,"nickName":"蒙小顺ipx","beSelCnt":0},{"uin":100141,"nickName":"华为卡卡","beSelCnt":0},{"uin":100034,"nickName":"dyj_哇哦","beSelCnt":0}]}
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
        return "QuestionRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * freezeStatus : 0
         * freezeTs : 0
         * nowTs : 1512743749
         * freezeDuration : 60
         * total : 15
         * index : 1
         * question : {"qid":12253,"qtext":"笑点比较低","qiconUrl":"http://yplay-1253229355.image.myqcloud.com/qicon/87.png","optionGender":0,"replyGender":0,"status":0,"ts":1509940385}
         * options : [{"uin":100033,"nickName":"蒙小萌MIX2","beSelCnt":1},{"uin":100065,"nickName":"蒙小顺ipx","beSelCnt":0},{"uin":100141,"nickName":"华为卡卡","beSelCnt":0},{"uin":100034,"nickName":"dyj_哇哦","beSelCnt":0}]
         */

        private int freezeStatus;
        private int freezeTs;
        private int nowTs;
        private int freezeDuration;
        private int total;
        private int index;
        private QuestionBean question;
        private List<OptionsBean> options;

        public int getFreezeStatus() {
            return freezeStatus;
        }

        public void setFreezeStatus(int freezeStatus) {
            this.freezeStatus = freezeStatus;
        }

        public int getFreezeTs() {
            return freezeTs;
        }

        public void setFreezeTs(int freezeTs) {
            this.freezeTs = freezeTs;
        }

        public int getNowTs() {
            return nowTs;
        }

        public void setNowTs(int nowTs) {
            this.nowTs = nowTs;
        }

        public int getFreezeDuration() {
            return freezeDuration;
        }

        public void setFreezeDuration(int freezeDuration) {
            this.freezeDuration = freezeDuration;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public QuestionBean getQuestion() {
            return question;
        }

        public void setQuestion(QuestionBean question) {
            this.question = question;
        }

        public List<OptionsBean> getOptions() {
            return options;
        }

        public void setOptions(List<OptionsBean> options) {
            this.options = options;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "freezeStatus=" + freezeStatus +
                    ", freezeTs=" + freezeTs +
                    ", nowTs=" + nowTs +
                    ", freezeDuration=" + freezeDuration +
                    ", total=" + total +
                    ", index=" + index +
                    ", question=" + question +
                    ", options=" + options +
                    '}';
        }

        public static class QuestionBean {
            /**
             * qid : 12253
             * qtext : 笑点比较低
             * qiconUrl : http://yplay-1253229355.image.myqcloud.com/qicon/87.png
             * optionGender : 0
             * replyGender : 0
             * status : 0
             * ts : 1509940385
             */

            private int qid;
            private String qtext;
            private String qiconUrl;
            private int optionGender;
            private int replyGender;
            private int status;
            private int ts;

            public int getQid() {
                return qid;
            }

            public void setQid(int qid) {
                this.qid = qid;
            }

            public String getQtext() {
                return qtext;
            }

            public void setQtext(String qtext) {
                this.qtext = qtext;
            }

            public String getQiconUrl() {
                return qiconUrl;
            }

            public void setQiconUrl(String qiconUrl) {
                this.qiconUrl = qiconUrl;
            }

            public int getOptionGender() {
                return optionGender;
            }

            public void setOptionGender(int optionGender) {
                this.optionGender = optionGender;
            }

            public int getReplyGender() {
                return replyGender;
            }

            public void setReplyGender(int replyGender) {
                this.replyGender = replyGender;
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
                return "QuestionBean{" +
                        "qid=" + qid +
                        ", qtext='" + qtext + '\'' +
                        ", qiconUrl='" + qiconUrl + '\'' +
                        ", optionGender=" + optionGender +
                        ", replyGender=" + replyGender +
                        ", status=" + status +
                        ", ts=" + ts +
                        '}';
            }
        }

        public static class OptionsBean {
            /**
             * uin : 100033
             * nickName : 蒙小萌MIX2
             * beSelCnt : 1
             */

            private int uin;
            private String nickName;
            private int beSelCnt;

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public int getBeSelCnt() {
                return beSelCnt;
            }

            public void setBeSelCnt(int beSelCnt) {
                this.beSelCnt = beSelCnt;
            }

            @Override
            public String toString() {
                return "OptionsBean{" +
                        "uin=" + uin +
                        ", nickName='" + nickName + '\'' +
                        ", beSelCnt=" + beSelCnt +
                        '}';
            }
        }
    }
}
