package com.yeejay.yplay.contribute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    RelativeLayout rlEditText;
    @BindView(R.id.contribute_new)
    ImageView contributeNew;

    @OnClick(R.id.con_edit)
    public void clickEdit() {
        Log.d(TAG, "con_edit clicked!");
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
                System.out.println("问题不为空");
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
            LogUtils.getInstance().debug(TAG + " , mContributeBr, flag = " + String.valueOf(flag));
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
                Log.d(TAG, "rl_edittext focused!, hasFocus = " + hasFocus);
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
                    System.out.println("回车键被点击");
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
                System.out.println("1---currentSelectEmoji---" + currentSelectEmoji
                        + "current_emoji_index" + emojiIndex);
                String demojiUrl = EMOJI_URL + emojiIndex + ".png";
                Picasso.with(ActivityContribute1.this).load(demojiUrl).into(selectedImg);

                enableButton(conEdit.getText().toString().trim());
            }
        } else if(requestCode == 3 && resultCode == 3) {
            selectedImg.setVisibility(View.GONE);
            conImg.setVisibility(View.VISIBLE);

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


    private void submitQuestion(String qtext, int qiconId) {

        Map<String, Object> conMap = new HashMap<>();
        conMap.put("qtext", qtext);
        conMap.put("qiconId", qiconId);
        conMap.put("uin", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_UIN, 0));
        conMap.put("token", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        conMap.put("ver", SharePreferenceUtil.get(ActivityContribute1.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .submiteQuestion(conMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRespond baseRespond) {
                        System.out.println("投稿---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0){
                            //conEdit.setEnabled(false);

                            //投稿成功，跳转到投稿完成页面;
                            startActivityForResult(new Intent(ActivityContribute1.this,
                                    ActivityContributeComplete.class), 3);
                        }else {
                            Toast.makeText(ActivityContribute1.this,"提交失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("投稿异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
