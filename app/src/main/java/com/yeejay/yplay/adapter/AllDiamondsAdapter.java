package com.yeejay.yplay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.customview.RecyclerImageView;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;

import java.util.List;

/**
 * 个人信息中钻石列表的适配器
 * Created by xjg on 2018/01/06
 */

public class AllDiamondsAdapter extends BaseAdapter{

    private Context context;

    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> diamondsInfoList;

    public AllDiamondsAdapter(Context context,
                                  List<UsersDiamondInfoRespond.PayloadBean.StatsBean> list) {
        this.context = context;
        this.diamondsInfoList = list;
    }


    @Override
    public int getCount() {
        return diamondsInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return diamondsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_afi, null);
            holder = new ViewHolder();
            holder.itemAmiIndex = (TextView) convertView.findViewById(R.id.afi_item_index);
            holder.itemAmiImg = (RecyclerImageView) convertView.findViewById(R.id.afi_item_img);
            holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);
            holder.itemAmiCount = (TextView) convertView.findViewById(R.id.afi_item_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean = diamondsInfoList.get(position);

        holder.itemAmiIndex.setText(String.valueOf(position+1));

        String url = statsBean.getQiconUrl();
        if (!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).resizeDimen(R.dimen.item_diamonds_list_img_width,
                    R.dimen.item_diamonds_list_img_height).into(holder.itemAmiImg);
        }else {
            holder.itemAmiImg.setImageResource(R.drawable.diamond_null);
        }
        holder.itemAmiText.setText(statsBean.getQtext());
        holder.itemAmiCount.setText(String.valueOf(statsBean.getGemCnt()));

        if(position == 0) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.gold_medal);

        } else if(position == 1) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.silver_medal);

        } else if(position == 2) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.bronze_medal);

        }else {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.shape_transparent);

        }

        return convertView;
    }

    private static class ViewHolder {
        TextView itemAmiIndex;
        RecyclerImageView itemAmiImg;
        TextView itemAmiText;
        TextView itemAmiCount;
    }
}