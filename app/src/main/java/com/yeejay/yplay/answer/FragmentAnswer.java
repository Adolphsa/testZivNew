package com.yeejay.yplay.answer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.contribute.ActivityContribute1;
import com.yeejay.yplay.customview.ProgressButton;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.QuestionCandidateRespond;
import com.yeejay.yplay.model.QuestionRespond;
import com.yeejay.yplay.model.VoteOptionsBean;
import com.yeejay.yplay.model.VoteRespond;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
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
import pl.droidsonroids.gif.GifImageView;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * 答题
 * Created by Administrator on 2017/10/26.
 */

public class FragmentAnswer extends BaseFragment {

    private static final String TAG = "FragmentAnswer";

    @BindView(R.id.frans_question_number)
    TextView fransTvQuestionCount;
    @BindView(R.id.frgans_img)
    ImageView frgansImg;
    @BindView(R.id.frg_message_count)
    TextView addFriendCount;
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
    ImageButton frgansBtnInvite;
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
    @BindView(R.id.frans_progress)
    GifImageView frandProgress;

    int questionNum = 1;
    int btn1Cnt, btn2Cnt, btn3Cnt, btn4Cnt;
    int total;
    int colorCount = 7;
    int changeCount = 0;    //换一换的次数
    boolean isFreeze;

    int backgroundColor[] = {R.color.play_color7,
            R.color.play_color2,
            R.color.play_color3,
            R.color.play_color4,
            R.color.play_color5,
            R.color.play_color6,
            R.color.play_color1,};

//    int buttonColor[] = {R.color.button_color207,
//            R.color.button_color202,
//            R.color.button_color203,
//            R.color.button_color204,
//            R.color.button_color205,
//            R.color.button_color206,
//            R.color.button_color201};

    int selectButtonColor[] = {R.color.button_color707,
            R.color.button_color702,
            R.color.button_color703,
            R.color.button_color704,
            R.color.button_color705,
            R.color.button_color706,
            R.color.button_color701};

    QuestionRespond.PayloadBean.QuestionBean questionBean;
    List<VoteOptionsBean> voteOptionsBeanList;

    @OnClick(R.id.frgans_tv_relieve)
    public void relieve(View view) {
        System.out.println("解除冷冻");
//        relieve();
    }

    //投稿
    @OnClick(R.id.frg_edit)
    public void contribute(View view) {
        //如果上次为有新投稿信息高亮状态，则重置成非高亮状态;
        frgEdit.setImageResource(R.drawable.contribute_normal);

        startActivity(new Intent(getActivity(), ActivityContribute1.class));
    }

