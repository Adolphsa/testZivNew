package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.SubmitQueryListRespond;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 未上线正在审核中的投稿适配器
 * Created by xjg on 2018/01/16.
 */

public class ContributeOfflineReviewingAdapter extends
        RecyclerView.Adapter<ContributeOfflineReviewingAdapter.ContributeViewHolder> {

    public interface OnRecycleImageListener <T>{
        void OnRecycleImageClick(View v,T o);
    }

    private OnRecycleImageListener listener;
    private Context mContext;
    List<SubmitQueryListRespond.PayloadBean.ContributesBean> mDataList =  new ArrayList<>();

    public ContributeOfflineReviewingAdapter(Context context,
                                             OnRecycleImageListener listener,
                                             List<SubmitQueryListRespond.PayloadBean.ContributesBean> dataList) {
        this.mContext = context;
        this.listener = listener;
        this.mDataList = dataList;
    }

    @Override
    public ContributeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContributeViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_contribute_offline_reviewing, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributeViewHolder holder, final int position) {
        //item头部图片
        if (!TextUtils.isEmpty(mDataList.get(position).getQiconUrl())){
            Picasso.with(mContext).load(mDataList.get(position).getQiconUrl())
                    .resizeDimen(R.dimen.item_contribute_reviewing_width,
                            R.dimen.item_contribute_reviewing_height).into(holder.headerImg);
        }else {
            holder.headerImg.setImageResource(R.drawable.header_deafult);
        }

        //item中间的message
        holder.message.setText(mDataList.get(position).getQtext());

        //item尾部的图片
        holder.endImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRecycleImageClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void addRecycleImageListener(OnRecycleImageListener listener){
        this.listener = listener;
    }

    class ContributeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cora_item_header_img)
        ImageView headerImg;
        @BindView(R.id.cora_item_end_img)
        ImageView endImg;
        @BindView(R.id.cora_item_msg)
        TextView message;

        private ContributeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
