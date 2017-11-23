package com.yeejay.yplay.im;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imcore.SessionType;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.AppManager;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.ImCustomMsgData;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.GsonUtil;
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
                        getOfflineMsgs();
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());


                        for (TIMConversation timCon : conversations) {

                            TIMConversationExt conExt = new TIMConversationExt(timCon);
                            conExt.getMessage(YPlayConstant.YPLAY_OFFINE_MSG_COUNT,
                                    null,
                                    new TIMValueCallBack<List<TIMMessage>>() {
                                        @Override
                                        public void onError(int i, String s) {

                                        }

                                        @Override
                                        public void onSuccess(List<TIMMessage> timMessages) {
                                            System.out.println("onRefreshConversation拉取离线消息");
                                            ImConfig.getImInstance().updateSession(timMessages);
                                        }
                                    });

                            conExt.setReadMessage(null, new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    System.out.println("onRefreshConversation设置会话已读错误---" + s);
                                }

                                @Override
                                public void onSuccess() {
                                    System.out.println("onRefreshConversation设置会话已读成功");
                                }
                            });


                        }

                    }
                });

        //消息扩展用户配置
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //禁用消息存储
                .enableStorage(true)
                //开启消息已读回执
                .enableReadReceipt(true)
                .enableRecentContact(false)
                .enableRecentContactNotify(false);


        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);

        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {//收到新消息
                //消息的内容解析请参考消息收发文档中的消息解析说明
                updateSession(list);

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

    //会话消息插入或更新
    public void updateSession(List<TIMMessage> list) {

        System.out.println("消息长度---" + list.size());
        int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        ImMsgDao imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();

        HashMap<String, Integer> sessions = new HashMap<String, Integer>();

        for (TIMMessage timMessage : list) {

            if (timMessage.getElementCount() == 0) {
                continue;
            }

            String sessionId = timMessage.getMsg().session().sid();
            int sessionType = timMessage.getMsg().session().type().ordinal();
//            String identifier = timMessage.getMsg().session().identifier();
//            String peer       =  timMessage.getMsg().session().

            if (sessionType != SessionType.kGroup.ordinal()) {
                System.out.println("消息会话类型非群会话---" + sessionType);
                continue;
            }

            long msgId = timMessage.getMsgUniqueId();
            String sender = timMessage.getSender();
            int msgType = timMessage.getElement(0).getType().ordinal();
            String msgContent = new String(((TIMCustomElem) timMessage.getElement(0)).getData());
            long msgTs = timMessage.getMsg().time();

            System.out.printf("sessionId:%s, msgId:%d, sender:%s, msgType:%d, msgTs:%d, msgContent:%s\n",
                    sessionId, msgId, sender, msgType, msgTs, msgContent);

            String headerUrl = "";
            String nickName = "";
            int status = -1;

            if (msgType == TIMElemType.Custom.ordinal()) {

                ImCustomMsgData imCustomMsgData = GsonUtil.GsonToBean(msgContent, ImCustomMsgData.class);

                int customType = imCustomMsgData.getDataType();
                String customData = imCustomMsgData.getData();

                if (customType == 1) {//第一次投票消息
                    status = 0;
                }

                if (customType == 2) {//第一次的回复消息
                    status = 1;
                }

                if (customType == 1 || customType == 2) {
                    UserInfoResponde.PayloadBean.InfoBean senderInfo =
                            GsonUtil.GsonToBean(customData, UserInfoResponde.PayloadBean.InfoBean.class);
                    headerUrl = senderInfo.getHeadImgUrl();
                    nickName = senderInfo.getNickName();
                }

            } else {
                status = 2;
            }

            if (status == -1) {
                System.out.println("im message status -1");
                continue;
            }

            sessions.put(sessionId, 1);

            ImMsg imMsg = new ImMsg(null, sessionId, msgId, sender, msgType, msgContent, msgTs);
            try {
                imMsgDao.insert(imMsg);
            } catch (Exception e) {
                System.out.println("消息插入异常");
            }

            //会话表
            String chater = sender;
            if (String.valueOf(uin).equals(sender)) {    //如果sender是自己
                chater = "";
                nickName = "";
                headerUrl = "";
            }

            //如果是第一次投票消息并且发送者是自己  不插入会话表
            if ((status == 0) && (String.valueOf(uin).equals(sender))) {
                continue;
            }

            ImSession imSession = imSessionDao.queryBuilder()
                    .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                    .build().unique();

            if (imSession == null) {
                ImSession session = new ImSession(null,
                        sessionId,
                        chater,
                        status,
                        nickName,
                        headerUrl,
                        msgId,
                        sender,
                        msgType,
                        msgContent,
                        msgTs,
                        0);
                imSessionDao.insert(session);
            } else {
                //更新会话表
                imSession.setLastMsgId(msgId);
                imSession.setLastSender(sender);
                imSession.setMsgContent(msgContent);
                imSession.setMsgType(msgType);
                imSession.setMsgTs(msgTs);
                imSession.setStatus(status);

                if (!TextUtils.isEmpty(chater) && (TextUtils.isEmpty(imSession.getChater()))) {
                    imSession.setChater(chater);
                }

                if (!TextUtils.isEmpty(nickName) && (TextUtils.isEmpty(imSession.getNickName()))) {
                    imSession.setNickName(nickName);
                }

                if (!TextUtils.isEmpty(headerUrl) && (TextUtils.isEmpty(imSession.getHeaderImgUrl()))) {
                    imSession.setHeaderImgUrl(headerUrl);
                }

                imSessionDao.update(imSession);
            }


        }

        Iterator iter = sessions.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String sid = (String) entry.getKey();

            System.out.println("设置会话已读开始---" + sid);

            TIMConversation conversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.Group, sid);

            TIMConversationExt conExt = new TIMConversationExt(conversation);
            conExt.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    System.out.println("设置会话已读错误---" + s);
                }

                @Override
                public void onSuccess() {
                    System.out.println("设置会话已读成功");
                }
            });


        }


    }

    //拉取离线会话消息
    private void getOfflineMsgs() {

        System.out.println("REFRESH获取离线消息");
        List<TIMConversation> offlineList = TIMManagerExt.getInstance().getConversationList();

        for (TIMConversation timCon : offlineList) {

            TIMConversationExt conExt = new TIMConversationExt(timCon);
            conExt.getMessage(YPlayConstant.YPLAY_OFFINE_MSG_COUNT,
                    null,
                    new TIMValueCallBack<List<TIMMessage>>() {
                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onSuccess(List<TIMMessage> timMessages) {
                            System.out.println("IM登录成功，拉取离线消息");
                            ImConfig.getImInstance().updateSession(timMessages);
                        }
                    });

            conExt.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    System.out.println("设置会话已读错误---" + s);
                }

                @Override
                public void onSuccess() {
                    System.out.println("设置会话已读成功");
                }
            });


        }
    }
}
