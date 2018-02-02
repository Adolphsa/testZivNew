package com.yeejay.yplay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMImageElem;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ImMsg;
import com.yeejay.yplay.model.ImageInfo;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private static final String TAG = "ChatAdapter";

    //消息的三种状态;
    private static final int MSG_STATUS_SENDING = 0;
    private static final int MSG_STATUS_SUCCESS = 1;
    private static final int MSG_STATUS_ERROR = 2;

    public enum ITEM_TYPE {
        ITEM_TYPE_LEFT,
        ITEM_TYPE_RIGHT,
        ITEM_TYPE_VOTE_CARD,
        ITEM_TYPE_CENTER,
        ITEM_TYPE_IMAGE_LEFT,
        ITEM_TYPE_IMAGE_RIGHT,
        ITEM_TYPE_TIME
    }

    private Context context;
    private List<ImMsg> mDataList;
    private OnItemClickListener mItemClickListener;
    String content;

    public interface OnItemClickListener{
        void onItemClick(View v);
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v);
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

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
            RightMsgViewHolder holder = new RightMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_message_chat_text_right, parent, false));
            holder.msgNotFriend.setOnClickListener(this);

            return holder;

        } else if (viewType == ITEM_TYPE.ITEM_TYPE_VOTE_CARD.ordinal()) {
//            System.out.println("投票卡片");
            return new VoteCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_card, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_CENTER.ordinal()){    //中间
            return new CenterMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_text_center, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_TIME.ordinal()){    //时间消息类型
            return new TimeMsgViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_center_time, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_IMAGE_LEFT.ordinal()){    //左边的图片

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_imge_left, parent, false);
            LeftImageViewHolder holder = new LeftImageViewHolder(view);
            holder.msgImageLeft.setOnClickListener(this);
            return holder;
        }else if (viewType == ITEM_TYPE.ITEM_TYPE_IMAGE_RIGHT.ordinal()){   //右边的图片

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message_chat_image_right, parent, false);
            RightImageViewHolder holder = new RightImageViewHolder(view);
            holder.msgImageRight.setOnClickListener(this);
            holder.msgImageNotFriend.setOnClickListener(this);

            return holder;
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
        int status = imMsg.getMsgSucess();

        if (immsgType == 101){//非好友状态下，发送失败的消息类型（消息左边会显示感叹号，另外此条消息依然会插入数据库）
            if (!TextUtils.isEmpty(msgContent)){
                ((RightMsgViewHolder) holder).msgRight.setText(msgContent);
                ((RightMsgViewHolder) holder).msgNotFriend.setVisibility(View.VISIBLE);
                ((RightMsgViewHolder) holder).msgNotFriend.setTag(position);
            }
        }else if (immsgType == 6) {//自定义消息（投票卡片）
            try {
                JSONObject jsonObject = new JSONObject(msgContent);
                int dataType = jsonObject.getInt("DataType");
                String data = jsonObject.getString("Data");
                MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);

                if (dataType == 2) {
                    content = msgContent2.getContent();
//                    System.out.println("chatAdapter消息---" + content);
                }

                if (holder instanceof LeftMsgViewHolder) {      //左边的聊天框
                    if (!TextUtils.isEmpty(content)) {
                        ((LeftMsgViewHolder) holder).msgLeft.setText(content);
                    }

                } else if (holder instanceof RightMsgViewHolder) {    //右边的聊天框

//                    System.out.println("chatAdapter右边---" + content);
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
        }else if (immsgType == TIMElemType.Text.ordinal()){//文本消息
            if (holder instanceof LeftMsgViewHolder) {      //左边的聊天框
                if (!TextUtils.isEmpty(msgContent)) {
                    ((LeftMsgViewHolder) holder).msgLeft.setText(msgContent);
                }

            } else if (holder instanceof RightMsgViewHolder) {    //右边的聊天框
                if (!TextUtils.isEmpty(msgContent)){
                    ((RightMsgViewHolder) holder).msgRight.setText(msgContent);
                    ((RightMsgViewHolder) holder).msgNotFriend.setTag(position);

                    //根据消息数据表中的MSG_SUCESS字段判断当前消息的发送状态;
                    switch (status) {
                        case MSG_STATUS_SENDING :
                            ((RightMsgViewHolder) holder).msgNotFriend.setVisibility(View.GONE);
                            ((RightMsgViewHolder) holder).msgRightBar.setVisibility(View.VISIBLE);
                            Glide.with(context).load(R.drawable.pic_rotate)
                                    .asGif()
                                    .into(((RightMsgViewHolder) holder).msgRightBar);
                            break;
                        case MSG_STATUS_SUCCESS :
                            ((RightMsgViewHolder) holder).msgNotFriend.setVisibility(View.GONE);
                            ((RightMsgViewHolder) holder).msgRightBar.setVisibility(View.GONE);
                            break;
                        case MSG_STATUS_ERROR :
                            ((RightMsgViewHolder) holder).msgNotFriend.setVisibility(View.VISIBLE);
                            ((RightMsgViewHolder) holder).msgRightBar.setVisibility(View.GONE);
                            break;
                        default:
                    }
                }
            }
        }else if (immsgType == 100){    //中间的提示
            ((CenterMsgViewHolder)holder).msgCenter.setText(msgContent);
        } else if (immsgType == 200){    //时间提示
            LogUtils.getInstance().debug("onBindViewHolder ITEM_TYPE_TIME");
            ((TimeMsgViewHolder)holder).msgTime.setText(msgContent);
        } else if (immsgType == TIMElemType.Image.ordinal()){//图片消息
            if (holder instanceof LeftImageViewHolder){     //左边的图片
                ((LeftImageViewHolder)holder).msgImageLeft.setTag(position);
                ImageInfo imageInfo = GsonUtil.GsonToBean(msgContent, ImageInfo.class);
                String url;
                int imageFormat = imageInfo.getImageFormat();
                int width;
                int height;
                if (imageFormat == TIMImageElem.TIM_IMAGE_FORMAT_GIF){

                    width = imageInfo.getOriginalImage().getImageWidth();
                    height = imageInfo.getOriginalImage().getImageHeight();
                    url = imageInfo.getOriginalImage().getImageUrl();
                }else {
                    width = imageInfo.getThumbImage().getImageWidth();
                    height = imageInfo.getThumbImage().getImageHeight();
                    url = imageInfo.getThumbImage().getImageUrl();
                }
                if (!TextUtils.isEmpty(url)){
                    ViewGroup.LayoutParams lp = ((LeftImageViewHolder)holder).msgImageLeft.getLayoutParams();
                    ViewGroup.LayoutParams lpUp = ((LeftImageViewHolder)holder).msgImageLeftUp.getLayoutParams();
                    if (width > height){
                        lp.width = DensityUtil.dp2px(context,200);
                        lp.height = DensityUtil.dp2px(context,200*height/width);
                        ((LeftImageViewHolder)holder).msgImageLeft.setLayoutParams(lp);

                        lpUp.width = DensityUtil.dp2px(context,200);
                        lpUp.height = DensityUtil.dp2px(context,200*height/width);
                        ((LeftImageViewHolder)holder).msgImageLeftUp.setLayoutParams(lpUp);

                    }else {
                        lp.width = DensityUtil.dp2px(context,200*width/height);
                        lp.height = DensityUtil.dp2px(context,200);
                        ((LeftImageViewHolder)holder).msgImageLeft.setLayoutParams(lp);

                        lpUp.width = DensityUtil.dp2px(context,200*width/height);
                        lpUp.height = DensityUtil.dp2px(context,200);
                        ((LeftImageViewHolder)holder).msgImageLeftUp.setLayoutParams(lpUp);
                    }

                    if (imageFormat == TIMImageElem.TIM_IMAGE_FORMAT_GIF){
                        Glide.with(context).load(url)
                                .asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(((LeftImageViewHolder)holder).msgImageLeft);
//                        Log.i(TAG, "onBindViewHolder: left gif---" + url);
                    }else {
                        Picasso.with(context).load(url)
                                .resize(lp.width,lp.height)
                                .config(Bitmap.Config.RGB_565)
                                .centerCrop()
                                .into(((LeftImageViewHolder)holder).msgImageLeft);
                    }

                }

            }else if (holder instanceof RightImageViewHolder){      //右边的图片
                //根据消息数据表中的MSG_SUCESS字段判断当前消息的发送状态;
                switch (status) {
                    case MSG_STATUS_SENDING :
                        ((RightImageViewHolder) holder).msgImageNotFriend.setVisibility(View.GONE);
                        ((RightImageViewHolder) holder).msgRightBar.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.pic_rotate)
                                .asGif()
                                .into(((RightImageViewHolder) holder).msgRightBar);
                        break;
                    case MSG_STATUS_SUCCESS :
                        ((RightImageViewHolder) holder).msgImageNotFriend.setVisibility(View.GONE);
                        ((RightImageViewHolder) holder).msgRightBar.setVisibility(View.GONE);
                        break;
                    case MSG_STATUS_ERROR :
                        ((RightImageViewHolder) holder).msgImageNotFriend.setVisibility(View.VISIBLE);
                        ((RightImageViewHolder) holder).msgRightBar.setVisibility(View.GONE);
                        break;
                    default:
                }

                ((RightImageViewHolder)holder).msgImageNotFriend.setTag(position);
                ((RightImageViewHolder)holder).msgImageRight.setTag(position);
                ImageInfo imageInfo = GsonUtil.GsonToBean(msgContent, ImageInfo.class);
                if (imageInfo == null || imageInfo.getThumbImage() == null) return;
                String url;
                int imageFormat = imageInfo.getImageFormat();
                int width;
                int height;
                if (imageFormat == TIMImageElem.TIM_IMAGE_FORMAT_GIF){
                    width = imageInfo.getOriginalImage().getImageWidth();
                    height = imageInfo.getOriginalImage().getImageHeight();
                    url = imageInfo.getOriginalImage().getImageUrl();
                }else {
                    width = imageInfo.getThumbImage().getImageWidth();
                    height = imageInfo.getThumbImage().getImageHeight();
                    url = imageInfo.getThumbImage().getImageUrl();
                }

                if(!TextUtils.isEmpty(url)){

                    ViewGroup.LayoutParams lp = ((RightImageViewHolder)holder).msgImageRight.getLayoutParams();
                    ViewGroup.LayoutParams lpUp = ((RightImageViewHolder)holder).msgImageRightUp.getLayoutParams();

                    if (width > height){
                        lp.width = DensityUtil.dp2px(context,200);
                        lp.height = DensityUtil.dp2px(context,200*height/width);
                        ((RightImageViewHolder)holder).msgImageRight.setLayoutParams(lp);

                        lpUp.width = DensityUtil.dp2px(context,200);
                        lpUp.height = DensityUtil.dp2px(context,200*height/width);
                        ((RightImageViewHolder)holder).msgImageRightUp.setLayoutParams(lpUp);
                    } else{
                        lp.width = DensityUtil.dp2px(context,200*width/height);
                        lp.height = DensityUtil.dp2px(context,200);
                        ((RightImageViewHolder)holder).msgImageRight.setLayoutParams(lp);

                        lpUp.width = DensityUtil.dp2px(context,200*width/height);
                        lpUp.height = DensityUtil.dp2px(context,200);
                        ((RightImageViewHolder)holder).msgImageRightUp.setLayoutParams(lpUp);
                    }

                    if (imageFormat == TIMImageElem.TIM_IMAGE_FORMAT_GIF){
                        Glide.with(context).load(url)
                                .asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(((RightImageViewHolder)holder).msgImageRight);
//                        Log.i(TAG, "onBindViewHolder: right gif---" + url);
                    }else {


                        Log.i(TAG, "onBindViewHolder: imagePath---" + url);
                        Picasso.with(context).load("file://" + url)
                                .resize(lp.width,lp.height)
                                .config(Bitmap.Config.RGB_565)
                                .centerCrop()
                                .into(((RightImageViewHolder)holder).msgImageRight);
                    }
                }

            }
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
        }else if (immsgType == 101){
            return ITEM_TYPE.ITEM_TYPE_RIGHT.ordinal();
        }else if (immsgType == 100){
            return ITEM_TYPE.ITEM_TYPE_CENTER.ordinal();
        } else if (immsgType == 2){      //图片消息
            if (!sender.equals(String.valueOf(uin))) { //发送者不是自己
                return ITEM_TYPE.ITEM_TYPE_IMAGE_LEFT.ordinal();
            } else if (sender.equals(String.valueOf(uin))) {  //发送者是自己
                return ITEM_TYPE.ITEM_TYPE_IMAGE_RIGHT.ordinal();
            }
        } else if (immsgType == 200){//时间消息类型
            return ITEM_TYPE.ITEM_TYPE_TIME.ordinal();
        }

        return -1;
    }

    private void initVoteCard(String msgContent, VoteCardViewHolder holder) {

        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");
//            Log.i(TAG, "initVoteCard: DataType---" + dataType + "，Data---" + data);

            MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
//            System.out.println("投票卡片---" + msgContent2.toString());
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
        @BindView(R.id.msg_item_right_not_friend)
        ImageView msgNotFriend;
        @BindView(R.id.msg_item_progressbar)
        ImageView msgRightBar;

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

    public static class TimeMsgViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_center_time)
        TextView msgTime;

        public TimeMsgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class LeftImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_image_left_up)
        ImageView msgImageLeftUp;
        @BindView(R.id.msg_item_image_left)
        ImageView msgImageLeft;

        public LeftImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RightImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_progressbar)
        ImageView msgRightBar;
        @BindView(R.id.msg_item_image_right_up)
        ImageView msgImageRightUp;
        @BindView(R.id.msg_item_image_right)
        ImageView msgImageRight;
        @BindView(R.id.msg_item_image_right_not_friend)
        ImageView msgImageNotFriend;

        public RightImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
