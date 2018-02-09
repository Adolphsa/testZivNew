package com.yeejay.yplay.answer;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.contribute.ActivityContribute1;
import com.yeejay.yplay.customview.ProgressButton;
import com.yeejay.yplay.customview.RankViewPager;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.greendao.RankInfo;
import com.yeejay.yplay.greendao.RankInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.FriendRankRespond;
import com.yeejay.yplay.model.QuestionCandidateRespond;
import com.yeejay.yplay.model.QuestionRankRespond;
import com.yeejay.yplay.model.QuestionRespond;
import com.yeejay.yplay.model.VoteOptionsBean;
import com.yeejay.yplay.model.VoteRespond;
import com.yeejay.yplay.userinfo.ActivityMyInfo;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import pl.droidsonroids.gif.GifImageView;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * 答题
 * Created by Administrator on 2017/10/26.
 */

public class FragmentAnswer extends BaseFragment {

    private static final String TAG = "FragmentAnswer";

    @BindView(R.id.frans_play_root)
    RelativeLayout fransPlayroot;
    @BindView(R.id.frans_cooling_root)
    RelativeLayout fransCoolingRoot;
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
    RelativeLayout frgansLinlearLayout;
    @BindView(R.id.frans_progress)
    GifImageView frandProgress;

    //排行榜相关
    @BindView(R.id.fa_rank_list)
    LinearLayout mainLinearLayout;
    @BindView(R.id.marl_title)
    TextView marlTitle;
    @BindView(R.id.marl_viewPager)
    RankViewPager marlViewPager;

    int questionNum = 1;
    int questionTotal;
    int btn1Cnt, btn2Cnt, btn3Cnt, btn4Cnt;
    int total;
    int colorCount = 4;
    int changeCount = 0;    //换一换的次数
    boolean isFreeze;

    protected static final float FLIP_DISTANCE = 50;
    GestureDetector mDetector;
    boolean upDownStatus = true;
    ObjectAnimator topPullAnimation;
    ObjectAnimator topUpAnimation;
    float rlTopShareHeight;
    RankInfoDao rankInfoDao;
    List<RankInfo> questionRankList;

    int backgroundColor[] = {R.drawable.shape_answer_play1,
            R.drawable.shape_answer_play2,
            R.drawable.shape_answer_play3,
            R.drawable.shape_answer_play4
    };

    int backgroundStartColor[] = {
            R.color.play_gradient_1_start,
            R.color.play_gradient_2_start,
            R.color.play_gradient_3_start,
            R.color.play_gradient_4_start
    };

    int backgroundEndColor[] = {
            R.color.play_gradient_1_end,
            R.color.play_gradient_2_end,
            R.color.play_gradient_3_end,
            R.color.play_gradient_4_end
    };

    int buttonColor[] = {R.drawable.shape_play_button_background1,
            R.drawable.shape_play_button_background2,
            R.drawable.shape_play_button_background3,
            R.drawable.shape_play_button_background4
    };

    int selectButtonColor[] = {R.color.button_selector_color_1,
            R.color.button_selector_color_2,
            R.color.button_selector_color_3,
            R.color.button_selector_color_4
    };

    QuestionRespond.PayloadBean.QuestionBean questionBean;
    List<VoteOptionsBean> voteOptionsBeanList;

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

            Log.i(TAG, "btn1: qid----" + questionBean.getQid());

            frgansBtn1.setEnabled(false);
            frgansBtn2.setEnabled(false);
            frgansBtn3.setEnabled(false);
            frgansBtn4.setEnabled(false);

            total = btn1Cnt + btn2Cnt + btn3Cnt + btn4Cnt + 1;

            int width = frgansBtn1.getWidth();
            LogUtils.getInstance().debug("宽度 = {}", width);

            frgansBtn1.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn2.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn3.setButtonColor(selectButtonColor[questionNum % colorCount]);
            frgansBtn4.setButtonColor(selectButtonColor[questionNum % colorCount]);

