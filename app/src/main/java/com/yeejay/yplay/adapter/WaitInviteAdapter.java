package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.model.GetRecommendsRespond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.http.POST;

/**
 * 通讯录好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class WaitInviteAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    private List<ContactsInfo> contentList;
    private Map<String,Integer> alphaIndexer;
    private List<String> sections;
    private boolean flag;//标志用于只执行一次代码
    private OnGetAlphaIndexerAndSectionsListener listener;

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
        sections=new ArrayList<>();

        for (int i=0; i < list.size(); i++) {

            //当前汉语拼音的首字母
            String currentAlpha = list.get(i).getSortKey();
            //上一个拼音的首字母，如果不存在则为""
            String previewAlpha=(i-1)>=0?list.get(i-1).getSortKey():"";
            if (!previewAlpha.equals(currentAlpha)){    //保存第一个字母出现的位置
                String firstAlpha=list.get(i).getSortKey();
                alphaIndexer.put(firstAlpha,i);
                sections.add(firstAlpha);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getCount() {

        if (!flag){
            if (listener!=null){
                listener.getAlphaIndexerAndSectionsListner(alphaIndexer,sections);
            }
            flag=true;
        }
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
            convertView = View.inflate(context, R.layout.item_wait_invite_friends_new, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position >= 1){
            String currentAlpha = contentList.get(position).getSortKey();
            String previewAlpha = contentList.get(position-1).getSortKey();
            if (!previewAlpha.equals(currentAlpha)){ //不相等表示有新的字母项产生且为该类字母堆中的第一个字母索引项
                holder.afiFirstAlpha.setText(currentAlpha);
                holder.afiFirstAlpha.setVisibility(View.VISIBLE);
            }else {
                holder.afiFirstAlpha.setVisibility(View.GONE);
            }

        }else {
            holder.afiFirstAlpha.setText(contentList.get(position).getSortKey());
        }

        String nickName = contentList.get(position).getName().trim();
        if (!TextUtils.isEmpty(nickName)){
            holder.afItemFamilyName.setText(nickName.substring(0,1));
        }
        holder.afItemName.setText(nickName);
        holder.afItemTvSharesFriends.setText(contentList.get(position).getPhone());
//        holder.afBtnHide.setOnClickListener(hideListener);
//        holder.afBtnHide.setTag(position);
        //holder.afBtnHide.setVisibility(View.VISIBLE);
//        int status = contentList.get(position).getStatus();
//        if (status == 5){
//            holder.afBtnAccept.setBackgroundResource(R.drawable.friend_invitation_done);
//            holder.afBtnAccept.setEnabled(false);
//        }else {
            holder.afBtnAccept.setBackgroundResource(R.drawable.friend_invitation);
            holder.afBtnAccept.setEnabled(true);
            holder.afBtnAccept.setOnClickListener(acceptListener);
//        }
        holder.afBtnAccept.setTag(position);
        return convertView;
    }

    public void setOnGetAlphaIndeserAndSectionListener(OnGetAlphaIndexerAndSectionsListener listener){
        this.listener=listener;
    }

    public interface OnGetAlphaIndexerAndSectionsListener{
        public void getAlphaIndexerAndSectionsListner(Map<String,Integer>alphaIndexer,List<String>sections);

    }

    static class ViewHolder {
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
            ButterKnife.bind(this, view);
        }
    }
}
