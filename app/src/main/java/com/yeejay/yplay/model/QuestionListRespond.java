package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 问题列表返回
 * Created by Administrator on 2017/10/30.
 */

public class QuestionListRespond implements Parcelable {


    /**
     * code : 0
     * msg : succ
     * payload : {"questions":[{"qid":11025,"qtext":"不介意同穿一条裤子","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11065,"qtext":"希望我能成为ta","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11118,"qtext":"还保持着一颗童心","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","status":0,"ts":1508835434},{"qid":11009,"qtext":"很喜欢看电影","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835433},{"qid":11057,"qtext":"老烟枪","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11135,"qtext":"总给我带来好消息的人","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11035,"qtext":"喜欢约 ta出去吃饭","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11133,"qtext":"对我的帮助很大","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11086,"qtext":"年轻人中的老干部","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","status":0,"ts":1508835434},{"qid":11079,"qtext":"COSPLAY重度玩家","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11107,"qtext":"冬天可能会舔铁栅栏","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835434},{"qid":11028,"qtext":"喜欢时尚都市","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","status":0,"ts":1508835434},{"qid":11003,"qtext":"喜欢养鱼","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png","status":0,"ts":1508835433},{"qid":11022,"qtext":"喜欢爬山","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","status":0,"ts":1508835434},{"qid":11016,"qtext":"最喜欢的课外活动是踢毽子","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","status":0,"ts":1508835434}]}
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
        return "QuestionListRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {

        private int freezeStatus;
        private int freezeTs;
        private int nowTs;
        private int freezeDuration;
        private int total;
        private List<QuestionsBean> questions;

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

        public List<QuestionsBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean> questions) {
            this.questions = questions;
        }

        public static Creator<PayloadBean> getCREATOR() {
            return CREATOR;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "freezeStatus=" + freezeStatus +
                    ", freezeTs=" + freezeTs +
                    ", nowTs=" + nowTs +
                    ", freezeDuration=" + freezeDuration +
                    ", total=" + total +
                    ", questions=" + questions +
                    '}';
        }


        public static class QuestionsBean implements Parcelable {
            /**
             * qid : 11025
             * qtext : 不介意同穿一条裤子
             * qiconUrl : http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_2.png
             * status : 0
             * ts : 1508835434
             */

            private int qid;
            private String qtext;
            private String qiconUrl;
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
                return "QuestionsBean{" +
                        "qid=" + qid +
                        ", qtext='" + qtext + '\'' +
                        ", qiconUrl='" + qiconUrl + '\'' +
                        ", status=" + status +
                        ", ts=" + ts +
                        '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.qid);
                dest.writeString(this.qtext);
                dest.writeString(this.qiconUrl);
                dest.writeInt(this.status);
                dest.writeInt(this.ts);
            }

            public QuestionsBean() {
            }

            protected QuestionsBean(Parcel in) {
                this.qid = in.readInt();
                this.qtext = in.readString();
                this.qiconUrl = in.readString();
                this.status = in.readInt();
                this.ts = in.readInt();
            }

            public static final Parcelable.Creator<QuestionsBean> CREATOR = new Parcelable.Creator<QuestionsBean>() {
                @Override
                public QuestionsBean createFromParcel(Parcel source) {
                    return new QuestionsBean(source);
                }

                @Override
                public QuestionsBean[] newArray(int size) {
                    return new QuestionsBean[size];
                }
            };
        }

        public PayloadBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.freezeStatus);
            dest.writeInt(this.freezeTs);
            dest.writeInt(this.nowTs);
            dest.writeInt(this.freezeDuration);
            dest.writeInt(this.total);
            dest.writeTypedList(this.questions);
        }

        protected PayloadBean(Parcel in) {
            this.freezeStatus = in.readInt();
            this.freezeTs = in.readInt();
            this.nowTs = in.readInt();
            this.freezeDuration = in.readInt();
            this.total = in.readInt();
            this.questions = in.createTypedArrayList(QuestionsBean.CREATOR);
        }

        public static final Creator<PayloadBean> CREATOR = new Creator<PayloadBean>() {
            @Override
            public PayloadBean createFromParcel(Parcel source) {
                return new PayloadBean(source);
            }

            @Override
            public PayloadBean[] newArray(int size) {
                return new PayloadBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeParcelable(this.payload, flags);
    }

    public QuestionListRespond() {
    }

    protected QuestionListRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<QuestionListRespond> CREATOR = new Parcelable.Creator<QuestionListRespond>() {
        @Override
        public QuestionListRespond createFromParcel(Parcel source) {
            return new QuestionListRespond(source);
        }

        @Override
        public QuestionListRespond[] newArray(int size) {
            return new QuestionListRespond[size];
        }
    };
}
