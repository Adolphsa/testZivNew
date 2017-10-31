package com.yeejay.yplay.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友动态返回
 * Created by Administrator on 2017/10/31.
 */

public class FriendFeedsRespond implements Parcelable {

    /**
     * code : 0
     * msg : succ
     * payload : {"feeds":[{"voteRecordId":357,"friendUin":100004,"friendNickName":"TT","friendGender":2,"friendHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png","qid":11104,"qtext":"很酷，不聊骚","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","voteFromUin":100008,"voteFromGender":2,"voteFromSchoolId":4,"voteFromSchoolType":2,"voteFromSchoolName":"深圳实验高中","voteFromGrade":3,"ts":1509439347357},{"voteRecordId":356,"friendUin":100000,"friendNickName":"frankshi","friendGender":1,"friendHeadImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/13145710821.png","qid":11132,"qtext":"希望和ta同班","qiconUrl":"http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png","voteFromUin":100008,"voteFromGender":2,"voteFromSchoolId":4,"voteFromSchoolType":2,"voteFromSchoolName":"深圳实验高中","voteFromGrade":3,"ts":1509439345587}]}
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
        return "FriendFeedsRespond{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static class PayloadBean implements Parcelable {
        private List<FeedsBean> feeds;

        public List<FeedsBean> getFeeds() {
            return feeds;
        }

        public void setFeeds(List<FeedsBean> feeds) {
            this.feeds = feeds;
        }

        @Override
        public String toString() {
            return "PayloadBean{" +
                    "feeds=" + feeds +
                    '}';
        }

        public static class FeedsBean implements Parcelable {
            /**
             * voteRecordId : 357
             * friendUin : 100004
             * friendNickName : TT
             * friendGender : 2
             * friendHeadImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1509122247953.png
             * qid : 11104
             * qtext : 很酷，不聊骚
             * qiconUrl : http://shuyaimgs-1253229355.cossh.myqcloud.com/qicon/qicon_1.png
             * voteFromUin : 100008
             * voteFromGender : 2
             * voteFromSchoolId : 4
             * voteFromSchoolType : 2
             * voteFromSchoolName : 深圳实验高中
             * voteFromGrade : 3
             * ts : 1509439347357
             */

            private int voteRecordId;
            private int friendUin;
            private String friendNickName;
            private int friendGender;
            private String friendHeadImgUrl;
            private int qid;
            private String qtext;
            private String qiconUrl;
            private int voteFromUin;
            private int voteFromGender;
            private int voteFromSchoolId;
            private int voteFromSchoolType;
            private String voteFromSchoolName;
            private int voteFromGrade;
            private long ts;

            public int getVoteRecordId() {
                return voteRecordId;
            }

            public void setVoteRecordId(int voteRecordId) {
                this.voteRecordId = voteRecordId;
            }

            public int getFriendUin() {
                return friendUin;
            }

            public void setFriendUin(int friendUin) {
                this.friendUin = friendUin;
            }

            public String getFriendNickName() {
                return friendNickName;
            }

            public void setFriendNickName(String friendNickName) {
                this.friendNickName = friendNickName;
            }

            public int getFriendGender() {
                return friendGender;
            }

            public void setFriendGender(int friendGender) {
                this.friendGender = friendGender;
            }

            public String getFriendHeadImgUrl() {
                return friendHeadImgUrl;
            }

