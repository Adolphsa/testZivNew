package com.yeejay.yplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.UpdateContactsRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactsService extends Service {

    private static final String TAG = "ContactsService";

     int offset = 0;
    ContactsInfoDao contactsInfoDao;

    public ContactsService() {
        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        upLoadingContacts();
    }

    //上传通讯录
    private void upLoadingContacts() {

        List<ContactsInfo> contactsInfoList = queryContacts();

        if (contactsInfoList == null || contactsInfoList.size() <= 0){
            Log.i(TAG, "upLoadingContacts: 没有查询到数据");
            return;
        }

        Map<String, Object> contactsMap = new HashMap<>();
        String contactString = GsonUtil.GsonString(contactsInfoList);
        String encodedString = Base64.encodeToString(contactString.getBytes(), Base64.DEFAULT);
        contactsMap.put("data", encodedString);
        contactsMap.put("uin", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, 0));
        contactsMap.put("token", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contactsMap.put("ver", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .updateContacts(contactsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateContactsRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull UpdateContactsRespond baseRespond) {

                        if (baseRespond.getCode() == 0) {
                            System.out.println("上传通讯录成功---" + baseRespond.toString());
                            offset++;
                            Log.i(TAG, "onNext: offset---" + offset);

                            List<UpdateContactsRespond.PayloadBean.InfosBean> infoList = baseRespond.getPayload().getInfos();
                            updateSuccessHandle(infoList);

                            upLoadingContacts();

                        } else {
                            System.out.println("上传通讯录失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("上传通讯录失败---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //上传成功后更新数据库数据
    private void updateSuccessHandle(List<UpdateContactsRespond.PayloadBean.InfosBean> infoList) {
        for (UpdateContactsRespond.PayloadBean.InfosBean infosBean : infoList) {
            ContactsInfo contactsInfo = contactsInfoDao.queryBuilder()
                    .where(ContactsInfoDao.Properties.OrgPhone.eq(infosBean.getOrgPhone()))
                    .build().unique();
            if (contactsInfo != null){
                contactsInfo.setPhone(infosBean.getPhone());
                contactsInfo.setUin(infosBean.getUin());
                if (!TextUtils.isEmpty(infosBean.getNickName())){
                    contactsInfo.setNickName(infosBean.getNickName());
                }
                if (!TextUtils.isEmpty(infosBean.getHeadImgUrl())){
                    contactsInfo.setHeadImgUrl(infosBean.getHeadImgUrl());
                }
                contactsInfoDao.update(contactsInfo);
            }
        }
    }


    //查询数据库
    private List<ContactsInfo> queryContacts(){

         return contactsInfoDao.queryBuilder()
                .orderAsc(ContactsInfoDao.Properties.SortKey)
                .offset(offset*50)
                .limit(50)
                .list();

    }


}
