package com.yeejay.yplay.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ContactsInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 通讯录好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class WaitInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "WaitInviteAdapter";

    private int TYPE_NORMAL = 1000;
    private int TYPE_TEXT = 1001;

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    private List<ContactsInfo> contentList;
    private Map<String, Integer> alphaIndexer;
    private List<String> sections;
    private boolean flag;//标志用于只执行一次代码
    private OnGetAlphaIndexerAndSectionsListener listener;
    public interface OnRecycleItemListener <T>{
        void onRecycleItemClick(View v,T o);
    }
    private OnRecycleItemListener itemListener;

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
                             List<ContactsInfo> list) {
        this.hideCallback = hideCallback;
        this.acceptCallback = acceptCallback;
        this.context = context;
        this.contentList = list;

        alphaIndexer = new HashMap<>();
        sections = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            int uin = list.get(i).getUin();
            if (uin == 0) {
                //当前汉语拼音的首字母
                String currentAlpha = list.get(i).getSortKey();
                //上一个拼音的首字母，如果不存在则为""
                String previewAlpha = (i - 1) >= 0 ? list.get(i - 1).getSortKey() : "";
                if (!TextUtils.isEmpty(previewAlpha) && !previewAlpha.equals(currentAlpha)) {    //保存第一个字母出现的位置
                    String firstAlpha = list.get(i).getSortKey();
                    alphaIndexer.put(firstAlpha, i);
                    sections.add(firstAlpha);
                }
            }

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_NORMAL) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_wait_invite_friends_new, parent, false));
        } else if (viewType == TYPE_TEXT) {
            return new TextViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_add_friend_contatcs, parent, false));
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.i(TAG, "getView: " + position);

        int uin = contentList.get(position).getUin();
        if (uin == 0) {  //未开通
            if (position >= 1) {
                String currentAlpha = contentList.get(position).getSortKey();
                String previewAlpha = contentList.get(position - 1).getSortKey();
                if (!TextUtils.isEmpty(previewAlpha) && !previewAlpha.equals(currentAlpha)) { //不相等表示有新的字母项产生且为该类字母堆中的第一个字母索引项
                    ((ViewHolder) holder).afiFirstAlpha.setText(currentAlpha);
                    ((ViewHolder) holder).afiFirstAlpha.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolder) holder).afiFirstAlpha.setVisibility(View.GONE);
                }

            } else {
                ((ViewHolder) holder).afiFirstAlpha.setText(contentList.get(position).getSortKey());
            }

            ((ViewHolder) holder).afItemHeaderImg.setVisibility(View.GONE);
            ((ViewHolder) holder).afItemFamilyName.setVisibility(View.VISIBLE);

            String nickName = contentList.get(position).getName().trim();
            if (!TextUtils.isEmpty(nickName)) {
                ((ViewHolder) holder).afItemFamilyName.setText(nickName.substring(0, 1));
            }
            ((ViewHolder) holder).afItemName.setText(nickName);
            ((ViewHolder) holder).afItemTvSharesFriends.setText(contentList.get(position).getPhone());

            ((ViewHolder) holder).afBtnAccept.setBackgroundResource(R.drawable.friend_invitation);
            ((ViewHolder) holder).afBtnAccept.setEnabled(true);
            ((ViewHolder) holder).afBtnAccept.setOnClickListener(acceptListener);

            ((ViewHolder) holder).afBtnAccept.setTag(position);
        } else if (uin == 2) {   //已开通文字
            String contactTitle = contentList.get(position).getName();
            ((TextViewHolder) holder).contactsTitle.setText(contactTitle);
        } else if (uin == 3) {    //未开通文字
            String contactTitle = contentList.get(position).getName();
            ((TextViewHolder) holder).contactsTitle.setText(contactTitle);
        } else if (uin > 1000) {  //通讯录已开通好友

            ((ViewHolder) holder).afItemHeaderImg.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).afItemFamilyName.setVisibility(View.INVISIBLE);

            ContactsInfo contactsInfo = contentList.get(position);

            String url = contactsInfo.getHeadImgUrl();
            String nickName = contactsInfo.getNickName().trim();

            ((ViewHolder) holder).afItemName.setText(nickName);
            ((ViewHolder) holder).afItemTvSharesFriends.setText("通讯录好友");

            if (!TextUtils.isEmpty(url)) {
                Picasso.with(context).load(url).resizeDimen(R.dimen.item_add_friends_width,
                        R.dimen.item_add_friends_height).into(((ViewHolder) holder).afItemHeaderImg);
            } else {
                ((ViewHolder) holder).afItemHeaderImg.setImageResource(R.drawable.header_deafult);
            }

            ((ViewHolder) holder).afBtnAccept.setEnabled(true);
            ((ViewHolder) holder).afBtnAccept.setBackgroundResource(R.drawable.add_friend_icon);
            ((ViewHolder) holder).afBtnAccept.setOnClickListener(acceptListener);
            ((ViewHolder) holder).afBtnAccept.setTag(position);

            ((ViewHolder) holder).afiItenRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onRecycleItemClick(v,position);
                }
            });

        }

    }

    public void addRecycleItemListener(OnRecycleItemListener itemListener){
        this.itemListener = itemListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (!flag) {
            if (listener != null) {
                listener.getAlphaIndexerAndSectionsListner(alphaIndexer, sections);
            }
            flag = true;
        }
        return contentList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int uin = contentList.get(position).getUin();
        if (uin == 2 || uin == 3) {
            return TYPE_TEXT;
        } else {
            return TYPE_NORMAL;
        }

    }

    public void setOnGetAlphaIndeserAndSectionListener(OnGetAlphaIndexerAndSectionsListener listener) {
        this.listener = listener;
    }

    public interface OnGetAlphaIndexerAndSectionsListener {
        public void getAlphaIndexerAndSectionsListner(Map<String, Integer> alphaIndexer, List<String> sections);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.afi_item_root)
        LinearLayout afiItenRootView;
        @BindView(R.id.afi_item_header_img)
        EffectiveShapeView afItemHeaderImg;
        @BindView(R.id.afi_item_first_alpha)
        TextView afiFirstAlpha;
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
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_contacts_not_open_title)
        TextView contactsTitle;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
