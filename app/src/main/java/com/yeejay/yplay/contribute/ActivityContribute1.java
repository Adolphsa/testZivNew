package com.yeejay.yplay.contribute;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
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

    private static final String EMOJI_URL = "http://yplay-1253229355.image.myqcloud.com/qicon/";

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.con_img)
    ImageView conImg;
    @BindView(R.id.con_edit)
    EditText conEdit;
    @BindView(R.id.con_text_count)
    TextView conTextCount;
    @BindView(R.id.con_apply_button)
    Button conApplyButton;
    @BindView(R.id.con_apply_ll)
    LinearLayout conApplyLl;
    @BindView(R.id.con_complet_button)
    Button conCompletButton;
    @BindView(R.id.con_complete_ll)
    LinearLayout conCompleteLl;


    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }


    @OnClick(R.id.con_img)
    public void conEmojiImg() {
        Intent intent = new Intent(ActivityContribute1.this, ActivityContribute2.class);
        startActivityForResult(intent, 2);
    }

    @OnClick(R.id.con_apply_button)
    public void submit() {

        System.out.println("提交");
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

    @OnClick(R.id.con_complet_button)
    public void conComplete() {
        System.out.println("完成");
        finish();
    }

    int emojiIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute1);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContribute1.this, true);
        layoutTitleBack2.setImageResource(R.drawable.con_back);
        layoutTitle2.setText("投稿");
        layoutTitle2.setTextColor(getResources().getColor(R.color.contribute_color));

        conApplyButton.setEnabled(false);

        initEdit();
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
                int currentSelectEmoji = data.getIntExtra("current_select_emoji", 0);
                emojiIndex = data.getIntExtra("current_emoji_index", 0);
                System.out.println("1---currentSelectEmoji---" + currentSelectEmoji
                        + "current_emoji_index" + emojiIndex);
                String demojiUrl = EMOJI_URL + emojiIndex + ".png";
                Picasso.with(ActivityContribute1.this).load(demojiUrl).into(conImg);
                //conImg.setImageResource(currentSelectEmoji);

                enableButton(conEdit.getText().toString().trim());
            }
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
                            layoutTitleBack2.setVisibility(View.INVISIBLE);
                            conEdit.setEnabled(false);
                            conApplyLl.setVisibility(View.GONE);
                            conCompleteLl.setVisibility(View.VISIBLE);
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
