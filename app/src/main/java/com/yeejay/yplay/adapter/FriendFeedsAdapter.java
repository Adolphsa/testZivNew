package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 动态适配器
 * Created by Administrator on 2017/10/26.
 */

public class FriendFeedsAdapter extends RecyclerView.Adapter<FriendFeedsAdapter.FeedsViewHolder> {

    Context mContext;

    public FriendFeedsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_feeds, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedsViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class FeedsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ff_item_header_img)
        ImageView ffItemHeaderImg;
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

        public FeedsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
