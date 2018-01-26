package com.yeejay.yplay.contribute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityContribute1 extends BaseActivity {
    private static final String TAG = "ActivityContribute1";

    private static final String EMOJI_URL = "http://yplay-1253229355.image.myqcloud.com/qicon/";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.contribute_history)
    ImageButton contributeHistory;
    @BindView(R.id.con_img)
    LinearLayout conImg;
    @BindView(R.id.con_selected_img)
    ImageView selectedImg;
    @BindView(R.id.con_edit)
    EditText conEdit;
    @BindView(R.id.con_text_count)
    TextView conTextCount;
    @BindView(R.id.con_apply_button)
    Button conApplyButton;
    @BindView(R.id.con_apply_ll)
    LinearLayout conApplyLl;
    @BindView(R.id.rl_edittext)
    FrameLayout rlEditText;
    @BindView(R.id.contribute_new)
    ImageView contributeNew;

    @OnClick(R.id.con_edit)
    public void clickEdit() {
        rlEditText.setBackgroundResource(R.drawable.shape_con1_edit_selected_background);
        conEdit.setCursorVisible(true);
    }

    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    @OnClick(R.id.contribute_history)
    public void toContributeHistory() {
        //点击后，如果上次处于有新投稿的状态，则隐藏红旗图标;
        contributeNew.setVisibility(View.GONE);

        startActivity(new Intent(ActivityContribute1.this, ActivityContributeQuery.class));
    }

    @OnClick(R.id.con_img)
    public void conEmojiImg() {
        Intent intent = new Intent(ActivityContribute1.this, ActivityContribute2.class);
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.con_selected_img)
    public void clickSelected_img() {
        Intent intent = new Intent(ActivityContribute1.this, ActivityContribute2.class);
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.con_apply_button)
    public void submit() {
        if(NetWorkUtil.isNetWorkAvailable(ActivityContribute1.this)){

            String questionText = conEdit.getText().toString();
            if (!TextUtils.isEmpty(questionText)){
                submitQuestion(questionText,emojiIndex);
                LogUtils.getInstance().debug("问题不为空");
            }

        }else {
            Toast.makeText(ActivityContribute1.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    int emojiIndex = -1;

    private BroadcastReceiver mContributeBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra("contribute_flag", 0);
            LogUtils.getInstance().debug("mContributeBr, flag = {}" + flag);
            if (1 == flag) { //表示有新的投稿消息;
                contributeNew.setVisibility(View.VISIBLE);
            }
        }
    };

    private void registerBr() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yeejay.br.contribute");
        registerReceiver(mContributeBr, intentFilter);
    }

    private void unregisterBr() {
        unregisterReceiver(mContributeBr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute1);

        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContribute1.this, true);

        registerBr();
        conApplyButton.setEnabled(false);

        initEdit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBr();
    }


    private void initEdit() {
        rlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.getInstance().debug("rl_edittext focused!, hasFocus = {}", hasFocus);
                if (hasFocus) {
                    rlEditText.setBackgroundResource(R.drawable.shape_con1_edit_selected_background);
                    conEdit.setCursorVisible(true);
                } else {
                    rlEditText.setBackgroundResource(R.drawable.shape_con1_edit_background);
                    conEdit.setCursorVisible(false);
                }
            }
        });

        conEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                conTextCount.setText(s.toString().length() + "/30");
                enableButton(s.toString());

            }
        });

        conEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())){
                    LogUtils.getInstance().debug("回车键被点击");
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 1) {
            if (data != null) {
                conImg.setVisibility(View.GONE);
                selectedImg.setVisibility(View.VISIBLE);

                int currentSelectEmoji = data.getIntExtra("current_select_emoji", 0);
                emojiIndex = data.getIntExtra("current_emoji_index", 0);
                LogUtils.getInstance().debug("1---currentSelectEmoji = {}, current_emoji_index = {}",
                        currentSelectEmoji, emojiIndex);
                String demojiUrl = EMOJI_URL + emojiIndex + ".png";
                Picasso.with(ActivityContribute1.this).load(demojiUrl).resizeDimen(
                        R.dimen.non_ques_head_img_width, R.dimen.non_ques_head_img_height)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(selectedImg);

                enableButton(conEdit.getText().toString().trim());
            }
        } else if(requestCode == 3 && resultCode == 3) {
            selectedImg.setVisibility(View.GONE);
            conImg.setVisibility(View.VISIBLE);
            emojiIndex = -1;

            conEdit.setText("");
        }

        //判断源头页面是否从查询投稿页面返回的;
        if(resultCode == 4) {
            selectedImg.setVisibility(View.GONE);
            conImg.setVisibility(View.VISIBLE);
            emojiIndex = -1;

            conEdit.setText("");
        }
    }

    private void enableButton(String s){
        if (s.length() > 0 && emojiIndex != (-1)) {
            conApplyButton.setBackgroundResource(R.drawable.shape_purple_btn_backround);
            conApplyButton.setTextColor(getResources().getColor(R.color.white));
            conApplyButton.setEnabled(true);
        } else {
            conApplyButton.setBackgroundResource(R.drawable.shape_gray_btn_backround);
            conApplyButton.setTextColor(getResources().getColor(R.color.text_color_gray2));
            conApplyButton.setEnabled(false);
        }
    }

    //提交投稿
    private void submitQuestion(String qtext, int qiconId) {

        Map<String, Object> conMap = new HashMap<>();
        conMap.put("qtext", qtext);
        conMap.put("qiconId", qiconId);
        conMap.put("uin", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_UIN, 0));
        conMap.put("token", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        conMap.put("ver", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_SUBMITQUESTION, conMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleSubmitQuestionResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("投稿异常");
                    }
                });

    }

    private void handleSubmitQuestionResponse(String result) {
        BaseRespond baseRespond = GsonUtil.GsonToBean(result, BaseRespond.class);
        LogUtils.getInstance().debug("投稿, {}", baseRespond.toString());
        if (baseRespond.getCode() == 0){
            //conEdit.setEnabled(false);

            //投稿成功，跳转到投稿完成页面;
            startActivityForResult(new Intent(ActivityContribute1.this,
                    ActivityContributeComplete.class), 3);
        }else {
            Toast.makeText(ActivityContribute1.this,"提交失败",Toast.LENGTH_SHORT).show();
        }
    }
}
