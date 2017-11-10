package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.ArrayList;
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
    private List<Integer> listCb = new ArrayList<>();//用于记录位置

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

        daoFriendFeeds = daoFriendFeedsList.get(position);
//        System.out.println("position---" + position + ",是否已读---" + daoFriendFeeds.getIsReaded());

        holder.ffItemHeaderImg.setImageResource(R.drawable.header_deafult);
        String url = daoFriendFeeds.getFriendHeadImgUrl();
        holder.ffItemHeaderImg.setTag(url);

        if (!TextUtils.isEmpty(url)){
            Picasso.with(mContext).load(url).into(holder.ffItemHeaderImg);
        }else {
            holder.ffItemHeaderImg.setImageResource(R.drawable.header_deafult);
        }

        holder.ffItemName.setText(daoFriendFeeds.getFriendNickName());
        holder.ffItemTvTime.setText(YplayTimeUtils.format(daoFriendFeeds.getTs()));
        holder.ffItemQuestionContent.setText(daoFriendFeeds.getQtext());
        if (daoFriendFeeds.getVoteFromGender() == 1){//男
            holder.ffItemSmallImg.setImageResource(R.drawable.feeds_boy);
        }else{//女
            holder.ffItemSmallImg.setImageResource(R.drawable.feeds_girl);
        }
        StringBuilder builder = new StringBuilder("来自:");
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
