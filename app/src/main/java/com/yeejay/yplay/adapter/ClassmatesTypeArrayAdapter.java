package com.yeejay.yplay.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.yeejay.yplay.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 同校同学筛选类型适配器
 * Created by xjg on 2017/12/24.
 */

public class ClassmatesTypeArrayAdapter extends ArrayAdapter{
    private static final int FLAG_NO_DROPDOWN = 0;
    private static final int FLAG_DROPDOWN = 1;
    private Context context;
    List<String> contentList;
    private int resource;
    private int dropdownFlag = 0;//为0表示收起，为1表示展开

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

    public ClassmatesTypeArrayAdapter(Context context, int resourceId, List<String> objects) {
        super(context, resourceId, objects);

        this.context = context;
        this.contentList = objects;
        this.resource = resourceId;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Log.d("XJG" , "getDropDownView, position = " + position);
        dropdownFlag = FLAG_DROPDOWN;
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.spinner_dropdown_layout_filter_classmat_type, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(contentList.get(position));
        holder.icon.setVisibility(View.INVISIBLE);
        SharedPreferences settings = context.getSharedPreferences("preferences_class_type",
                Context.MODE_PRIVATE);
        int selectOpn = settings.getInt("position", -1);
        if(position == selectOpn) {
            holder.icon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("XJG" , "getView, position = " + position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, resource, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(contentList.get(position));

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
