package com.yeejay.yplay.contribute;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

public class ActivityContributeReedit extends BaseActivity {
    @BindView(R.id.layout_title2)
    TextView titleView;
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

    private static final String TAG = "ContributeReedit";
    private static final String EMOJI_URL = "http://yplay-1253229355.image.myqcloud.com/qicon/";

    private String mQiconUrl;
    private int mPosition;

    @OnClick(R.id.con_edit)
    public void clickEdit() {
        rlEditText.setBackgroundResource(R.drawable.shape_con1_edit_selected_background);
        conEdit.setCursorVisible(true);
    }

    @OnClick(R.id.rl_edittext)
    public void clickRlEdittext() {
        rlEditText.setBackgroundResource(R.drawable.shape_con1_edit_selected_background);
        conEdit.setCursorVisible(true);
    }

    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    @OnClick(R.id.con_img)
    public void conEmojiImg() {
        Intent intent = new Intent(ActivityContributeReedit.this, ActivityContribute2.class);
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.con_selected_img)
    public void conSelectedEmojiImg() {
        Intent intent = new Intent(ActivityContributeReedit.this, ActivityContribute2.class);
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.con_apply_button)
    public void submit() {
        System.out.println("提交");
        if(NetWorkUtil.isNetWorkAvailable(ActivityContributeReedit.this)){

            String questionText = conEdit.getText().toString();
            if (!TextUtils.isEmpty(questionText)){
                System.out.println("问题不为空" + "v, emojiIndex = " + emojiIndex +
                        " , mQiconUrl = " + mQiconUrl
                        + " , getQiconId(mQiconUrl) = " + getQiconId(mQiconUrl));
                submitQuestion(questionText, emojiIndex != -1 ? emojiIndex : getQiconId(mQiconUrl));
            }

        }else {
            Toast.makeText(ActivityContributeReedit.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    int emojiIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute1);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContributeReedit.this, true);

        conApplyButton.setEnabled(false);

        initOthers();
        initEdit();
    }

    private void initOthers() {
        layoutTitleBack2.setImageResource(R.drawable.back_black);
        titleView.setText(R.string.re_edit);
        contributeHistory.setVisibility(View.GONE);

        //设置之前选择的未通过审核投稿的文本和图片；
        conEdit.setText(getIntent().getStringExtra("selected_con_qtext"));
        mQiconUrl = getIntent().getStringExtra("selected_con_qiconurl");
        mPosition = getIntent().getIntExtra("selected_con_position", 0);
        conImg.setVisibility(View.GONE);
        selectedImg.setVisibility(View.VISIBLE);
        Picasso.with(ActivityContributeReedit.this).load(mQiconUrl).into(selectedImg);
    }

    private void initEdit() {

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
                System.out.println("1---currentSelectEmoji---" + currentSelectEmoji
                        + "current_emoji_index" + emojiIndex);
                String demojiUrl = EMOJI_URL + emojiIndex + ".png";
                Picasso.with(ActivityContributeReedit.this).load(demojiUrl).into(selectedImg);

                enableButton(conEdit.getText().toString().trim());
            }
        }
    }

    private void enableButton(String s){
        if (s.length() > 0 && (!TextUtils.isEmpty(mQiconUrl) || emojiIndex != -1)) {
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
        conMap.put("uin", SharePreferenceUtil.get(ActivityContributeReedit.this, YPlayConstant.YPLAY_UIN, 0));
        conMap.put("token", SharePreferenceUtil.get(ActivityContributeReedit.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        conMap.put("ver", SharePreferenceUtil.get(ActivityContributeReedit.this, YPlayConstant.YPLAY_VER, 0));
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

                            Toast.makeText(ActivityContributeReedit.this,R.string.contribute_success,
                                    Toast.LENGTH_SHORT).show();
                            //通知查询投稿页面中的未上线子页面，更新未上线列表信息;
                            Intent dataIntent = new Intent();
                            dataIntent.putExtra("position", mPosition);
                            setResult(6, dataIntent);

                            finish();
                        }else {
                            Toast.makeText(ActivityContributeReedit.this,"提交失败",Toast.LENGTH_SHORT).show();
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

    private int getQiconId(String qIconUrl) {
        int ret = -1;
        if (!TextUtils.isEmpty(qIconUrl)) {
            int start = qIconUrl.lastIndexOf('/');
            int end = qIconUrl.lastIndexOf('.');
            try {
                LogUtils.getInstance().debug(qIconUrl.substring(start + 1, end));
                ret = Integer.parseInt(qIconUrl.substring(start + 1, end));
            } catch (NumberFormatException e) {
                LogUtils.getInstance().error("exception happend; + " + e.getMessage());
            }
        }

        return ret;
    }
}