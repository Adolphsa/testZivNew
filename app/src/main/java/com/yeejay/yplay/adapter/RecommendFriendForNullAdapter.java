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
                                         List<GetRecommendsRespond.PayloadBean.FriendsBean> list) {
        this.hideCallback = hideCallback;
        this.acceptCallback = acceptCallback;
        this.context = context;
        this.contentList = list;
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
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        String url = contentList.get(position).getHeadImgUrl();
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).into(holder.rfHeaderImg);
            holder.afiTvFamilyName.setVisibility(View.INVISIBLE);
        }else {
            String nickName = contentList.get(position).getNickName();
            if (!TextUtils.isEmpty(nickName)){
                holder.afiTvFamilyName.setText(nickName.substring(0,1));
                holder.rfHeaderImg.setVisibility(View.INVISIBLE);
            }
        }
        String nickName = contentList.get(position).getNickName();
        holder.afItemName.setText(nickName);
        int recommendType = contentList.get(position).getRecommendType();
        if (recommendType == 1 || recommendType == 2){
            holder.afItemTvSharesFriends.setText("通讯录好友");
            holder.afBtnAccept.setBackgroundResource(R.drawable.play_invite_no);
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);

        }else if (recommendType == 3){
            holder.afItemTvSharesFriends.setText("同校好友");
            holder.afBtnAccept.setBackgroundResource(R.drawable.btn_add_friend); //加好友
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);
        }

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
