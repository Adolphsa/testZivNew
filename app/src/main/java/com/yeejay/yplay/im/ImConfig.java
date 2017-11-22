package com.yeejay.yplay.im;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.AppManager;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * IM消息配置
 * Created by Administrator on 2017/11/22.
 */

public class ImConfig {

    private static final String tag = "ImConfig";
    private static final int IM_SDK_APP_ID = 1400046572;


    private TIMManager timManager = TIMManager.getInstance();
    private TIMSdkConfig config;
//    private TIMUserConfig userConfig;


    private ImConfig() {
    }

    public static synchronized ImConfig getImInstance() {
        return ImSingletonHolder.instance;
    }

    private static class ImSingletonHolder {
        private static final ImConfig instance = new ImConfig();
    }

    public void imConfig() {

        System.out.println("IM配置");

        //初始化SDK基本配置
        config = new TIMSdkConfig(IM_SDK_APP_ID)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/imyplay/");

        //初始化SDK
        timManager.init(YplayApplication.getInstance(), config);

    }

    public void userConfig() {

        System.out.println("用户配置");

        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        System.out.println("你的账号已在其他地方登录");
                        DialogUtils.repeatLoginDialog(AppManager.getAppManager().currentActivity(), "您的账号已在其他地方登录");
                        Log.i(tag, "onForceOffline");
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新userSig重新登录SDK
                        System.out.println("签名过期了");
                        getImSignature();
                        Log.i(tag, "onUserSigExpired");
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(tag, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(tag, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(tag, "onWifiNeedAuth");
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(tag, "onRefresh");
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());
                    }
                });

        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //禁用消息存储
                .enableStorage(false)
                //开启消息已读回执
                .enableReadReceipt(true);

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);

        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {//收到新消息
                //消息的内容解析请参考消息收发文档中的消息解析说明
                System.out.println("消息数---" + list.size());
                int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
                ImMsgDao imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
                ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
                for (TIMMessage timMessage : list) {
                    if (timMessage.getElementCount() == 0) {
                        continue;
                    }
                    String sessionId = timMessage.getMsg().session().sid();
                    long msgId = timMessage.getMsgUniqueId();
                    String sender = timMessage.getSender();
                    int msgType = timMessage.getElement(0).getType().ordinal();
                    String msgContent = ((TIMCustomElem) timMessage.getElement(0)).getData().toString();
                    long msgTs = timMessage.getMsg().time();

                    ImMsg imMsg = new ImMsg(null,sessionId,msgId,sender,msgType,msgContent,msgTs);
                    try {
                        imMsgDao.insert(imMsg);
                    }catch (Exception e){
                        System.out.println("消息插入异常");
                    }

                    //会话表
                    String chater = sender;
                    if (String.valueOf(uin).equals(sender)){
                        chater = "";
                    }

                    ImSession imSession = imSessionDao.queryBuilder()
                            .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                            .build().unique();
                    if (imSession == null){
                        ImSession session = new ImSession(null,
                                sessionId,
                                chater,
                                null,
                                null,
                                0,
                                sender,
                                msgType,
                                msgContent,
                                msgTs,
                                0);
                        imSessionDao.insert(session);
                    }else{
                        //更新会话表
                        imSession.setLastSender(sender);
                        imSession.setMsgContent(msgContent);
                        imSession.setMsgTs(msgTs);
                        imSessionDao.update(imSession);
                    }
                }
                return true;//返回true将终止回调链，不再调用下一个新消息监听器
            }
        });

        System.out.println("用户配置完毕");
    }


    //获取im签名
    private void getImSignature() {

        final Map<String, Object> imMap = new HashMap<>();
        final int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        imMap.put("uin", uin);
        imMap.put("token", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        imMap.put("ver", SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_VER, 0));
        imMap.put("identifier", uin);


        YPlayApiManger.getInstance().getZivApiService()
                .getImSignature(imMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImSignatureRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImSignatureRespond imSignatureRespond) {
                        if (imSignatureRespond.getCode() == 0) {
                            String imSig = imSignatureRespond.getPayload().getSig();
                            System.out.println("im签名---" + imSig);
                            if (!TextUtils.isEmpty(imSig)) {
                                imLogin(String.valueOf(uin), imSig);
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * im登录
     *
     * @param identifier 用户账号
     * @param imSig      用户签名
     */
    private void imLogin(String identifier, String imSig) {

        System.out.println("mainactivity---identifier" + identifier +
                ",imSig---" + imSig);

        TIMManager.getInstance().login(String.valueOf(identifier),
                imSig,
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        System.out.println("登录错误---" + s);
                    }

                    @Override
                    public void onSuccess() {
                        System.out.println("登录成功");
                    }
                });
    }
}
