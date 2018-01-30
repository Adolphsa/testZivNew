package com.yeejay.yplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.UpdateContactsRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final List<com.yeejay.yplay.model.ContactsInfo> waitUploadingContactsList = new ArrayList<>();

        if (contactsInfoList == null || contactsInfoList.size() <= 0){
            Log.i(TAG, "upLoadingContacts: 没有查询到数据");
            return;
        }else {
            waitUploadingContactsList.clear();
            for (ContactsInfo contactsInfo : contactsInfoList){
                Log.i(TAG, "upLoadingContacts: name---" + contactsInfo.getName() + "orgPhone---" + contactsInfo.getOrgPhone());
                waitUploadingContactsList.add(new com.yeejay.yplay.model.ContactsInfo(contactsInfo.getName(), contactsInfo.getOrgPhone()));
            }
        }

        Map<String, Object> contactsMap = new HashMap<>();
        String contactString = GsonUtil.GsonString(waitUploadingContactsList);
        String encodedString = Base64.encodeToString(contactString.getBytes(), Base64.DEFAULT);
        contactsMap.put("data", encodedString);
        contactsMap.put("uin", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, 0));
        contactsMap.put("token", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contactsMap.put("ver", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_CONTACTS_UPDATE, contactsMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        UpdateContactsRespond baseRespond = GsonUtil.GsonToBean(result, UpdateContactsRespond.class);
                        if (baseRespond.getCode() == 0) {
                            Log.i(TAG, "onComplete: 上传通讯录成功---" + baseRespond.toString());
                            offset++;
                            Log.i(TAG, "onNext: offset---" + offset);

                            List<UpdateContactsRespond.PayloadBean.InfosBean> infoList = baseRespond.getPayload().getInfos();
                            updateSuccessHandle(waitUploadingContactsList,infoList);

                            upLoadingContacts();

                        } else {
                            Log.i(TAG, "onNext: 上传通讯录失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onTimeOut() {

                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("onError:更新通讯录失败");
                    }
                });
    }

    //上传成功后更新数据库数据
    private void updateSuccessHandle(List<com.yeejay.yplay.model.ContactsInfo> uploadList, List<UpdateContactsRespond.PayloadBean.InfosBean> infoList) {

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
                Log.i(TAG, "updateSuccessHandle: ---" + infosBean.getNickName() + "---" + infosBean.getHeadImgUrl());
                LogUtils.getInstance().error("更新通讯录好友---" + infosBean.getNickName() + "---" + infosBean.getHeadImgUrl());
            }else {
                Log.i(TAG, "updateSuccessHandle: 查询到的orgPhone为空---" + infosBean.getOrgPhone());
            }
        }

        //对于服务器返回的校验每个号码是否返回 如果没有返回，说明号码格式非手机号 不用再次上传
        for(com.yeejay.yplay.model.ContactsInfo info : uploadList){

            boolean find = false;
            String curOrgPhone = info.getPhone();

            if(TextUtils.isEmpty(curOrgPhone)){
                continue;
            }

            for (UpdateContactsRespond.PayloadBean.InfosBean infosBean : infoList) {
                if(curOrgPhone.equals(infosBean.getOrgPhone())){
                    find = true;
                    break;
                }
            }

            //如果没有找到 这说明非手机号
            if(!find){

                ContactsInfo contactsInfo = contactsInfoDao.queryBuilder()
                        .where(ContactsInfoDao.Properties.OrgPhone.eq(curOrgPhone))
                        .build().unique();

                if (contactsInfo != null){
                    contactsInfo.setUin(2); //0 已经上传 1 还未删除 2 非手机号
                    contactsInfoDao.update(contactsInfo);
                    LogUtils.getInstance().error("更新通讯录,非手机号码---" + curOrgPhone);
                }

            }
        }
    }


    //查询数据库
    private List<ContactsInfo> queryContacts(){

         return contactsInfoDao.queryBuilder()
                 .where(ContactsInfoDao.Properties.Uin.eq(1))
                .orderAsc(ContactsInfoDao.Properties.SortKey)
                .offset(offset*50)
                .limit(50)
                .list();

    }


}
