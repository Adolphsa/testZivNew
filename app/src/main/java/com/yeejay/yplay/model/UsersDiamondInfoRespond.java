package com.yeejay.yplay.model;

import java.util.List;

/**
 * 用户钻石信息
 * Created by Administrator on 2017/11/3.
 */

public class UsersDiamondInfoRespond {

    /**
     * code : 0
     * msg : succ
     * payload : {"total":28,"stats":[{"uin":100006,"qid":11038,"qtext":"无辣不欢","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":2},{"uin":100006,"qid":11069,"qtext":"有一双美腿","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11009,"qtext":"很喜欢看电影","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11156,"qtext":"喜欢摄影","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1},{"uin":100006,"qid":11027,"qtext":"喜欢田园生活","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11100,"qtext":"性格温柔","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1},{"uin":100006,"qid":11159,"qtext":"喜欢逛街","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11101,"qtext":"很想和ta做好朋友","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11109,"qtext":"搞笑到经常笑哭我","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11114,"qtext":"志向远大","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1}]}
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
        return "UsersDiamondInfoRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * total : 28
         * stats : [{"uin":100006,"qid":11038,"qtext":"无辣不欢","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":2},{"uin":100006,"qid":11069,"qtext":"有一双美腿","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11009,"qtext":"很喜欢看电影","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11156,"qtext":"喜欢摄影","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1},{"uin":100006,"qid":11027,"qtext":"喜欢田园生活","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11100,"qtext":"性格温柔","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1},{"uin":100006,"qid":11159,"qtext":"喜欢逛街","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11101,"qtext":"很想和ta做好朋友","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11109,"qtext":"搞笑到经常笑哭我","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","gemCnt":1},{"uin":100006,"qid":11114,"qtext":"志向远大","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","gemCnt":1}]
         */

        private int total;
        private List<StatsBean> stats;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<StatsBean> getStats() {
            return stats;
        }

        public void setStats(List<StatsBean> stats) {
            this.stats = stats;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "total=" + total +
                    ", stats=" + stats +
                    '}';
        }

        public static class StatsBean {
            /**
             * uin : 100006
             * qid : 11038
             * qtext : 无辣不欢
             * qiconUrl : http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png
             * gemCnt : 2
             */

            private int uin;
            private int qid;
            private String qtext;
            private String qiconUrl;
            private int gemCnt;

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

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

            public int getGemCnt() {
                return gemCnt;
            }

            public void setGemCnt(int gemCnt) {
                this.gemCnt = gemCnt;
            }

            @Override
            public String toString() {
                return "StatsBean{" +
                        "uin=" + uin +
                        ", qid=" + qid +
                        ", qtext='" + qtext + '\'' +
                        ", qiconUrl='" + qiconUrl + '\'' +
                        ", gemCnt=" + gemCnt +
                        '}';
            }
        }
    }
}
