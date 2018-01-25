package com.yeejay.yplay.utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imcore.SessionType;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.im.ImConfig;
import com.yeejay.yplay.model.ImCustomMsgData;
import com.yeejay.yplay.model.ImageInfo;
import com.yeejay.yplay.model.LogUploadRespond;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.model.UserInfoResponde;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 会话消息更新
 * Created by Administrator on 2017/11/28.
 */

public class MessageUpdateUtil {

    private static final String TAG = "MessageUpdateUtil";
    private static final String BASE_URL_USER = "http://sh.file.myqcloud.com";
    private static final String IMAGE_AUTHORIZATION = "ZijsNfCd4w8zOyOIAnbyIykTgBdhPTEyNTMyMjkzNTUmYj15cGxheSZrPUFLSURyWjFFRzQwejcyaTdMS3NVZmFGZm9pTW15d2ZmbzRQViZlPTE1MTcxMjM1ODcmdD0xNTA5MzQ3NTg3JnI9MTAwJnU9MCZmPQ==";

    MessageUpdateListener messageUpdateListener;
    SessionUpdateListener sessionUpdateListener;
    int uin;

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

    private void handleUploadLogs() {
        UpdateTask task = new UpdateTask();
        task.execute();
    }

    private static final String getZipName() {
        String uin = Integer.toString((int)SharePreferenceUtil.get(YplayApplication.getContext(),
                YPlayConstant.YPLAY_UIN, 0));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        return uin + "_" + df.format(System.currentTimeMillis()) + ".zip";
    }

    private static class UpdateTask extends AsyncTask<Void,Integer,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            String dirOri = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "yplay" + File.separator + "logs";
            File file = new File(dirOri);
            if (file.exists()) {
                String dirDest = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "yplay" + File.separator + getZipName();
                try {
                    ZipUtils.zip(dirOri, dirDest);
                } catch (IOException ex) {
                    Log.i(TAG, "zip log file exception! " + ex.getMessage());
                }

                //将指定LOG压缩后，接下来通过网络接口发给服务器；
                uploadLogs(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "yplay", getZipName());
            }

