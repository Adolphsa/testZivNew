package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.FriendsListRespond;

import java.util.List;

import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 个人信息中我的好友列表的适配器
 * Created by xjg on 2018/01/06
 */

public class MyFriendsAdapter extends BaseAdapter{

    private Context context;

    List<FriendsListRespond.PayloadBean.FriendsBean> friendsInfoList;

    public MyFriendsAdapter(Context context,
                              List<FriendsListRespond.PayloadBean.FriendsBean> list) {
        this.context = context;
        this.friendsInfoList = list;
    }


    @Override
    public int getCount() {
        return friendsInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_my_friend,null);
            holder = new ViewHolder();
            holder.itemMyFriendImg = (EffectiveShapeView) convertView.findViewById(R.id.item_my_friend_img);
            holder.itemMyFriendName = (TextView) convertView.findViewById(R.id.item_my_friend_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = friendsInfoList.get(position).getHeadImgUrl();
        holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_my_friends_width,
                    R.dimen.item_my_friends_height).into(holder.itemMyFriendImg);
        }else {
            holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
        }
        String name = friendsInfoList.get(position).getNickName();
        holder.itemMyFriendName.setText(name);

        return convertView;
    }

    private static class ViewHolder{
        EffectiveShapeView itemMyFriendImg;
        TextView itemMyFriendName;
    }
}