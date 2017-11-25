package com.yeejay.yplay.message;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.ChatAdapter;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityChatWindow extends BaseActivity {


    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.layout_title_rl)
    RelativeLayout layoutTitleRl;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.acw_recycle_view)
    RecyclerView acwRecycleView;
    @BindView(R.id.acw_edit)
    EditText acwEdit;
    @BindView(R.id.acw_send)
    ImageButton acwSend;

    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    @OnClick(R.id.layout_setting)
    public void megMore() {
        System.out.println("聊天对象资料");
    }

    @OnClick(R.id.acw_send)
    public void send() {
        System.out.println("发送消息");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityChatWindow.this, true);

        initTitle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        acwRecycleView.setLayoutManager(linearLayoutManager);

        acwRecycleView.setAdapter(new ChatAdapter(ActivityChatWindow.this));
        System.out.println("设置聊天Adapter");
    }

    private void initTitle() {

        layoutTitleBack2.setImageResource(R.drawable.meaage_back);
        layoutTitle2.setTextColor(getResources().getColor(R.color.message_title_color));
        layoutTitle2.setText("聊天人姓名");
        layoutSetting.setVisibility(View.VISIBLE);
        layoutSetting.setImageResource(R.drawable.message_more);
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
                    acwEdit.setCursorVisible(false);

                } else {
                    acwEdit.setCursorVisible(true);
                }
            }
        });
    }
}