    @OnClick(R.id.frgans_btn1)
    public void btn1(View view) {
        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);
            vote(questionBean.getQid(),
                    questionNum,
                    voteOptionsBeanList.get(0).getUin(),
                    GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            frgansBtn1.setEnabled(false);
            frgansBtn2.setEnabled(false);
            frgansBtn3.setEnabled(false);
            frgansBtn4.setEnabled(false);

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            int width = frgansBtn1.getWidth();
            System.out.println("宽度---" + width);

            frgansBtn1.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn2.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn3.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn4.setButtonColor(selectButtonColor[questionNum % colorCount]);

            new ProgressTask((1 + btn1Cnt) * 100 / total, 1).execute();
            new ProgressTask(btn2Cnt * 100 / total, 2).execute();
            new ProgressTask(btn3Cnt * 100 / total, 3).execute();
            new ProgressTask(btn4Cnt * 100 / total, 4).execute();
        } else {
            System.out.println("返回异常1");
            //frandProgress.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.frgans_btn2)
    public void btn2(View view) {
        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);
            vote(questionBean.getQid(),
                    questionNum,
                    voteOptionsBeanList.get(1).getUin(),
                    GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            frgansBtn1.setEnabled(false);
            frgansBtn2.setEnabled(false);
            frgansBtn3.setEnabled(false);
            frgansBtn4.setEnabled(false);

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            frgansBtn1.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn2.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn3.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn4.setButtonColor(selectButtonColor[questionNum % colorCount]);

            new ProgressTask(btn1Cnt * 100 / total, 1).execute();
            new ProgressTask((1 + btn2Cnt) * 100 / total, 2).execute();
            new ProgressTask(btn3Cnt * 100 / total, 3).execute();
            new ProgressTask(btn4Cnt * 100 / total, 4).execute();
        } else {
            System.out.println("返回异常2");
            //frandProgress.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.frgans_btn3)
    public void btn3(View view) {
        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);
            vote(questionBean.getQid(),
                    questionNum,
                    voteOptionsBeanList.get(2).getUin(),
                    GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            frgansBtn1.setEnabled(false);
            frgansBtn2.setEnabled(false);
            frgansBtn3.setEnabled(false);
            frgansBtn4.setEnabled(false);

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            frgansBtn1.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn2.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn3.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn4.setButtonColor(selectButtonColor[questionNum % colorCount]);

            new ProgressTask(btn1Cnt * 100 / total, 1).execute();
            new ProgressTask((btn2Cnt) * 100 / total, 2).execute();
            new ProgressTask((1 + btn3Cnt) * 100 / total, 3).execute();
            new ProgressTask(btn4Cnt * 100 / total, 4).execute();
        } else {
            System.out.println("返回异常3");
            //frandProgress.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.frgans_btn4)
    public void btn4(View view) {

        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);
            vote(questionBean.getQid(),
                    questionNum,
                    voteOptionsBeanList.get(3).getUin(),
                    GsonUtil.GsonString(voteOptionsBeanList));
            hideNextQuestion();

            frgansBtn1.setEnabled(false);
            frgansBtn2.setEnabled(false);
            frgansBtn3.setEnabled(false);
            frgansBtn4.setEnabled(false);

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            frgansBtn1.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn2.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn3.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn4.setButtonColor(selectButtonColor[questionNum % colorCount]);

            new ProgressTask(btn1Cnt * 100 / total, 1).execute();
            new ProgressTask((btn2Cnt) * 100 / total, 2).execute();
            new ProgressTask((btn3Cnt) * 100 / total, 3).execute();
            new ProgressTask((1 + btn4Cnt) * 100 / total, 4).execute();
        } else {
            System.out.println("返回异常4");
            //frandProgress.setVisibility(View.VISIBLE);

        }
    }

    //换一换
    @OnClick(R.id.frgans_tn_next_person)
    public void nextPersons(View view) {
        //换批人
        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {

            frandProgress.setVisibility(View.VISIBLE);
            Log.i(TAG, "nextPersons: 换一换--" + questionNum);
            getQuestionsCandidate(questionBean.getQid(), questionNum);

        } else {
            //frandProgress.setVisibility(View.VISIBLE);
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

        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);
            questionNum++;
            changeCount = 0;

            hideKeep();
            getQuestion();

            frgansBtn1.updateProgress(0);
            frgansBtn2.updateProgress(0);
            frgansBtn3.updateProgress(0);
            frgansBtn4.updateProgress(0);

            frgansBtn1.setEnabled(true);
            frgansBtn2.setEnabled(true);
            frgansBtn3.setEnabled(true);
            frgansBtn4.setEnabled(true);

        } else {
            System.out.println("返回异常");
            //frandProgress.setVisibility(View.VISIBLE);
        }

    }

    //下一题
    @OnClick(R.id.frgans_btn_next_question)
    public void nextQuestion(View view) {

        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);

            System.out.println("过questionNum----" + questionNum);
            changeCount = 0;

            frgansBtn1.updateProgress(0);
            frgansBtn2.updateProgress(0);
            frgansBtn3.updateProgress(0);
            frgansBtn4.updateProgress(0);

            frgansBtn1.setEnabled(true);
            frgansBtn2.setEnabled(true);
            frgansBtn3.setEnabled(true);
            frgansBtn4.setEnabled(true);

