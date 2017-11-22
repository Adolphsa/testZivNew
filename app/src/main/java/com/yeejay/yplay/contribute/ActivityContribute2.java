package com.yeejay.yplay.contribute;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.GridSpacingItemDecoration;
import com.yeejay.yplay.model.EmojiImgBean;
import com.yeejay.yplay.utils.StatuBarUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityContribute2 extends BaseActivity {

    private static final int COLUMN = 5;


    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack2;
    @BindView(R.id.layout_title2)
    TextView layoutTitle2;
    @BindView(R.id.layout_setting)
    ImageButton layoutSetting;
    @BindView(R.id.con_recycle_view)
    SwipeMenuRecyclerView conRecycleView;

    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    @OnClick(R.id.layout_setting)
    public void conOk() {
        System.out.println("提交");
        Intent intent = new Intent();
        intent.putExtra("current_select_emoji",currentSelectEmoji);
        intent.putExtra("current_emoji_index",emojiIndex);
        setResult(1,intent);
        finish();
    }

    private List<EmojiImgBean> mDatas;
    EmojiAdapter emojiAdapter;
    int currentSelectEmoji;
    int emojiIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute2);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityContribute2.this, true);
        layoutTitleBack2.setImageResource(R.drawable.con_back);
        layoutTitle2.setText("投稿");
        layoutTitle2.setTextColor(getResources().getColor(R.color.contribute_color));

        layoutSetting.setVisibility(View.VISIBLE);
        layoutSetting.setImageResource(R.drawable.con_not_ok);
        layoutSetting.setEnabled(false);

        initData();
        System.out.println("fileName = " + mDatas.size());

        initRecycleView();
    }

    private void initData() {

        ApplicationInfo applicationInfo = getApplicationInfo();
        mDatas = new ArrayList<>();
        //获取drawable文件名列表，不包含扩展名
        Field[] fields = R.drawable.class.getDeclaredFields();
        for (Field field : fields) {

            int resID = getResources().getIdentifier(field.getName(),
                    "drawable", applicationInfo.packageName);
//            System.out.println("fileName = " + field.getName()
//                    + "    resId = " + resID + "," + applicationInfo.packageName);

            String fName = field.getName();
            if (fName.startsWith("c_")) {
                mDatas.add(new EmojiImgBean(fName, resID, false));
            }
        }
    }

    private void initRecycleView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMN, GridLayoutManager.VERTICAL, false);
        conRecycleView.setLayoutManager(gridLayoutManager);
        conRecycleView.addItemDecoration(new GridSpacingItemDecoration(COLUMN,
                10,
                true));

        conRecycleView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                EmojiImgBean emojiImgBean = mDatas.get(position);

                for (int i = 0; i < mDatas.size(); i++) {
                    mDatas.get(i).setSelected(false);
                }
                emojiImgBean.setSelected(true);
                emojiAdapter.notifyDataSetChanged();
                System.out.println("被点击表情的name---" + emojiImgBean.getFileName());

                layoutSetting.setEnabled(true);
                layoutSetting.setImageResource(R.drawable.con_ok);

                currentSelectEmoji = emojiImgBean.getResId();
                System.out.println("2---currentSelectEmoji---" + currentSelectEmoji);
                String emojiName = emojiImgBean.getFileName();
                System.out.println("选中的emojiname---" + emojiName);
                String tempStr = emojiName.substring(2,emojiName.length());
                System.out.println("tempstr---" + tempStr);
                emojiIndex = Integer.valueOf(tempStr);
            }
        });

        emojiAdapter = new EmojiAdapter();
        conRecycleView.setAdapter(emojiAdapter);
    }

    class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {

        @Override
        public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            EmojiViewHolder emojiViewHolder = new EmojiViewHolder(
                    LayoutInflater.from(ActivityContribute2.this)
                            .inflate(R.layout.item_com_image, parent, false));
            return emojiViewHolder;
        }

        @Override
        public void onBindViewHolder(final EmojiViewHolder holder, int position) {

            EmojiImgBean emojiImgBean = mDatas.get(position);

            boolean isselected = emojiImgBean.isSelected();

            holder.emojiImg.setImageResource(mDatas.get(position).getResId());

            if (isselected) {
                holder.emojiImg.setBackground(getDrawable(R.drawable.shape_con_emoji_select));
            } else {
                holder.emojiImg.setBackground(getDrawable(R.color.color_transparent));
            }
        }

        @Override
        public int getItemCount() {
            return null != mDatas ? mDatas.size() : 0;
        }

        class EmojiViewHolder extends RecyclerView.ViewHolder {

            ImageView emojiImg;

            public EmojiViewHolder(View itemView) {
                super(itemView);
                emojiImg = (ImageView) itemView.findViewById(R.id.item_com_img_view);
            }
        }

    }
}
