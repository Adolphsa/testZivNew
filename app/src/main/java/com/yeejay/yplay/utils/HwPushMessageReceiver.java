package com.yeejay.yplay.utils;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushToken;


/**
 * 华为推送接收
 */
public class HwPushMessageReceiver extends PushReceiver {
    private final String TAG = "HwPushMessageReceiver";

    private long mBussId = 2831;

    @Override
    public void onToken(Context context, String token, Bundle extras){
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        Log.e(TAG, content);
        TIMOfflinePushToken param = new TIMOfflinePushToken(mBussId, token);
        TIMManager.getInstance().setOfflinePushToken(param, null);
    }


    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            Log.e(TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            Log.e(TAG, content);
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean b) {
        super.onPushState(context, b);
        Log.i("TAG", "Push连接状态为:" + b);
    }
}
