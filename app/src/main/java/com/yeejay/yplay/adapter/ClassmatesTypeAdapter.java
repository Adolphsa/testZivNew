package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.GetAddFriendMsgs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 同校同学筛选类型适配器
 * Created by xjg on 2017/12/24.
 */

public class ClassmatesTypeAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    List<String> contentList;

    public ClassmatesTypeAdapter(Context context,
                                 List<String> list) {

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
            convertView = View.inflate(context, R.layout.spinner_layout_filer_classmate_type, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(contentList.get(position));
        if (position >= 0 && position <=3) {
            holder.icon.setVisibility(View.INVISIBLE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.spinner_title)
        TextView title;
        @BindView(R.id.spinner_icon)
        ImageView icon;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