            new ProgressTask((1 + btn1Cnt) * 100 / total, 1).execute();
            new ProgressTask(btn2Cnt * 100 / total, 2).execute();
            new ProgressTask(btn3Cnt * 100 / total, 3).execute();
            new ProgressTask(btn4Cnt * 100 / total, 4).execute();
        } else {
            LogUtils.getInstance().debug("返回异常1");
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
            LogUtils.getInstance().debug("返回异常2");
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
            LogUtils.getInstance().debug("返回异常3");
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
            LogUtils.getInstance().debug("返回异常4");
            //frandProgress.setVisibility(View.VISIBLE);

        }
    }

    //换一换
    @OnClick(R.id.frgans_tn_next_person)
    public void nextPersons(View view) {
        //换批人
        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {

            frandProgress.setVisibility(View.VISIBLE);
            LogUtils.getInstance().debug("nextPersons: 换一换, {}", questionNum);
            getQuestionsCandidate(questionBean.getQid(), questionNum);

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
        LogUtils.getInstance().debug("点击继续");

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
            LogUtils.getInstance().debug("返回异常");
            //frandProgress.setVisibility(View.VISIBLE);
        }

    }

    //下一题
    @OnClick(R.id.frgans_btn_next_question)
    public void nextQuestion(View view) {

        if (NetWorkUtil.isNetWorkAvailable(getActivity()) && questionBean != null) {
            frandProgress.setVisibility(View.VISIBLE);

            LogUtils.getInstance().debug("过questionNum = {}", questionNum);
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
            LogUtils.getInstance().debug("当前跳过的编号, {} ; qid = {}", questionNum,
                    questionBean.getQid());

        } else {
            LogUtils.getInstance().debug("返回异常");
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

        getActivity().getWindow().setStatusBarColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));
        fransPlayroot.setBackgroundResource(backgroundColor[questionNum % colorCount]);
        baseTitleRl.setBackgroundColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));
        rankInfoDao = YplayApplication.getInstance().getDaoSession().getRankInfoDao();


        voteOptionsBeanList = new ArrayList<>();

        frgTitle.setVisibility(View.INVISIBLE);

        frgUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.getInstance().debug("跳转到我的资料");
                startActivity(new Intent(getActivity(), ActivityMyInfo.class));
            }
        });

        questionRankList = new ArrayList<>();

        getQuestion();
        frgansCountDownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                LogUtils.getInstance().debug("倒计时结束");
                relieve();
            }
        });


        mDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp: viewPager被点击了");

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if (e1.getY() - e2.getY() > FLIP_DISTANCE) {
                    Log.i(TAG, "向上滑...");
                    if (mainLinearLayout.isShown()) {
                        upDownStatus = true;
                        disappearRankList();
                        getActivity().getWindow().setStatusBarColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));
                        return true;
                    }

                }

                if (e2.getY() - e1.getY() > FLIP_DISTANCE) {
                    Log.i(TAG, "向下滑...");
                    if (upDownStatus) {
                        upDownStatus = false;
                        showRankList();
                        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                        return true;
                    }

                }

