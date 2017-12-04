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
import com.tencent.imsdk.TIMElemType;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.model.MsgContent1;
import com.yeejay.yplay.model.MsgContent2;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.utils.YplayTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 消息适配器
 * Created by Administrator on 2017/10/27.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageHolder> {

    Context context;
    List<ImSession> imSessionList;

    public MessageAdapter(Context context, List<ImSession> imSessionList) {
        this.context = context;
        this.imSessionList = imSessionList;
    }

    @Override
    public messageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new messageHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(messageHolder holder, int position) {
        ImSession imSession = imSessionList.get(position);
        String msgContent = imSession.getMsgContent();
        int status = imSession.getStatus();
        int unreadMsgNum = imSession.getUnreadMsgNum();
        if (unreadMsgNum > 0){
            holder.msgItemNewMsg.setVisibility(View.VISIBLE);
        }else {
            holder.msgItemNewMsg.setVisibility(View.GONE);
        }
        if (0 == status) {   //投票
            setStatusIs0View(holder, msgContent, imSession);
        } else if (status == 1) {
            String lastSender = imSession.getLastSender();
            int uin = (int) SharePreferenceUtil.get(context, YPlayConstant.YPLAY_UIN, (int) 0);
            // if lastSend == self
            if (lastSender.equals(String.valueOf(uin))) {
                initItem2(holder, msgContent, imSession);
            } else {
                initItem3(holder, msgContent, imSession);
            }

        } else if (status == 2) {
            initItem4(holder, msgContent, imSession);
        }
    }

    @Override
    public int getItemCount() {
        return imSessionList.size();
    }

    class messageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_header_img)
        EffectiveShapeView msgItemHeaderImg;
        @BindView(R.id.msg_item_name)
        TextView msgItemName;
        @BindView(R.id.msg_item_cuo)
        TextView msgItemCuo;
        @BindView(R.id.msg_item_tv_time)
        TextView msgItemTvTime;
        @BindView(R.id.msg_item_send_status_img)
        ImageView msgItemStatusImg;
        @BindView(R.id.msg_item_content)
        TextView msgItemContent;
        @BindView(R.id.msg_item_new_msg)
        View msgItemNewMsg;

        public messageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //status == o, dataType == 1
    private void setStatusIs0View(messageHolder holder, String msgContent, ImSession imSession) {

        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");
            if (1 == dataType) {

                MsgContent1 msgContent1 = GsonUtil.GsonToBean(data, MsgContent1.class);
                MsgContent1.SenderInfoBean senderInfoBean = msgContent1.getSenderInfo();
                int gender = senderInfoBean.getGender();
                String genderStr = gender == 1 ? "男生" : "女生";
                int grade = senderInfoBean.getGrade();
                int schoolType = senderInfoBean.getSchoolType();
                String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType, grade);

                if (gender == 1) {
                    holder.msgItemHeaderImg.setImageResource(R.drawable.feeds_boy);
                    holder.msgItemName.setTextColor(context.getResources().getColor(R.color.boy_color));
                } else if (gender == 2) {
                    holder.msgItemHeaderImg.setImageResource(R.drawable.feeds_girl);
                    holder.msgItemName.setTextColor(context.getResources().getColor(R.color.girl_color));
                }

                holder.msgItemName.setText(gradeAndSchool + genderStr);
                holder.msgItemCuo.setVisibility(View.VISIBLE);
                holder.msgItemContent.setText("来自：投票");
                holder.msgItemTvTime.setText(YplayTimeUtils.format(imSession.getMsgTs()*1000));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //status = 1 data_type = 2 sender = self
    private void initItem2(messageHolder holder, String msgContent, ImSession imSession) {
        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");
            if (2 == dataType) {
                MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                MsgContent2.ReceiverInfoBean receiverInfoBean = msgContent2.getReceiverInfo();
                int gender = receiverInfoBean.getGender();
                String genderStr = gender == 1 ? "男生" : "女生";
                int grade = receiverInfoBean.getGrade();
                int schoolType = receiverInfoBean.getSchoolType();
                String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType, grade);

                if (gender == 1) {
                    holder.msgItemHeaderImg.setImageResource(R.drawable.feeds_boy);
                    holder.msgItemName.setTextColor(context.getResources().getColor(R.color.boy_color));
                } else if (gender == 2) {
                    holder.msgItemHeaderImg.setImageResource(R.drawable.feeds_girl);
                    holder.msgItemName.setTextColor(context.getResources().getColor(R.color.girl_color));
                }

                holder.msgItemName.setText(gradeAndSchool + genderStr);
                holder.msgItemCuo.setVisibility(View.VISIBLE);
                holder.msgItemContent.setText("来自：投票");
                holder.msgItemTvTime.setText(YplayTimeUtils.format(imSession.getMsgTs()*1000));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //status = 1 data_type = 2 sender = not self
    private void initItem3(messageHolder holder, String msgContent, ImSession imSession) {
        try {
            JSONObject jsonObject = new JSONObject(msgContent);
            int dataType = jsonObject.getInt("DataType");
            String data = jsonObject.getString("Data");
            if (2 == dataType) {
                MsgContent2 msgContent2 = GsonUtil.GsonToBean(data, MsgContent2.class);
                MsgContent2.SenderInfoBean senderInfoBean = msgContent2.getSenderInfo();
                String headerUrl = senderInfoBean.getHeadImgUrl();
//                int gender = senderInfoBean.getGender();
//                int grade = senderInfoBean.getGrade();
//                int schoolType = senderInfoBean.getSchoolType();
//                String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType,grade);
                String nickName = senderInfoBean.getNickName();
                String content = msgContent2.getContent();
                if (TextUtils.isEmpty(headerUrl)) {
                    holder.msgItemHeaderImg.setImageResource(R.drawable.header_deafult);
                } else {
                    Picasso.with(context).load(headerUrl).into(holder.msgItemHeaderImg);

                }
                holder.msgItemName.setText(nickName);
                holder.msgItemName.setTextColor(context.getResources().getColor(R.color.text_color_gray));
                holder.msgItemCuo.setVisibility(View.GONE);
                holder.msgItemContent.setText(content);
                holder.msgItemTvTime.setText(YplayTimeUtils.format(imSession.getMsgTs()*1000));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //status = 2
    private void initItem4(messageHolder holder, String msgContent, ImSession imSession) {

        int msgType = imSession.getMsgType();
        if (msgType == TIMElemType.Custom.ordinal()){

        }

        String headerUrl = imSession.getHeaderImgUrl();
        String nickName = imSession.getNickName();
        long msgTime = imSession.getMsgTs();
        //String msgTime = YplayTimeUtils.format(imSession.getMsgTs());

        if (TextUtils.isEmpty(headerUrl)) {
            holder.msgItemHeaderImg.setImageResource(R.drawable.header_deafult);
        } else {
            Picasso.with(context).load(headerUrl).into(holder.msgItemHeaderImg);

        }
        holder.msgItemName.setText(nickName);
        holder.msgItemName.setTextColor(context.getResources().getColor(R.color.text_color_gray));
        holder.msgItemCuo.setVisibility(View.GONE);
        holder.msgItemContent.setText(msgContent);
        holder.msgItemTvTime.setText(YplayTimeUtils.format(msgTime*1000));
    }

}
