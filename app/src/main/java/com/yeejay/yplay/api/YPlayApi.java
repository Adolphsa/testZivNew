package com.yeejay.yplay.api;

import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendFeedsMakesureRespond;
import com.yeejay.yplay.model.FriendFeedsRespond;
import com.yeejay.yplay.model.FriendsListRespond;
import com.yeejay.yplay.model.GetAddFriendMsgs;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.model.LogUploadRespond;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.model.PushNotifyRespond;
import com.yeejay.yplay.model.QuestionCandidateRespond;
import com.yeejay.yplay.model.QuestionListRespond;
import com.yeejay.yplay.model.QuestionRespond;
import com.yeejay.yplay.model.UnReadMsgCountRespond;
import com.yeejay.yplay.model.UpdateContactsRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.model.VoteRespond;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * yplayApi
 * Created by Administrator on 2017/10/29.
 */

public interface YPlayApi {

    //发送验证码
    @FormUrlEncoded
    @POST("/api/account/sendsms")
    Observable<BaseRespond> sendMessage(@Field("phone") String phone);

    //登录
    @FormUrlEncoded
    @POST("/api/account/login2")
    Observable<LoginRespond> login(@Field("phone") String phone,
                                   @Field("code") String code,
                                   @Field("uuid") long uuid,
                                   @Field("device") String device,
                                   @Field("os") String os,
                                   @Field("appVer") String appVer);

    //校验邀请码
    @FormUrlEncoded
    @POST("/api/account/checkinvitecode")
    Observable<BaseRespond> checkInviteCode(@FieldMap Map<String,Object> filemap);

    //通讯录上传或更新
    @FormUrlEncoded
    @POST("/api/addr/update")
    Observable<UpdateContactsRespond> updateContacts(@FieldMap Map<String,Object> filemap);

    //通讯录remove
    @FormUrlEncoded
    @POST("/api/addr/remove")
    Observable<UpdateContactsRespond> removeContacts(@FieldMap Map<String,Object> filemap);

    //搜索学校列表
    @FormUrlEncoded
    @POST("/api/account/searchschools")
    Observable<NearestSchoolsRespond> getSearchScools(@FieldMap Map<String,Object> filemap);

    //最近学校列表
    @FormUrlEncoded
    @POST("/api/account/getnearestschools")
    Observable<NearestSchoolsRespond> getNearestScools(@FieldMap Map<String,Object> filemap);

    //选择学校
    @FormUrlEncoded
    @POST("/api/user/updateschoolinfo")
    Observable<BaseRespond> choiceSchool(@FieldMap Map<String,Object> filemap);

    //选择性别
    @FormUrlEncoded
    @POST("/api/user/updateuserprofile")
    Observable<BaseRespond> choiceSex(@FieldMap Map<String,Object> filemap);

    //昵称
    @FormUrlEncoded
    @POST("/api/user/updateuserprofile")
    Observable<BaseRespond> settingName(@FieldMap Map<String,Object> filemap);

    //上传图片
    @Multipart
    @POST("/files/v2/1253229355/yplay/headimgs/{imagename}")
    Observable<ImageUploadRespond> uploadHeaderImg(@Header("Authorization") String authorization,
                                                   @Path("imagename") String imagename,
                                                   @Part("op") RequestBody upload,
                                                   @Part MultipartBody.Part file);

    //拉取图片
    @FormUrlEncoded
    @POST("/api/user/updateuserprofile")
    Observable<BaseRespond> updateHeaderImg(@FieldMap Map<String,Object> filemap);

    //拉取问题列表
    @FormUrlEncoded
    @POST("/api/vote/getrandomquestions2")
    Observable<QuestionListRespond> getQuestionsList(@FieldMap Map<String,Object> filemap);

    //拉取问题
    @FormUrlEncoded
    @POST("/api/vote/getquestionandoptions")
    Observable<QuestionRespond> getQuestion(@FieldMap Map<String,Object> filemap);

    //某个问题的拉取候选者---新
    @FormUrlEncoded
    @POST("/api/vote/getoptions")
    Observable<QuestionCandidateRespond> getQuestionsCandidateNew(@FieldMap Map<String,Object> filemap);

    //跳过下个问题
    @FormUrlEncoded
    @POST("/api/vote/doskip")
    Observable<BaseRespond> doskipQuestion(@FieldMap Map<String,Object> filemap);

    //某个问题的拉取候选者
    @FormUrlEncoded
    @POST("/api/vote/getrandomoptions")
    Observable<QuestionCandidateRespond> getQuestionsCandidate(@FieldMap Map<String,Object> filemap);

    //投票
    @FormUrlEncoded
    @POST("/api/vote/dovote")
    Observable<VoteRespond> vote(@FieldMap Map<String,Object> filemap);

    //获取好友动态
    @FormUrlEncoded
    @POST("/api/feed/getfeeds")
    Observable<FriendFeedsRespond> getFriendFeeds(@FieldMap Map<String,Object> filemap);

