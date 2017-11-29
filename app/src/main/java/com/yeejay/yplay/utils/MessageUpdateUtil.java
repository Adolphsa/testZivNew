package com.yeejay.yplay.utils;

import android.text.TextUtils;

import com.tencent.imcore.SessionType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.ImCustomMsgData;
import com.yeejay.yplay.model.MsgContent2;

/**
 * 会话消息更新
 * Created by Administrator on 2017/11/28.
 */

public class MessageUpdateUtil {

    MessageUpdateListener messageUpdateListener;
    SessionUpdateListener sessionUpdateListener;

    public void setMessageUpdateListener(MessageUpdateListener messageUpdateListener) {
        this.messageUpdateListener = messageUpdateListener;
    }

    public void setSessionUpdateListener(SessionUpdateListener sessionUpdateListener) {
        this.sessionUpdateListener = sessionUpdateListener;
    }

    //消息更新接口
    public interface MessageUpdateListener {
        void onMessageUpdate(ImMsg imMsg);

    }

    //会话列表更新接口
    public interface SessionUpdateListener {
        void onSessionUpdate(ImSession imSession);

    }

    private MessageUpdateUtil() {
    }

    public static synchronized MessageUpdateUtil getMsgUpdateInstance() {
        return MessageUpdateUtilSingletonHolder.instance;
    }

    private static class MessageUpdateUtilSingletonHolder {
        private static final MessageUpdateUtil instance = new MessageUpdateUtil();
    }

    //会话列表更新
    public void updateSessionAndMessage(TIMMessage timMessage,int msgSuccess){

        ImMsgDao imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);

        if (timMessage.getElementCount() == 0) {
            return;
        }

        String sessionId = timMessage.getMsg().session().sid();
        int sessionType = timMessage.getMsg().session().type().ordinal();

        if (sessionType != SessionType.kGroup.ordinal()) {
            System.out.println("消息会话类型非群会话---" + sessionType);
            return;
        }

        long msgId = timMessage.getMsgUniqueId();
        String sender = timMessage.getSender();
        int msgType = timMessage.getElement(0).getType().ordinal();
        long msgTs = timMessage.getMsg().time();

        String headerUrl = "";
        String nickName = "";
        int status = -1;

        String msgContent = "";

        //会话表
        String chater = sender;
        if (String.valueOf(uin).equals(sender)) {    //如果sender是自己
            chater = "";
            nickName = "";
            headerUrl = "";
        }

        System.out.printf("sessionId:%s, msgId:%d, sender:%s, msgType:%d, msgTs:%d, msgContent:%s\n",
                sessionId, msgId, sender, msgType, msgTs, msgContent);

        //收到文本消息
        if(msgType == TIMElemType.Text.ordinal()){

            String text = ((TIMTextElem)timMessage.getElement(0)).getText();
            System.out.println("收到的文本消息---" + text);

            status = 2;
            msgContent = text;
        }

        if (msgType == TIMElemType.Custom.ordinal()) {

            msgContent = new String(((TIMCustomElem) timMessage.getElement(0)).getData());

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

                MsgContent2 content2 = GsonUtil.GsonToBean(customData, MsgContent2.class);

                MsgContent2.SenderInfoBean   senderInfo   = content2.getSenderInfo();
                MsgContent2.ReceiverInfoBean receiverInfo = content2.getReceiverInfo();

                /*
                MsgContent2.SenderInfoBean senderInfo =
                        ;GsonUtil.GsonToBean(customData, MsgContent2.SenderInfoBean.class)

                MsgContent2.ReceiverInfoBean receiverInfo =
                        GsonUtil.GsonToBean(customData, MsgContent2.ReceiverInfoBean.class);
                */

                System.out.printf("customData  %s\n", customData);

                System.out.printf("customType sender nickName %s, headImgUrl %s, receiver nickName %s, headImgUrl %s\n",
                        senderInfo.getNickName(), senderInfo.getHeadImgUrl(), receiverInfo.getNickName(),receiverInfo.getHeadImgUrl());

                if (!String.valueOf(uin).equals(sender)){
                    headerUrl = senderInfo.getHeadImgUrl();
                    nickName = senderInfo.getNickName();
                }else{
                    headerUrl = receiverInfo.getHeadImgUrl();
                    nickName  = receiverInfo.getNickName();
                }
            }

        }

        if (status == -1) {
            System.out.println("im message status -1");
            return;
        }

        ImMsg imMsg = new ImMsg(null, sessionId, msgId, sender, msgType, msgContent, msgTs,msgSuccess);
        if (messageUpdateListener != null){
            messageUpdateListener.onMessageUpdate(imMsg);
        }


        try {
            imMsgDao.insert(imMsg);
        } catch (Exception e) {
            System.out.println("消息插入异常");
        }

        //如果是第一次投票消息并且发送者是自己  不插入会话表
        if ((status == 0) && (String.valueOf(uin).equals(sender))) {
            return;
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

            if (sessionUpdateListener != null) {
                sessionUpdateListener.onSessionUpdate(session);
            }

        } else {
            //更新会话表

            //如果数据库的消息更新，不更新数据库的会话信息
            if(imSession.getMsgTs() >= msgTs){
                System.out.printf("老的消息到达,不更新会话信息, sessionId %s, msgId %d, curSessionMsgTs %d, msgTs %d\n",
                        sessionId, msgId, imSession.getMsgTs(), msgTs);
                return;
            }

            imSession.setLastMsgId(msgId);
            imSession.setLastSender(sender);
            imSession.setMsgContent(msgContent);
            imSession.setMsgType(msgType);
            imSession.setMsgTs(msgTs);
            imSession.setStatus(status);

            System.out.printf("chater [%s][%s], nickName [%s][%s], headerUrl [%s][%s]",
                    chater, imSession.getChater(), nickName, imSession.getNickName(), headerUrl, imSession.getHeaderImgUrl());

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

            if (sessionUpdateListener != null){
                sessionUpdateListener.onSessionUpdate(imSession);
            }
        }
    }
}
