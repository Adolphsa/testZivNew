package com.yeejay.yplay.answer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.customview.ProgressButton;
import com.yeejay.yplay.model.QuestionCandidateRespond;
import com.yeejay.yplay.model.QuestionListRespond;
import com.yeejay.yplay.model.VoteOptionsBean;
import com.yeejay.yplay.model.VoteRespond;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    ProgressButton frgansBtn1;
    @BindView(R.id.frgans_btn2)
    ProgressButton frgansBtn2;
    @BindView(R.id.frgans_btn3)
    ProgressButton frgansBtn3;
    @BindView(R.id.frgans_btn4)
    ProgressButton frgansBtn4;
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
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;

    List<QuestionListRespond.PayloadBean.QuestionsBean> questionsList;
    QuestionListRespond.PayloadBean.QuestionsBean questionsBean;

    List<QuestionCandidateRespond.PayloadBean.OptionsBean> optionsList;
    QuestionCandidateRespond.PayloadBean.OptionsBean optionsBean;

    List<VoteOptionsBean> voteOptionsBeanList;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        getQuestionsList();
        frgUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("跳转到我的资料");
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });
    }

    @OnClick(R.id.frgans_btn1)
    public void btn1(View view) {
        vote(questionsBean.getQid(),optionsList.get(0).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
        hideNextQuestion();
    }

    @OnClick(R.id.frgans_btn2)
    public void btn2(View view) {
        vote(questionsBean.getQid(),optionsList.get(1).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
        hideNextQuestion();
    }

    @OnClick(R.id.frgans_btn3)
    public void btn3(View view) {
        vote(questionsBean.getQid(),optionsList.get(2).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
        hideNextQuestion();
    }

    @OnClick(R.id.frgans_btn4)
    public void btn4(View view) {
        vote(questionsBean.getQid(),optionsList.get(3).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
        hideNextQuestion();
    }

    @OnClick(R.id.frgans_tn_next_person)
    public void nextPersons(View view) {
//        if (nextPersonCount > 5){
//            Toast.makeText(getActivity(),"技能冷却",Toast.LENGTH_SHORT).show();
//            return;
//        }
        //换批人
        getQuestionsCandidate(questionsBean.getQid());
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
        startActivity(new Intent(getActivity(),ActivityInviteFriend.class));
    }

    private void nextQuestionUpdate() {

        System.out.println("questionNum---" + questionNum);
        questionsBean = questionsList.get(questionNum);
        frgansTvNumber.setText(questionNum + "/15");
        Picasso.with(getActivity()).load(questionsBean.getQiconUrl()).into(frgansImg);
        frgansQuestion.setText(questionsBean.getQtext());
        getQuestionsCandidate(questionsBean.getQid());
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

    //拉取问题列表
    private void getQuestionsList(){

        Map<String,Object> questionsListMap = new HashMap<>();
        questionsListMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN,0));
        questionsListMap.put("token",SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN,"yplay"));
        questionsListMap.put("ver",SharePreferenceUtil.get(getActivity(),YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .getQuestionsList(questionsListMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QuestionListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull QuestionListRespond questionListRespond) {
                        System.out.println("问题列表---" + questionListRespond.toString());
                        if (questionListRespond.getCode() == 0){
                            questionsList = questionListRespond.getPayload().getQuestions();
                            nextQuestionUpdate();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //某个问题的候选人
    private void getQuestionsCandidate(int qid){

        Map<String,Object> questionsCandidateMap = new HashMap<>();
        questionsCandidateMap.put("qid",qid);
        questionsCandidateMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN,0));
        questionsCandidateMap.put("token",SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN,"yplay"));
        questionsCandidateMap.put("ver",SharePreferenceUtil.get(getActivity(),YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .getQuestionsCandidate(questionsCandidateMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QuestionCandidateRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull QuestionCandidateRespond questionCandidateRespond) {
                        System.out.println("某个问题的候选者---" + questionCandidateRespond.toString());
                        if (questionCandidateRespond.getCode() == 0){
                            optionsList = questionCandidateRespond.getPayload().getOptions();
                            voteOptionsBeanList = new ArrayList<VoteOptionsBean>();
                            if (voteOptionsBeanList.size() > 0){
                                voteOptionsBeanList.clear();
                            }else {
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(0).getUin(),optionsList.get(0).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(1).getUin(),optionsList.get(1).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(2).getUin(),optionsList.get(2).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(3).getUin(),optionsList.get(3).getNickName()));
                            }
                            changeName(optionsList.get(0).getNickName(),
                                    optionsList.get(1).getNickName(),
                                    optionsList.get(2).getNickName(),
                                    optionsList.get(3).getNickName()
                            );
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //投票
    private void vote(int qid, int voteToUin, String options){

        Map<String,Object> voteMap = new HashMap<>();
        voteMap.put("qid",qid);
        voteMap.put("voteToUin",voteToUin);
        voteMap.put("options",options);
        voteMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN,0));
        voteMap.put("token",SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN,"yplay"));
        voteMap.put("ver",SharePreferenceUtil.get(getActivity(),YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .vote(voteMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VoteRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull VoteRespond voteRespond) {
                        System.out.println("投票返回---" + voteRespond);
                        frgansBtn1.setProgress(optionsList.get(0).getBeSelCnt() + 50);
                        frgansBtn2.setProgress(optionsList.get(1).getBeSelCnt() + 50);
                        frgansBtn3.setProgress(optionsList.get(2).getBeSelCnt() + 50);
                        frgansBtn4.setProgress(optionsList.get(3).getBeSelCnt() + 50);

                        frgansBtn1.setEnabled(false);
                        frgansBtn2.setEnabled(false);
                        frgansBtn3.setEnabled(false);
                        frgansBtn4.setEnabled(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