            return null;
        }
    }

    private static void uploadLogs(String logZipPath, final String logZipName){
        final String fileStr = logZipPath + File.separator + logZipName;
        Log.i(TAG, "logZipPath = " + logZipPath + " , logZipName = " + logZipName
                + " , fileStr = " + fileStr);

        File logFile = new File(fileStr);
        if (!logFile.exists()) {
            return;
        }

        Log.i(TAG, "file path = " + logFile.getPath() + " , file name = " + logFile.getName()
                + " , file absolute path = " + logFile.getAbsolutePath());

        byte[] datas = ZipUtils.File2byte(logFile.getAbsolutePath());
        Log.i(TAG, "logZipPath: zip size = " + datas.length);
        RequestBody upload = RequestBody.create(MediaType.parse("application/octstream"), "upload");
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("application/octstream"),
                        new File(logFile.getAbsolutePath())
                );
        MultipartBody.Part aa = MultipartBody.Part.createFormData("filecontent",
                logFile.getAbsolutePath(), requestFile);

        YPlayApiManger.getInstance().getZivApiServiceParameters(BASE_URL_USER)
                .uploadLogs(IMAGE_AUTHORIZATION, logZipName, upload, aa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LogUploadRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull LogUploadRespond logUploadRespond) {
                        if (logUploadRespond.getCode() == 0){
                            //上传成功，则删除在手机上生成的zip文件
                            Log.i(TAG, "upload successfully! ");
                            File file = new File(fileStr);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.i(TAG, "upload error! " + e.getMessage());
                        File file = new File(fileStr);
                        if (file.exists()) {
                            file.delete();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

        //会话列表更新
    public void updateSessionAndMessage(TIMMessage timMessage, int msgSuccess, boolean isOffline) {

        ImMsgDao imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();

        uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);

        if (timMessage.getElementCount() == 0) {
            return;
        }

        String sessionId = timMessage.getMsg().session().sid();
        int sessionType = timMessage.getMsg().session().type().ordinal();


        if (sessionType == SessionType.kC2C.ordinal()) {

            String msgContent = new String(((TIMCustomElem) timMessage.getElement(0)).getData());

            ImCustomMsgData imCustomMsgData = GsonUtil.GsonToBean(msgContent, ImCustomMsgData.class);
            int customType = imCustomMsgData.getDataType();
            if (3 == customType) {   //加好友

                MyInfo myInfo = myInfoDao.queryBuilder()
                        .where(MyInfoDao.Properties.Uin.eq(uin))
                        .build().unique();
                if (myInfo != null) {
                    int addFriendNum = myInfo.getAddFriendNum();
                    Log.i(TAG, "updateSessionAndMessage: addFriendNum---" + addFriendNum);
                    if (!ImConfig.isOffline) {
                        addFriendNum++;
                        Log.i(TAG, "updateSessionAndMessage: addFriendNum2---" + addFriendNum);
                    }

                    myInfo.setAddFriendNum(addFriendNum);
                    myInfoDao.update(myInfo);
                }

                Intent intent = new Intent("messageService");
                intent.putExtra("broadcast_type", 3);
                YplayApplication.getInstance().sendBroadcast(intent);
            } else if (4 == customType) { //冷却
                //冷却
            } else if (5 == customType) { //动态
                Intent intent = new Intent("messageService");
                intent.putExtra("broadcast_type", 5);
                YplayApplication.getInstance().sendBroadcast(intent);
            } else if (6 == customType) { //解除好友的离线通知
                Log.i(TAG, "updateSessionAndMessage: 6");
                if (!ImConfig.getImInstance().isOffline) {
                    try {
                        JSONObject jsonObject = new JSONObject(imCustomMsgData.getData());
                        int friendUin = jsonObject.getInt("uin");
                        int ts = jsonObject.getInt("ts");
                        Log.i(TAG, "PushNotify: jsonObject friendUin---" + friendUin + ",ts---" + ts);
                        DbHelper dbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
                        FriendInfo friendInfo = dbHelper.queryFriendInfo(friendUin);
                        dbHelper.deleteFriendInfo(friendInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (7 == customType) {
                Log.i(TAG, "updateSessionAndMessage: 7");
                if (!ImConfig.getImInstance().isOffline) {
                    try {
                        String data = imCustomMsgData.getData();
                        UserInfoResponde.PayloadBean.InfoBean msgsBean = GsonUtil.GsonToBean(data, UserInfoResponde.PayloadBean.InfoBean.class);
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
                                msgsBean.getTs(),
                                BaseUtils.getSortKey(msgsBean.getNickName()),
                                String.valueOf(SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, 0))));
                    } catch (Exception e) {
                        Log.i(TAG, "PushNotify: 7---" + e.getMessage());
                    }
                }
            } else if (8 == customType) {//设置对用户当前的日志进行操作；
                Log.i(TAG, "updateSessionAndMessage: 8");
                if (!ImConfig.getImInstance().isOffline) {
                    String cmdStr = imCustomMsgData.getData();
                    if ("debug".equals(cmdStr)) {
                        Log.i(TAG, "updateSessionAndMessage: 8, cmd = " + cmdStr);
                        try {
                            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                            loggerContext.getLogger("LOG_WRITE").setLevel(Level.valueOf("DEBUG"));
                        } catch (Exception e) {
                            Log.i(TAG, "exception happened when updating logger level as debug!"
                                    + e.toString());
                        }
                    } else if ("error".equals(cmdStr)) {
                        Log.i(TAG, "updateSessionAndMessage: 8, cmd = " + cmdStr);
                        try {
                            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                            loggerContext.getLogger("LOG_WRITE").setLevel(Level.valueOf("ERROR"));
                        } catch (Exception e) {
                            Log.i(TAG, "exception happened when updating logger level as error"
                                    + e.toString());
                        }
                    } else if ("upload".equals(cmdStr)) {
                        Log.i(TAG, "updateSessionAndMessage: 8, cmd = " + cmdStr);
                        handleUploadLogs();
                    }
                }
            } else if (9 == customType) {//表示有新的提交问题被审核通过了
                //发广播，通知相关界面更新投稿图标为高亮；
                LogUtils.getInstance().debug("new contribute msg arrived!");
                Intent ContributeIntent = new Intent("com.yeejay.br.contribute");
                ContributeIntent.putExtra("contribute_flag", 1);
                YplayApplication.getContext().sendBroadcast(ContributeIntent);
            }

            PushUtil.getInstance().PushNotify(timMessage);

            TIMConversation conversation = TIMManager.getInstance().getConversation(
                    TIMConversationType.C2C,    //会话类型：单聊
                    sessionId);                      //会话对方用户帐号
            Log.i(TAG, "updateSessionAndMessage: 单聊sessionId---" + sessionId);
            //获取会话扩展实例
            TIMConversationExt conExt = new TIMConversationExt(conversation);
            //将此会话的所有消息标记为已读
            conExt.setReadMessage(timMessage, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    Log.e(TAG, "onError: 单聊错误---" + desc);
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: 单聊设置已读成功");
                }
            });

            return;
        }

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

//        System.out.printf("sessionId:%s, msgId:%d, sender:%s, msgType:%d, msgTs:%d, msgContent:%s\n",
//                sessionId, msgId, sender, msgType, msgTs, msgContent);

        //收到文本消息
        if (msgType == TIMElemType.Text.ordinal()) {

            String text = ((TIMTextElem) timMessage.getElement(0)).getText();
            System.out.println("收到的文本消息---" + text);

            status = 2;
            msgContent = text;
        }

        //收到图片消息
        if (msgType == TIMElemType.Image.ordinal()){

            if (String.valueOf(uin).equals(sender)){ //是图片消息并且是自己发送的
                return;
            }

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dirStr = root + File.separator + "yplay" + File.separator + "image";
            File dir = new File(dirStr);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final String imageName = System.currentTimeMillis() + ".jpg";
            TIMElem elem = timMessage.getElement(0);
            TIMImageElem e = (TIMImageElem)elem;
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageFormat(e.getImageFormat());
            Log.i(TAG, "updateSessionAndMessage: imageFormat---" + e.getImageFormat());

            for(TIMImage image : e.getImageList()) {

                //获取图片类型, 大小, 宽高
                Log.i(TAG, "image type: " + image.getType() +
                        " image size " + image.getSize() +
                        " image height " + image.getHeight() +
                        " image width " + image.getWidth()
                );
                TIMImageType imageType = image.getType();
                if (imageType == TIMImageType.Original){
                    ImageInfo.OriginalImage originalImage = new ImageInfo.OriginalImage();
                    originalImage.setImageType(image.getType());
                    originalImage.setImageWidth((int)image.getWidth());
                    originalImage.setImageHeight((int)image.getHeight());
                    originalImage.setImageUrl(image.getUrl());
                    originalImage.setImageSize((int)image.getSize());
                    imageInfo.setOriginalImage(originalImage);
                }else if (imageType == TIMImageType.Thumb){
                    ImageInfo.ThumbImage thumbImage = new ImageInfo.ThumbImage();
                    thumbImage.setImageType(image.getType());
                    thumbImage.setImageWidth((int)image.getWidth());
                    thumbImage.setImageHeight((int)image.getHeight());
                    thumbImage.setImageUrl(image.getUrl());
                    thumbImage.setImageSize((int)image.getSize());
                    imageInfo.setThumbImage(thumbImage);
                }else if (imageType == TIMImageType.Large){
                    ImageInfo.LargeImage largeImage = new ImageInfo.LargeImage();
                    largeImage.setImageType(image.getType());
                    largeImage.setImageWidth((int)image.getWidth());
                    largeImage.setImageHeight((int)image.getHeight());
                    largeImage.setImageUrl(image.getUrl());
                    largeImage.setImageSize((int)image.getSize());
                    imageInfo.setLargeImage(largeImage);
                }
            }

            String imageInfoStr = GsonUtil.GsonString(imageInfo);
            Log.i(TAG, "updateSessionAndMessage: imageInfoStr---" + imageInfoStr);

            status = 2;
            msgContent = imageInfoStr;
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

                MsgContent2.SenderInfoBean senderInfo = content2.getSenderInfo();
                MsgContent2.ReceiverInfoBean receiverInfo = content2.getReceiverInfo();

                System.out.printf("customData  %s\n", customData);

                String receiverNickName = "";
                String receiverHeadImgUrl = "";

                String senderNickName = "";
                String senderHeadImgUrl = "";

                if (receiverInfo == null) {
                    receiverNickName = "";
                    receiverHeadImgUrl = "";
                } else {
                    receiverNickName = receiverInfo.getNickName();
                    receiverHeadImgUrl = receiverInfo.getHeadImgUrl();
                }

                if (senderInfo == null) {
                    senderNickName = "";
                    senderHeadImgUrl = "";
                } else {
                    senderNickName = senderInfo.getNickName();
                    senderHeadImgUrl = senderInfo.getHeadImgUrl();
                }

//                System.out.printf("customType sender nickName %s, headImgUrl %s, receiver nickName %s, headImgUrl %s\n",
//                        senderNickName, senderHeadImgUrl, receiverNickName,receiverHeadImgUrl);

                if (!String.valueOf(uin).equals(sender)) {
                    headerUrl = senderHeadImgUrl;
                    nickName = senderNickName;
                } else {
                    headerUrl = receiverHeadImgUrl;
                    nickName = receiverNickName;
                }
            }

        }

        if (status == -1) {
            System.out.println("im message status -1");
            return;
        }

        ImMsg imMsg = new ImMsg(null, sessionId, msgId, sender, msgType, msgContent, msgTs, msgSuccess);
        if (messageUpdateListener != null) {
            messageUpdateListener.onMessageUpdate(imMsg);
        }

        try {
            imMsgDao.insert(imMsg);
        } catch (Exception e) {
//            System.out.println("消息插入异常");
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
                    uin,
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
                    0,
                    1);

            imSessionDao.insert(session);

            if (sessionUpdateListener != null) {
                sessionUpdateListener.onSessionUpdate(session);
            }

        } else {
            //更新会话表

            //如果数据库的消息更新，不更新数据库的会话信息
            if (imSession.getMsgTs() >= msgTs) {
//                System.out.printf("老的消息到达,不更新会话信息, sessionId %s, msgId %d, curSessionMsgTs %d, msgTs %d\n",
//                        sessionId, msgId, imSession.getMsgTs(), msgTs);
                return;
            }

            //设置会话未读数目
            int unreadNum = imSession.getUnreadMsgNum();
            if (!sender.isEmpty() && !sender.equals(String.valueOf(uin))) {
                unreadNum++;
            }

            Log.i(TAG, "updateSessionAndMessage: unreadNum---" + unreadNum
                    + ",sender---" + sender
                    + ",uin---" + uin);

            imSession.setLastMsgId(msgId);
            imSession.setLastSender(sender);
            imSession.setMsgContent(msgContent);
            imSession.setMsgType(msgType);
            imSession.setMsgTs(msgTs);
            imSession.setStatus(status);
            imSession.setUnreadMsgNum(unreadNum);

//            System.out.printf("chater [%s][%s], nickName [%s][%s], headerUrl [%s][%s]",
//                    chater, imSession.getChater(), nickName, imSession.getNickName(), headerUrl, imSession.getHeaderImgUrl());

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

            if (sessionUpdateListener != null) {
                sessionUpdateListener.onSessionUpdate(imSession);
            }
        }

        PushUtil.getInstance().PushNotify(timMessage);

    }
}
