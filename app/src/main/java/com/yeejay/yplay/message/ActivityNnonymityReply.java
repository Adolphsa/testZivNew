package com.yeejay.yplay.message;

import android.content.Context;
import android.graphics.Rect;
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

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.customview.ProgressButton;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.MsgContent1;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityNnonymityReply extends AppCompatActivity {

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
        finish();
    }

    @OnClick(R.id.non_send)
    public void replayVote() {
        System.out.println("投票回复");

        if (NetWorkUtil.isNetWorkAvailable(ActivityNnonymityReply.this)) {
            nonSend.setEnabled(false);
            replayImVote(sessionId, nonEdit.getText().toString().trim());
        } else {
            Toast.makeText(ActivityNnonymityReply.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    String sessionId;
    ImSession imSession;
    boolean isFirst = true;

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
            System.out.println("匿名---sessionId---" + sessionId
                    + "status---" + status
                    + "sender---" + sender);

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


                nonEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            nonSend.setClickable(true);
                            nonSend.setImageResource(R.drawable.feather_yes);
                        } else {
                            nonSend.setClickable(false);
                            nonSend.setImageResource(R.drawable.feather_no);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

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
        System.out.println("non---total---" + total);

        nonButton1.setText(nickName1);
        nonButton2.setText(nickName2);
        nonButton3.setText(nickName3);
        nonButton4.setText(nickName4);

        if (selectIndex == 1) {
            nonButton1.setBackground(getDrawable(R.drawable.nonymity_reply_select));
//            nonButton1.setButtonColor(R.color.message_button__selcet70);
//            nonButton2.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton3.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton4.setButtonColor(R.color.message_button_no_selcet20);
//
//            nonButton1.updateProgress((bsc1 + 1) * 100 / total);
//            nonButton2.updateProgress(bsc2 * 100 / total);
//            nonButton3.updateProgress(bsc3 * 100 / total);
//            nonButton4.updateProgress(bsc4 * 100 / total);

//            System.out.println("被选中的是1");

        } else if (selectIndex == 2) {
            nonButton2.setBackground(getDrawable(R.drawable.nonymity_reply_select));

//            nonButton1.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton2.setButtonColor(R.color.message_button__selcet70);
//            nonButton3.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton4.setButtonColor(R.color.message_button_no_selcet20);
//
//            nonButton1.updateProgress(bsc1 * 100 / total);
//            nonButton2.updateProgress((bsc2 + 1) * 100 / total);
//            nonButton3.updateProgress(bsc3 * 100 / total);
//            nonButton4.updateProgress(bsc4 * 100 / total);

//            System.out.println("被选中的是2");

        } else if (selectIndex == 3) {
            nonButton3.setBackground(getDrawable(R.drawable.nonymity_reply_select));
//            nonButton1.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton2.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton3.setButtonColor(R.color.message_button__selcet70);
//            nonButton4.setButtonColor(R.color.message_button_no_selcet20);
//
//            nonButton1.updateProgress(bsc1 * 100 / total);
//            nonButton2.updateProgress(bsc2 * 100 / total);
//            nonButton3.updateProgress((bsc3 + 1) * 100 / total);
//            nonButton4.updateProgress(bsc4 * 100 / total);

//            System.out.println("被选中的是3");
        } else if (selectIndex == 4) {
            nonButton4.setBackground(getDrawable(R.drawable.nonymity_reply_select));

//            nonButton1.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton2.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton3.setButtonColor(R.color.message_button_no_selcet20);
//            nonButton4.setButtonColor(R.color.message_button__selcet70);
//
//            nonButton1.updateProgress(bsc1 * 100 / total);
//            nonButton2.updateProgress(bsc2 * 100 / total);
//            nonButton3.updateProgress(bsc3 * 100 / total);
//            nonButton4.updateProgress((bsc4 + 1) * 100 / total);
//
//            System.out.println("被选中的是41");
        }

    }


    //回复消息
    private void replayImVote(String sessionId, String content) {

        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("uin", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_UIN, 0));
        tempMap.put("token", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        tempMap.put("ver", SharePreferenceUtil.get(ActivityNnonymityReply.this, YPlayConstant.YPLAY_VER, 0));
        tempMap.put("sessionId", sessionId);
        tempMap.put("content", content);

        YPlayApiManger.getInstance().getZivApiService()
                .replayImVote(tempMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRespond baseRespond) {
                        System.out.println("投票回复---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0) {
                            hideKeyword();
                            nonInputLl.setVisibility(View.GONE);
                            nonWaitReplay.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(ActivityNnonymityReply.this, "发送失败", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(ActivityNnonymityReply.this, "隐藏", Toast.LENGTH_SHORT).show();

                } else {
                    nonEdit.setCursorVisible(true);
//                    Toast.makeText(ActivityNnonymityReply.this, "弹出", Toast.LENGTH_SHORT).show();
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
}
