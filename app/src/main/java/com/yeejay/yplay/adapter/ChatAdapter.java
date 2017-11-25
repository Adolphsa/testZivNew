package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yeejay.yplay.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天适配器
 * Created by Administrator on 2017/11/25.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static enum ITEM_TYPE {
        ITEM_TYPE_LEFT,
        ITEM_TYPE_RIGHT
    }

    private Context context;


    public ChatAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_LEFT.ordinal()){
            System.out.println("左边");
            return new LeftMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_left, parent, false));

        }else {
            System.out.println("右边");
            return new RightMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_right, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ITEM_TYPE.ITEM_TYPE_LEFT.ordinal() : ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal();
    }

    public static class LeftMsgViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.msg_item_left)
        TextView msgLeft;

        public LeftMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class RightMsgViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.msg_item_right)
        TextView msgLeft;

        public RightMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
