package com.yeejay.yplay.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;

import java.util.Observable;
import java.util.Observer;

/**
 * 在线消息通知展示
 */
public class PushUtil implements Observer {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private final int pushId = 1;

    private static PushUtil instance = new PushUtil();

    private PushUtil() {
//        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }


    public void PushNotify(TIMMessage msg) {

        //系统消息，自己发的消息，程序在前台的时候不通知
        if (msg == null || Foreground.get().isForeground() ||
                (msg.getConversation().getType() != TIMConversationType.Group &&
                        msg.getConversation().getType() != TIMConversationType.C2C) ||
                msg.isSelf() ||
                msg.getRecvFlag() == TIMGroupReceiveMessageOpt.ReceiveNotNotify)
            return;

        if (msg.getElementCount() == 0) {
            return;
        }


        String senderStr, contentStr = null;
        senderStr = msg.getSender();
        String sessionId = msg.getMsg().session().sid();
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        ImSession imSession = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .build().unique();
        String nickName = imSession.getNickName();

        System.out.println("PushUtil senderStr---" + senderStr + ",nickname---" + nickName);
        int msgType = msg.getElement(0).getType().ordinal();

        if (msgType == TIMElemType.Text.ordinal()) {

            String text = ((TIMTextElem) msg.getElement(0)).getText();
            System.out.println("PushUtil收到的文本消息---" + text);
            contentStr = text;
        }


//        Message message = MessageFactory.getMessage(msg);
//        if (message == null) return;
//        senderStr = message.getSender();
//        contentStr = message.getSummary();
        Log.d(TAG, "recv msg " + contentStr);
        NotificationManager mNotificationManager = (NotificationManager) YplayApplication.getContext().getSystemService(YplayApplication.getContext().NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(YplayApplication.getContext());
        Intent notificationIntent = new Intent(YplayApplication.getContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(YplayApplication.getContext(), 0,
                notificationIntent, 0);
        mBuilder.setContentTitle(nickName)//设置通知栏标题
                .setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(nickName + ":" + contentStr) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher_yplay);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(pushId, notify);
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) YplayApplication.getContext().getSystemService(YplayApplication.getContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
//        if (observable instanceof MessageEvent){
//            if (data instanceof TIMMessage) {
//                TIMMessage msg = (TIMMessage) data;
//                if (msg != null){
//                    PushNotify(msg);
//                }
//            }
//        }
    }
}
