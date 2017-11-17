package com.yeejay.yplay.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;

import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 名片dialog
 * Created by Administrator on 2017/11/17.
 */

public class CardDialog extends Dialog {

    private String cardImgStr;

    private String cardDiamondCountStr;

    private String cardNameStr;

    private String cardSchoolNameStr;

    private String cardGradeStr;

    private View.OnClickListener addFriendListener;

    private View.OnClickListener carDialogRlListener;

    private Context context;

    public CardDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CardDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dialog_card);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initCardDialog();
    }

    private void initCardDialog(){
        RelativeLayout cardDialogRl = (RelativeLayout) findViewById(R.id.card_dialog_rl);
        EffectiveShapeView cardImg = (EffectiveShapeView) findViewById(R.id.card_img);
        TextView cardDiamondCount = (TextView) findViewById(R.id.card_diamond_count);
        TextView cardName = (TextView) findViewById(R.id.card_name);
        TextView cardSchoolName = (TextView) findViewById(R.id.card_school_name);
        TextView cardGrade = (TextView) findViewById(R.id.card_grade);
        ImageButton cardAddFriends = (ImageButton) findViewById(R.id.card_add_friends);

        //头像
        if (!TextUtils.isEmpty(cardImgStr)){
            Picasso.with(context).load(cardImgStr).into(cardImg);
        }else {
            cardImg.setImageResource(R.drawable.header_deafult);
        }

        //钻石数
        cardDiamondCount.setText(cardDiamondCountStr);

        //姓名
        cardName.setText(cardNameStr);

        //学校
        cardSchoolName.setText(cardSchoolNameStr);

        //年级
        cardGrade.setText(cardGradeStr);

        //加好友按钮
        cardAddFriends.setOnClickListener(addFriendListener);

        cardDialogRl.setOnClickListener(carDialogRlListener);
    }

    public void setCardImgStr(String cardImgStr) {
        this.cardImgStr = cardImgStr;
    }

    public void setCardDiamondCountStr(String cardDiamondCountStr) {
        this.cardDiamondCountStr = cardDiamondCountStr;
    }

    public void setCardNameStr(String cardNameStr) {
        this.cardNameStr = cardNameStr;
    }

    public void setCardSchoolNameStr(String cardSchoolNameStr) {
        this.cardSchoolNameStr = cardSchoolNameStr;
    }

    public void setCardGradeStr(String cardGradeStr) {
        this.cardGradeStr = cardGradeStr;
    }

    public void setAddFriendListener(View.OnClickListener addFriendListener) {
        this.addFriendListener = addFriendListener;
    }

    public void setCarDialogRlListener(View.OnClickListener carDialogRlListener) {
        this.carDialogRlListener = carDialogRlListener;
    }
}
