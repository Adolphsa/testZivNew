package com.yeejay.yplay.answer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 答题
 * Created by Administrator on 2017/10/26.
 */

public class FragmentAnswer extends BaseFragment {


    @BindView(R.id.frgans_tv_number)
    TextView frgansTvNumber;
    @BindView(R.id.frgans_img)
    ImageView frgansImg;
    @BindView(R.id.frgans_question)
    TextView frgansQuestion;
    @BindView(R.id.frgans_btn1)
    Button frgansBtn1;
    @BindView(R.id.frgans_btn2)
    Button frgansBtn2;
    @BindView(R.id.frgans_btn3)
    Button frgansBtn3;
    @BindView(R.id.frgans_btn4)
    Button frgansBtn4;
    @BindView(R.id.frgans_tn_next_person)
    Button frgansTnNextPerson;
    @BindView(R.id.frgans_btn_keep)
    Button frgansBtnKeep;
    @BindView(R.id.frgans_btn_next_question)
    Button frgansBtnNextQuestion;


    int questionNum = 1;
    int nextPersonCount = 1;

    @BindView(R.id.frgans_tv_or)
    TextView frgansTvOr;
    @BindView(R.id.frgans_tv_relieve)
    TextView frgansTvRelieve;
    @BindView(R.id.frgans_btn_invite)
    Button frgansBtnInvite;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

    }

    @OnClick(R.id.frgans_btn1)
    public void btn1(View view) {
        hideNextQuestion();
        frgansBtn2.setEnabled(false);
        frgansBtn3.setEnabled(false);
        frgansBtn4.setEnabled(false);
    }

    @OnClick(R.id.frgans_btn2)
    public void btn2(View view) {
        hideNextQuestion();
        frgansBtn1.setEnabled(false);
        frgansBtn3.setEnabled(false);
        frgansBtn4.setEnabled(false);
    }

    @OnClick(R.id.frgans_btn3)
    public void btn3(View view) {
        hideNextQuestion();
        frgansBtn1.setEnabled(false);
        frgansBtn2.setEnabled(false);
        frgansBtn4.setEnabled(false);
    }

    @OnClick(R.id.frgans_btn4)
    public void btn4(View view) {
        hideNextQuestion();
        frgansBtn1.setEnabled(false);
        frgansBtn2.setEnabled(false);
        frgansBtn3.setEnabled(false);
    }

    @OnClick(R.id.frgans_tn_next_person)
    public void nextPersons(View view) {
//        if (nextPersonCount > 5){
//            Toast.makeText(getActivity(),"技能冷却",Toast.LENGTH_SHORT).show();
//            return;
//        }
        //换批人
        changeName("B1", "B2", "B3", "B4");
        nextPersonCount++;
    }

    @OnClick(R.id.frgans_btn_keep)
    public void btnKeep(View view) {
        //点击继续
        hideKeep();
        nextQuestionUpdate();
        frgansBtn1.setEnabled(true);
        frgansBtn2.setEnabled(true);
        frgansBtn3.setEnabled(true);
        frgansBtn4.setEnabled(true);

    }

    @OnClick(R.id.frgans_btn_next_question)
    public void nextQuestion(View view) {
        //过
        nextQuestionUpdate();
        System.out.println("过");
    }

    @OnClick(R.id.frgans_btn_invite)
    public void inviteFriend(View view){
        //邀请好友
        startActivity(new Intent(getContext(),ActivityInviteFriend.class));
    }

    private void nextQuestionUpdate() {
        System.out.println("questionNum---" + questionNum);
        frgansTvNumber.setText(questionNum + "/15");
        frgansImg.setImageDrawable(getResources().getDrawable(R.drawable.app_img));
        frgansQuestion.setText("white和faker五五开");
        changeName("A1", "A2", "A3", "A4");
        questionNum++;
        if (questionNum > 15)
            questionOut15();
    }

    private void changeName(String btn1, String btn2, String btn3, String btn4) {
        frgansBtn1.setText(btn1);
        frgansBtn2.setText(btn2);
        frgansBtn3.setText(btn3);
        frgansBtn4.setText(btn4);
    }

    private void hideNextQuestion() {
        frgansTnNextPerson.setVisibility(View.INVISIBLE);
        frgansBtnNextQuestion.setVisibility(View.INVISIBLE);
        frgansBtnKeep.setVisibility(View.VISIBLE);
    }

    private void hideKeep() {
        frgansTnNextPerson.setVisibility(View.VISIBLE);
        frgansBtnNextQuestion.setVisibility(View.VISIBLE);
        frgansBtnKeep.setVisibility(View.INVISIBLE);
    }

    //点击继续15次
    private void questionOut15(){
        frgansImg.setImageDrawable(getResources().getDrawable(R.drawable.suo));
        frgansQuestion.setText("技能冷却60分钟00秒");
        frgansBtn1.setVisibility(View.INVISIBLE);
        frgansBtn2.setVisibility(View.INVISIBLE);
        frgansBtn3.setVisibility(View.INVISIBLE);
        frgansBtn4.setVisibility(View.INVISIBLE);

        frgansTvOr.setVisibility(View.VISIBLE);
        frgansTvRelieve.setVisibility(View.VISIBLE);

        frgansTnNextPerson.setVisibility(View.INVISIBLE);
        frgansBtnNextQuestion.setVisibility(View.INVISIBLE);
        frgansBtnInvite.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
