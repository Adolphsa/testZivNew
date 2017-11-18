package com.yeejay.yplay.model;

/**
 * 用户的修改配额
 * Created by Administrator on 2017/11/18.
 */

public class UserUpdateLeftCountRespond {

    /**
     * code : 0
     * msg : succ
     * payload : {"info":{"uin":100141,"field":1,"hasModCnt":15,"leftCnt":85}}
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
        return "UserUpdateLeftCountRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * info : {"uin":100141,"field":1,"hasModCnt":15,"leftCnt":85}
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "info=" + info +
                    '}';
        }

        public static class InfoBean {
            /**
             * uin : 100141
             * field : 1
             * hasModCnt : 15
             * leftCnt : 85
             */

            private int uin;
            private int field;
            private int hasModCnt;
            private int leftCnt;

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

            public int getField() {
                return field;
            }

            public void setField(int field) {
                this.field = field;
            }

            public int getHasModCnt() {
                return hasModCnt;
            }

            public void setHasModCnt(int hasModCnt) {
                this.hasModCnt = hasModCnt;
            }

            public int getLeftCnt() {
                return leftCnt;
            }

            public void setLeftCnt(int leftCnt) {
                this.leftCnt = leftCnt;
            }

            @Override
            public String toString() {
                return "InfoBean{" +
                        "uin=" + uin +
                        ", field=" + field +
                        ", hasModCnt=" + hasModCnt +
                        ", leftCnt=" + leftCnt +
                        '}';
            }
        }
    }
}
