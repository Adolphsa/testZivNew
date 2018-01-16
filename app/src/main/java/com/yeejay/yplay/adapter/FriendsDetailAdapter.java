package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class FriendsDetailAdapter extends RecyclerView.Adapter<FriendsDetailAdapter.ViewHolder> implements View.OnClickListener {

    public interface OnRecycleItemListener <T>{
        void onRecycleItemClick(View v,T o);
    }

    private OnRecycleItemListener listener;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_accept_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String url = contentList.get(position).getFromHeadImgUrl();
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_add_friends_width,
                    R.dimen.item_add_friends_height).into(holder.afItemHeaderImg);
        }else {
            holder.afItemHeaderImg.setImageResource(R.drawable.header_deafult);
        }

        holder.afItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecycleItemClick(v,position);
            }
        });

        holder.afItemTvSharesFriends.setText(contentList.get(position).getMsgDesc());
        holder.afItemName.setText(contentList.get(position).getFromNickName());
        holder.afBtnHide.setOnClickListener(hideListener);
        holder.afBtnHide.setTag(position);
        int status = contentList.get(position).getStatus();
        if (status == 1){
            holder.afBtnAccept.setBackgroundResource(R.drawable.be_as_friends);
            holder.afBtnAccept.setEnabled(false);
        }else {
            holder.afBtnAccept.setBackgroundResource(R.drawable.friends_accept);
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);
        }
        holder.afBtnAccept.setTag(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public void addRecycleItemListener(OnRecycleItemListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.af_item_root)
        RelativeLayout afItemRoot;
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
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
