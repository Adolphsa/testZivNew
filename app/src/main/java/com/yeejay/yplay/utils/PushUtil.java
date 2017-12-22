package com.yeejay.yplay.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tencent.imcore.SessionType;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.message.ActivityChatWindow;
import com.yeejay.yplay.message.ActivityNnonymityReply;
import com.yeejay.yplay.model.ImCustomMsgData;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.userinfo.ActivityMyInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * 在线消息通知展示
 */
public class PushUtil implements Observer {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private  static int pushId = 0;
    private static int requestCode = 0;
    Intent notificationIntent;
    int uin;

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
        uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        String senderStr, contentStr = null, title = null;
        senderStr = msg.getSender();
        String sessionId = msg.getMsg().session().sid();
        int sessionType = msg.getMsg().session().type().ordinal();

        if (sessionType == SessionType.kGroup.ordinal()){
            ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
            ImSession imSession = imSessionDao.queryBuilder()
                    .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                    .build().unique();

            contentStr = DatebaseUtil.getNotificationTitle(imSession);
            title = DatebaseUtil.title;
            System.out.println("PushUtil senderStr---" + senderStr + ",title---" + title);
            int msgType = msg.getElement(0).getType().ordinal();

            int status = imSession.getStatus();
            String sender = imSession.getLastSender();

            if (msgType == TIMElemType.Custom.ordinal()){
                if (status == 1 && !sender.isEmpty() && !sender.equals(String.valueOf(uin))){
                    notificationIntent = new Intent(YplayApplication.getContext(), ActivityChatWindow.class);
                }else {
                    notificationIntent = new Intent(YplayApplication.getContext(), ActivityNnonymityReply.class);
                }

            }else if (msgType == TIMElemType.Text.ordinal()) {

                String text = ((TIMTextElem) msg.getElement(0)).getText();
                System.out.println("PushUtil收到的文本消息---" + text);
                contentStr = text;
                Log.d(TAG, "recv msg " + contentStr);
                notificationIntent = new Intent(YplayApplication.getContext(), ActivityChatWindow.class);
            }



//            String sessionId = imSession.getSessionId();
            String msgContent = imSession.getMsgContent();
            String nickName = imSession.getNickName();

            notificationIntent.putExtra("yplay_sessionId",sessionId);
            notificationIntent.putExtra("yplay_session_status",status);
            notificationIntent.putExtra("yplay_sender",sender);
            notificationIntent.putExtra("yplay_msg_content",msgContent);
            notificationIntent.putExtra("yplay_nick_name",nickName);

            Log.i(TAG, "PushNotify: sessionID---" + sessionId + ",nickname----"
             +nickName + ",msgContent---" + msgContent);

        }else if (sessionType == SessionType.kC2C.ordinal()){
            String msgContent = new String(((TIMCustomElem) msg.getElement(0)).getData());
            ImCustomMsgData imCustomMsgData = GsonUtil.GsonToBean(msgContent, ImCustomMsgData.class);
            int dataType = imCustomMsgData.getDataType();

            if (3 == dataType){
                Log.i(TAG, "PushNotify: 加好友");
                String data = imCustomMsgData.getData();
                title = data;
                contentStr = "同学～加个好友呗(*/ω＼*)";
                notificationIntent = new Intent(YplayApplication.getContext(), ActivityMyInfo.class);
            }else if (4 == dataType){
                title = "冷却解除";
                contentStr = "开始新一轮投票吧(๑‾ ꇴ ‾๑)";
                notificationIntent = new Intent(YplayApplication.getContext(), MainActivity.class);
            }else if (5 == dataType){
                Log.i(TAG, "PushNotify: 动态");
                return;
            }else if (6 == dataType){ //解除好友的推送
                String data = imCustomMsgData.getData();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    int friendUin = jsonObject.getInt("uin");
                    int ts = jsonObject.getInt("ts");
                    Log.i(TAG, "PushNotify: jsonObject friendUin---" + friendUin + ",ts---" + ts);
                    DbHelper dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
                    FriendInfo friendInfo = dbHelper.queryFriendInfo(friendUin);
                    dbHelper.deleteFriendInfo(friendInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (7 == dataType){

                try{
                    String data = imCustomMsgData.getData();
                    UserInfoResponde.PayloadBean.InfoBean msgsBean = GsonUtil.GsonToBean(data,UserInfoResponde.PayloadBean.InfoBean.class);
                    DbHelper dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
                    dbHelper.insertFriendInfo(new FriendInfo(null,
                            msgsBean.getUin(),
                            msgsBean.getNickName(),
                            msgsBean.getHeadImgUrl(),
                            msgsBean.getGender(),
                            msgsBean.getGrade(),
                            msgsBean.getSchoolId(),
                            msgsBean.getSchoolType(),
                            msgsBean.getSchoolName(),
                            msgsBean.getTs()));
                }catch (Exception e){
                    Log.i(TAG, "PushNotify: 7---" + e.getMessage());
                }

            }
        }

        NotificationManager mNotificationManager = (NotificationManager) YplayApplication.getContext().getSystemService(YplayApplication.getContext().NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(YplayApplication.getContext());

        if (notificationIntent != null){
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        PendingIntent intent = PendingIntent.getActivity(YplayApplication.getContext(), ++requestCode,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "PushNotify: requestCode---" + requestCode);

        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏点击意图
                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(title + ":" + contentStr) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher_yplay);//设置通知小ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(++pushId, notify);
        Log.i(TAG, "PushNotify: pushID---" + pushId);

        setHuaWeiBadgenumber(pushNum);
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        Log.i(TAG, "reset: push---" + pushId);
        NotificationManager notificationManager = (NotificationManager) YplayApplication.getContext().getSystemService(YplayApplication.getContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    //设置华为角标
    public static void setHuaWeiBadgenumber(int i){

        try{
            String deviceMan = android.os.Build.MANUFACTURER;
            if (deviceMan.equals("HUAWEI")){
                Bundle extra =new Bundle();
                extra.putString("package", "com.yeejay.yplay");
                extra.putString("class", "com.yeejay.yplay.login.LoadingActivity");
                extra.putInt("badgenumber", i);
                YplayApplication.getInstance().getContentResolver().call(Uri.parse(
                        "content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, extra);
            }

        }catch (Exception e){
            Log.i(TAG, "setHuaWeiBadgenumber: 华为角标---" + e.getMessage());
        }

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