            public void setFriendHeadImgUrl(String friendHeadImgUrl) {
                this.friendHeadImgUrl = friendHeadImgUrl;
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

            public int getVoteFromUin() {
                return voteFromUin;
            }

            public void setVoteFromUin(int voteFromUin) {
                this.voteFromUin = voteFromUin;
            }

            public int getVoteFromGender() {
                return voteFromGender;
            }

            public void setVoteFromGender(int voteFromGender) {
                this.voteFromGender = voteFromGender;
            }

            public int getVoteFromSchoolId() {
                return voteFromSchoolId;
            }

            public void setVoteFromSchoolId(int voteFromSchoolId) {
                this.voteFromSchoolId = voteFromSchoolId;
            }

            public int getVoteFromSchoolType() {
                return voteFromSchoolType;
            }

            public void setVoteFromSchoolType(int voteFromSchoolType) {
                this.voteFromSchoolType = voteFromSchoolType;
            }

            public String getVoteFromSchoolName() {
                return voteFromSchoolName;
            }

            public void setVoteFromSchoolName(String voteFromSchoolName) {
                this.voteFromSchoolName = voteFromSchoolName;
            }

            public int getVoteFromGrade() {
                return voteFromGrade;
            }

            public void setVoteFromGrade(int voteFromGrade) {
                this.voteFromGrade = voteFromGrade;
            }

            public long getTs() {
                return ts;
            }

            public void setTs(long ts) {
                this.ts = ts;
            }

            @Override
            public String toString() {
                return "FeedsBean{" +
                        "voteRecordId=" + voteRecordId +
                        ", friendUin=" + friendUin +
                        ", friendNickName='" + friendNickName + '\'' +
                        ", friendGender=" + friendGender +
                        ", friendHeadImgUrl='" + friendHeadImgUrl + '\'' +
                        ", qid=" + qid +
                        ", qtext='" + qtext + '\'' +
                        ", qiconUrl='" + qiconUrl + '\'' +
                        ", voteFromUin=" + voteFromUin +
                        ", voteFromGender=" + voteFromGender +
                        ", voteFromSchoolId=" + voteFromSchoolId +
                        ", voteFromSchoolType=" + voteFromSchoolType +
                        ", voteFromSchoolName='" + voteFromSchoolName + '\'' +
                        ", voteFromGrade=" + voteFromGrade +
                        ", ts=" + ts +
                        '}';
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.voteRecordId);
                dest.writeInt(this.friendUin);
                dest.writeString(this.friendNickName);
                dest.writeInt(this.friendGender);
                dest.writeString(this.friendHeadImgUrl);
                dest.writeInt(this.qid);
                dest.writeString(this.qtext);
                dest.writeString(this.qiconUrl);
                dest.writeInt(this.voteFromUin);
                dest.writeInt(this.voteFromGender);
                dest.writeInt(this.voteFromSchoolId);
                dest.writeInt(this.voteFromSchoolType);
                dest.writeString(this.voteFromSchoolName);
                dest.writeInt(this.voteFromGrade);
                dest.writeLong(this.ts);
            }

            public FeedsBean() {
            }

            protected FeedsBean(Parcel in) {
                this.voteRecordId = in.readInt();
                this.friendUin = in.readInt();
                this.friendNickName = in.readString();
                this.friendGender = in.readInt();
                this.friendHeadImgUrl = in.readString();
                this.qid = in.readInt();
                this.qtext = in.readString();
                this.qiconUrl = in.readString();
                this.voteFromUin = in.readInt();
                this.voteFromGender = in.readInt();
                this.voteFromSchoolId = in.readInt();
                this.voteFromSchoolType = in.readInt();
                this.voteFromSchoolName = in.readString();
                this.voteFromGrade = in.readInt();
                this.ts = in.readLong();
            }

            public static final Creator<FeedsBean> CREATOR = new Creator<FeedsBean>() {
                @Override
                public FeedsBean createFromParcel(Parcel source) {
                    return new FeedsBean(source);
                }

                @Override
                public FeedsBean[] newArray(int size) {
                    return new FeedsBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(this.feeds);
        }

        public PayloadBean() {
        }

        protected PayloadBean(Parcel in) {
            this.feeds = new ArrayList<FeedsBean>();
            in.readList(this.feeds, FeedsBean.class.getClassLoader());
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

    public FriendFeedsRespond() {
    }

    protected FriendFeedsRespond(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.payload = in.readParcelable(PayloadBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<FriendFeedsRespond> CREATOR = new Parcelable.Creator<FriendFeedsRespond>() {
        @Override
        public FriendFeedsRespond createFromParcel(Parcel source) {
            return new FriendFeedsRespond(source);
        }

        @Override
        public FriendFeedsRespond[] newArray(int size) {
            return new FriendFeedsRespond[size];
        }
    };
}
