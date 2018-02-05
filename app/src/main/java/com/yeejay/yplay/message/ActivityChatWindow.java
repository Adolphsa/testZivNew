package com.yeejay.yplay.message;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.PhotoUtils;
import com.donkingliang.imageselector.utils.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ChatAdapter;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CardBigDialog;
import com.yeejay.yplay.customview.HeadRefreshView;
import com.yeejay.yplay.customview.NoDataView;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.friend.ActivityFriendsInfo;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.FriendInfoDao;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.ImageInfo;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.MessageUpdateUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import org.greenrobot.greendao.query.DeleteQuery;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.donkingliang.imageselector.ImageSelectorActivity.hasSdcard;

public class ActivityChatWindow extends BaseActivity implements MessageUpdateUtil.MessageUpdateListener {

    private static final String TAG = "ActivityChatWindow";
    private static final int REQUEST_CODE = 0x00000011;
    private static final int CODE_CAMERA_REQUEST = 0xa01;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0xa03;

    private static final int RESULT_CODE_FRIEND_CHAT_REPLY = 2;

    private File fileUri;
    private Uri imageUri;

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.acw_title)
    RelativeLayout layoutTitleRl;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.acw_recycle_view)
    RecyclerView acwRecycleView;
    @BindView(R.id.acw_img_choice)
    ImageButton acwImgChoice;
    @BindView(R.id.acw_edit)
    EditText acwEdit;
    @BindView(R.id.acw_send)
    ImageButton acwSend;
    @BindView(R.id.acw_pull_refresh)
    PullToRefreshLayout acwRefreshView;
    private LinearLayoutManager linearLayoutManager;

    @OnClick(R.id.layout_title_back2)
    public void back() {
        Intent intent = new Intent();
        intent.putExtra("update_sessionID", sessionId);
        intent.putExtra("has_new_mesg", mHasNewMsg);
        setResult(RESULT_CODE_FRIEND_CHAT_REPLY, intent);
        finish();
    }

    @OnClick(R.id.layout_setting)
    public void megMore() {
        showBottomDialog();
        LogUtils.getInstance().debug("聊天对象资料");
    }

    @OnClick(R.id.acw_img_choice)
    public void acwImgChoice() {
        LogUtils.getInstance().debug("acwImgChoice: 图片选择");
        showImageBottomDialog();
    }

    @OnClick(R.id.acw_send)
    public void send() {
        sendTextMessage();
    }

    //发送文本消息
    private void sendTextMessage() {

        //点击之后立马变为不可点状态
        acwSend.setClickable(false);
        acwSend.setImageResource(R.drawable.feather_no);

        //如果网络异常，提示用户后返回
        if (!NetWorkUtil.isNetWorkAvailable(ActivityChatWindow.this)) {
            Toast.makeText(ActivityChatWindow.this, "网络异常", Toast.LENGTH_SHORT).show();
            LogUtils.getInstance().error("uin {}, 发送消息，网络异常", uin);
            return;
        }

        //获取要发送的文本
        final String str = acwEdit.getText().toString().trim();
        LogUtils.getInstance().debug("uin {}, 发送文本消息 {}", uin, str);

        //如果是空，则直接返回
        if (TextUtils.isEmpty(str)) {
            LogUtils.getInstance().debug("uin {}, 发送文本消息，文本内容为空!", uin);
            Toast.makeText(ActivityChatWindow.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ImSession imSession = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .build().unique();

        if (imSession == null) {
            LogUtils.getInstance().error("uin {}, 发送文本消息{}，在数据库未找到sessionId {}", uin, str, sessionId);
            return;
        }

        //插入时间戳;
        insertTimestampMsg();

        final String chater = imSession.getChater();
        LogUtils.getInstance().debug("uin {}, 发送文本消息{}，chater {}", uin, str, chater);

        //如果不是好友 则提示用户非好友信息后 返回
        FriendInfo friendInfo = mDbHelper.queryFriendInfo(Integer.valueOf(chater), uin);
        if (friendInfo == null) {

            LogUtils.getInstance().error("uin {}, 发送文本消息{}，chater {}, 已经非好友！", uin, str, chater);

            ImMsg imMsg0 = new ImMsg(null,
                    sessionId,
                    System.currentTimeMillis(),
                    String.valueOf(uin),
                    101,
                    str,
                    (System.currentTimeMillis() / 1000),
                    1);
            imMsgDao.insert(imMsg0);

            ImMsg imMsg1 = new ImMsg(null,
                    sessionId,
                    System.currentTimeMillis(),
                    String.valueOf(uin),
                    100,
                    "对方已不是你的好友",
                    (System.currentTimeMillis() / 1000),
                    1);
            imMsgDao.insert(imMsg1);

            ImMsg imMsg2 = new ImMsg(null,
                    sessionId,
                    System.currentTimeMillis(),
                    String.valueOf(uin),
                    100,
                    "先和对方成为好友，才能聊天哦~",
                    (System.currentTimeMillis() / 1000),
                    1);
            imMsgDao.insert(imMsg2);

            mDataList.add(0, imMsg0);
            mDataList.add(0, imMsg1);
            mDataList.add(0, imMsg2);
            chatAdapter.notifyDataSetChanged();
            acwEdit.setText("");
            acwRecycleView.scrollToPosition(mDataList.size() - 1);
            return;
        }

        //构造一条消息
        final TIMMessage msg = new TIMMessage();
        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(str);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            LogUtils.getInstance().error("uin {}, 发送文本消息{}，chater {}, addElement error", uin, str, chater);
            return;
        }

        //取毫秒数作为临时的一个消息ID
        msgId = System.currentTimeMillis();

        //msgSuccess -> 0 表示发送中 1->表示发送成功 2表示发送失败
        //先插入到消息和会话表中，在成功或者失败之后，更新其对应的消息状态msgSuccess->1或者msgSuccess->2
        ImMsg imMsg = new ImMsg(null, sessionId, msgId, String.valueOf(uin), TIMElemType.Text.ordinal(), str, msgId / 1000, 0);
        try {
            imMsgDao.insert(imMsg);
        } catch (Exception e) {
            LogUtils.getInstance().error("uin {}, 发送文本消息{}，chater {}, insert to msgtable error {}", uin, str, chater, e.getMessage());
            return;
        }

        //更新adpater里面的数据
        mDataList.add(0, imMsg);
        chatAdapter.notifyDataSetChanged();
        acwRecycleView.scrollToPosition(mDataList.size() - 1);

        //更新会话表中的信息
        imSession.setLastMsgId(msgId);
        imSession.setLastSender(String.valueOf(uin));
        imSession.setMsgType(TIMElemType.Text.ordinal());
        imSession.setMsgTs(msgId / 1000);
        imSession.setMsgContent(str);
        imSession.setUnreadMsgNum(0);

        imSessionDao.update(imSession);

        TIMMessageOfflinePushSettings offlineSettings = new TIMMessageOfflinePushSettings();
        offlineSettings.setDescr(str);
        offlineSettings.setEnabled(true);

        TIMMessageOfflinePushSettings.AndroidSettings andSetting = new TIMMessageOfflinePushSettings.AndroidSettings();
        andSetting.setTitle(myselfNickName);
        andSetting.setNotifyMode(TIMMessageOfflinePushSettings.NotifyMode.Custom);

        TIMMessageOfflinePushSettings.IOSSettings iosSettings = new TIMMessageOfflinePushSettings.IOSSettings();
        iosSettings.setBadgeEnabled(true);

        offlineSettings.setAndroidSettings(andSetting);
        offlineSettings.setIosSettings(iosSettings);
        msg.setOfflinePushSettings(offlineSettings);

        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.Group,      //会话类型：群组
                sessionId);                     //群组Id

        LogUtils.getInstance().debug("uin {}, 发送文本消息 {}，chater {}, 发送中...", uin, str, chater);

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调

            @Override
            public void onError(int code, String desc) {//发送消息失败
                mHasNewMsg = false;

                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表

                LogUtils.getInstance().error("uin {}, 发送文本消息{}，chater {}, 发送失败 code {}, desc {}", uin, str, chater, code, desc);

                ImMsg timMsg = imMsgDao.queryBuilder()
                        .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                        .where(ImMsgDao.Properties.MsgId.eq(msgId))
                        .build().unique();

                if (timMsg != null) {
                    //更新数据库
                    timMsg.setMsgSucess(2);//设置为消息发送失败
                    imMsgDao.update(timMsg);

                    //更新adpater里面的数据
                    ImMsg t;
                    for(int i = 0; i < mDataList.size(); ++i){
                        t = mDataList.get(i);
                        if(t!=null){
                            if (t.getMsgId() == msgId){
                                mDataList.get(i).setMsgSucess(2);
                                chatAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }

                ImSession timSession = imSessionDao.queryBuilder()
                        .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                        .build().unique();
                if (timSession != null) {
                    //设置msg发送失败
                    //imSessionDao.update(timSession);
                }

                //更新会话列表
                //MessageUpdateUtil.getMsgUpdateInstance().updateSessionAndMessage(msg, 0, false);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                mHasNewMsg = true;

                long tmsgId = msg.getMsgUniqueId();
                long tmsgTs = msg.getMsg().time();

                LogUtils.getInstance().debug("uin {}, 发送文本消息 {}，chater {}, 发送成功 msgId {}, msgTs {}", uin, str, chater, tmsgId, tmsgTs);

                ImMsg timMsg = imMsgDao.queryBuilder()
                        .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                        .where(ImMsgDao.Properties.MsgId.eq(msgId))
                        .build().unique();

                if (timMsg != null) {

                    //更新数据库
                    timMsg.setMsgId(tmsgId);
                    //timMsg.setMsgTs(tmsgTs);
                    timMsg.setMsgSucess(1);//设置为消息发送成功
                    imMsgDao.update(timMsg);

                    //更新adpater里面的数据
                    ImMsg t;
                    for(int i = 0; i < mDataList.size(); ++i){
                        t = mDataList.get(i);
                        if (t != null){
                            if (t.getMsgId() == tmsgId){
                                mDataList.get(i).setMsgSucess(1);
                                chatAdapter.notifyDataSetChanged();

                                break;
                            }
                        }
                    }
                }

                ImSession timSession = imSessionDao.queryBuilder()
                        .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                        .build().unique();
                if (timSession != null) {
                    timSession.setLastMsgId(tmsgId);
                    timSession.setMsgTs(tmsgTs);
                    //todo 更新状态

                    imSessionDao.update(timSession);
                }

                //更新会话列表
                //MessageUpdateUtil.getMsgUpdateInstance().updateSessionAndMessage(msg, 1, false);

            }
        });

        acwEdit.setText("");
    }

    //发送图片消息
    private void sendImageMessage(String imagePath){

        //点击之后立马变为不可点状态
        acwSend.setClickable(false);
        acwSend.setImageResource(R.drawable.feather_no);

        //如果网络异常，提示用户后返回
        if (!NetWorkUtil.isNetWorkAvailable(ActivityChatWindow.this)) {
            Toast.makeText(ActivityChatWindow.this, "网络异常", Toast.LENGTH_SHORT).show();
            LogUtils.getInstance().error("uin {}, 发送消息，网络异常", uin);
            return;
        }

        //如果是空，则直接返回
        if (TextUtils.isEmpty(imagePath)) {
            LogUtils.getInstance().debug("uin {}, 发送图片消息，图片路径为空!", uin);
            Toast.makeText(ActivityChatWindow.this, "发送图片不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ImSession imSession = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .build().unique();

        if (imSession == null) {
            LogUtils.getInstance().error("uin {}, 发送图片消息{}，在数据库未找到sessionId {}", uin, imagePath, sessionId);
            return;
        }

        //构造消息表中的content字段 是一个json结构的字符串
        File imageFile = new File(imagePath);
        //实际使用过程中出现过发消息时拍照图片bitmap null pointer导致的异常，
        // 故此处先判断下图片文件确实存在;
        if (!imageFile.exists()) {
            LogUtils.getInstance().error("uin {}, 发送图片消息{}, 图片不存在", uin, imagePath);
            return;
        }

        //插入时间戳;
        insertTimestampMsg();

        ImageInfo imageInfo = new ImageInfo();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int imageFormat = getImageFormat(imagePath);
        imageInfo.setImageFormat(imageFormat);

        ImageInfo.OriginalImage originalImage = new ImageInfo.OriginalImage();
        originalImage.setImageType(TIMImageType.Original);
        originalImage.setImageWidth(bitmap.getWidth());
        originalImage.setImageHeight(bitmap.getHeight());
        originalImage.setImageUrl(imagePath);
        imageInfo.setOriginalImage(originalImage);

        ImageInfo.ThumbImage thumbImage = new ImageInfo.ThumbImage();
        thumbImage.setImageType(TIMImageType.Thumb);
        thumbImage.setImageWidth(bitmap.getWidth());
        thumbImage.setImageHeight(bitmap.getHeight());
        thumbImage.setImageUrl(imagePath);
        imageInfo.setThumbImage(thumbImage);

        ImageInfo.LargeImage largeImage = new ImageInfo.LargeImage();
        largeImage.setImageType(TIMImageType.Large);
        largeImage.setImageWidth(bitmap.getWidth());
        largeImage.setImageHeight(bitmap.getHeight());
        largeImage.setImageUrl(imagePath);
        imageInfo.setLargeImage(largeImage);

        //消息表中的字符串
        final String imageInfoStr = GsonUtil.GsonString(imageInfo);

        final String chater = imSession.getChater();
        LogUtils.getInstance().debug("uin {}, 发送图片消息{}，chater {}", uin, imagePath, chater);

        //取毫秒数作为临时的一个消息ID
        msgId = System.currentTimeMillis();

        //如果不是好友 则提示用户非好友信息后 返回
        FriendInfo friendInfo = mDbHelper.queryFriendInfo(Integer.valueOf(chater), uin);
        if (friendInfo == null) {

            LogUtils.getInstance().error("uin {}, 发送图片消息{}，chater {}, 已经非好友！", uin, imagePath, chater);

            ImMsg imMsg0 = new ImMsg(null,
                    sessionId,
                    msgId,
                    String.valueOf(uin),
                    TIMElemType.Image.ordinal(),
                    imageInfoStr,
                    (msgId / 1000),
                    2); //发送失败
            imMsgDao.insert(imMsg0);

            ImMsg imMsg1 = new ImMsg(null,
                    sessionId,
                    msgId+1,
                    String.valueOf(uin),
                    100,
                    "对方已不是你的好友",
                    ((msgId+1) / 1000),
                    1);
            imMsgDao.insert(imMsg1);

            ImMsg imMsg2 = new ImMsg(null,
                    sessionId,
                    msgId+2,
                    String.valueOf(uin),
                    100,
                    "先和对方成为好友，才能聊天哦~",
                    ((msgId+2) / 1000),
                    1);
            imMsgDao.insert(imMsg2);

            mDataList.add(0, imMsg0);
            mDataList.add(0, imMsg1);
            mDataList.add(0, imMsg2);
            chatAdapter.notifyDataSetChanged();
            acwEdit.setText("");
            acwRecycleView.scrollToPosition(mDataList.size() - 1);
            return;
        }

        //msgSuccess -> 0 表示发送中 1->表示发送成功 2表示发送失败
        //先插入到消息和会话表中，在成功或者失败之后，更新其对应的消息状态msgSuccess->1或者msgSuccess->2
        ImMsg imMsg = new ImMsg(null, sessionId, msgId, String.valueOf(uin), TIMElemType.Image.ordinal(), imageInfoStr, msgId / 1000, 0);
        try {
            imMsgDao.insert(imMsg);
        } catch (Exception e) {
            LogUtils.getInstance().error("uin {}, 发送图片消息{}，chater {}, insert to msgtable error {}", uin, imageInfoStr, chater, e.getMessage());
            return;
        }

        //更新adpater里面的数据
        mDataList.add(0, imMsg);
        chatAdapter.notifyDataSetChanged();
        acwRecycleView.scrollToPosition(mDataList.size() - 1);

        //更新会话表中的信息
        imSession.setLastMsgId(msgId);
        imSession.setLastSender(String.valueOf(uin));
        imSession.setMsgType(TIMElemType.Image.ordinal());
        imSession.setMsgTs(msgId / 1000);
        imSession.setUnreadMsgNum(0);

        imSessionDao.update(imSession);

        //构造一条消息
        final TIMMessage msg = new TIMMessage();

        //添加图片
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(imagePath);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            LogUtils.getInstance().error("uin {}, 发送图片消息{}，chater {}, addElement error", uin, imageInfoStr, chater);
            return;
        }

        TIMMessageOfflinePushSettings offlineSettings = new TIMMessageOfflinePushSettings();
        offlineSettings.setDescr("[图片]");
        offlineSettings.setEnabled(true);

        TIMMessageOfflinePushSettings.AndroidSettings andSetting = new TIMMessageOfflinePushSettings.AndroidSettings();
        andSetting.setTitle(myselfNickName);
        andSetting.setNotifyMode(TIMMessageOfflinePushSettings.NotifyMode.Custom);

        TIMMessageOfflinePushSettings.IOSSettings iosSettings = new TIMMessageOfflinePushSettings.IOSSettings();
        iosSettings.setBadgeEnabled(true);

        offlineSettings.setAndroidSettings(andSetting);
        offlineSettings.setIosSettings(iosSettings);
        msg.setOfflinePushSettings(offlineSettings);

        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.Group,      //会话类型：群组
                sessionId);                     //群组Id

        LogUtils.getInstance().debug("uin {}, 发送图片消息 {}，chater {}, 发送中...", uin, imageInfoStr, chater);

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调

            @Override
            public void onError(int code, String desc) {//发送消息失败
                mHasNewMsg = false;

                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表

                LogUtils.getInstance().error("uin {}, 发送图片消息{}，chater {}, 发送失败 code {}, desc {}", uin, imageInfoStr, chater, code, desc);

                ImMsg timMsg = imMsgDao.queryBuilder()
                        .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                        .where(ImMsgDao.Properties.MsgId.eq(msgId))
                        .build().unique();

                if (timMsg != null) {
                    //更新数据库
                    timMsg.setMsgSucess(2);//设置为消息发送失败
                    imMsgDao.update(timMsg);

                    //更新adpater里面的数据
                    ImMsg t;
                    for(int i = 0; i < mDataList.size(); ++i){
                        t = mDataList.get(i);
                        if(t!=null){
                            if (t.getMsgId() == msgId){
                                mDataList.get(i).setMsgSucess(2);
                                chatAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }

                ImSession timSession = imSessionDao.queryBuilder()
                        .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                        .build().unique();
                if (timSession != null) {
                    //todo 更新状态
                    //设置msg发送失败
                    //imSessionDao.update(timSession);
                }
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                mHasNewMsg = true;

                long tmsgId = msg.getMsgUniqueId();
                long tmsgTs = msg.getMsg().time();

                LogUtils.getInstance().debug("uin {}, 发送图片消息 {}，chater {}, 发送成功 msgId {}, msgTs {}", uin, imageInfoStr, chater, tmsgId, tmsgTs);

                ImMsg timMsg = imMsgDao.queryBuilder()
                        .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                        .where(ImMsgDao.Properties.MsgId.eq(msgId))
                        .build().unique();

                if (timMsg != null) {

                    //更新数据库
                    timMsg.setMsgId(tmsgId);
                    //timMsg.setMsgTs(tmsgTs);
                    timMsg.setMsgSucess(1);//设置为消息发送成功
                    imMsgDao.update(timMsg);

                    //更新adpater里面的数据
                    ImMsg t;
                    for(int i = 0; i < mDataList.size(); ++i){
                        t = mDataList.get(i);
                        if (t != null){
                            if (t.getMsgId() == tmsgId){
                                mDataList.get(i).setMsgSucess(1);
                                chatAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }

                ImSession timSession = imSessionDao.queryBuilder()
                        .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                        .build().unique();
                if (timSession != null) {
                    timSession.setLastMsgId(tmsgId);
                    timSession.setMsgTs(tmsgTs);
                    //todo 更新状态
                    imSessionDao.update(timSession);
                }
            }
        });

        acwEdit.setText("");
    }

    String sessionId;
    long msgId;
    ImMsgDao imMsgDao;
    ImSessionDao imSessionDao;
    FriendInfoDao friendInfoDao;
    int dataOffset = 0;
    List<ImMsg> mDataList;
    ChatAdapter chatAdapter;
    String nickName;
    String myselfNickName;
    int uin;
    int mSender;
    int status;
    String tempNickname;
    String tempNickname2;
    StringBuilder gradeAndGenderStr;

    TIMConversation conversation;

    HeadRefreshView headRefreshView;
    NoDataView noDataView;
    DbHelper mDbHelper;

    private boolean mHasNewMsg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityChatWindow.this, true);

        MessageUpdateUtil.getMsgUpdateInstance().setMessageUpdateListener(this);
        mDbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
        imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        friendInfoDao = YplayApplication.getInstance().getDaoSession().getFriendInfoDao();
        uin = (int) SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_UIN, (int) 0);
        myselfNickName = (String) SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_NICK_NAME, "");

        receiveBundleData();

        initTitle();

        linearLayoutManager = new LinearLayoutManager(this);
        acwRecycleView.setLayoutManager(linearLayoutManager);

        LogUtils.getInstance().debug("onCreate: status = {}, uin = {}, mSender = {}",
                status, uin, mSender);

        if (1 == status && (uin == mSender)) {
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的姓名");

            ImMsg imMsg2 = new ImMsg();
            imMsg2.setMsgType(100);
            imMsg2.setMsgContent("对方回复后，双方互相实名，能够继续聊天 ");
            mDataList.add(0, imMsg1);
            mDataList.add(0, imMsg2);

            acwImgChoice.setVisibility(View.GONE);

            LogUtils.getInstance().debug("onCreate: mDataList-size = {}", mDataList.size());
        }

        if (1 == status && (uin != mSender) && mDataList.size() == 2) {
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("此时回复，对方将看到你的真实姓名");
            mDataList.add(0, imMsg1);

            acwImgChoice.setVisibility(View.VISIBLE);
        }

        if (1 == status && (uin != mSender) && mDataList.size() == 4) {
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的真实姓名");
            mDataList.add(0, imMsg1);

            acwImgChoice.setVisibility(View.VISIBLE);
        }

        chatAdapter = new ChatAdapter(ActivityChatWindow.this, mDataList);
        acwRecycleView.setAdapter(chatAdapter);

        chatAdapter.setItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (view.getId() == R.id.msg_item_image_left || view.getId() == R.id.msg_item_image_right) {
                    //处理消息中的图片点击事件；
                    int position = (Integer) view.getTag();
                    int realPosition = mDataList.size() - (position + 1);
                    LogUtils.getInstance().debug("onItemClick: 图片被点击了---{}, position = {}, realPosition = {}",
                            mDataList.get(realPosition).getMsgContent(), position, realPosition);
                    String imageInfoStr = mDataList.get(realPosition).getMsgContent();
                    String currentSender = mDataList.get(realPosition).getSender();
                    ImageInfo imageInfo = GsonUtil.GsonToBean(imageInfoStr, ImageInfo.class);
                    String url = imageInfo.getLargeImage().getImageUrl();
                    int imageFormat = imageInfo.getImageFormat();
                    int largeWidth = imageInfo.getLargeImage().getImageWidth();
                    int largeHeight = imageInfo.getLargeImage().getImageHeight();
                    if (!TextUtils.isEmpty(url) && imageFormat != TIMImageElem.TIM_IMAGE_FORMAT_GIF) {
                        showImageDialog(url, largeWidth, largeHeight, currentSender);
                    }

                    View llView = linearLayoutManager.findViewByPosition(position);

                    if (llView == null) return;
                    if (uin == Integer.valueOf(currentSender)) {    //发送者是自己
//                    RelativeLayout rightRl = (RelativeLayout) llView;
//                    ImageView rightImgView = (ImageView) rightRl.findViewById(R.id.msg_item_image_right);
//
//                    ViewGroup.LayoutParams lp = rightImgView.getLayoutParams();
//                    if (lp == null) return;
//                    Picasso.with(ActivityChatWindow.this).load(url)
//                            .resize(lp.width, lp.height)
//                            .config(Bitmap.Config.RGB_565)
//                            .centerCrop()
//                            .into(rightImgView);

                        LogUtils.getInstance().debug("onItemClick: 右设置大图");
                    } else {                 //非自己
                        RelativeLayout leftRl = (RelativeLayout) llView;
                        ImageView leftImgView = (ImageView) leftRl.findViewById(R.id.msg_item_image_left);

                        ViewGroup.LayoutParams lp = leftImgView.getLayoutParams();
                        if (lp == null) return;
                        Picasso.with(ActivityChatWindow.this).load(url)
                                .resize(lp.width, lp.height)
                                .config(Bitmap.Config.RGB_565)
                                .centerCrop()
                                .into(leftImgView);
                        LogUtils.getInstance().debug("onItemClick: 左设置大图");
                    }
                } else if (view.getId() == R.id.msg_item_right_not_friend) {
                    //处理文本消息发送失败时，感叹号的点击事件;
                    handleTextClickExclamation((Integer)view.getTag());
                } else if (view.getId() == R.id.msg_item_image_right_not_friend) {
                    //处理图片发送失败时，感叹号的点击事件：
                    handleImgClickExclamation((Integer)view.getTag());
                }
            }
        });

        if (mDataList != null && mDataList.size() > 0) {
            acwRecycleView.scrollToPosition(mDataList.size() - 1);
        }

        acwRefreshView.setCanLoadMore(false);
        headRefreshView = new HeadRefreshView(ActivityChatWindow.this);
        noDataView = new NoDataView(ActivityChatWindow.this);
        acwRefreshView.setHeaderView(headRefreshView);
        acwRefreshView.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                dataOffset++;
                List<ImMsg> tempList = queryDatabaseForImsession(sessionId);
                if (tempList != null && tempList.size() > 0) {
                    mDataList.addAll(tempList);
                    chatAdapter.notifyDataSetChanged();
                    acwRecycleView.scrollToPosition(0);
                    LogUtils.getInstance().debug("滚动---{}", dataOffset);
                } else {
                    LogUtils.getInstance().debug("tempList无数据");
                    Toast.makeText(ActivityChatWindow.this, "没有更多数据了", Toast.LENGTH_LONG).show();
                }
                acwRefreshView.finishRefresh();
            }

            @Override
            public void loadMore() {
            }
        });
    }

    /*
    * 文本发送失败时,点击左侧感叹号重发：
    */
    private void handleTextClickExclamation(int position) {
        LogUtils.getInstance().debug("位置 {} 的文本左侧感叹号被点击!", position);
        //sendMessageAfterError(position);
    }

    /*
    * 图片发送失败时,点击左侧感叹号重发：
    */
    private void handleImgClickExclamation(int position) {
        LogUtils.getInstance().debug("位置 {} 的图片左侧感叹号被点击!", position);
        //sendMessageAfterError(position);
    }

    private void initTitle() {

        layoutTitleBack2.setImageResource(R.drawable.meaage_back);
        layoutTitle2.setTextColor(getResources().getColor(R.color.message_title_color));
        if (TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(tempNickname)) {
            nickName = tempNickname;
        }

        if (status == 2) {
            FriendInfo friendInfo = friendInfoDao.queryBuilder()
                    .where(FriendInfoDao.Properties.MyselfUin.eq(String.valueOf(uin)))
                    .where(FriendInfoDao.Properties.FriendUin.eq(mSender))
                    .build().unique();
            if (friendInfo != null) {
                nickName = friendInfo.getFriendName();
            }
        }

        layoutTitle2.setText(nickName);
        layoutSetting.setVisibility(View.VISIBLE);
        layoutSetting.setImageResource(R.drawable.message_more);

        acwEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //软键盘弹出时，recyclerView要滑动到最低端;
                if (chatAdapter.getItemCount() > 0) {
                    acwRecycleView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });
        acwEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    acwSend.setClickable(true);
                    acwSend.setImageResource(R.drawable.feather_yes);
                } else {
                    acwSend.setClickable(false);
                    acwSend.setImageResource(R.drawable.feather_no);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //状态为1  发送者是自己
        if (1 == status && (uin == mSender)) {
            layoutTitle2.setText(gradeAndGenderStr);
            layoutSetting.setVisibility(View.INVISIBLE);
            acwEdit.setHint("等待回复");
            Drawable nav_up = getResources().getDrawable(R.drawable.wait_repeat);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            acwEdit.setCompoundDrawables(nav_up, null, null, null);
            acwEdit.setCompoundDrawablePadding(25);
            acwEdit.setEnabled(false);
            acwSend.setEnabled(false);
            acwImgChoice.setEnabled(false);
        }
    }

    //接受从FragmentMessage传过来的数据
    private void receiveBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionId = bundle.getString("yplay_sessionId");
            status = bundle.getInt("yplay_session_status");
            String sender = bundle.getString("yplay_sender");
            mSender = Integer.valueOf(sender);
            String msgContent = bundle.getString("yplay_msg_content");
            tempNickname = bundle.getString("yplay_nick_name");
            LogUtils.getInstance().debug("receiveBundleData: sessionId = {}, status = {}",
                    sessionId, status);

            if (status == 0 || status == 1) {
                try {
                    JSONObject jsonObject = new JSONObject(msgContent);
                    int dataType = jsonObject.getInt("DataType");
                    String data = jsonObject.getString("Data");

                    LogUtils.getInstance().debug("receiveBundleData: dataType = {}", dataType);

                    if (dataType == 1) {
                        MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                        MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
                        nickName = receiverInfoBean.getNickName();
                        LogUtils.getInstance().debug("receiveBundleData: dataType为1, nickName = {}",
                                nickName);

                    } else if (dataType == 2) {
                        MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);

                        if (uin != mSender) {
                            MsgContent2.SenderInfoBean senderInfoBean = msgContent2.getSenderInfo();
                            nickName = senderInfoBean.getNickName();
                            tempNickname2 = senderInfoBean.getNickName();

                        } else {
                            MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
                            tempNickname2 = receiverInfoBean.getNickName();
                            nickName = receiverInfoBean.getNickName();

                            int gender = receiverInfoBean.getGender();
                            String genderStr = gender == 1 ? "男生" : "女生";
                            LogUtils.getInstance().debug("receiveBundleData: genderStr = {}",
                                    genderStr);
                            int grade = receiverInfoBean.getGrade();
                            int schoolType = receiverInfoBean.getSchoolType();
                            LogUtils.getInstance().debug("receiveBundleData: grade = {}",
                                    grade);
                            String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType, grade);
                            gradeAndGenderStr = new StringBuilder(gradeAndSchool);
//                        gradeAndGenderStr.append(gradeAndSchool);
                            gradeAndGenderStr.append(genderStr);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(sessionId)) {
            //查询消息表
            mDataList = queryDatabaseForImsession(sessionId);
            LogUtils.getInstance().debug("消息列表的长度 = {}",mDataList.size());
        }
    }

    private ImMsg insertMsg(TIMMessage msg, int MsgSucess) {

        long tempMsgId = msg.getMsgUniqueId();
        int msgType = msg.getElement(0).getType().ordinal();
        long msgTs = msg.getMsg().time();
        ImMsg imMsg = new ImMsg(null,
                sessionId,
                tempMsgId,
                String.valueOf(uin),
                msgType,
                acwEdit.getText().toString().trim(),
                msgTs,
                MsgSucess);

        return imMsg;
    }

    //从数据库中查找消息列表
    private List<ImMsg> queryDatabaseForImsession(String sessionId) {

        return imMsgDao.queryBuilder()
                .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                .orderDesc(ImMsgDao.Properties.MsgTs)
                .offset(dataOffset * 10)
                .limit(10)
                .list();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.getInstance().debug("onRsume:");
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    acwEdit.setCursorVisible(false);

                } else {
                    acwEdit.setCursorVisible(true);
                }
            }
        });
    }

    /*
    * 判断离上一条消息的时间间隔是否超过3分钟，超过 则在UI中显示上一条信息的时间信息：
    */
    private void insertTimestampMsg () {
        //获取最近一条消息的时间戳;
        List<ImMsg> imMsgList = imMsgDao.queryBuilder()
                .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                .where(ImMsgDao.Properties.MsgType.eq(200))
                .orderDesc(ImMsgDao.Properties.MsgTs)
                .limit(1)
                .list();

        long currentTs = System.currentTimeMillis() / 1000 - 1;

        if (imMsgList != null && imMsgList.size() > 0) {
            long lastImMsgTs = imMsgList.get(0).getMsgTs();

            LogUtils.getInstance().debug("lastImMsgTs = {}, currentTs = {}", lastImMsgTs, currentTs);
            if ((currentTs - lastImMsgTs) >= (long) 180) {
                //如果当前时间戳跟最近一条消息时间戳相隔超过3分钟，则插入到数据列表中;
                ImMsg imMsg = new ImMsg(null,
                        sessionId,
                        System.currentTimeMillis(),
                        String.valueOf(uin),
                        200,
                        getCurrentTime(currentTs),
                        currentTs,
                        1);

                //插入时间戳消息到数据库，并插入到数据列表中;
                imMsgDao.insert(imMsg);
                mDataList.add(0, imMsg);
            }
        }else{

            //如果实名之后 从来没有插入时间消息，则直接插入一条时间消息
            List<ImMsg> timMsgList = imMsgDao.queryBuilder()
                    .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                    .where(ImMsgDao.Properties.MsgType.eq(1))
                    .orderDesc(ImMsgDao.Properties.MsgTs)
                    .limit(1)
                    .list();

            if (timMsgList != null && timMsgList.size() > 0){
                //如果当前时间戳跟最近一条消息时间戳相隔超过3分钟，则插入到数据列表中;
                ImMsg imMsg = new ImMsg(null,
                        sessionId,
                        System.currentTimeMillis(),
                        String.valueOf(uin),
                        200,
                        getCurrentTime(currentTs),
                        currentTs,
                        1);

                //插入时间戳消息到数据库，并插入到数据列表中;
                imMsgDao.insert(imMsg);
                mDataList.add(0, imMsg);
            }
        }
    }

    private String getCurrentTime(long imMsgTs) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(imMsgTs * 1000).toString();
    }

    @Override
    public void onMessageUpdate(ImMsg imMsg) {
        mHasNewMsg = true;

        String content = imMsg.getMsgContent();
        String tempSessionId = imMsg.getSessionId();
        LogUtils.getInstance().debug("聊天窗口收到了 content = {}, SessionId = {}",
                content, tempSessionId);

        if (!TextUtils.isEmpty(tempSessionId) && sessionId.equals(tempSessionId)) {
            //判断离上一条消息的时间间隔是否超过3分钟，超过 则在UI中显示时间信息：
            insertTimestampMsg();

            //更新UI显示;
            mDataList.add(0, imMsg);
            chatAdapter.notifyItemInserted(mDataList.size() - 1);
            acwRecycleView.scrollToPosition(mDataList.size() - 1);
        }
        if (!acwEdit.isEnabled()) {
            acwEdit.setEnabled(true);
            acwEdit.setHint("回复");
            acwEdit.setCompoundDrawables(null, null, null, null);
            acwImgChoice.setEnabled(true);
        }
        if (!acwSend.isEnabled()) {
            acwSend.setEnabled(true);
        }
        if (!layoutSetting.isShown()) {
            layoutSetting.setVisibility(View.VISIBLE);
        }

        if (status == 1 && uin != Integer.valueOf(imMsg.getSender())) {
            layoutTitle2.setText(tempNickname2);
            acwImgChoice.setVisibility(View.VISIBLE);
        }

        if (1 == status && (uin != Integer.valueOf(imMsg.getSender())) && mDataList.size() == 2) {
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("此时回复，对方将看到你的真实姓名");
            mDataList.add(0, imMsg1);

            acwImgChoice.setVisibility(View.GONE);
        }

        if (1 == status && (uin == Integer.valueOf(imMsg.getSender())) && mDataList.size() == 4) {
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的真实姓名");
            mDataList.add(0, imMsg1);

            acwImgChoice.setVisibility(View.VISIBLE);
        }

        LogUtils.getInstance().debug("onMessageUpdate: status = {}, sender = {}",
                status, imMsg.getSender());
    }


    //显示底部对话框
    private void showBottomDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_content_circle, null);
        bottomDialog.setContentView(contentView);

        Button msgProBt = (Button) contentView.findViewById(R.id.message_profile);
        Button msgDeleteBt = (Button) contentView.findViewById(R.id.message_delete);
        Button msgCancelBt = (Button) contentView.findViewById(R.id.message_cancel);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_profile:
                        LogUtils.getInstance().debug("onClick: 查看资料");
                        ImSession imSession = imSessionDao.queryBuilder()
                                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                                .build().unique();
                        getFriendInfo(Integer.valueOf(imSession.getChater()));
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_delete:
                        LogUtils.getInstance().debug("onClick: 删除对话");
                        deleteSession();
                        bottomDialog.dismiss();
                        finish();
                        break;
                    case R.id.message_cancel:
                        LogUtils.getInstance().debug("onClick: 取消");
                        bottomDialog.dismiss();
                        break;
                }
            }
        };

        msgProBt.setOnClickListener(onClickListener);
        msgDeleteBt.setOnClickListener(onClickListener);
        msgCancelBt.setOnClickListener(onClickListener);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    //删除对话
    private void deleteSession() {

        DeleteQuery imgDeleteQuery = imMsgDao.queryBuilder()
                .where(ImMsgDao.Properties.SessionId.eq(sessionId))
                .buildDelete();
        imgDeleteQuery.executeDeleteWithoutDetachingEntities();

        DeleteQuery sessionDelete = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .buildDelete();
        sessionDelete.executeDeleteWithoutDetachingEntities();
    }

    //查询朋友的信息
    private void getFriendInfo(int friendUin) {
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETUSERPROFILE, friendMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetFriendInfoResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("获取朋友资料异常");
                    }
                });
    }

    private void handleGetFriendInfoResponse(String result) {
        UserInfoResponde userInfoResponde = GsonUtil.GsonToBean(result, UserInfoResponde.class);
        LogUtils.getInstance().debug("获取朋友资料, {}", userInfoResponde.toString());
        if (userInfoResponde.getCode() == 0) {
            UserInfoResponde.PayloadBean.InfoBean infoBean =
                    userInfoResponde.getPayload().getInfo();
            int status = userInfoResponde.getPayload().getStatus();
            if (status == 1) {
                Intent intent = new Intent(ActivityChatWindow.this, ActivityFriendsInfo.class);
                intent.putExtra("yplay_friend_name", infoBean.getNickName());
                intent.putExtra("yplay_friend_uin", infoBean.getUin());
                LogUtils.getInstance().debug("朋友的uin, {}", infoBean.getUin());
                startActivity(intent);
            } else {
                showCardDialog(userInfoResponde.getPayload());
            }

        }
    }

    //发送加好友的请求
    private void addFriend(int toUin, int srcType) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("srcType", srcType);
        addFreindMap.put("uin", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_ADDFRIEND, addFreindMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleAddFriendResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("发送加好友请求异常");
                    }
                });
    }

    private void handleAddFriendResponse(String result) {
        AddFriendRespond addFriendRespond = GsonUtil.GsonToBean(result, AddFriendRespond.class);
        LogUtils.getInstance().debug("发送加好友请求, {}" + addFriendRespond.toString());
    }

    //显示名片
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean) {
        final UserInfoResponde.PayloadBean.InfoBean infoBean = payloadBean.getInfo();

        //状态
        int status = payloadBean.getStatus();

        final CardBigDialog cardDialog = new CardBigDialog(ActivityChatWindow.this, R.style.CustomDialog,
                payloadBean);

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView button = (ImageView) v;
                if (NetWorkUtil.isNetWorkAvailable(ActivityChatWindow.this)) {
                    button.setImageResource(R.drawable.peer_friend_requested);
                    addFriend(infoBean.getUin(), 8);
                } else {
                    Toast.makeText(ActivityChatWindow.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardDialog.show();
    }

    private void initData() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirStr = root + File.separator + "yplay" + File.separator + "image";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String tempImage = String.valueOf(System.currentTimeMillis());
        fileUri = new File(dirStr, tempImage + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.getInstance().debug("onActivityResult: requestCode = {}", requestCode);

        if (requestCode == REQUEST_CODE && data != null) {
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            LogUtils.getInstance().debug("onActivityResult: images_url = {}", images.get(0));

            String imagePath = images.get(0);
            int imgType = getImageFormat(imagePath);
            LogUtils.getInstance().debug("imgType = {}", imgType);

            if (imgType == TIMImageElem.TIM_IMAGE_FORMAT_GIF){
                sendImageMessage(imagePath);
            }else {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                String dirStr = root + File.separator + "yplay" + File.separator + "image";
                String localImagePath = "";
                if (bitmap != null) {
                    localImagePath = ImageUtil.saveImage(bitmap, dirStr);
                    bitmap.recycle();
                    bitmap = null;
                }
                LogUtils.getInstance().debug("onActivityResult: local imagePATH = {}", localImagePath);

                sendImageMessage(localImagePath);
            }

        } else if (requestCode == CODE_CAMERA_REQUEST) {
            String imagePath = fileUri.getAbsolutePath();
            LogUtils.getInstance().debug("onActivityResult: 拍照的url = {}, imagePath = {}",
                    imageUri, imagePath);

            sendImageMessage(imagePath);
        }
    }

    //显示图片的dialog
    private void showImageDialog(String imagePath, int largeWidth, int largeHeight, String currentSender) {
        LogUtils.getInstance().debug("showImageDialog: currentSender = {}", currentSender);
        final AlertDialog dialog = new AlertDialog.Builder(ActivityChatWindow.this, R.style.StyleDialog).create();
        dialog.show();

        dialog.setContentView(R.layout.layout_show_chat_image);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.acw_show_image);
        int screenWidth = DensityUtil.getScreenWidth(this);
        ViewGroup.LayoutParams imageLp = imageView.getLayoutParams();
        imageLp.width = screenWidth;
        imageLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        LogUtils.getInstance().debug("showImageDialog: lh = {}, lw = {}, height = {}, width = {}",
                largeHeight, largeWidth, largeHeight * screenWidth / largeWidth, imageLp.width);
        imageView.setLayoutParams(imageLp);

        imageView.setMaxWidth(screenWidth);
        //imageView.setMaxHeight(largeHeight * screenWidth / largeWidth);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!TextUtils.isEmpty(imagePath)) {
            if (uin == Integer.valueOf(currentSender)){
                imagePath = "file://" + imagePath;
            }
            Picasso.with(ActivityChatWindow.this).load(imagePath)
                    .resize(screenWidth, largeHeight * screenWidth / largeWidth)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView);
        }

        dialog.setCanceledOnTouchOutside(true);
    }

    //显示底部对话框
    private void showImageBottomDialog() {

        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_content_circle, null);
        bottomDialog.setContentView(contentView);

        Button msgProBt = (Button) contentView.findViewById(R.id.message_profile);
        msgProBt.setText("相册");
        Button msgDeleteBt = (Button) contentView.findViewById(R.id.message_delete);
        msgDeleteBt.setText("拍照");
        msgDeleteBt.setTextColor(getResources().getColor(R.color.message_profile_blue));
        Button msgCancelBt = (Button) contentView.findViewById(R.id.message_cancel);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_profile:
                        LogUtils.getInstance().debug("onClick: 相册");
                        ImageSelectorUtils.openPhoto(ActivityChatWindow.this, REQUEST_CODE, true, false, 0);
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_delete:
                        LogUtils.getInstance().debug("onClick: 拍照");
                        initData();
                        autoObtainCameraPermission();
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_cancel:
                        LogUtils.getInstance().debug("onClick: 取消");
                        bottomDialog.dismiss();
                        break;
                }
            }
        };

        msgProBt.setOnClickListener(onClickListener);
        msgDeleteBt.setOnClickListener(onClickListener);
        msgCancelBt.setOnClickListener(onClickListener);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(ActivityChatWindow.this, "com.donkingliang.imageselector", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                LogUtils.getInstance().debug("autoObtainCameraPermission: CODE_CAMERA_REQUEST = ", CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, "设备没有SD卡！");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        imageUri = FileProvider.getUriForFile(ActivityChatWindow.this, "com.donkingliang.imageselector", fileUri);//通过FileProvider创建一个content类型的Uri
                    PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    ToastUtils.showShort(this, "设备没有SD卡！");
                }
            } else {

                ToastUtils.showShort(this, "请允许打开相机！！");
            }
        }
    }

    private int getImageFormat(String imagePath) {
        LogUtils.getInstance().debug("getImageFormat: imagePath = ", imagePath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        String type = options.outMimeType;

        if (TextUtils.isEmpty(type)) {
            LogUtils.getInstance().debug("getImageFormat: 图片类型无法识别");
            return 255;
        } else if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            return TIMImageElem.TIM_IMAGE_FORMAT_JPG;
        } else if (type.equals("image/gif")) {
            return TIMImageElem.TIM_IMAGE_FORMAT_GIF;
        } else if (type.equals("image/png")) {
            return TIMImageElem.TIM_IMAGE_FORMAT_PNG;
        } else if (type.equals("image/bmp")) {
            return TIMImageElem.TIM_IMAGE_FORMAT_BMP;
        }

        return 255;

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("update_sessionID", sessionId);
        intent.putExtra("has_new_mesg", mHasNewMsg);
        setResult(RESULT_CODE_FRIEND_CHAT_REPLY, intent);

        super.onBackPressed();
    }
}
