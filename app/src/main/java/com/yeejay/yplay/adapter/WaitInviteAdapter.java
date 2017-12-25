package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.model.GetRecommendsRespond;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通讯录好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class WaitInviteAdapter extends BaseAdapter implements View.OnClickListener {

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

    public WaitInviteAdapter(Context context,
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
            convertView = View.inflate(context, R.layout.item_wait_invite_friends, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        String nickName = contentList.get(position).getNickName();
        if (!TextUtils.isEmpty(nickName)){
            holder.afItemFamilyName.setText(nickName.substring(0,1));
        }
        holder.afItemName.setText(nickName);
        holder.afItemTvSharesFriends.setText(contentList.get(position).getPhone());
//        holder.afBtnHide.setOnClickListener(hideListener);
//        holder.afBtnHide.setTag(position);
        //holder.afBtnHide.setVisibility(View.VISIBLE);
        int status = contentList.get(position).getStatus();
        if (status == 5){
            holder.afBtnAccept.setBackgroundResource(R.drawable.friend_invitation_done);
            holder.afBtnAccept.setEnabled(false);
        }else {
            holder.afBtnAccept.setBackgroundResource(R.drawable.friend_invitation);
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);
        }
        holder.afBtnAccept.setTag(position);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.afi_item_text_family_name)
        TextView afItemFamilyName;
        @BindView(R.id.af_item_name)
        TextView afItemName;
        @BindView(R.id.af_item_tv_shares_friends)
        TextView afItemTvSharesFriends;
        @BindView(R.id.af_btn_accept)
        Button afBtnAccept;
        @BindView(R.id.af_btn_hide)
        Button afBtnHide;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
