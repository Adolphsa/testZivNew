package com.yeejay.yplay.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMElemType;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.customview.ProgressButton;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.greendao.ImMsgDao;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.greendao.ImSessionDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.MsgContent1;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityNnonymityReply extends AppCompatActivity {

    private static final String TAG = "ActivityNnonymityReply";
    private static final int RESULT_CODE_NONMITY_REPLY = 1;

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.non_title)
    RelativeLayout layoutTitleRl;
    @BindView(R.id.non_ques_head_img)

    ImageView nonQuesHeadImg;
    @BindView(R.id.non_ques_text)
    TextView nonQuesText;
    @BindView(R.id.non_button1)
    ProgressButton nonButton1;
    @BindView(R.id.non_button2)
    ProgressButton nonButton2;
    @BindView(R.id.non_button3)
    ProgressButton nonButton3;
    @BindView(R.id.non_button4)
    ProgressButton nonButton4;
    @BindView(R.id.non_wait_replay)
    TextView nonWaitReplay;
    @BindView(R.id.non_edit)
    EditText nonEdit;
    @BindView(R.id.non_send)
    ImageButton nonSend;
    @BindView(R.id.non_input_ll)
    LinearLayout nonInputLl;

    @OnClick(R.id.layout_title_back2)
    public void back() {
        if (insertedDbManually) {
            Intent intent = new Intent();
            intent.putExtra("inserted_sessionID", insertedSessionId);
            setResult(RESULT_CODE_NONMITY_REPLY, intent);
        }

        finish();
    }

    @OnClick(R.id.non_send)
    public void replayVote() {
        LogUtils.getInstance().debug("投票回复");

        if (NetWorkUtil.isNetWorkAvailable(ActivityNnonymityReply.this)) {
            nonSend.setEnabled(false);
            replayImVote(sessionId, nonEdit.getText().toString().trim());
        } else {
            Toast.makeText(ActivityNnonymityReply.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    String sessionId;
    private boolean insertedDbManually = false;
    private String insertedSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nnonymity_reply);
        ButterKnife.bind(this);

        initView();

        int uin = (int) SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_UIN, (int) 0);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            sessionId = bundle.getString("yplay_sessionId");
            int status = bundle.getInt("yplay_session_status");
            String sender = bundle.getString("yplay_sender");
            String msgContent = bundle.getString("yplay_msg_content");
            LogUtils.getInstance().debug("匿名---sessionId = {}, status = {}, sender = {}",
                    sessionId, status, sender);

            if (status == 0) {
                initView1(msgContent);
            } else if (status == 1) {
                if (!TextUtils.isEmpty(sender) && sender.equals(String.valueOf(uin))) {
                    initView2(msgContent);
                } else {
                    initView3(msgContent);
                }
            }

        }
    }

    private void initView() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.message_title_color));
        layoutTitleBack2.setImageResource(R.drawable.white_back);
        layoutTitle2.setVisibility(View.GONE);
        layoutTitleRl.setBackgroundColor(getResources().getColor(R.color.message_title_color));
        nonSend.setEnabled(false);

        nonEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.getInstance().debug("onTextChanged, s = {}", s);
                if (s.length() > 0) {
                    nonSend.setEnabled(true);
                    nonSend.setImageResource(R.drawable.feather_yes);
                } else {
                    nonSend.setEnabled(false);
                    nonSend.setImageResource(R.drawable.feather_no);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //status == o, dataType == 1
    private void initView1(String msgContent) {

        nonWaitReplay.setVisibility(View.GONE);

        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");

            if (1 == dataType) {
                MsgContent1 msgContent1 = GsonUtil.GsonToBean(data, MsgContent1.class);
                int selectIndex = msgContent1.getSelIndex();
                MsgContent1.QuestionInfoBean questionInfoBean = msgContent1.getQuestionInfo();
                List<MsgContent1.OptionsBean> optionsBeanList = msgContent1.getOptions();

                String headUrl = questionInfoBean.getQiconUrl();
                String questionText = questionInfoBean.getQtext();

                if (!TextUtils.isEmpty(headUrl)) {
                    Picasso.with(ActivityNnonymityReply.this).load(headUrl)
                            .resizeDimen(R.dimen.non_ques_head_img_width, R.dimen.non_ques_head_img_height)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(nonQuesHeadImg);
                    nonQuesText.setText(questionText);
                }

                if (optionsBeanList != null && optionsBeanList.size() == 4) {

                    int beSelectCnt1 = optionsBeanList.get(0).getBeSelCnt();
                    int beSelectCnt2 = optionsBeanList.get(1).getBeSelCnt();
                    int beSelectCnt3 = optionsBeanList.get(2).getBeSelCnt();
                    int beSelectCnt4 = optionsBeanList.get(3).getBeSelCnt();

                    String nickName1 = optionsBeanList.get(0).getNickName();
                    String nickName2 = optionsBeanList.get(1).getNickName();
                    String nickName3 = optionsBeanList.get(2).getNickName();
                    String nickName4 = optionsBeanList.get(3).getNickName();

                    initButton(selectIndex,
                            beSelectCnt1, beSelectCnt2, beSelectCnt3, beSelectCnt4,
                            nickName1, nickName2, nickName3, nickName4);
                }




            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //status = 1 data_type = 2 sender = self
    private void initView2(String msgContent) {

        nonInputLl.setVisibility(View.GONE);
        nonWaitReplay.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");

            if (2 == dataType) {
                MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                int selectIndex = msgContent2.getSelIndex();
                MsgContent2.QuestionInfoBean questionInfoBean = msgContent2.getQuestionInfo();
                List<MsgContent2.OptionsBean> optionsBeanList = msgContent2.getOptions();

                String headUrl = questionInfoBean.getQiconUrl();
                String questionText = questionInfoBean.getQtext();

                if (!TextUtils.isEmpty(headUrl)) {
                    Picasso.with(ActivityNnonymityReply.this).load(headUrl).into(nonQuesHeadImg);
                    nonQuesText.setText(questionText);
                }

                if (optionsBeanList != null && optionsBeanList.size() == 4) {

                    int beSelectCnt1 = optionsBeanList.get(0).getBeSelCnt();
                    int beSelectCnt2 = optionsBeanList.get(1).getBeSelCnt();
                    int beSelectCnt3 = optionsBeanList.get(2).getBeSelCnt();
                    int beSelectCnt4 = optionsBeanList.get(3).getBeSelCnt();

                    String nickName1 = optionsBeanList.get(0).getNickName();
                    String nickName2 = optionsBeanList.get(1).getNickName();
                    String nickName3 = optionsBeanList.get(2).getNickName();
                    String nickName4 = optionsBeanList.get(3).getNickName();

                    initButton(selectIndex,
                            beSelectCnt1, beSelectCnt2, beSelectCnt3, beSelectCnt4,
                            nickName1, nickName2, nickName3, nickName4);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //status = 1 data_type = 2 sender = not self
    private void initView3(String msgContent) {

    }

    //status = 2


    //button ui
    private void initButton(int selectIndex,
                            int bsc1, int bsc2, int bsc3, int bsc4,
                            String nickName1, String nickName2, String nickName3, String nickName4) {

        int total = bsc1 + bsc2 + bsc3 + bsc4 + 1;
        LogUtils.getInstance().debug("non---total = {}", total);

        nonButton1.setText(nickName1);
        nonButton2.setText(nickName2);
        nonButton3.setText(nickName3);
        nonButton4.setText(nickName4);

        if (selectIndex == 1) {
            nonButton1.setBackground(getDrawable(R.drawable.nonymity_reply_select));
        } else if (selectIndex == 2) {
            nonButton2.setBackground(getDrawable(R.drawable.nonymity_reply_select));
        } else if (selectIndex == 3) {
            nonButton3.setBackground(getDrawable(R.drawable.nonymity_reply_select));
        } else if (selectIndex == 4) {
            nonButton4.setBackground(getDrawable(R.drawable.nonymity_reply_select));
        }

    }

    //回复消息
    private void replayImVote(final String sessionId, final String content) {
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("uin", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_UIN, 0));
        tempMap.put("token", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        tempMap.put("ver", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_VER, 0));
        tempMap.put("sessionId", sessionId);
        tempMap.put("content", content);

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_SENDVOTEREPLYMSG, tempMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleReplayImVoteResponse(result, sessionId, content);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void handleReplayImVoteResponse(String result, final String sessionId, final String content) {
        BaseRespond baseRespond = GsonUtil.GsonToBean(result, BaseRespond.class);
        LogUtils.getInstance().debug("投票回复, {}", baseRespond.toString());
        if (baseRespond.getCode() == 0) {
            hideKeyword();
            nonInputLl.setVisibility(View.GONE);
            nonWaitReplay.setVisibility(View.VISIBLE);
        } else if (baseRespond.getCode() == 15005) {
            //如果为特殊错误码15005, 则UI上要保持跟返回码为0一样的状态，同时构造一条消息插入本地数据库
            hideKeyword();
            nonInputLl.setVisibility(View.GONE);
            nonWaitReplay.setVisibility(View.VISIBLE);

            //插入本地数据库及更新session处理
            handleInsertNotFriendMsgIntoDb(sessionId, content);
        }
        else {
            Toast.makeText(ActivityNnonymityReply.this, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }


    /*
     * 收到非好友发过来的匿名消息时进行特殊处理：构造一条消息插入数据库，并更新session,触发外部回调接口;
     */
    private void handleInsertNotFriendMsgIntoDb(final String sessionId, final String content){
        LogUtils.getInstance().debug("sessionId = {}, content = {}", sessionId, content);

        ImMsgDao imMsgDao = YplayApplication.getInstance().getDaoSession().getImMsgDao();
        ImSessionDao imSessionDao = YplayApplication.getInstance().getDaoSession().getImSessionDao();

        ImSession imSession = imSessionDao.queryBuilder()
                .where(ImSessionDao.Properties.SessionId.eq(sessionId))
                .build().unique();

        if (imSession == null) {
            //会话必须是存在的，因为是本地在回复消息，如果不存在，则会话窗口就不会存在
            LogUtils.getInstance().error("非好友匿名投票消息不存在");
            return;
        }

        String lastMsgContent = imSession.getMsgContent();

        int dataType = -1;
        String data = null;
        try {
            JSONObject jsonObject = new JSONObject(lastMsgContent);
            dataType = jsonObject.getInt("DataType");
            data = jsonObject.getString("Data");
        } catch (JSONException e) {
            LogUtils.getInstance().error("exception = {}", e.toString());
        }

        if(dataType != 1){
            LogUtils.getInstance().error("type = {}, which is wrong", dataType);
            return;
        }

        MsgContent2 msgContent2 = GsonUtil.GsonToBean(data,MsgContent2.class);
        MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
        MsgContent2.SenderInfoBean senderInfoBean = msgContent2.getSenderInfo();

        MsgContent2.SenderInfoBean newSenderInfo = new MsgContent2.SenderInfoBean();
        MsgContent2.ReceiverInfoBean newReceiverInfo = new MsgContent2.ReceiverInfoBean();;

        newSenderInfo.setUin(receiverInfoBean.getUin());
        newSenderInfo.setUserName(receiverInfoBean.getUserName());
        newSenderInfo.setPhone(receiverInfoBean.getPhone());
        newSenderInfo.setNickName(receiverInfoBean.getNickName());
        newSenderInfo.setHeadImgUrl(receiverInfoBean.getHeadImgUrl());
        newSenderInfo.setGender(receiverInfoBean.getGender());
        newSenderInfo.setAge(receiverInfoBean.getAge());
        newSenderInfo.setGrade(receiverInfoBean.getGrade());
        newSenderInfo.setSchoolId(receiverInfoBean.getSchoolId());
        newSenderInfo.setSchoolName(receiverInfoBean.getSchoolName());
        newSenderInfo.setSchoolType(receiverInfoBean.getSchoolType());
        newSenderInfo.setCountry(receiverInfoBean.getCountry());
        newSenderInfo.setProvince(receiverInfoBean.getProvince());
        newSenderInfo.setCity(receiverInfoBean.getCity());
        newSenderInfo.setTs(receiverInfoBean.getTs());

        newReceiverInfo.setUin(senderInfoBean.getUin());
        newReceiverInfo.setUserName(senderInfoBean.getUserName());
        newReceiverInfo.setPhone(senderInfoBean.getPhone());
        newReceiverInfo.setNickName(senderInfoBean.getNickName());
        newReceiverInfo.setHeadImgUrl(senderInfoBean.getHeadImgUrl());
        newReceiverInfo.setGender(senderInfoBean.getGender());
        newReceiverInfo.setAge(senderInfoBean.getAge());
        newReceiverInfo.setGrade(senderInfoBean.getGrade());
        newReceiverInfo.setSchoolId(senderInfoBean.getSchoolId());
        newReceiverInfo.setSchoolName(senderInfoBean.getSchoolName());
        newReceiverInfo.setSchoolType(senderInfoBean.getSchoolType());
        newReceiverInfo.setCountry(senderInfoBean.getCountry());
        newReceiverInfo.setProvince(senderInfoBean.getProvince());
        newReceiverInfo.setCity(senderInfoBean.getCity());
        newReceiverInfo.setTs(senderInfoBean.getTs());

        msgContent2.setReceiverInfo(newReceiverInfo);
        msgContent2.setSenderInfo(newSenderInfo);

        msgContent2.setContent(content);

        String newData = GsonUtil.GsonString(msgContent2);
        int newDataType = 2;

        JSONObject newJsonObject = new JSONObject();
        try {
            newJsonObject.put("DataType", newDataType);
            newJsonObject.put("Data", newData);
        } catch (JSONException e) {
            LogUtils.getInstance().error("exception = {}", e.toString());
        }
        String tmsgContent = newJsonObject.toString();

        long msgId = (System.currentTimeMillis() / 1000);
        int uin = (int)SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        String sender = String.valueOf(uin);
        int msgType = TIMElemType.Custom.ordinal();
        long msgTs = msgId;
        int msgSuccess = 1;
        int status = 1;//1表示投稿恢复状态;

        ImMsg imMsg = new ImMsg(null, sessionId, msgId, sender, msgType, tmsgContent, msgTs, msgSuccess);

        try {
            imMsgDao.insert(imMsg);
        } catch (Exception e) {
            LogUtils.getInstance().error("插入构造的非好友匿名投票回复消息 异常");
        }

        imSession.setLastMsgId(msgId);
        imSession.setLastSender(sender);
        imSession.setMsgContent(tmsgContent);
        imSession.setMsgType(msgType);
        imSession.setMsgTs(msgTs);
        imSession.setStatus(status);
        imSession.setUnreadMsgNum(0);//会话未读数目为0

        imSessionDao.update(imSession);
        updateUi();

        //这个insertedSessionId将要传给FragmentMessage;
        insertedSessionId = sessionId;
        //设置标记位，当退出该页面时通知FragmentMessage更新UI;
        insertedDbManually = true;
    }

    private void updateUi() {
        layoutSetting.setVisibility(View.INVISIBLE);
        nonEdit.setHint("等待回复");
        Drawable nav_up = getResources().getDrawable(R.drawable.wait_repeat);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        nonEdit.setCompoundDrawables(nav_up, null, null, null);
        nonEdit.setCompoundDrawablePadding(25);
        nonEdit.setEnabled(false);
        nonSend.setEnabled(false);
    }

    //收起键盘
    private void hideKeyword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nonEdit.getWindowToken(), 0);


    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    nonEdit.setCursorVisible(false);

                } else {
                    nonEdit.setCursorVisible(true);
                }
            }
        });


    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (ActivityNnonymityReply.this.getCurrentFocus() != null) {
                if (ActivityNnonymityReply.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(ActivityNnonymityReply.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        if (insertedDbManually) {
            Intent intent = new Intent();
            setResult(RESULT_CODE_NONMITY_REPLY, intent);
        }

        super.onBackPressed();
    }
}
