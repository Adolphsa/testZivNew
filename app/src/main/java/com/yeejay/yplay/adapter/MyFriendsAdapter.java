package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.FriendInfo;
import com.yeejay.yplay.model.FriendsListRespond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 个人信息中我的好友列表的适配器
 * Created by xjg on 2018/01/06
 */

public class MyFriendsAdapter extends BaseAdapter{

    private Context context;
    private Map<String,Integer> alphaIndexer;
    private List<String> sections;
    private boolean flag;//标志用于只执行一次代码
    private OnGetAlphaIndexerAndSectionsListener listener;
    List<FriendInfo> friendsInfoList;

    public MyFriendsAdapter(Context context, List<FriendInfo> list) {
        this.context = context;
        this.friendsInfoList = list;

        alphaIndexer = new HashMap<>();
        sections=new ArrayList<>();

        String currentAlpha = null;
        String previewAlpha = null;
        String firstAlpha = null;
        for (int i=0; i < list.size(); i++) {

            //当前汉语拼音的首字母
            currentAlpha = list.get(i).getSortKey();
            //上一个拼音的首字母，如果不存在则为""
            previewAlpha=(i-1)>=0?list.get(i-1).getSortKey():"";
            if (!previewAlpha.equals(currentAlpha)){    //保存第一个字母出现的位置
                firstAlpha=list.get(i).getSortKey();
                alphaIndexer.put(firstAlpha,i);
                sections.add(firstAlpha);
            }
        }

    }

    @Override
    public int getCount() {
        if (!flag){
            if (listener!=null){
                listener.getAlphaIndexerAndSectionsListner(alphaIndexer,sections);
            }
            flag=true;
        }
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
            holder.itemMyFriendFirstName = (TextView) convertView.findViewById(R.id.item_my_friend_first_alpha);
            holder.itemMyFriendImg = (EffectiveShapeView) convertView.findViewById(R.id.item_my_friend_img);
            holder.itemMyFriendName = (TextView) convertView.findViewById(R.id.item_my_friend_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        FriendInfo friendInfo = friendsInfoList.get(position);

        String url = friendInfo.getFriendHeadUrl();
        holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_my_friends_width,
                    R.dimen.item_my_friends_height).into(holder.itemMyFriendImg);
        }else {
            holder.itemMyFriendImg.setImageResource(R.drawable.header_deafult);
        }
        String name = friendInfo.getFriendName();
        holder.itemMyFriendName.setText(name);

        if (position >= 1){
            String currentAlpha = friendsInfoList.get(position).getSortKey();
            String previewAlpha = friendsInfoList.get(position-1).getSortKey();
            if (!previewAlpha.equals(currentAlpha)){ //不相等表示有新的字母项产生且为该类字母堆中的第一个字母索引项
                holder.itemMyFriendFirstName.setText(currentAlpha);
                holder.itemMyFriendFirstName.setVisibility(View.VISIBLE);
            }else {
                holder.itemMyFriendFirstName.setVisibility(View.GONE);
            }

        }else {
            holder.itemMyFriendFirstName.setText(friendInfo.getSortKey());
        }
        return convertView;
    }

    public void setOnGetAlphaIndeserAndSectionListener(OnGetAlphaIndexerAndSectionsListener listener){
        this.listener=listener;
    }

    public interface OnGetAlphaIndexerAndSectionsListener{
        public void getAlphaIndexerAndSectionsListner(Map<String,Integer>alphaIndexer,List<String>sections);

    }

    private static class ViewHolder{
        TextView itemMyFriendFirstName;
        EffectiveShapeView itemMyFriendImg;
        TextView itemMyFriendName;
    }
}