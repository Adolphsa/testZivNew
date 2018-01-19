package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class ContributeOnlineAdapter extends
        RecyclerView.Adapter<ContributeOnlineAdapter.ContributeViewHolder> {

    public interface OnRecycleImageListener <T>{
        void OnRecycleImageClick(View v,T o);
    }

    private OnRecycleImageListener listener;
    private Context mContext;
    List<SubmitQueryListRespond.PayloadBean.ContributesBean> mDataList =  new ArrayList<>();

    public ContributeOnlineAdapter(Context context,
                                    OnRecycleImageListener listener,
                                    List<SubmitQueryListRespond.PayloadBean.ContributesBean> dataList) {
        this.mContext = context;
        this.listener = listener;
        this.mDataList = dataList;
    }

    @Override
    public ContributeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContributeViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_contribute_online2, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributeViewHolder holder, final int position) {
        //如果是最新的投稿，则背景高亮；
        if(mDataList.get(position).getFlag() == 1) {
            holder.ll.setBackgroundResource(R.drawable.item_contribute_list_light_background);
        }

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

        //热度值
        holder.hotValue.setText(String.valueOf(mDataList.get(position).getVotedCnt()));

        //扩展的箭头图片焦点区域太小不好点击，放大到整个RelaytiveLayout;
        //热度值不为0才能点击展开;
        if(mDataList.get(position).getVotedCnt() > 0) {
            holder.rlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //如果是高亮的新投稿项,则点击后去掉高亮背景;
                    View parentView = (View) v.getParent();
                    if (mDataList.get(position).getFlag() == 1) {
                        if (parentView != null) {
                            parentView.setBackgroundResource(R.drawable.item_contribute_list_big_background);
                        }
                    }

                    listener.OnRecycleImageClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void addRecycleImageListener(OnRecycleImageListener listener){
        this.listener = listener;
    }

    class ContributeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll)
        LinearLayout ll;
        @BindView(R.id.cora_item_rl)
        RelativeLayout rlLayout;
        @BindView(R.id.cora_item_header_img)
        ImageView headerImg;
        @BindView(R.id.cora_item_end_img)
        ImageView endImg;
        @BindView(R.id.cora_item_msg)
        TextView message;
        @BindView(R.id.contri_heat_value)
        TextView hotValue;
        @BindView(R.id.arrow)
        ImageView arrowImg;
        //扩展项;
        @BindView(R.id.expand_ll)
        LinearLayout expandLl;
        @BindView(R.id.expand_note)
        TextView expandNote;
        @BindView(R.id.expand_list)
        ListView expandList;

        private ContributeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}