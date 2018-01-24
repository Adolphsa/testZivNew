package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 通讯录好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class RecommendFriendForNullAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> contentList;
    int activityType;

    View.OnClickListener hideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideCallback.hideClick(v);
        }
    };
    View.OnClickListener acceptListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            acceptCallback.acceptClick(v);
        }
    };

    public interface hideCallback {
        void hideClick(View v);
    }

    public interface acceptCallback {
        void acceptClick(View v);
    }

    public RecommendFriendForNullAdapter(Context context,
                                         hideCallback hideCallback,
                                         acceptCallback acceptCallback,
                                         List<GetRecommendsRespond.PayloadBean.FriendsBean> list,
                                         int activityType) {
        this.hideCallback = hideCallback;
        this.acceptCallback = acceptCallback;
        this.context = context;
        this.contentList = list;
        this.activityType = activityType;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_recommend_friend_for_null, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GetRecommendsRespond.PayloadBean.FriendsBean friendsBean = contentList.get(position);
        String url = friendsBean.getHeadImgUrl();
        int recommendType = friendsBean.getRecommendType();
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_add_friends_width,
                    R.dimen.item_add_friends_height).into(holder.rfHeaderImg);
            holder.afiTvFamilyName.setVisibility(View.INVISIBLE);
        } else {
            String nickName = friendsBean.getNickName();

            if (recommendType == 1 || recommendType == 3 || recommendType == 4) {
                holder.afiTvFamilyName.setVisibility(View.INVISIBLE);
                holder.rfHeaderImg.setVisibility(View.VISIBLE);
                holder.rfHeaderImg.setImageResource(R.drawable.header_deafult);
            } else if (recommendType == 2) {
                holder.afiTvFamilyName.setText(nickName.substring(0, 1));
                holder.rfHeaderImg.setVisibility(View.INVISIBLE);
            }

        }
        String nickName = friendsBean.getNickName();
        holder.afItemName.setText(nickName);
        holder.afItemTvSharesFriends.setText(friendsBean.getRecommendDesc());

        if (YPlayConstant.YPLAY_FEEDS_TYPE == activityType){
            if (recommendType == 2) { //邀请
                //holder.afItemTvSharesFriends.setText("通讯录好友");
                holder.afBtnAccept.setBackgroundResource(R.drawable.red_invite);
            } else if (recommendType == 1 || recommendType == 3 || recommendType == 4) { //加好友
                //holder.afItemTvSharesFriends.setText("同校好友");
                holder.afBtnAccept.setBackgroundResource(R.drawable.btn_add_friend); //加好友
            }
        }else {
            if (recommendType == 2) { //邀请
                //holder.afItemTvSharesFriends.setText("通讯录好友");
                holder.afBtnAccept.setBackgroundResource(R.drawable.play_invite_no);
            } else if (recommendType == 1 || recommendType == 3 || recommendType == 4) { //加好友
                //holder.afItemTvSharesFriends.setText("同校好友");
                holder.afBtnAccept.setBackgroundResource(R.drawable.green_add_friend); //加好友
        }

        }
        holder.afBtnAccept.setEnabled(true);
        holder.afBtnAccept.setOnClickListener(acceptListener);
        holder.afBtnAccept.setTag(position);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ff_item_header_img)
        EffectiveShapeView rfHeaderImg;
        @BindView(R.id.afi_item_text_family_name)
        TextView afiTvFamilyName;
        @BindView(R.id.af_item_name)
        TextView afItemName;
        @BindView(R.id.af_item_tv_shares_friends)
        TextView afItemTvSharesFriends;
        @BindView(R.id.af_btn_accept)
        Button afBtnAccept;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
