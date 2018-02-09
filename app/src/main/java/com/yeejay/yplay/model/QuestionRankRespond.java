package com.yeejay.yplay.model;

import java.util.List;

/**
 * 问题排行榜
 * Created by Adolph on 2018/2/8.
 */

public class QuestionRankRespond {


    /**
     * code : 0
     * msg : succ
     * payload : {"rankingInSameSchool":[{"uin":100339,"nickName":"p","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg","votedCnt":1}],"rankingPercentInSameSchool":"100%","rankingInFriends":[{"uin":100206,"nickName":"淡定","headImgUrl":"","votedCnt":2},{"uin":100244,"nickName":"芳草萋萋鹦鹉洲","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1516588209072.png","votedCnt":1},{"uin":100339,"nickName":"p","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg","votedCnt":1}],"rankingPercentInFriends":"50%"}
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

    public static class PayloadBean {
        /**
         * rankingInSameSchool : [{"uin":100339,"nickName":"p","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg","votedCnt":1}]
         * rankingPercentInSameSchool : 100%
         * rankingInFriends : [{"uin":100206,"nickName":"淡定","headImgUrl":"","votedCnt":2},{"uin":100244,"nickName":"芳草萋萋鹦鹉洲","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1516588209072.png","votedCnt":1},{"uin":100339,"nickName":"p","headImgUrl":"http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg","votedCnt":1}]
         * rankingPercentInFriends : 50%
         */

        private String rankingPercentInSameSchool;
        private String rankingPercentInFriends;
        private List<RankingInSameSchoolBean> rankingInSameSchool;
        private List<RankingInFriendsBean> rankingInFriends;

        public String getRankingPercentInSameSchool() {
            return rankingPercentInSameSchool;
        }

        public void setRankingPercentInSameSchool(String rankingPercentInSameSchool) {
            this.rankingPercentInSameSchool = rankingPercentInSameSchool;
        }

        public String getRankingPercentInFriends() {
            return rankingPercentInFriends;
        }

        public void setRankingPercentInFriends(String rankingPercentInFriends) {
            this.rankingPercentInFriends = rankingPercentInFriends;
        }

        public List<RankingInSameSchoolBean> getRankingInSameSchool() {
            return rankingInSameSchool;
        }

        public void setRankingInSameSchool(List<RankingInSameSchoolBean> rankingInSameSchool) {
            this.rankingInSameSchool = rankingInSameSchool;
        }

        public List<RankingInFriendsBean> getRankingInFriends() {
            return rankingInFriends;
        }

        public void setRankingInFriends(List<RankingInFriendsBean> rankingInFriends) {
            this.rankingInFriends = rankingInFriends;
        }

        public static class RankingInSameSchoolBean {
            /**
             * uin : 100339
             * nickName : p
             * headImgUrl : http://yplay-1253229355.image.myqcloud.com/headimgs/1517277175592.jpg
             * votedCnt : 1
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int votedCnt;

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

            public String getHeadImgUrl() {
                return headImgUrl;
            }

            public void setHeadImgUrl(String headImgUrl) {
                this.headImgUrl = headImgUrl;
            }

            public int getVotedCnt() {
                return votedCnt;
            }

            public void setVotedCnt(int votedCnt) {
                this.votedCnt = votedCnt;
            }
        }

        public static class RankingInFriendsBean {
            /**
             * uin : 100206
             * nickName : 淡定
             * headImgUrl :
             * votedCnt : 2
             */

            private int uin;
            private String nickName;
            private String headImgUrl;
            private int votedCnt;

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

            public String getHeadImgUrl() {
                return headImgUrl;
            }

            public void setHeadImgUrl(String headImgUrl) {
                this.headImgUrl = headImgUrl;
            }

            public int getVotedCnt() {
                return votedCnt;
            }

            public void setVotedCnt(int votedCnt) {
                this.votedCnt = votedCnt;
            }
        }
    }
}
