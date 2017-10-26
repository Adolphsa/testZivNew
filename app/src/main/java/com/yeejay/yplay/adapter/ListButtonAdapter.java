package com.yeejay.yplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yeejay.yplay.R;

import java.util.List;

/**
 * ListView内部有按钮的适配器
 * Created by Administrator on 2017/10/26.
 */

public class ListButtonAdapter extends BaseAdapter implements View.OnClickListener {

    private List<String> mContentList;
    private LayoutInflater mInflater;
    private LbCallback mCallback;

    public interface LbCallback {
        void click(View v);
    }

    public ListButtonAdapter(Context context, List<String> contentList, LbCallback callback) {
        mContentList = contentList;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        mCallback.click(v);

    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_invite_friends, null);
            holder = new ViewHolder();
            holder.itemInviteName = (TextView) convertView.findViewById(R.id.aif_item_tv_invite_name);
            holder.itemBtnInvite = (Button) convertView.findViewById(R.id.aif_item_btn_invite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemInviteName.setText(mContentList.get(position));
        holder.itemBtnInvite.setOnClickListener(this);
        holder.itemBtnInvite.setTag(position);
        return convertView;
    }

    static class ViewHolder {
        TextView itemInviteName;
        Button itemBtnInvite;
    }
}
