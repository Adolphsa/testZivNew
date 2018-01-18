package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.SubmitQueryListRespond;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 未上线正在审核中的投稿适配器
 * Created by xjg on 2018/01/16.
 */

public class ContributeOfflineRefusedAdapter extends
        RecyclerView.Adapter<ContributeOfflineRefusedAdapter.ContributeViewHolder> {

    private ContributeOfflineReviewingAdapter.OnRecycleImageListener listener;
    private Context mContext;
    List<SubmitQueryListRespond.PayloadBean.ContributesBean> mDataList =  new ArrayList<>();

    public ContributeOfflineRefusedAdapter(Context context,
                                           ContributeOfflineReviewingAdapter.OnRecycleImageListener listener,
                                             List<SubmitQueryListRespond.PayloadBean.ContributesBean> dataList) {
        this.mContext = context;
        this.listener = listener;
        this.mDataList = dataList;
    }

    @Override
    public ContributeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContributeViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_contribute_offline_refused, parent, false));
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

        //warning图片

        //warning message;
        holder.warningMsg.setText(mDataList.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void addRecycleImageListener(ContributeOfflineReviewingAdapter.OnRecycleImageListener listener){
        this.listener = listener;
    }

    class ContributeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contri_refused_item_header_img)
        ImageView headerImg;
        @BindView(R.id.contri_refused_item_end_img)
        ImageView endImg;
        @BindView(R.id.contri_refused_item_msg)
        TextView message;
        @BindView(R.id.contri_refused_item_waring_img)
        ImageView warningImag;
        @BindView(R.id.contri_refused_item_warning_msg)
        TextView warningMsg;
        //隐藏项目
        @BindView(R.id.ll_refused_more)
        LinearLayout llRefusedMore;
        @BindView(R.id.refused_delete)
        TextView detele;
        @BindView(R.id.refused_edit)
        TextView edit;

        private ContributeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}