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
import com.yeejay.yplay.model.GetAddFriendMsgs;
import com.yeejay.yplay.utils.NetWorkUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 加好友请求适配器
 * Created by Administrator on 2017/10/27.
 */

public class FriendsDetailAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    List<GetAddFriendMsgs.PayloadBean.MsgsBean> contentList;
    ViewHolder holder;

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
            if (NetWorkUtil.isNetWorkAvailable(context)){
                holder.afBtnHide.setVisibility(View.GONE);
            }

        }
    };

    public interface hideCallback {
        void hideClick(View v);
    }

    public interface acceptCallback {
        void acceptClick(View v);

    }

    public FriendsDetailAdapter(Context context,
                                hideCallback hideCallback,
                                acceptCallback acceptCallback,
                                List<GetAddFriendMsgs.PayloadBean.MsgsBean> list) {
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

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_add_friends, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        String url = contentList.get(position).getFromHeadImgUrl();
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).into(holder.afItemHeaderImg);
        }else {
            holder.afItemHeaderImg.setImageResource(R.drawable.header_deafult);
        }

        holder.afItemTvSharesFriends.setText(contentList.get(position).getMsgDesc());
        holder.afItemName.setText(contentList.get(position).getFromNickName());
        holder.afBtnHide.setOnClickListener(hideListener);
        holder.afBtnHide.setTag(position);
        holder.afBtnHide.setVisibility(View.VISIBLE);
        int status = contentList.get(position).getStatus();
        if (status == 1){
            holder.afBtnAccept.setBackgroundResource(R.drawable.is_friend);
            holder.afBtnAccept.setEnabled(false);
        }else {
            holder.afBtnAccept.setBackgroundResource(R.drawable.green_accept);
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);
        }
        holder.afBtnAccept.setTag(position);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.af_item_header_img)
        EffectiveShapeView afItemHeaderImg;
        @BindView(R.id.af_item_name)
        TextView afItemName;
        @BindView(R.id.af_item_tv_shares_friends)
        TextView afItemTvSharesFriends;
        @BindView(R.id.af_btn_accept2)
        Button afBtnAccept;
        @BindView(R.id.af_btn_hide)
        Button afBtnHide;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
