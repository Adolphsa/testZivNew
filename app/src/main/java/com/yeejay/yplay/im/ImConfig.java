package com.yeejay.yplay.im;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.AppManager;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.GetOfflineMsg;
import com.yeejay.yplay.utils.MessageUpdateUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.Iterator;
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
    public static boolean isOffline = true;

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
                .setLogLevel(TIMLogLevel.ERROR)
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
                        System.out.println("你的账号已在其他地方登录---" + YPlayConstant.IM_ERROR_CODE);
                        if (6208 == YPlayConstant.IM_ERROR_CODE) {
                            System.out.println("错误码为6208，重新登录");
                            getImSignature();
                        } else {
                            DialogUtils.repeatLoginDialog(AppManager.getAppManager().currentActivity(), "您的账号已在其他地方登录");
                        }

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
                        isOffline = false;
                        handleOfflineMsg();
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());
                    }
                });

        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //禁用消息存储
                .enableStorage(true)
                //开启消息已读回执
                .enableReadReceipt(true)
                .enableRecentContact(true)
                .enableRecentContactNotify(false);

        //TIMGroupSettings groupSettings = new TIMGroupSettings();
        userConfig = new TIMUserConfigGroupExt(userConfig)
                .enableGroupStorage(true);

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);

        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {//收到新消息
                //消息的内容解析请参考消息收发文档中的消息解析说明
                Log.i(tag, "onNewMessages: 收到新消息");
                handleNewMsg(list);

                return true;//返回true将终止回调链，不再调用下一个新消息监听器
            }
        });

        System.out.println("用户配置完毕");
    }

    private void handleOfflineMsg() {
        AsyncTask task  = new HandleOfflineMsgTask();
        task.execute();
    }

    private void handleNewMsg(final List<TIMMessage> list) {
        AsyncTask task  = new HandleOnlineMsgTask(list);
        task.execute();
    }

    private static class HandleOnlineMsgTask extends AsyncTask<Object,Integer,Integer> {
        private List<TIMMessage> list;

        public HandleOnlineMsgTask (List<TIMMessage> lt) {
            list = lt;
        }

        @Override
        protected Integer doInBackground(Object... voids) {
            onLineUpdateSession(list);
            sendBroadcast();

            return null;
        }
    }

    private static class HandleOfflineMsgTask extends AsyncTask<Object,Integer,Integer> {
        @Override
        protected Integer doInBackground(Object... voids) {
            GetOfflineMsg.getOfflineMsgs();

            return null;
        }
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

        System.out.println("IMConfig---identifier" + identifier +
                ",imSig---" + imSig);

        TIMManager.getInstance().login(String.valueOf(identifier),
                imSig,
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        System.out.println("IMConfig登录错误---" + s);
                    }

                    @Override
                    public void onSuccess() {
                        System.out.println("IMConfig登录成功");
                    }
                });
    }

    //会话消息插入或更新
    public static void updateSession(List<TIMMessage> list) {

        System.out.println("消息长度---" + list.size());

        for (TIMMessage timMessage : list) {
            MessageUpdateUtil.getMsgUpdateInstance().updateSessionAndMessage(timMessage,1,false);
        }
    }

    public static void onLineUpdateSession(List<TIMMessage> list) {

        System.out.println("在线消息长度---" + list.size());

        if(list == null){
            return;
        }

        if(list.size() == 0){
            return;
        }

        updateSession(list);

        HashMap<TIMConversationExt, Integer> sessions = new HashMap<>();

        String sid = null;
        for (TIMMessage timMessage : list) {

            sid = timMessage.getMsg().session().sid();
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, sid);

            TIMConversationExt conExt = new TIMConversationExt(con);
            sessions.put(conExt,1);
        }

        Iterator iter = sessions.entrySet().iterator();

        Map.Entry entry = null;
        while (iter.hasNext()) {

            entry = (Map.Entry) iter.next();
            TIMConversationExt conExt = (TIMConversationExt) entry.getKey();

            conExt.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    Log.i(tag, "onError: 群聊设置已读错误---" + s);
                }

                @Override
                public void onSuccess() {
                    Log.i(tag, "onSuccess: 群聊设置已读正确");
                }
            });
        }
    }

    //发送广播
    private static void sendBroadcast(){
        Intent intent = new Intent("messageService");
        intent.putExtra("broadcast_type",1);
        YplayApplication.getInstance().sendBroadcast(intent);
    }
}
