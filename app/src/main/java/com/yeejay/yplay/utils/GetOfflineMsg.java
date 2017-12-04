package com.yeejay.yplay.utils;

import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.im.ImConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 拉取离线消息
 * Created by Administrator on 2017/11/29.
 */

public class GetOfflineMsg {

    private static final String TAG = "GetOfflineMsg";

    //拉取离线会话消息
    public static void getOfflineMsgs() {

        final ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        List<TIMConversation> offlineList = TIMManagerExt.getInstance().getConversationList();
        if (offlineList != null) {
            Log.i(TAG, "getOfflineMsgs: 获取会话数目---" + offlineList.size());
        } else {
            Log.i(TAG, "getOfflineMsgs: 获取会话数目无");
        }

        HashMap<TIMConversationExt, Integer> sessions = new HashMap<>();

        for (TIMConversation timCon : offlineList) {

            final TIMConversationExt conExt = new TIMConversationExt(timCon);
            final long num = conExt.getUnreadMessageNum();

            Log.i(TAG, "getOfflineMsgs: 未读消息数目---" + num);

            if (num == 0) {
                continue;
            }

            conExt.getMessage((int) num, null, new TIMValueCallBack<List<TIMMessage>>() {

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (timMessages == null) {
                        return;
                    }
                    //设置会话未读数目
                    Log.i(TAG, "getMessages return size: ---" + timMessages.size());
                    String sessionId = timMessages.get(0).getMsg().session().sid();
                    Log.i(TAG, "sessionId: ---" + sessionId);
                    ImSession imSession = imSessionDao.queryBuilder()
                            .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                            .build().unique();
                    if (imSession != null){
                        int unreadNum = imSession.getUnreadMsgNum();
                        unreadNum = unreadNum + timMessages.size();

                        imSession.setUnreadMsgNum(unreadNum);
                        imSessionDao.update(imSession);
                    }

                    ImConfig.getImInstance().updateSession(timMessages);
                }
            });

            sessions.put(conExt, 1);
        }

        Iterator iter = sessions.entrySet().iterator();

        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            TIMConversationExt conExt = (TIMConversationExt) entry.getKey();

            conExt.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, "onError: 设置会话已读错误---" + s);
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: 设置会话已读成功");
                }
            });
        }
    }
}
