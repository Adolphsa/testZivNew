package com.yeejay.yplay.api;

import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendFeedsMakesureRespond;
import com.yeejay.yplay.model.FriendFeedsRespond;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.model.QuestionCandidateRespond;
import com.yeejay.yplay.model.QuestionListRespond;
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
    @POST("/api/account/login")
    Observable<LoginRespond> login(@Field("phone") String phone, @Field("code") String code);

    //通讯录上传或更新
    @FormUrlEncoded
    @POST("/api/addr/update")
    Observable<BaseRespond> updateContacts(@FieldMap Map<String,Object> filemap);

    //通讯录remove
    @FormUrlEncoded
    @POST("/api/addr/remove")
    Observable<BaseRespond> removeContacts(@FieldMap Map<String,String> filemap);

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
    @POST("/api/vote/getrandomquestions")
    Observable<QuestionListRespond> getQuestionsList(@FieldMap Map<String,Object> filemap);

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
    Observable<FriendFeedsMakesureRespond> MakeSureFeeds(@FieldMap Map<String,Object> filemap);

}