            doskipQuestion(questionNum, questionBean.getQid());
            System.out.println("当前跳过的编号---" + questionNum + ",qid---" +
                    questionBean.getQid());


        } else {
            System.out.println("返回异常");
            //frandProgress.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.frgans_btn_invite)
    public void inviteFriend(View view) {
        //邀请好友
        startActivity(new Intent(getActivity(), ActivityInviteFriend.class));
    }

    private BroadcastReceiver mContributeBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra("contribute_flag", 0);
            LogUtils.getInstance().debug(TAG + " , mContributeBr, flag = " + String.valueOf(flag));
            if (1 == flag) { //表示有新的投稿消息;
                frgEdit.setImageResource(R.drawable.contribute_light);
            }
        }
    };

    private void registerBr() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yeejay.br.contribute");
        getActivity().registerReceiver(mContributeBr, intentFilter);
    }

    private void unregisterBr() {
        getActivity().unregisterReceiver(mContributeBr);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        registerBr();

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.play_color4));
        frgansLinlearLayout.setBackgroundColor(getResources().getColor(R.color.play_color4));
        baseTitleRl.setBackgroundColor(getResources().getColor(R.color.play_color4));
        //Glide.with(getActivity()).load(R.drawable.loading).asGif().
        //        diskCacheStrategy(DiskCacheStrategy.NONE).into(frandProgressContent);

        voteOptionsBeanList = new ArrayList<VoteOptionsBean>();

        frgTitle.setVisibility(View.INVISIBLE);

        frgUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("跳转到我的资料");
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });
        getQuestion();
        frgansCountDownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                System.out.println("倒计时结束");
                relieve();
            }
        });
    }

    //修改颜色
    private void changeColor(int color) {
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
            frgEdit.setVisibility(View.VISIBLE);
            MainActivity mainActivity = (MainActivity) getActivity();
            System.out.println("queationNum%7 == " + (questionNum % colorCount) + ",queationNum---" + questionNum);
            mainActivity.setmColor(backgroundColor[questionNum % colorCount]);
            if (!NetWorkUtil.isNetWorkAvailable(getActivity())) {
                frandProgress.setVisibility(View.VISIBLE);
            } else {
                frandProgress.setVisibility(View.INVISIBLE);
            }
            setFriendCount();

            if (isFreeze) {  //如果是冷却状态就去拉一把问题
                getQuestion();
            }
        }
    }

    private void nextQuestionUpdate() {

        fransTvQuestionCount.setText(questionNum + "/15");
        String url = questionBean.getQiconUrl();
        if (!TextUtils.isEmpty(url)) {
            Log.i(TAG, "nextQuestionUpdate: 加载的图片---" + url);
            Picasso.with(getActivity()).load(url).resizeDimen(R.dimen.non_ques_head_img_width,
                    R.dimen.non_ques_head_img_height).memoryPolicy(NO_CACHE, NO_STORE).into(frgansImg);
        }
        frgansQuestion.setText(questionBean.getQtext());

        changeName(voteOptionsBeanList.get(0).getNickName(),
                voteOptionsBeanList.get(1).getNickName(),
                voteOptionsBeanList.get(2).getNickName(),
                voteOptionsBeanList.get(3).getNickName());

        //获取被投票的次数
        btn1Cnt = voteOptionsBeanList.get(0).getBeSelCnt();
        btn2Cnt = voteOptionsBeanList.get(1).getBeSelCnt();
        btn3Cnt = voteOptionsBeanList.get(2).getBeSelCnt();
        btn4Cnt = voteOptionsBeanList.get(3).getBeSelCnt();

        changeColor(backgroundColor[questionNum % colorCount]);


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

        isFreeze = true;

        //修改背景颜色
        changeColor(R.color.play_color2);

        frgansImg.setImageDrawable(getResources().getDrawable(R.drawable.play_socket));
        frgansQuestion.setText("技能冷却");
        frgansCountDownView.setVisibility(View.VISIBLE);
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
    private void relieve() {

        isFreeze = false;

        questionNum = 1;
        getQuestion();

        fransTvQuestionCount.setVisibility(View.VISIBLE);
        fransTvQuestionCount.setText(questionNum + "");

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

    //拉取问题
    private void getQuestion() {

        Map<String, Object> questionsListMap = new HashMap<>();
        questionsListMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        questionsListMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsListMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .getQuestion(questionsListMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QuestionRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull QuestionRespond questionRespond) {
//                        System.out.println("问题列表---" + questionListRespond.toString());
                        Log.i(TAG, "onNext: 拉取问题---" + questionRespond.toString());
                        if (questionRespond.getCode() == 0) {

                            QuestionRespond.PayloadBean payloadBean = questionRespond.getPayload();
                            if (payloadBean != null && payloadBean.getFreezeStatus() == 0) {
                                Log.i(TAG, "onNext: 正常状态");
                                if (isFreeze) {
                                    isFreeze = false;
                                    relieve();
                                }

                                //非冷冻状态
                                questionBean = questionRespond.getPayload().getQuestion();
                                List<QuestionRespond.PayloadBean.OptionsBean> optionsList = questionRespond.getPayload().getOptions();
                                questionNum = questionRespond.getPayload().getIndex();

                                if (voteOptionsBeanList.size() > 0) {
                                    voteOptionsBeanList.clear();
                                }
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(0).getUin(), optionsList.get(0).getNickName(), optionsList.get(0).getBeSelCnt()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(1).getUin(), optionsList.get(1).getNickName(), optionsList.get(1).getBeSelCnt()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(2).getUin(), optionsList.get(2).getNickName(), optionsList.get(2).getBeSelCnt()));
                                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(3).getUin(), optionsList.get(3).getNickName(), optionsList.get(3).getBeSelCnt()));

                                if (questionBean != null) {
                                    nextQuestionUpdate();
                                }

                            } else if (payloadBean != null && payloadBean.getFreezeStatus() == 1) {
                                Log.i(TAG, "onNext: 冷冻状态");

                                //冷冻状态
                                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.play_color2));
                                //进入冷冻的时间点
                                int freezeTs = payloadBean.getFreezeTs();
                                int nowTs = payloadBean.getNowTs();
                                int freezeDuration = payloadBean.getFreezeDuration();

                                //应该倒计时的时间
                                int countTime = freezeDuration - (nowTs - freezeTs);
                                frgansCountDownView.start(countTime * 1000);
                                System.out.println("倒计时时间---" + countTime);
                                questionOut15();
                            }

                            frandProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("问题列表异常---" + e.getMessage());
                        frandProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //某个问题的候选人
    private void getQuestionsCandidate(int qid, int index) {

        Map<String, Object> questionsCandidateMap = new HashMap<>();
        questionsCandidateMap.put("qid", qid);
        questionsCandidateMap.put("index", index);
        questionsCandidateMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        questionsCandidateMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsCandidateMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .getQuestionsCandidateNew(questionsCandidateMap)
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
                            List<QuestionCandidateRespond.PayloadBean.OptionsBean> optionsList = questionCandidateRespond.getPayload().getOptions();
                            changeCount++;
                            if (voteOptionsBeanList.size() > 0) {
                                voteOptionsBeanList.clear();
                            }
                            voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(0).getUin(), optionsList.get(0).getNickName(), optionsList.get(0).getBeSelCnt()));
                            voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(1).getUin(), optionsList.get(1).getNickName(), optionsList.get(1).getBeSelCnt()));
                            voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(2).getUin(), optionsList.get(2).getNickName(), optionsList.get(2).getBeSelCnt()));
                            voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(3).getUin(), optionsList.get(3).getNickName(), optionsList.get(3).getBeSelCnt()));

                            changeName(optionsList.get(0).getNickName(),
                                    optionsList.get(1).getNickName(),
                                    optionsList.get(2).getNickName(),
                                    optionsList.get(3).getNickName());

                            //获取被投票的次数
                            btn1Cnt = optionsList.get(0).getBeSelCnt();
                            btn2Cnt = optionsList.get(1).getBeSelCnt();
                            btn3Cnt = optionsList.get(2).getBeSelCnt();
                            btn4Cnt = optionsList.get(3).getBeSelCnt();

                            frgansBtn1.setEnabled(true);
                            frgansBtn2.setEnabled(true);
                            frgansBtn3.setEnabled(true);
                            frgansBtn4.setEnabled(true);

                            frandProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取某个问题候选者异常---" + e.getMessage());
                        frandProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //投票
    private void vote(int qid, int index, int voteToUin, String options) {

        Map<String, Object> voteMap = new HashMap<>();
        voteMap.put("qid", qid);
        voteMap.put("index", index);
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

                        frandProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("投票返回异常");
                        frandProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //跳过下个问题
    private void doskipQuestion(int index, int qid) {

        Map<String, Object> doskipMap = new HashMap<>();
        doskipMap.put("qid", qid);
        doskipMap.put("index", index);
        doskipMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        doskipMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        doskipMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .doskipQuestion(doskipMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRespond baseRespond) {
                        System.out.println("跳过下个问题---" + baseRespond);
                        if (baseRespond.getCode() == 0) {
                            questionNum++;
                            getQuestion();

                            frandProgress.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("跳过下个问题异常---" + e.getMessage());
                        frandProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }


    public void setFriendCount() {

        MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        int uin = (int) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_UIN, (int) 0);
        MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null) {
            int addFriendNum = myInfo.getAddFriendNum();
            if (addFriendNum == 0) {
                if (addFriendCount != null)
                    addFriendCount.setText("");
            } else {
                if (addFriendCount != null)
                    addFriendCount.setText(addFriendNum + "");
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBr();
    }
}
