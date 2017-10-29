package com.yeejay.yplay.api;

import com.yeejay.yplay.model.ContactsRespond;
import com.yeejay.yplay.model.LoginRespond;
import com.yeejay.yplay.model.NearestSchoolsRespond;
import com.yeejay.yplay.model.SendSmsRespond;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * yplayApi
 * Created by Administrator on 2017/10/29.
 */

public interface YPlayApi {

    //发送验证码
    @FormUrlEncoded
    @POST("/api/account/sendsms")
    Observable<SendSmsRespond> sendMessage(@Field("phone") String phone);

    //登录
    @FormUrlEncoded
    @POST("/api/account/login")
    Observable<LoginRespond> login(@Field("phone") String phone, @Field("code") String code);

    //通讯录上传或更新
    @FormUrlEncoded
    @POST("/api/addr/update")
    Observable<ContactsRespond> updateContacts(@FieldMap Map<String,String> filemap);

    //通讯录remove
    @FormUrlEncoded
    @POST("/api/addr/remove")
    Observable<ContactsRespond> removeContacts(@FieldMap Map<String,String> filemap);

    //最近学校列表
    @FormUrlEncoded
    @POST("/api/account/getnearestschools")
    Observable<NearestSchoolsRespond> getNearestScools(@FieldMap Map<String,Object> filemap);
}
