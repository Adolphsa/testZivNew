package com.yeejay.yplay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.DaoFriendFeeds;
import com.yeejay.yplay.greendao.DaoFriendFeedsDao;
import com.yeejay.yplay.utils.YplayTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

import static com.yeejay.yplay.utils.FriendFeedsUtil.boyOrGirl;
import static com.yeejay.yplay.utils.FriendFeedsUtil.schoolType;

/**
 * 动态适配器
 * Created by Administrator on 2017/10/26.
 */

public class FriendFeedsAdapter extends RecyclerView.Adapter<FriendFeedsAdapter.FeedsViewHolder> {

    private Context mContext;
    private DaoFriendFeedsDao mDaoFriendFeedsDao;
    private List<DaoFriendFeeds> daoFriendFeedsList;
    private DaoFriendFeeds daoFriendFeeds;

    public FriendFeedsAdapter(Context context,
                              List<DaoFriendFeeds> daoFriendFeedsList,
                              DaoFriendFeedsDao mDaoFriendFeedsDao) {
        this.mContext = context;
        this.daoFriendFeedsList = daoFriendFeedsList;
        this.mDaoFriendFeedsDao = mDaoFriendFeedsDao;
    }

    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_feeds, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedsViewHolder holder, int position) {

        System.out.println("position---" + position);
        daoFriendFeeds = daoFriendFeedsList.get(position);

        if (daoFriendFeeds != null && daoFriendFeeds.getIsReaded()){
            System.out.println("修改背景");
            holder.ffItemRl.setBackgroundColor(Color.parseColor("#FF4081"));
        }

        Picasso.with(mContext).load(daoFriendFeeds.getFriendHeadImgUrl()).into(holder.ffItemHeaderImg);
        holder.ffItemName.setText(daoFriendFeeds.getFriendNickName());
        holder.ffItemTvTime.setText(YplayTimeUtils.format(daoFriendFeeds.getTs()));
        holder.ffItemQuestionContent.setText(daoFriendFeeds.getQtext());
        Picasso.with(mContext).load(daoFriendFeeds.getQiconUrl()).into(holder.ffItemSmallImg);
        StringBuilder builder = new StringBuilder("来自");
        builder.append(schoolType(daoFriendFeeds.getVoteFromSchoolType(),daoFriendFeeds.getVoteFromGrade()));
        builder.append("的");
        builder.append(boyOrGirl(daoFriendFeeds.getVoteFromGender()));
        holder.ffItemTvWhere.setText(builder);
    }

    @Override
    public int getItemCount() {
        return daoFriendFeedsList == null ? 0 : daoFriendFeedsList.size();
    }

    class FeedsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ff_item_rl)
        RelativeLayout ffItemRl;
        @BindView(R.id.ff_item_header_img)
        EffectiveShapeView ffItemHeaderImg;
        @BindView(R.id.ff_item_name)
        TextView ffItemName;
        @BindView(R.id.ff_item_receive)
        TextView ffItemReceive;
        @BindView(R.id.ff_item_tv_time)
        TextView ffItemTvTime;
        @BindView(R.id.ff_item_question_content)
        TextView ffItemQuestionContent;
        @BindView(R.id.ff_item_small_img)
        ImageView ffItemSmallImg;
        @BindView(R.id.ff_item_tv_where)
        TextView ffItemTvWhere;

        private FeedsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
