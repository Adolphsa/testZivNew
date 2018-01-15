package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.model.GetRecommendsRespond;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 通讯录好友适配器
 * Created by Administrator on 2017/10/27.
 */

public class GuideContactsAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "GuideContactsAdapter";

    private Context context;
    private hideCallback hideCallback;
    private acceptCallback acceptCallback;
    List<ContactsInfo> contentList;
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

    public GuideContactsAdapter(Context context,
                                hideCallback hideCallback,
                                acceptCallback acceptCallback,
                                List<ContactsInfo> list,
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
            convertView = View.inflate(context, R.layout.item_guide_add_friends, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactsInfo contactsInfo = contentList.get(position);
        String url = contactsInfo.getHeadImgUrl();
        String nickName = contactsInfo.getNickName();
        String phone = contactsInfo.getPhone();

        Log.i(TAG, "getView: nickname---" + nickName + "---" + phone);

        holder.afItemName.setText(nickName);
        holder.afItemTvSharesFriends.setText(phone);
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_add_friends_width,
                    R.dimen.item_add_friends_height).into(holder.afItemHeaderImg);
        }else {
            holder.afItemHeaderImg.setImageResource(R.drawable.header_deafult);
        }

//        holder.afBtnHide.setOnClickListener(hideListener);
//        holder.afBtnHide.setTag(position);
//        holder.afBtnHide.setVisibility(View.VISIBLE);
        //holder.afBtnAccept2.setVisibility(View.GONE);
        holder.afBtnAccept.setBackgroundResource(R.drawable.guide_add_friend_no);
        holder.afBtnAccept.setOnClickListener(acceptListener);
        holder.afBtnAccept.setTag(position);

//        for (Integer temp : positionList){
//            if (temp == position){
//                Log.i(TAG, "getView: 已经接受的item---" + temp);
//                holder.afBtnAccept.setBackgroundResource(R.drawable.guide_add_friend_yes);
//                holder.afBtnAccept.setOnClickListener(acceptListener);
//            }
//        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.af_item_header_img)
        EffectiveShapeView afItemHeaderImg;
        @BindView(R.id.af_item_text_family_name)
        TextView afItemFamilyName;
        @BindView(R.id.af_item_name)
        TextView afItemName;
        @BindView(R.id.af_item_tv_shares_friends)
        TextView afItemTvSharesFriends;
        @BindView(R.id.af_btn_accept2)
        Button afBtnAccept;
//        @BindView(R.id.af_btn_accept2)
//        Button afBtnAccept2;
        @BindView(R.id.af_btn_hide)
        Button afBtnHide;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
