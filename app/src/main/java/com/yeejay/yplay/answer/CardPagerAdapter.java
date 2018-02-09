package com.yeejay.yplay.answer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.customview.DisallowChildClickFrameLayout;
import com.yeejay.yplay.customview.NotClickListView;
import com.yeejay.yplay.greendao.RankInfo;
import com.yeejay.yplay.model.FriendRankRespond;
import com.yeejay.yplay.model.QuestionRankRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.List;

import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 排行榜卡片适配器
 * Created by Adolph on 2018/2/5.
 */

public class CardPagerAdapter extends PagerAdapter{

    Context context;

    AnimatorSet mRightOutSet;
    AnimatorSet mLeftInSet;
    boolean mIsShowBack = false;

    int cardCount;
    FriendRankRespond friendRankRespond;
    List<RankInfo> questionRankList;


    interface ItemPagerListener{
        void onItemPagerClick(View v,int position);
    }

    private ItemPagerListener itemPagerListener;


    public CardPagerAdapter(Context context, int cardCount, FriendRankRespond friendRankRespond, List<RankInfo> questionRankList){
        this.context = context;
        this.cardCount = cardCount;
        this.friendRankRespond = friendRankRespond;
        this.questionRankList = questionRankList;
    }

    @Override
    public int getCount() {
        return cardCount+1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        DisallowChildClickFrameLayout view = null;
        if (position == getCount() -1){
            view = (DisallowChildClickFrameLayout)LayoutInflater.from(container.getContext())
                    .inflate(R.layout.layout_friend_list_frame, container, false);
            final CardView friendListFront = (CardView) view.findViewById(R.id.friend_list_front);
            final CardView friendListBack = (CardView) view.findViewById(R.id.friend_list_back);

            //设置好友排行数据
            initFriendCard(friendListFront,friendListBack);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemPagerListener.onItemPagerClick(v,position);
                    setAnimators((FrameLayout) v);
                    setCameraDistance(friendListFront,friendListBack);
                    flipCard(friendListFront,friendListBack);
                }
            });

        }else {

            view = (DisallowChildClickFrameLayout)LayoutInflater.from(container.getContext())
                    .inflate(R.layout.layout_question_list_frame, container, false);
            final CardView questionListFront = (CardView) view.findViewById(R.id.question_list_front);
            final CardView questionListBack = (CardView) view.findViewById(R.id.question_list_back);

            //设置题目排行榜数据
            initQuestionCard(questionListFront,questionListBack, position);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemPagerListener.onItemPagerClick(v,position);
                    setAnimators((FrameLayout) v);
                    setCameraDistance(questionListFront,questionListBack);
                    flipCard(questionListFront,questionListBack);
                }
            });

        }


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void addItemPagerListener(ItemPagerListener itemPagerListener){
        this.itemPagerListener = itemPagerListener;
    }


    // 设置动画
    private void setAnimators(final FrameLayout mFlContainer) {
        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_right_out);
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_left_in);

        // 设置点击事件
        mRightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mFlContainer.setClickable(false);
            }
        });
        mLeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFlContainer.setClickable(true);
            }
        });
    }

    // 改变视角距离, 贴近屏幕
    private void setCameraDistance(CardView friendListFront, CardView friendListBack) {
        int distance = 16000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        friendListFront.setCameraDistance(scale);
        friendListBack.setCameraDistance(scale);
    }

    // 翻转卡片
    public void flipCard(CardView friendListFront, CardView friendListBack) {
        // 正面朝上
        if (!mIsShowBack) {
            mRightOutSet.setTarget(friendListFront);
            mLeftInSet.setTarget(friendListBack);
            mRightOutSet.start();
            mLeftInSet.start();
            mIsShowBack = true;
        } else { // 背面朝上
            mRightOutSet.setTarget(friendListBack);
            mLeftInSet.setTarget(friendListFront);
            mRightOutSet.start();
            mLeftInSet.start();
            mIsShowBack = false;
        }
    }

    //设置好友排行榜数据
    private void initFriendCard(CardView friendListFront, CardView friendListBack){

        //正面
        EffectiveShapeView friendFrontHeaderImg = (EffectiveShapeView) friendListFront.findViewById(R.id.lrfl_header_img);
        TextView friendFrontWeekCount = (TextView) friendListFront.findViewById(R.id.lrfl_week_count);
        TextView friendFrontName = (TextView) friendListFront.findViewById(R.id.lrfl_name);

        String url = friendRankRespond.getPayload().getFriendHeadImgUrl();
        int friendCnt = friendRankRespond.getPayload().getFriendCnt();
        String friendName = friendRankRespond.getPayload().getFriendNickName();

        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resize(60,60).into(friendFrontHeaderImg);
        }else {
            friendFrontHeaderImg.setImageResource(R.drawable.header_deafult);
        }
        friendFrontWeekCount.setText("蝉联" + friendCnt + "周");
        friendFrontName.setText(friendName);

        //反面
        TextView backSchoolName = (TextView) friendListBack.findViewById(R.id.lcsl_text1);
        TextView backGrade = (TextView) friendListBack.findViewById(R.id.lcsl_grade);
        EffectiveShapeView backHeaderImg = (EffectiveShapeView) friendListBack.findViewById(R.id.lcsl_header_img);
        TextView backWeekCount = (TextView) friendListBack.findViewById(R.id.lcsl_week_count);
        TextView backName = (TextView) friendListBack.findViewById(R.id.lcsl_name);

        String bSchoolName = friendRankRespond.getPayload().getSchoolName();
        String grade = FriendFeedsUtil.schoolType(friendRankRespond.getPayload().getSchoolType(),friendRankRespond.getPayload().getGrade());
        String bHeaderImg = friendRankRespond.getPayload().getHeadImgUrl();
        int bCnt = friendRankRespond.getPayload().getCnt();
        String bName = friendRankRespond.getPayload().getNickName();

        backSchoolName.setText(bSchoolName);
        backGrade.setText(grade);
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(bHeaderImg).resize(60,60).into(backHeaderImg);
        }else {
            backHeaderImg.setImageResource(R.drawable.header_deafult);
        }
        backWeekCount.setText("蝉联" + bCnt + "周");
        backName.setText(bName);
    }

    //设置问题排行榜数据
    private void initQuestionCard(CardView questionListFront, CardView questionListBack,int position){

        RankInfo rankInfo = questionRankList.get(position);
        String myselfImgUrl = (String) SharePreferenceUtil.get(context, YPlayConstant.YPLAY_MYSELF_IMG_URL,"");

        String result = rankInfo.getResult();
        String questionContent = rankInfo.getQuestionText();


        //正面
        EffectiveShapeView frontHeaderImgView = (EffectiveShapeView) questionListFront.findViewById(R.id.lccqfl_header_img);
        TextView frontOverNumberView = (TextView) questionListFront.findViewById(R.id.lccqfl_over_number);
        TextView frontQuestionTextView = (TextView) questionListFront.findViewById(R.id.lccqfl_question_content);
        NotClickListView frontListView = (NotClickListView) questionListFront.findViewById(R.id.lccqfl_list);
        TextView frontDefaultView = (TextView) questionListFront.findViewById(R.id.lccqfl_default_text);

        //背面
        EffectiveShapeView backHeaderImgView = (EffectiveShapeView) questionListBack.findViewById(R.id.lccqgl_header_img);
        TextView backOverNumberView = (TextView) questionListBack.findViewById(R.id.lccqgl_over_number);
        TextView backQuestionTextView = (TextView) questionListBack.findViewById(R.id.lccqgl_question_content);
        ListView backListView = (ListView) questionListBack.findViewById(R.id.lccqgl_list);
        TextView backDefaultView = (TextView) questionListBack.findViewById(R.id.lccqgl_default_text);


        //正面数据设置
        if (TextUtils.isEmpty(myselfImgUrl)){
            frontHeaderImgView.setImageResource(R.drawable.header_deafult);
        }else {
            Picasso.with(context).load(myselfImgUrl).resize(50,50).into(frontHeaderImgView);
        }

        frontQuestionTextView.setText(questionContent);


        //背面数据设置
        if (TextUtils.isEmpty(myselfImgUrl)){
            backHeaderImgView.setImageResource(R.drawable.header_deafult);
        }else {
            Picasso.with(context).load(myselfImgUrl).resize(50,50).into(backHeaderImgView);
        }

        backQuestionTextView.setText(questionContent);

        //排行榜数据  为空则return
        QuestionRankRespond questionRankRespond = GsonUtil.GsonToBean(result,QuestionRankRespond.class);
        if (questionRankRespond == null) return;

        QuestionRankRespond.PayloadBean payloadBean = questionRankRespond.getPayload();

        //正面相关
        String frontOverNumber = payloadBean.getRankingPercentInFriends();
        final List<QuestionRankRespond.PayloadBean.RankingInFriendsBean> rankingInFriends = payloadBean.getRankingInFriends();

        frontOverNumberView.setText("你已经超过了" + frontOverNumber + "的好友");

        if (rankingInFriends != null && rankingInFriends.size() > 0){
            frontDefaultView.setVisibility(View.GONE);
            frontListView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return rankingInFriends.size();
                }

                @Override
                public Object getItem(int position) {
                    return rankingInFriends.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    convertView = View.inflate(context, R.layout.item_rank_list, null);

                    ImageView indexView = (ImageView) convertView.findViewById(R.id.irl_item_index);
                    EffectiveShapeView itemRankHeaderImgView = (EffectiveShapeView) convertView.findViewById(R.id.irl_header_img);
                    TextView itemRankName = (TextView) convertView.findViewById(R.id.irl_item_text);

                    String imgUrl = rankingInFriends.get(position).getHeadImgUrl();
                    String nickName = rankingInFriends.get(position).getNickName();

                    if (position == 0){
                        indexView.setImageResource(R.drawable.rank_diamond1);
                    }else if (position == 1){
                        indexView.setImageResource(R.drawable.rank_diamond2);
                    }else if (position == 2){
                        indexView.setImageResource(R.drawable.rank_diamond3);
                    }

                    if (TextUtils.isEmpty(imgUrl)){
                        itemRankHeaderImgView.setImageResource(R.drawable.header_deafult);
                    }else {
                        Picasso.with(context).load(imgUrl).resize(30,30).into(itemRankHeaderImgView);
                    }

                    itemRankName.setText(nickName);
                    return convertView;
                }

                @Override
                public boolean areAllItemsEnabled() {
                    return true;
                }
            });
        }else {
            frontDefaultView.setVisibility(View.VISIBLE);
        }

        //背面相关
        String backOverNumber = payloadBean.getRankingPercentInSameSchool();
        final List<QuestionRankRespond.PayloadBean.RankingInSameSchoolBean> rankingInSameSchool = payloadBean.getRankingInSameSchool();

        backOverNumberView.setText("你已经超过了" + backOverNumber + "的好友");

        if (rankingInSameSchool != null && rankingInSameSchool.size() > 0){
            backDefaultView.setVisibility(View.GONE);
            backListView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return rankingInSameSchool.size();
                }

                @Override
                public Object getItem(int position) {
                    return rankingInSameSchool.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    convertView = View.inflate(context, R.layout.item_rank_list, null);

                    ImageView indexView = (ImageView) convertView.findViewById(R.id.irl_item_index);
                    EffectiveShapeView itemRankHeaderImgView = (EffectiveShapeView) convertView.findViewById(R.id.irl_header_img);
                    TextView itemRankName = (TextView) convertView.findViewById(R.id.irl_item_text);

                    String imgUrl = rankingInSameSchool.get(position).getHeadImgUrl();
                    String nickName = rankingInSameSchool.get(position).getNickName();

                    if (position == 0){
                        indexView.setImageResource(R.drawable.rank_diamond1);
                    }else if (position == 1){
                        indexView.setImageResource(R.drawable.rank_diamond2);
                    }else if (position == 2){
                        indexView.setImageResource(R.drawable.rank_diamond3);
                    }

                    if (TextUtils.isEmpty(imgUrl)){
                        itemRankHeaderImgView.setImageResource(R.drawable.header_deafult);
                    }else {
                        Picasso.with(context).load(imgUrl).resize(30,30).into(itemRankHeaderImgView);
                    }

                    itemRankName.setText(nickName);
                    itemRankName.setTextColor(context.getResources().getColor(R.color.black));
                    return convertView;
                }

                @Override
                public boolean areAllItemsEnabled() {
                    return true;
                }
            });


        }else {
            backDefaultView.setVisibility(View.VISIBLE);
        }


    }

}
