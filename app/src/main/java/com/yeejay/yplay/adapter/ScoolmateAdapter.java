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
 * 添加朋友列表适配器
 * Created by Administrator on 2017/10/27.
 */

public class ScoolmateAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    private int type;
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

    public ScoolmateAdapter(Context context,
                            hideCallback hideCallback,
                            acceptCallback acceptCallback,
                            List<GetRecommendsRespond.PayloadBean.FriendsBean> list,
                            int type) {
        this.hideCallback = hideCallback;
        this.acceptCallback = acceptCallback;
        this.context = context;
        this.contentList = list;
        this.type = type;
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
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).into(holder.afItemHeaderImg);
        }
        holder.afItemName.setText(contentList.get(position).getNickName());
        holder.afBtnHide.setOnClickListener(hideListener);
        holder.afBtnHide.setTag(position);
        holder.afBtnHide.setVisibility(View.VISIBLE);
        //holder.afBtnAccept.setOnClickListener(acceptListener); //1---通讯录 2---等待邀请 3--同校好友
        int status = contentList.get(position).getStatus();
        if (type == 1 ){     //通讯录好友
            if (status == 2){
                holder.afBtnAccept.setText("已申请");
                holder.afBtnAccept.setEnabled(false);
            }else {
                holder.afBtnAccept.setText("加好友");
                holder.afBtnAccept.setEnabled(true);
                holder.afBtnAccept.setOnClickListener(acceptListener);
            }
        }else if (type == 2) {   //等待邀请
            if (status == 5){
                holder.afBtnAccept.setText("已邀请");
                holder.afBtnAccept.setEnabled(false);
            }else {
                holder.afBtnAccept.setText("邀请");
                holder.afBtnAccept.setEnabled(true);
                holder.afBtnAccept.setOnClickListener(acceptListener);
            }
        }else if (type == 3){   //同校好友
            if (status == 2){
                holder.afBtnAccept.setText("已申请");
                holder.afBtnAccept.setEnabled(false);
            }else {
                holder.afBtnAccept.setText("加好友");
                holder.afBtnAccept.setEnabled(true);
                holder.afBtnAccept.setOnClickListener(acceptListener);
            }
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
        @BindView(R.id.af_btn_accept)
        Button afBtnAccept;
        @BindView(R.id.af_btn_hide)
        Button afBtnHide;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
