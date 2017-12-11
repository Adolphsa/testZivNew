package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMElemType;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天适配器
 * Created by Administrator on 2017/11/25.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum ITEM_TYPE {
        ITEM_TYPE_LEFT,
        ITEM_TYPE_RIGHT,
        ITEM_TYPE_VOTE_CARD,
        ITEM_TYPE_CENTER
    }

    private Context context;
    private List<ImMsg> mDataList;
    String content;

    public ChatAdapter(Context context, List<ImMsg> dataList) {
        this.context = context;
        this.mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_LEFT.ordinal()) {
//            System.out.println("左边");
            return new LeftMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_left, parent, false));

        } else if (viewType == ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal()) {
//            System.out.println("右边");
            return new RightMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_right, parent, false));

        } else if (viewType == ITEM_TYPE.ITEM_TYPE_VOTE_CARD.ordinal()) {
//            System.out.println("投票卡片");
            return new VoteCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_card, parent, false));
        }else if (viewType == ITEM_TYPE.ITEM_TYPE_CENTER.ordinal()){    //中间
            return new CenterMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_center, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int length = mDataList.size();
        ImMsg imMsg = mDataList.get(length - 1 - position);
        int immsgType = imMsg.getMsgType();
        String msgContent = imMsg.getMsgContent();
        String sender = imMsg.getSender();
        if (immsgType == 6) {
            try {
                JSONObject jsonObject = new JSONObject(msgContent);
                int dataType = jsonObject.getInt("DataType");
                String data = jsonObject.getString("Data");
                MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);

                if (dataType == 2) {
                    content = msgContent2.getContent();
                    System.out.println("chatAdapter消息---" + content);
                }

                if (holder instanceof LeftMsgViewHolder) {      //左边的聊天框
                    if (!TextUtils.isEmpty(content)) {
                        ((LeftMsgViewHolder) holder).msgLeft.setText(content);
                    }

                } else if (holder instanceof RightMsgViewHolder) {    //右边的聊天框

                    System.out.println("chatAdapter右边---" + content);
                    ((RightMsgViewHolder) holder).msgRight.setText(content);


                } else if (holder instanceof VoteCardViewHolder) {        //投票卡片
                    int uin = (int) SharePreferenceUtil.get(context, YPlayConstant.YPLAY_UIN, (int) 0);
                    if (sender.equals(String.valueOf(uin))){     //发送者是自己

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.RIGHT;
                        lp.setMarginEnd(21);
                        ((VoteCardViewHolder) holder).msgRootRl.setLayoutParams(lp);
                        ((VoteCardViewHolder) holder).msgRootRl.setBackground(context.getDrawable(R.drawable.right_chat));

                    }else {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.LEFT;
                        lp.setMarginStart(21);
                        ((VoteCardViewHolder) holder).msgRootRl.setLayoutParams(lp);
                        ((VoteCardViewHolder) holder).msgRootRl.setBackground(context.getDrawable(R.drawable.right_chat_left));
                    }

                    initVoteCard(msgContent, (VoteCardViewHolder) holder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (immsgType == TIMElemType.Text.ordinal()){
            if (holder instanceof LeftMsgViewHolder) {      //左边的聊天框
                if (!TextUtils.isEmpty(msgContent)) {
                    ((LeftMsgViewHolder) holder).msgLeft.setText(msgContent);
                }

            } else if (holder instanceof RightMsgViewHolder) {    //右边的聊天框
                if (!TextUtils.isEmpty(msgContent)){
                    ((RightMsgViewHolder) holder).msgRight.setText(msgContent);
                }
            }
        }else if (immsgType == 100){    //中间的提示
            ((CenterMsgViewHolder)holder).msgCenter.setText(msgContent);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int uin = (int) SharePreferenceUtil.get(context, YPlayConstant.YPLAY_UIN, (int) 0);

        int length = mDataList.size();
        ImMsg imMsg = mDataList.get(length-1-position);
        int immsgType = imMsg.getMsgType();
        String sender = imMsg.getSender();
        String msgContent = imMsg.getMsgContent();

        if (immsgType == 6){
            try {
                JSONObject jsonObject = new JSONObject(msgContent);
                int dataType = jsonObject.getInt("DataType");
                if (dataType == 1) {
                    return ITEM_TYPE.ITEM_TYPE_VOTE_CARD.ordinal();
                } else if (dataType == 2) {
                    if (!sender.equals(String.valueOf(uin))) { //发送者不是自己
                        return ITEM_TYPE.ITEM_TYPE_LEFT.ordinal();
                    } else if (sender.equals(String.valueOf(uin))) {  //发送者是自己
                        return ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (immsgType == TIMElemType.Text.ordinal()){
            if (!sender.equals(String.valueOf(uin))) { //发送者不是自己
                return ITEM_TYPE.ITEM_TYPE_LEFT.ordinal();
            } else if (sender.equals(String.valueOf(uin))) {  //发送者是自己
                return ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal();
            }
        }else if (immsgType == 100){    //中间的提示
            return ITEM_TYPE.ITEM_TYPE_CENTER.ordinal();
        }

        return -1;
    }

    private void initVoteCard(String msgContent, VoteCardViewHolder holder) {

        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");
            System.out.println("DataType---" + dataType + "，Data---" + data);

            MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
            System.out.println("投票卡片---" + msgContent2.toString());
            int selectIndex = msgContent2.getSelIndex();
            MsgContent2.QuestionInfoBean questionInfoBean = msgContent2.getQuestionInfo();
            List<MsgContent2.OptionsBean> optionsBeanList = msgContent2.getOptions();

            String headUrl = questionInfoBean.getQiconUrl();
            String questionText = questionInfoBean.getQtext();

            if (!TextUtils.isEmpty(headUrl)) {
                Picasso.with(context).load(headUrl).into(holder.msgHead);
                holder.msgText.setText(questionText);
            }

            if (optionsBeanList != null && optionsBeanList.size() == 4) {

                String nickName1 = optionsBeanList.get(0).getNickName();
                String nickName2 = optionsBeanList.get(1).getNickName();
                String nickName3 = optionsBeanList.get(2).getNickName();
                String nickName4 = optionsBeanList.get(3).getNickName();

                initButton(selectIndex,
                        holder,
                        nickName1, nickName2, nickName3, nickName4);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //button ui
    private void initButton(int selectIndex,
                            VoteCardViewHolder holder,
                            String nickName1, String nickName2, String nickName3, String nickName4) {

        holder.msgBtn1.setText(nickName1);
        holder.msgBtn2.setText(nickName2);
        holder.msgBtn3.setText(nickName3);
        holder.msgBtn4.setText(nickName4);

        if (selectIndex == 1) {
            holder.msgBtn1.setBackground(context.getDrawable(R.drawable.nonymity_reply_select));

        } else if (selectIndex == 2) {

            holder.msgBtn2.setBackground(context.getDrawable(R.drawable.nonymity_reply_select));

        } else if (selectIndex == 3) {
            holder.msgBtn3.setBackground(context.getDrawable(R.drawable.nonymity_reply_select));

        } else if (selectIndex == 4) {
            holder.msgBtn4.setBackground(context.getDrawable(R.drawable.nonymity_reply_select));
        }

    }

    public static class LeftMsgViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_left)
        TextView msgLeft;

        public LeftMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RightMsgViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_right)
        TextView msgRight;

        public RightMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class VoteCardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lmc_ques_head_img)
        ImageView msgHead;
        @BindView(R.id.lmc_ques_text)
        TextView msgText;
        @BindView(R.id.lmc_button1)
        Button msgBtn1;
        @BindView(R.id.lmc_button2)
        Button msgBtn2;
        @BindView(R.id.lmc_button3)
        Button msgBtn3;
        @BindView(R.id.lmc_button4)
        Button msgBtn4;
        @BindView(R.id.lmc_root_rl)
        RelativeLayout msgRootRl;
        @BindView(R.id.lmc_root_ll)
        LinearLayout msgRootLl;

        public VoteCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class CenterMsgViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_center)
        TextView msgCenter;

        public CenterMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