    //确认feeds收到
    @FormUrlEncoded
    @POST("/api/feed/ackfeeds")
    Observable<FriendFeedsMakesureRespond> makeSureFeeds(@FieldMap Map<String,Object> filemap);

    //未读的添加好友请求消息数
    @FormUrlEncoded
    @POST("/api/sns/getaddfriendnewmsgcnt")
    Observable<UnReadMsgCountRespond> getUnreadMessageCount(@FieldMap Map<String,Object> filemap);

    //拉取添加好友消息数组
    @FormUrlEncoded
    @POST("/api/sns/getaddfriendmsgs")
    Observable<GetAddFriendMsgs> getAddFriendMsg(@FieldMap Map<String,Object> filemap);

    //接受加好友请求
    @FormUrlEncoded
    @POST("/api/sns/acceptaddfriend")
    Observable<BaseRespond> acceptAddFriend(@FieldMap Map<String,Object> filemap);

    //发送加好友请求
    @FormUrlEncoded
    @POST("/api/sns/addfriend")
    Observable<AddFriendRespond> addFriend(@FieldMap Map<String,Object> filemap);

    //删除好友
    @FormUrlEncoded
    @POST("/api/sns/removefriend")
    Observable<BaseRespond> removeFriend(@FieldMap Map<String,Object> filemap);

    //通过短信邀请好友
    @FormUrlEncoded
    @POST("/api/sns/invitefriendsbysms")
    Observable<BaseRespond> smsInviteFriends(@FieldMap Map<String,Object> filemap);

    //拉取同校好友列表
    @FormUrlEncoded
    @POST("/api/sns/getrecommends")
    Observable<GetRecommendsRespond> getSchoolmates(@FieldMap Map<String,Object> filemap);

    //通过username搜索好友
    @FormUrlEncoded
    @POST("/api/sns/searchfriend")
    Observable<GetRecommendsRespond> searchFriends(@FieldMap Map<String,Object> filemap);

    //获取用户的资料
    @FormUrlEncoded
    @POST("/api/user/getuserprofile")
    Observable<UserInfoResponde> getUserInfo(@FieldMap Map<String,Object> filemap);

    //获取自己的资料
    @FormUrlEncoded
    @POST("/api/user/getmyprofile")
    Observable<UserInfoResponde> getMyInfo(@FieldMap Map<String,Object> filemap);

    //获取我的好友列表
    @FormUrlEncoded
    @POST("/api/user/getmyfriends")
    Observable<FriendsListRespond> getMyFriendsList(@FieldMap Map<String,Object> filemap);

    //获取用户的钻石排行榜（可以查看自己的）
    @FormUrlEncoded
    @POST("/api/user/getusergemstatinfo")
    Observable<UsersDiamondInfoRespond> getUsersDamonInfo(@FieldMap Map<String,Object> filemap);

    //根据手机号查询用户信息
    @FormUrlEncoded
    @POST("/api/user/getusersbyphone")
    Observable<UserInfoResponde> getUserInfoByPhone(@FieldMap Map<String,Object> filemap);

    //空页面随机推荐好友
    @FormUrlEncoded
    @POST("/api/sns/getrandomrecommends")
    Observable<GetRecommendsRespond> recommendFriendsForNull(@FieldMap Map<String,Object> filemap);

    //退出登录
    @FormUrlEncoded
    @POST("/api/account/logout")
    Observable<BaseRespond> logout(@FieldMap Map<String,Object> filemap);

    //查询用户的修改配额
    @FormUrlEncoded
    @POST("/api/user/getmyprofilemodquota")
    Observable<UserUpdateLeftCountRespond> getUserUpdateCount(@FieldMap Map<String,Object> filemap);

    //用户投稿
    @FormUrlEncoded
    @POST("/api/vote/submitquestion")
    Observable<BaseRespond> submiteQuestion(@FieldMap Map<String,Object> filemap);

    //用户投稿
    @FormUrlEncoded
    @POST("/api/im/geneusersig")
    Observable<ImSignatureRespond> getImSignature(@FieldMap Map<String,Object> filemap);

    //投票消息回复
    @FormUrlEncoded
    @POST("/api/im/sendvotereplymsg")
    Observable<BaseRespond> replayImVote(@FieldMap Map<String,Object> filemap);

    //获取用户新通知
    @FormUrlEncoded
    @POST("/api/notify/getnewnotifystat")
    Observable<PushNotifyRespond> getNewNotify(@FieldMap Map<String,Object> filemap);

    //上传Log
    @Multipart
    @POST("/files/v2/1253229355/yplay/logs/{logzipname}")
    Observable<LogUploadRespond> uploadLogs(@Header("Authorization") String authorization,
                                            @Path("logzipname") String logzipname,
                                            @Part("op") RequestBody upload,
                                            @Part MultipartBody.Part file);

}
