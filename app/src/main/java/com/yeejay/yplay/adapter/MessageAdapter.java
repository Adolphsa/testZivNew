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
 * 消息适配器
 * Created by Administrator on 2017/10/27.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageHolder> {

    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public messageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new messageHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(messageHolder holder, int position) {
        if (position % 2 == 0){
            holder.msgItemMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    class messageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_header_img)
        ImageView msgItemHeaderImg;
        @BindView(R.id.msg_item_name)
        TextView msgItemName;
        @BindView(R.id.msg_item_tv_time)
        TextView msgItemTvTime;
        @BindView(R.id.msg_item_tv_message)
        TextView msgItemMessage;

        public messageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
