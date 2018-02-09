package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.GetRecommendsRespond;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 同校好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class SchoolmateAdapter extends RecyclerView.Adapter<SchoolmateAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "SchoolmateAdapter";

    public interface OnRecycleItemListener <T>{
        void onRecycleItemClick(View v,T o);
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    private OnRecycleItemListener listener;
    private OnLoadMoreListener loadMoreListener;
    private int pageNum;

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> contentList;
    List<Integer> positionList;


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

    public SchoolmateAdapter(Context context,
                             hideCallback hideCallback,
                             acceptCallback acceptCallback,
                             List<GetRecommendsRespond.PayloadBean.FriendsBean> list,
                             List positionList) {
        this.hideCallback = hideCallback;
        this.acceptCallback = acceptCallback;
        this.context = context;
        this.contentList = list;
        this.positionList = positionList;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_add_friends, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        GetRecommendsRespond.PayloadBean.FriendsBean friendsBean = contentList.get(position);

        String url = friendsBean.getHeadImgUrl();
        String nickName = friendsBean.getNickName();
        String str = friendsBean.getRecommendDesc();

        holder.afItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecycleItemClick(v,position);
            }
        });


        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_add_friends_width,
                    R.dimen.item_add_friends_height).into(holder.afItemHeaderImg);
        } else {
            holder.afItemHeaderImg.setImageResource(R.drawable.header_deafult);
        }
        holder.afItemName.setText(nickName);
        holder.afItemTvSharesFriends.setText(str);

//        holder.afBtnHide.setOnClickListener(hideListener);
//        holder.afBtnHide.setTag(position);
//        holder.afBtnHide.setVisibility(View.VISIBLE);

        holder.afBtnAccept.setEnabled(true);
        holder.afBtnAccept.setBackgroundResource(R.drawable.add_friend);
        holder.afBtnAccept.setOnClickListener(acceptListener);

        for (Integer temp : positionList){
            if (temp == position){
                Log.i(TAG, "getView: 已经接受的item---" + temp);
                holder.afBtnAccept.setBackgroundResource(R.drawable.add_friend_apply);
                holder.afBtnAccept.setOnClickListener(acceptListener);
            }
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
