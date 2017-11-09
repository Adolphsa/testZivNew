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
import com.yeejay.yplay.model.GetRecommendAll;
import com.yeejay.yplay.utils.FriendFeedsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 添加朋友列表适配器
 * Created by Administrator on 2017/10/27.
 */

public class ScoolmateAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;

    List<GetRecommendAll.PayloadBean.InfoBean.FriendsFromAddrBookBean> contentList;

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

    public ScoolmateAdapter(Context context,
                            hideCallback hideCallback,
                            acceptCallback acceptCallback,
                            List<GetRecommendAll.PayloadBean.InfoBean.FriendsFromAddrBookBean> list) {
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
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_add_friends2, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String url = contentList.get(position).getHeadImgUrl();
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).into(holder.afItemHeaderImg);
            holder.afTvFamilyName.setVisibility(View.INVISIBLE);
        }else {
            String nickName = contentList.get(position).getNickName();
            if (!TextUtils.isEmpty(nickName)){
                holder.afTvFamilyName.setText(nickName.substring(0,1));
                holder.afItemHeaderImg.setVisibility(View.INVISIBLE);
            }
        }
        holder.afItemName.setText(contentList.get(position).getNickName());
        int recommendType = contentList.get(position).getRecommendType();
        if (recommendType == 1){    //通讯录好友
            holder.afItemTvSharesFriends.setText(contentList.get(position).getPhone());
            holder.afBtnAccept.setBackgroundResource(R.drawable.feeds_status_add_friend);
            holder.afBtnAccept2.setVisibility(View.GONE);
        }else if (recommendType == 2){ //等待邀请
            holder.afItemTvSharesFriends.setText(contentList.get(position).getPhone());
            holder.afBtnAccept.setBackgroundResource(R.drawable.feeds_status_invite);
        }else if (recommendType == 3){ //同校非好友
            String str = FriendFeedsUtil.schoolType(contentList.get(position).getSchoolType(),
                    contentList.get(position).getGrade());
            holder.afItemTvSharesFriends.setText("同校" + str);
            holder.afBtnAccept.setBackgroundResource(R.drawable.feeds_status_add_friend);
        }
        holder.afBtnAccept.setOnClickListener(acceptListener);
        holder.afBtnAccept.setTag(position);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ff_item_header_img)
        EffectiveShapeView afItemHeaderImg;
        @BindView(R.id.afi_item_text_family_name)
        TextView afTvFamilyName;
        @BindView(R.id.af_item_name)
        TextView afItemName;
        @BindView(R.id.af_item_tv_shares_friends)
        TextView afItemTvSharesFriends;
        @BindView(R.id.af_btn_accept)
        Button afBtnAccept;
        @BindView(R.id.af_btn_accept2)
        Button afBtnAccept2;
        @BindView(R.id.af_btn_hide)
        Button afBtnHide;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
