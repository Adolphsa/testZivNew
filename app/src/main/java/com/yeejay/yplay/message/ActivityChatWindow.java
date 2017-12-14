package com.yeejay.yplay.message;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageOfflinePushSettings;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ChatAdapter;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.HeadRefreshView;
import com.yeejay.yplay.customview.NoDataView;
import com.yeejay.yplay.data.db.DbHelper;
import com.yeejay.yplay.data.db.ImpDbHelper;
import com.yeejay.yplay.friend.ActivityFriendsInfo;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.MessageUpdateUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import org.greenrobot.greendao.query.DeleteQuery;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityChatWindow extends BaseActivity implements MessageUpdateUtil.MessageUpdateListener {

    private static final String TAG = "ActivityChatWindow";

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
    @BindView(R.id.acw_edit)
    EditText acwEdit;
    @BindView(R.id.acw_send)
    ImageButton acwSend;
    @BindView(R.id.acw_pull_refresh)
    PullToRefreshLayout acwRefreshView;

    @OnClick(R.id.layout_title_back2)
    public void back() {
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        ImSession imSession = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .build().unique();
        imSession.setUnreadMsgNum(0);
        imSessionDao.update(imSession);
        finish();
    }

    @OnClick(R.id.layout_setting)
    public void megMore() {
        showBottomDialog();
        System.out.println("聊天对象资料");
    }

    @OnClick(R.id.acw_send)
    public void send() {
        System.out.println("发送消息");
        if (NetWorkUtil.isNetWorkAvailable(ActivityChatWindow.this)) {
            String str = acwEdit.getText().toString().trim();

            ImSession imSession = imSessionDao.queryBuilder()
                    .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                    .build().unique();
            String chater = imSession.getChater();
            Log.i(TAG, "send: chater---" + chater);

            //判断是否是好友关系
            FriendInfo friendInfo = mDbHelper.queryFriendInfo(Integer.valueOf(chater));
            if (friendInfo == null){

                ImMsg imMsg0 = new ImMsg(null,
                        sessionId,
                        System.currentTimeMillis(),
                        String.valueOf(uin),
                        101,
                        str,
                        (System.currentTimeMillis()/1000),
                        1);
                imMsgDao.insert(imMsg0);

                ImMsg imMsg1 = new ImMsg(null,
                        sessionId,
                        System.currentTimeMillis(),
                        String.valueOf(uin),
                        100,
                        "对方已不是你的好友",
                        (System.currentTimeMillis()/1000),
                        1);
                imMsgDao.insert(imMsg1);

                ImMsg imMsg2 = new ImMsg(null,
                        sessionId,
                        System.currentTimeMillis(),
                        String.valueOf(uin),
                        100,
                        "先和对方成为好友，才能聊天哦~",
                        (System.currentTimeMillis()/1000),
                        1);
                imMsgDao.insert(imMsg2);


                mDataList.add(0,imMsg0);
                mDataList.add(0,imMsg1);
                mDataList.add(0,imMsg2);
                chatAdapter.notifyDataSetChanged();
                acwEdit.setText("");
                acwRecycleView.scrollToPosition(mDataList.size()-1);
                return;
            }

            Log.i(TAG, "send: 编辑框的内容---" + str);
            if (!TextUtils.isEmpty(str)) {
                //构造一条消息
                final TIMMessage msg = new TIMMessage();
                //添加文本内容
                TIMTextElem elem = new TIMTextElem();
                elem.setText(str);
                //将elem添加到消息
                if (msg.addElement(elem) != 0) {
                    Log.d(TAG, "addElement failed");
                    return;
                }

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
                        sessionId);                       //群组Id

                Log.i(TAG, "send: sessionId---" + sessionId);

                //发送消息
                conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调

                    @Override
                    public void onError(int code, String desc) {//发送消息失败
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);
                        System.out.println("发送失败");
                        ImMsg imMsg = insertMsg(msg, 0); //发送失败为0
                        mDataList.add(0, imMsg);

                        //更新会话列表
                        MessageUpdateUtil.getMsgUpdateInstance().updateSessionAndMessage(msg, 0, false);
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//发送消息成功
                        Log.e(TAG, "SendMsg ok");
                        System.out.println("发送成功");

                        //更新会话列表
                        MessageUpdateUtil.getMsgUpdateInstance().updateSessionAndMessage(msg, 1, false);
                        acwEdit.setText("");
                    }
                });
            } else {
                Toast.makeText(ActivityChatWindow.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(ActivityChatWindow.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    String sessionId;
    ImMsgDao imMsgDao;
    ImSessionDao imSessionDao;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        ButterKnife.bind(this);

        Log.i(TAG, "onCreate:");

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityChatWindow.this, true);

        MessageUpdateUtil.getMsgUpdateInstance().setMessageUpdateListener(this);
        mDbHelper = new ImpDbHelper(YplayApplication.getInstance().getDaoSession());
        imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();
        uin = (int) SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_UIN, (int) 0);
        myselfNickName = (String) SharePreferenceUtil.get(ActivityChatWindow.this, YPlayConstant.YPLAY_NICK_NAME, "");

        receiveBundleData();

        initTitle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        acwRecycleView.setLayoutManager(linearLayoutManager);

        Log.i(TAG, "onCreate: status---" + status + ",uin---" + uin + ",mSender---" + mSender);

        if (1 == status && (uin == mSender)){
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的姓名");

            ImMsg imMsg2 = new ImMsg();
            imMsg2.setMsgType(100);
            imMsg2.setMsgContent("对方回复后，双方互相实名，能够继续聊天 ");
            mDataList.add(0,imMsg1);
            mDataList.add(0,imMsg2);

            Log.i(TAG, "onCreate: mDataList-size" + mDataList.size());
        }

        if (1 == status && (uin != mSender) && mDataList.size() == 2){
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("此时回复，对方将看到你的真实姓名");
            mDataList.add(0,imMsg1);
        }

        if (1 == status && (uin != mSender) && mDataList.size() == 4){
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的真实姓名");
            mDataList.add(0,imMsg1);
        }

        chatAdapter = new ChatAdapter(ActivityChatWindow.this, mDataList);
        acwRecycleView.setAdapter(chatAdapter);

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
                    System.out.println("滚动---" + dataOffset);
                } else {
                    System.out.println("tempList无数据");
                    Toast.makeText(ActivityChatWindow.this, "没有更多数据了", Toast.LENGTH_LONG).show();
                }
                acwRefreshView.finishRefresh();
            }

            @Override
            public void loadMore() {}
        });

    }

    private void initTitle() {

        layoutTitleBack2.setImageResource(R.drawable.meaage_back);
        layoutTitle2.setTextColor(getResources().getColor(R.color.message_title_color));
        if (TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(tempNickname)) {
            nickName = tempNickname;
        }
        layoutTitle2.setText(nickName);
        layoutSetting.setVisibility(View.VISIBLE);
        layoutSetting.setImageResource(R.drawable.message_more);

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
        if (1 == status && (uin == mSender)){
            layoutTitle2.setText(gradeAndGenderStr);
            layoutSetting.setVisibility(View.INVISIBLE);
            acwEdit.setHint("等待回复");
            Drawable nav_up = getResources().getDrawable(R.drawable.wait_repeat);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            acwEdit.setCompoundDrawables(nav_up,null,null,null);
            acwEdit.setCompoundDrawablePadding(25);
            acwEdit.setEnabled(false);
            acwSend.setEnabled(false);
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
            Log.d(TAG, "receiveBundleData: sessionId---" + sessionId);

            if (status == 0 || status == 1) {
                try {
                    JSONObject jsonObject = new JSONObject(msgContent);
                    int dataType = jsonObject.getInt("DataType");
                    String data = jsonObject.getString("Data");

                    Log.i(TAG, "receiveBundleData: dataType---" + dataType);

                    if (dataType == 1) {
                        MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                        MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
                        nickName = receiverInfoBean.getNickName();

                    } else if (dataType == 2) {
                        MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                        MsgContent2.SenderInfoBean senderInfoBean = msgContent2.getSenderInfo();
                        MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
                        tempNickname2=  receiverInfoBean.getNickName();
                        nickName = senderInfoBean.getNickName();

                        int gender = senderInfoBean.getGender();
                        String genderStr = gender == 1 ? "男生" : "女生";
                        Log.i(TAG, "receiveBundleData: genderStr---" + genderStr);
                        int grade = senderInfoBean.getGrade();
                        int schoolType = senderInfoBean.getSchoolType();
                        Log.i(TAG, "receiveBundleData: grade---" + grade);
                        String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType, grade);
                        gradeAndGenderStr = new StringBuilder(gradeAndSchool);
//                        gradeAndGenderStr.append(gradeAndSchool);
                        gradeAndGenderStr.append(genderStr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        if (!TextUtils.isEmpty(sessionId)) {
            //查询消息表
            mDataList = queryDatabaseForImsession(sessionId);
            System.out.println("消息列表的长度---" + mDataList.size());
        }
    }

    private ImMsg insertMsg(TIMMessage msg, int MsgSucess) {

        long msgId = msg.getMsgUniqueId();
        int msgType = msg.getElement(0).getType().ordinal();
        long msgTs = msg.getMsg().time();
        ImMsg imMsg = new ImMsg(null,
                sessionId,
                msgId,
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

        Log.i(TAG, "onResume: ");
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

    @Override
    public void onMessageUpdate(ImMsg imMsg) {
        String content = imMsg.getMsgContent();
        System.out.println("聊天窗口收到了contenty---" + content);
        String tempSessionId = imMsg.getSessionId();

        if (!TextUtils.isEmpty(tempSessionId) && sessionId.equals(tempSessionId)) {
            mDataList.add(0, imMsg);
            chatAdapter.notifyItemInserted(mDataList.size() - 1);
            acwRecycleView.scrollToPosition(mDataList.size() - 1);
        }
        if (!acwEdit.isEnabled()){
            acwEdit.setEnabled(true);
            acwEdit.setHint("回复");
            acwEdit.setCompoundDrawables(null,null,null,null);
        }
        if (!acwSend.isEnabled()){
            acwSend.setEnabled(true);
        }
        if (!layoutSetting.isShown()){
            layoutSetting.setVisibility(View.VISIBLE);
        }

        if (status == 1 && uin != Integer.valueOf(imMsg.getSender())){
            layoutTitle2.setText(tempNickname2);
        }

        if (1 == status && (uin != Integer.valueOf(imMsg.getSender())) && mDataList.size() == 2){
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("此时回复，对方将看到你的真实姓名");
            mDataList.add(0,imMsg1);
        }

        if (1 == status && (uin == Integer.valueOf(imMsg.getSender())) && mDataList.size() == 4){
            ImMsg imMsg1 = new ImMsg();
            imMsg1.setMsgType(100);
            imMsg1.setMsgContent("对方已看到你的真实姓名");
            mDataList.add(0,imMsg1);
        }
        Log.i(TAG, "onMessageUpdate: status---" + status + ",sender---" + imMsg.getSender());
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
                        Log.i(TAG, "onClick: 查看资料");
                        Intent intent = new Intent(ActivityChatWindow.this, ActivityFriendsInfo.class);
                        intent.putExtra("yplay_friend_name", nickName);
                        intent.putExtra("yplay_friend_uin", mSender);
                        bottomDialog.dismiss();
                        startActivity(intent);
                        break;
                    case R.id.message_delete:
                        Log.i(TAG, "onClick: 删除对话");
                        deleteSession();
                        bottomDialog.dismiss();
                        finish();
                        break;
                    case R.id.message_cancel:
                        Log.i(TAG, "onClick: 取消");
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

}
