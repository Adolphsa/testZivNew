package com.yeejay.yplay.model;

import java.util.List;

/**
 * 更新通讯录的返回
 * Created by Adolph on 2018/1/8.
 */

public class UpdateContactsRespond {

    /**
     * code : 0
     * msg : succ
     * payload : {"cnt":5,"infos":[{"phone":"13822737795","uin":0,"orgPhone":"13822737795"},{"phone":"13956271815","uin":0,"orgPhone":"13956271815"},{"phone":"13517494103","uin":0,"orgPhone":"13517494103"},{"phone":"15050738815","uin":0,"orgPhone":"15050738815"},{"phone":"15873367933","uin":0,"orgPhone":"15873367933"}]}
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
        return "UpdateContactsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean {
        /**
         * cnt : 5
         * infos : [{"phone":"13822737795","uin":0,"orgPhone":"13822737795"},{"phone":"13956271815","uin":0,"orgPhone":"13956271815"},{"phone":"13517494103","uin":0,"orgPhone":"13517494103"},{"phone":"15050738815","uin":0,"orgPhone":"15050738815"},{"phone":"15873367933","uin":0,"orgPhone":"15873367933"}]
         */

        private int cnt;
        private List<InfosBean> infos;

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public List<InfosBean> getInfos() {
            return infos;
        }

        public void setInfos(List<InfosBean> infos) {
            this.infos = infos;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "cnt=" + cnt +
                    ", infos=" + infos +
                    '}';
        }

        public static class InfosBean {
            /**
             * phone : 13822737795
             * uin : 0
             * orgPhone : 13822737795
             */

            private String phone;
            private int uin;
            private String orgPhone;
            private String nickName;
            private String headImgUrl;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public int getUin() {
                return uin;
            }

            public void setUin(int uin) {
                this.uin = uin;
            }

            public String getOrgPhone() {
                return orgPhone;
            }

            public void setOrgPhone(String orgPhone) {
                this.orgPhone = orgPhone;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getHeadImgUrl() {
                return headImgUrl;
            }

            public void setHeadImgUrl(String headImgUrl) {
                this.headImgUrl = headImgUrl;
            }


            @Override
            public String toString() {
                return "InfosBean{" +
                        "phone='" + phone + '\'' +
                        ", uin=" + uin +
                        ", orgPhone='" + orgPhone + '\'' +
                        ", nickName='" + nickName + '\'' +
                        ", headImgUrl='" + headImgUrl + '\'' +
                        '}';
            }
        }
    }
}
