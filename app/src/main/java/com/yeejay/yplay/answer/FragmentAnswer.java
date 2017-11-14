package com.yeejay.yplay.answer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.MainActivity;
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
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
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

    @BindView(R.id.frans_question_number)
    TextView fransTvQuestionCount;
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
    @BindView(R.id.frgans_tv_or)
    TextView frgansTvOr;
    @BindView(R.id.frgans_tv_relieve)
    TextView frgansTvRelieve;
    @BindView(R.id.frgans_btn_invite)
    Button frgansBtnInvite;
    @BindView(R.id.frg_user_info)
    ImageButton frgUserInfo;
    @BindView(R.id.frgans_count_down_view)
    CountdownView frgansCountDownView;
    @BindView(R.id.base_title_rl)
    RelativeLayout baseTitleRl;
    @BindView(R.id.frg_title)
    TextView frgTitle;
    @BindView(R.id.frg_edit)
    ImageButton frgEdit;
    @BindView(R.id.frgans_linear_layout)
    LinearLayout frgansLinlearLayout;
//    @BindView(R.id.frans_progress)
//    ProgressBar frandProgress;

    int questionNum = 0;
    int nextPersonCount = 1;
    int btn1Cnt, btn2Cnt, btn3Cnt, btn4Cnt;
    int total;

    int backgroundColor[] = {R.color.play_color1,
                            R.color.play_color2,
                            R.color.play_color3,
                            R.color.play_color4};

    int buttonColor[] = {R.color.button_color1,
                        R.color.button_color2,
                        R.color.button_color3,
                        R.color.button_color4};

    //倒计时时间
    private final static int LIMIT_TIME = 20*1000;
    List<QuestionListRespond.PayloadBean.QuestionsBean> questionsList;
    QuestionListRespond.PayloadBean.QuestionsBean questionsBean;

    List<QuestionCandidateRespond.PayloadBean.OptionsBean> optionsList;
    QuestionCandidateRespond.PayloadBean.OptionsBean optionsBean;

    List<VoteOptionsBean> voteOptionsBeanList;

    @OnClick(R.id.frgans_tv_relieve)
    public void relieve(View view){
        System.out.println("解除冷冻");
//        relieve();
    }

    @OnClick(R.id.frgans_btn1)
    public void btn1(View view) {
        if (questionsBean != null) {
            vote(questionsBean.getQid(), optionsList.get(0).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();
            frgansBtn1.setClickable(false);
            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            System.out.println("total---" + total);
            System.out.println(",btn1Cnt---" + btn1Cnt
                    + ",btn2Cnt---" + btn2Cnt
                    + ",btn3Cnt---" + btn3Cnt
                    + ",btn4Cnt---" + btn4Cnt);

            new ProgressTask((1 + btn1Cnt) * 120 / total, 1).execute();
            new ProgressTask(btn2Cnt * 120 / total, 2).execute();
            new ProgressTask(btn3Cnt * 120 / total, 3).execute();
            new ProgressTask(btn4Cnt * 120 / total, 4).execute();
        }else {
            System.out.println("返回异常");
        }

    }

    @OnClick(R.id.frgans_btn2)
    public void btn2(View view) {
        if (questionsBean != null) {
            vote(questionsBean.getQid(), optionsList.get(1).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            new ProgressTask(btn1Cnt * 120 / total, 1).execute();
            new ProgressTask((1 + btn2Cnt) * 120 / total, 2).execute();
            new ProgressTask(btn3Cnt * 120 / total, 3).execute();
            new ProgressTask(btn4Cnt * 120 / total, 4).execute();
        }else {
            System.out.println("返回异常");
        }
    }

    @OnClick(R.id.frgans_btn3)
    public void btn3(View view) {
        if (questionsBean != null) {
            vote(questionsBean.getQid(), optionsList.get(2).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            new ProgressTask(btn1Cnt * 120 / total, 1).execute();
            new ProgressTask((btn2Cnt) * 120 / total, 2).execute();
            new ProgressTask((1 + btn3Cnt) * 120 / total, 3).execute();
            new ProgressTask(btn4Cnt * 120 / total, 4).execute();
        }else {
            System.out.println("返回异常");
        }

    }

    @OnClick(R.id.frgans_btn4)
    public void btn4(View view) {
        if (questionsBean != null) {
            vote(questionsBean.getQid(), optionsList.get(3).getUin(), GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            new ProgressTask(btn1Cnt * 120 / total, 1).execute();
            new ProgressTask((btn2Cnt) * 120 / total, 2).execute();
            new ProgressTask((btn3Cnt) * 120 / total, 3).execute();
            new ProgressTask((1 + btn4Cnt) * 120 / total, 4).execute();
        }else {
            System.out.println("返回异常");
        }
    }

    //换一换
    @OnClick(R.id.frgans_tn_next_person)
    public void nextPersons(View view) {
//        if (nextPersonCount > 5){
//            Toast.makeText(getActivity(),"技能冷却",Toast.LENGTH_SHORT).show();
//            return;
//        }
        //换批人
        if (questionsBean != null) {
            getQuestionsCandidate(questionsBean.getQid());
            nextPersonCount++;
        }

    }

    public class ProgressTask extends AsyncTask<Void, Integer, Void> {

        private int initPrg = 0;
        private int mProgress;
        private int buttonType;

        public ProgressTask(int progress, int buttonType) {
            mProgress = progress;
            this.buttonType = buttonType;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (initPrg < mProgress) {
                publishProgress(initPrg += 3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            frgansBtn1.setClickable(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            System.out.println("问题个数---" + questionNum);
            frgansBtn1.setButtonColor(buttonColor[questionNum%3]);
            frgansBtn2.setButtonColor(buttonColor[questionNum%3]);
            frgansBtn3.setButtonColor(buttonColor[questionNum%3]);
            frgansBtn4.setButtonColor(buttonColor[questionNum%3]);

            System.out.println("颜色参数---" + questionNum%3);

            if (buttonType == 1) {
                frgansBtn1.updateProgress(values[0]);
            } else if (buttonType == 2) {
                frgansBtn2.updateProgress(values[0]);
            } else if (buttonType == 3) {
                frgansBtn3.updateProgress(values[0]);
            } else if (buttonType == 4) {
                frgansBtn4.updateProgress(values[0]);
            }

        }

    }

    @OnClick(R.id.frgans_btn_keep)
    public void btnKeep(View view) {
        //点击继续
        System.out.println("点击继续");

        if (questionsBean != null){
            hideKeep();
            questionNum++;
            System.out.println("继续questionNumber---" + questionNum);
            if (questionNum >= 15) {
                questionOut15();
                return;
            }
            nextQuestionUpdate();
            frgansBtn1.updateProgress(0);
            frgansBtn2.updateProgress(0);
            frgansBtn3.updateProgress(0);
            frgansBtn4.updateProgress(0);

            frgansBtn1.setEnabled(true);
            frgansBtn2.setEnabled(true);
            frgansBtn3.setEnabled(true);
            frgansBtn4.setEnabled(true);
        }else {
            System.out.println("返回异常");
        }

    }

    @OnClick(R.id.frgans_btn_next_question)
    public void nextQuestion(View view) {
        //过
        if (questionsBean != null){
            questionNum++;
            System.out.println("过questionNumber---" + questionNum);
            if (questionNum >= 15) {
                questionOut15();
                return;
            }
            nextQuestionUpdate();
            System.out.println("过");
            frgansBtn1.updateProgress(0);
            frgansBtn2.updateProgress(0);
            frgansBtn3.updateProgress(0);
            frgansBtn4.updateProgress(0);

            frgansBtn1.setEnabled(true);
            frgansBtn2.setEnabled(true);
            frgansBtn3.setEnabled(true);
            frgansBtn4.setEnabled(true);
        }else {
            System.out.println("返回异常");
        }

    }

    @OnClick(R.id.frgans_btn_invite)
    public void inviteFriend(View view) {
        //邀请好友
        startActivity(new Intent(getActivity(), ActivityInviteFriend.class));
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.play_color4));
        frgansLinlearLayout.setBackgroundColor(getResources().getColor(R.color.play_color4));
        baseTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color4));

        frgTitle.setVisibility(View.INVISIBLE);

        frgUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("跳转到我的资料");
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });
        getQuestionsList();
        frgansCountDownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                System.out.println("倒计时结束");
                relieve();
            }
        });
    }

    //修改颜色
    private void changeColor(int color){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setBottomColor(color);
        frgansLinlearLayout.setBackgroundColor(getResources().getColor(color));
        baseTitleRl.setBackgroundColor(getResources().getColor(color));
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser) {
            System.out.println("FragmentAnswer---答题可见");
            MainActivity mainActivity = (MainActivity) getActivity();
            System.out.println("queationNum%3 == " + (questionNum%3) + ",queationNum---" + questionNum);
            mainActivity.setmColor(backgroundColor[questionNum%3]);
            if (!NetWorkUtil.isNetWorkAvailable(getActivity())){
//             frandProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    private void nextQuestionUpdate() {

        if (questionsList != null && questionsList.size() > 0) {
            questionsBean = questionsList.get(questionNum);
            if (questionsBean != null) {
                System.out.println("quesionNum---" + questionNum);
                fransTvQuestionCount.setText(( questionNum+1) + "/15");
                String url = questionsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(getActivity()).load(url).into(frgansImg);
                }
                frgansQuestion.setText(questionsBean.getQtext());
                getQuestionsCandidate(questionsBean.getQid());

                changeColor(backgroundColor[questionNum%3]);
            }

        }

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
    private void questionOut15() {

        //修改背景颜色
        changeColor(R.color.play_color2);

        frgansImg.setImageDrawable(getResources().getDrawable(R.drawable.play_socket));
        frgansQuestion.setText("技能冷却");
        frgansCountDownView.setVisibility(View.VISIBLE);
        frgansCountDownView.start(LIMIT_TIME);

        fransTvQuestionCount.setVisibility(View.INVISIBLE);

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

    //解除冷冻
    private void relieve(){

        questionNum = 0;
        getQuestionsList();

        fransTvQuestionCount.setVisibility(View.VISIBLE);
        fransTvQuestionCount.setText(( questionNum+1) + "/15");

        frgansTvOr.setVisibility(View.INVISIBLE);
        frgansTvRelieve.setVisibility(View.INVISIBLE);
        frgansCountDownView.setVisibility(View.GONE);

        frgansTnNextPerson.setVisibility(View.VISIBLE);
        frgansBtnNextQuestion.setVisibility(View.VISIBLE);
        frgansBtnInvite.setVisibility(View.INVISIBLE);

        frgansBtn1.setVisibility(View.VISIBLE);
        frgansBtn2.setVisibility(View.VISIBLE);
        frgansBtn3.setVisibility(View.VISIBLE);
        frgansBtn4.setVisibility(View.VISIBLE);

        frgansBtn1.updateProgress(0);
        frgansBtn2.updateProgress(0);
        frgansBtn3.updateProgress(0);
        frgansBtn4.updateProgress(0);

        frgansBtn1.setEnabled(true);
        frgansBtn2.setEnabled(true);
        frgansBtn3.setEnabled(true);
        frgansBtn4.setEnabled(true);
    }

    //拉取问题列表
    private void getQuestionsList() {

        Map<String, Object> questionsListMap = new HashMap<>();
        questionsListMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        questionsListMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsListMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

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
                        if (questionListRespond.getCode() == 0) {
                            questionsList = questionListRespond.getPayload().getQuestions();
                            if (questionsList.size() > 0) {
                                nextQuestionUpdate();
                            }
//                            frandProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("问题列表异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //某个问题的候选人
    private void getQuestionsCandidate(int qid) {

        Map<String, Object> questionsCandidateMap = new HashMap<>();
        questionsCandidateMap.put("qid", qid);
        questionsCandidateMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        questionsCandidateMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsCandidateMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

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
                        if (questionCandidateRespond.getCode() == 0) {
                            optionsList = questionCandidateRespond.getPayload().getOptions();
                            voteOptionsBeanList = new ArrayList<VoteOptionsBean>();
                            if (voteOptionsBeanList.size() > 0) {
                                voteOptionsBeanList.clear();
                            } else {
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(0).getUin(), optionsList.get(0).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(1).getUin(), optionsList.get(1).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(2).getUin(), optionsList.get(2).getNickName()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(3).getUin(), optionsList.get(3).getNickName()));
                            }
                            changeName(optionsList.get(0).getNickName(),
                                    optionsList.get(1).getNickName(),
                                    optionsList.get(2).getNickName(),
                                    optionsList.get(3).getNickName());

                            //获取被投票的次数
                            btn1Cnt = optionsList.get(0).getBeSelCnt();
                            btn2Cnt = optionsList.get(1).getBeSelCnt();
                            btn3Cnt = optionsList.get(2).getBeSelCnt();
                            btn4Cnt = optionsList.get(3).getBeSelCnt();
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
    private void vote(int qid, int voteToUin, String options) {

        Map<String, Object> voteMap = new HashMap<>();
        voteMap.put("qid", qid);
        voteMap.put("voteToUin", voteToUin);
        voteMap.put("options", options);
        voteMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        voteMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        voteMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

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
