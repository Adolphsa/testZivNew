package com.yeejay.yplay.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询未上线的投稿列表返回的数据结构
 * Created by xjg on 2018/01/16.
 */
public class SubmitQueryListRespond implements Parcelable {
    /**
     * code : 0
     * msg : succ
     * payload : {"contributes":[{"submitId":357,"qid":100004,"qtext":"TT","qiconUrl":2,"status":"0","desc":*****,"votedCnt":"1","flag":1}]}
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
        return "SubmitQueryListRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        private List<ContributesBean> infos;

        public List<ContributesBean> getContributesInfo() {
            return infos;
        }

        public void setContributesInfo(List<ContributesBean> infos) {
            this.infos = infos;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "infos=" + infos +
                    '}';
        }

        public static class ContributesBean implements Parcelable {
            private int submitId;
            private int qid;
            private String qtext;
            private String qiconUrl;
            private int status;
            private String desc;
            private int votedCnt;
            private int flag;

            public int getSubmitId() {return this.submitId;}

            public void setSubmitId(int submitId) {this.submitId = submitId;}

            public int getQid() {return this.qid;}

            public void setQid(int qid) {this.qid = qid;}

            public String getQtext() {return this.qtext;}

            public void setQtext(String qtext) {this.qtext = qtext;}

            public String getQiconUrl() {return this.qiconUrl;}

            public void setQiconUrl(String qiconUrl) {this.qiconUrl = qiconUrl;}

            public int getStatus() {return this.status;}

            public void setStatus(int status) {this.status = status;}

            public String getDesc() {return this.desc;}

            public void setDesc(String desc) {this.desc = desc;}

            public int getVotedCnt() {return this.votedCnt;}

            public void setVotedCnt(int votedCnt) {
                this.votedCnt = votedCnt;
            }

            public int getFlag() {return this.flag;}

            public void setFlag(int flag) {this.flag = flag;}


            @Override
            public String toString() {
                return "ContributesBean{" +
                        "submitId=" + submitId +
                        ", qid=" + qid +
                        ", qtext='" + qtext + '\'' +
                        ", qiconUrl='" + qiconUrl + '\'' +
                        ", status=" + status +
                        ", desc='" + desc + '\'' +
                        ", votedCnt=" + votedCnt +
                        ", flag=" + flag +
                         '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.submitId);
                dest.writeInt(this.qid);
                dest.writeString(this.qtext);
                dest.writeString(this.qiconUrl);
                dest.writeInt(this.status);
                dest.writeString(this.desc);
                dest.writeInt(this.votedCnt);
                dest.writeInt(this.flag);
            }

            public ContributesBean() {
            }

            protected ContributesBean(Parcel in) {
                this.submitId = in.readInt();
                this.qid = in.readInt();
                this.qtext = in.readString();
                this.qiconUrl = in.readString();
                this.status = in.readInt();
                this.desc = in.readString();
                this.votedCnt = in.readInt();
                this.flag = in.readInt();
            }

            public static final Creator<ContributesBean> CREATOR = new Creator<ContributesBean>() {
                @Override
                public ContributesBean createFromParcel(Parcel source) {
                    return new ContributesBean(source);
                }

                @Override
                public ContributesBean[] newArray(int size) {
                    return new ContributesBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(this.infos);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.infos = new ArrayList<ContributesBean>();
            in.readList(this.infos, ContributesBean.class.getClassLoader());
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

    public SubmitQueryListRespond() {
    }

    protected SubmitQueryListRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SubmitQueryListRespond> CREATOR = new Parcelable.Creator<SubmitQueryListRespond>() {
        @Override
        public SubmitQueryListRespond createFromParcel(Parcel source) {
            return new SubmitQueryListRespond(source);
        }

        @Override
        public SubmitQueryListRespond[] newArray(int size) {
            return new SubmitQueryListRespond[size];
        }
    };
}