//                if (e1.getX() - e2.getX() > FLIP_DISTANCE){
//                    Log.i(TAG, "onFling: 左滑");
//
//                }
//
//                if (e2.getX() - e1.getX() > FLIP_DISTANCE){
//                    Log.i(TAG, "onFling: 右滑");
//
//
//                }
                return false;
            }
        });

        frgansLinlearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        marlViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        //解决在onCreate中不能获取高度的问题
        mainLinearLayout.post(new Runnable() {
            @Override
            public void run() {

                rlTopShareHeight = mainLinearLayout.getHeight();
                initAnimation();
            }
        });

    }


    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        if (isVisibleToUser) {
            LogUtils.getInstance().debug("FragmentAnswer---答题可见");
            frgEdit.setVisibility(View.VISIBLE);
            MainActivity mainActivity = (MainActivity) getActivity();
            LogUtils.getInstance().debug("queationNum%4 = {} , queationNum = {}",
                    (questionNum % colorCount), questionNum);
            mainActivity.setmColor(backgroundStartColor[questionNum % colorCount]);
//            if (!NetWorkUtil.isNetWorkAvailable(getActivity())) {
//                frandProgress.setVisibility(View.VISIBLE);
//            } else {
//                frandProgress.setVisibility(View.INVISIBLE);
//            }
            setFriendCount();

            if (isFreeze) {  //如果是冷却状态就去拉一把问题
                getQuestion();
            } else {
                //getActivity().getWindow().setStatusBarColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));
            }
        }
    }

    private void nextQuestionUpdate() {

        fransTvQuestionCount.setText("- " + questionNum + "/" + questionTotal + " -");
        String url = questionBean.getQiconUrl();
        if (!TextUtils.isEmpty(url)) {
            LogUtils.getInstance().error("nextQuestionUpdate: 加载的图片, {}", url);
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

        //更新界面颜色
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setBottomColor(backgroundEndColor[questionNum % colorCount]);
        fransPlayroot.setBackgroundResource(backgroundColor[questionNum % colorCount]);
        baseTitleRl.setBackgroundColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));
        getActivity().getWindow().setStatusBarColor(getResources().getColor(backgroundStartColor[questionNum % colorCount]));

        Log.i(TAG, "nextQuestionUpdate: questionNum---" + questionNum + ",colorCount---" + colorCount);
        Log.i(TAG, "nextQuestionUpdate: 颜色数组序号---" + questionNum % colorCount);

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

        baseTitleRl.setBackgroundColor(getResources().getColor(R.color.cooling_gradient_end));

        fransPlayroot.setVisibility(View.GONE);
        fransCoolingRoot.setVisibility(View.VISIBLE);
    }

    //解除冷冻
    private void relieve() {

        isFreeze = false;

        questionNum = 1;
        getQuestion();

        fransPlayroot.setVisibility(View.VISIBLE);
        fransCoolingRoot.setVisibility(View.GONE);

        frgansBtn1.updateProgress(0);
        frgansBtn2.updateProgress(0);
        frgansBtn3.updateProgress(0);
        frgansBtn4.updateProgress(0);
    }

    //拉取问题
    private void getQuestion() {
        Map<String, Object> questionsListMap = new HashMap<>();
        final int uin = (int)SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0);
        questionsListMap.put("uin", uin);
        questionsListMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsListMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETQUESTIONANDOPTIONS, questionsListMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetQuestionResponse(result,uin);
                        frandProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("问题列表异常");
                        frandProgress.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void handleGetQuestionResponse(String result,int uin) {
        QuestionRespond questionRespond = GsonUtil.GsonToBean(result, QuestionRespond.class);
        LogUtils.getInstance().debug("onNext: 拉取问题, {}", questionRespond.toString());
        Log.i(TAG, "handleGetQuestionResponse: 拉取问题---" + questionRespond.toString());

        if (questionRespond.getCode() == 0) {

            QuestionRespond.PayloadBean payloadBean = questionRespond.getPayload();
            if (payloadBean != null && payloadBean.getFreezeStatus() == 0) {
                LogUtils.getInstance().debug("onNext: 正常状态");
                if (isFreeze) {
                    isFreeze = false;
                    relieve();
                }

                //非冷冻状态
                questionBean = questionRespond.getPayload().getQuestion();
                List<QuestionRespond.PayloadBean.OptionsBean> optionsList = questionRespond.getPayload().getOptions();
                questionNum = questionRespond.getPayload().getIndex();
                questionTotal = questionRespond.getPayload().getTotal();

                //如果是第一题   则删掉该uin下所有记录
                if (questionNum == 1){

                    DeleteQuery deleteQuery =  rankInfoDao.queryBuilder()
                            .where(RankInfoDao.Properties.Uin.eq(uin))
                            .buildDelete();
                    deleteQuery.executeDeleteWithoutDetachingEntities();

                }

                //将题目内容插入到result字段中
                RankInfo rankInfo = rankInfoDao.queryBuilder()
                        .where(RankInfoDao.Properties.Uin.eq(uin))
                        .where(RankInfoDao.Properties.QuestionNumber.eq(questionNum))
                        .build().unique();
                if (rankInfo == null){
                    rankInfo = new RankInfo(null,uin,questionNum,questionBean.getQtext(),null);
                    rankInfoDao.insert(rankInfo);

                }else {
                    rankInfo.setQuestionNumber(questionNum);
                    rankInfo.setQuestionText(questionBean.getQtext());
                    rankInfoDao.update(rankInfo);
                }
                if (voteOptionsBeanList.size() > 0) {
                    voteOptionsBeanList.clear();
                }
                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(0).getUin(), optionsList.get(0).getNickName(), optionsList.get(0).getBeSelCnt()));
                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(1).getUin(), optionsList.get(1).getNickName(), optionsList.get(1).getBeSelCnt()));
                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(2).getUin(), optionsList.get(2).getNickName(), optionsList.get(2).getBeSelCnt()));
                voteOptionsBeanList.add(new VoteOptionsBean(optionsList.get(3).getUin(), optionsList.get(3).getNickName(), optionsList.get(3).getBeSelCnt()));

                frgansBtn1.setBackgroundResource(buttonColor[questionNum % colorCount]);
                frgansBtn2.setBackgroundResource(buttonColor[questionNum % colorCount]);
                frgansBtn3.setBackgroundResource(buttonColor[questionNum % colorCount]);
                frgansBtn4.setBackgroundResource(buttonColor[questionNum % colorCount]);

                Log.i(TAG, "handleGetQuestionResponse: base button color ---" + questionNum % colorCount);

                if (questionBean != null) {
                    nextQuestionUpdate();
                }

            } else if (payloadBean != null && payloadBean.getFreezeStatus() == 1) {
                LogUtils.getInstance().debug("onNext: 冷冻状态");

                //冷冻状态
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.cooling_gradient_end));
                //进入冷冻的时间点
                int freezeTs = payloadBean.getFreezeTs();
                int nowTs = payloadBean.getNowTs();
                int freezeDuration = payloadBean.getFreezeDuration();

                //应该倒计时的时间
                int countTime = freezeDuration - (nowTs - freezeTs);
                frgansCountDownView.start(countTime * 1000);
                LogUtils.getInstance().debug("倒计时时间, {}", countTime);
                questionOut15();
            }

        }
    }

    //某个问题的候选人
    private void getQuestionsCandidate(int qid, int index) {
        Map<String, Object> questionsCandidateMap = new HashMap<>();
        questionsCandidateMap.put("qid", qid);
        questionsCandidateMap.put("index", index);
        questionsCandidateMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        questionsCandidateMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionsCandidateMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETOPTIONS, questionsCandidateMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetQuestionsCandidateResponse(result);
                        frandProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("获取某个问题候选者异常");
                        frandProgress.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void handleGetQuestionsCandidateResponse(String result) {
        QuestionCandidateRespond questionCandidateRespond = GsonUtil.GsonToBean(result, QuestionCandidateRespond.class);
        LogUtils.getInstance().debug("某个问题的候选者, {}", questionCandidateRespond.toString());
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
        }
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

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_DOVOTE, voteMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        VoteRespond voteRespond = GsonUtil.GsonToBean(result, VoteRespond.class);
                        LogUtils.getInstance().debug("投票返回, {}", voteRespond.toString());
                        frandProgress.setVisibility(View.INVISIBLE);

                        //在投票的过程中去拉取问题排行榜并插入至数据库中
                        getQuestionRankList(questionBean.getQid());
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("投票返回异常");
                        frandProgress.setVisibility(View.VISIBLE);
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

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_DOSKIP, doskipMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleDoskipQuestionResponse(result);
                    }

                    @Override
                    public void onTimeOut() {
                        frandProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("跳过下个问题异常");
                        frandProgress.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void handleDoskipQuestionResponse(String result) {
        BaseRespond baseRespond = GsonUtil.GsonToBean(result, BaseRespond.class);
        LogUtils.getInstance().debug("跳过下个问题, {}", baseRespond.toString());
        if (baseRespond.getCode() == 0) {
            questionNum++;
            getQuestion();

            frandProgress.setVisibility(View.INVISIBLE);
        }
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

    //向下滑动 显示排行榜
    private void showRankList() {

        Log.i(TAG, "showRankList: rlTopShareHeight---" + rlTopShareHeight);
        if (!topPullAnimation.isRunning()) {
            Log.i(TAG, "showRankList: 运行动画");
            topPullAnimation.start();
        }
        marlTitle.setText("(" + questionNum + "/" + questionNum + ")");
        getFriendList();

    }


    //向上滑动  排行榜消失
    private void disappearRankList() {
        topUpAnimation = ObjectAnimator.ofFloat(
                mainLinearLayout, "translationY", -rlTopShareHeight);
        topPullAnimation.setDuration(800);
        topUpAnimation.start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBr();
    }

    /**
     * 初始化Animation
     */
    private void initAnimation() {
        Log.i(TAG, "initAnimation: rlTopShareHeight  " + rlTopShareHeight);
        //打开动画
        topPullAnimation = ObjectAnimator.ofFloat(
                mainLinearLayout, "translationY", 0);
        topPullAnimation.setDuration(800);
        topPullAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//        //关闭动画
        topUpAnimation = ObjectAnimator.ofFloat(
                mainLinearLayout, "translationY", -rlTopShareHeight);
        topUpAnimation.start();

    }


    //好友排行榜
    private void getFriendList(){

        String url = YPlayConstant.BASE_URL + YPlayConstant.API_WEEK_LIST_URL;

        Map<String, Object> friendRankMap = new HashMap<>();
        friendRankMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        friendRankMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendRankMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        friendRankMap.put("uid", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));

        WnsAsyncHttp.wnsRequest(url, friendRankMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 好友排行榜---" + result);
                handleFriendRankResponse(result);
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });
    }

    //处理好友排行榜返回
    private void handleFriendRankResponse(String result){

        FriendRankRespond friendRankRespond = GsonUtil.GsonToBean(result,FriendRankRespond.class);
        int uin = (int)SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0);
        if (friendRankRespond.getCode() == 0){

            questionRankList = rankInfoDao.queryBuilder()
                    .where(RankInfoDao.Properties.Uin.eq(uin))
                    .orderAsc(RankInfoDao.Properties.Id)
                    .list();

            Log.i(TAG, "handleFriendRankResponse: 从数据库查询到的问题列表大小---" + questionRankList.size());

            CardPagerAdapter cardPagerAdapter = new CardPagerAdapter(getContext(),questionRankList.size(),friendRankRespond,questionRankList);
            cardPagerAdapter.addItemPagerListener(new CardPagerAdapter.ItemPagerListener() {
                @Override
                public void onItemPagerClick(View v, int position) {
                    Log.i(TAG, "onItemPagerClick: 被点击了---" + position);
                }
            });
            marlViewPager.setAdapter(cardPagerAdapter);
            marlViewPager.setCurrentItem(questionRankList.size()-1);
            marlViewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    48, getResources().getDisplayMetrics()));
            marlViewPager.setPageTransformer(false, new ScaleTransformer(getContext()));

            marlViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position < questionNum){
                        marlTitle.setText("(" + (position+1) + "/" + questionNum + ")");
                    }else {
                        marlTitle.setText("上周话题明星");
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }

    }

    //问题排行榜
    private void getQuestionRankList(int qid){

        String url = YPlayConstant.BASE_URL + YPlayConstant.API_QUESTION_LIST_URL;
        final int uin = (int)SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0);

        final Map<String, Object> questionRankMap = new HashMap<>();
        questionRankMap.put("uin", uin);
        questionRankMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        questionRankMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));
        questionRankMap.put("qid", qid);

        WnsAsyncHttp.wnsRequest(url, questionRankMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {

                QuestionRankRespond questionRankRespond = GsonUtil.GsonToBean(result,QuestionRankRespond.class);
                if (questionRankRespond.getCode() == 0){

                    //查找数据库中uin和questionNum是否存在
                    RankInfo rankInfo = rankInfoDao.queryBuilder()
                            .where(RankInfoDao.Properties.Uin.eq(uin))
                            .where(RankInfoDao.Properties.QuestionNumber.eq(questionNum))
                            .build().unique();
                    if (rankInfo == null){
                        rankInfoDao.insert(new RankInfo(null,uin,questionNum,questionBean.getQtext(),result));
                    }else {
                        //更新result字段中的数据
                        rankInfo.setResult(result);
                        rankInfoDao.update(rankInfo);
                    }
                }

            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });

    }

}
