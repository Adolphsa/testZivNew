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
import com.yeejay.yplay.model.GetRecommendAll;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.userinfo.ActivityAllDiamond;
import com.yeejay.yplay.utils.FriendFeedsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

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
            holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
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
            Picasso.with(context).load(url).into(holder.itemAmiImg);
        }else {
            holder.itemAmiImg.setImageResource(R.drawable.diamond_null);
        }
        holder.itemAmiText.setText(statsBean.getQtext());
        holder.itemAmiCount.setText(String.valueOf(statsBean.getGemCnt()));

        if(position == 0) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.gold_medal);
            holder.itemAmiCount.setTextColor(context.getResources().getColor(R.color.gold_diamond_color));
        } else if(position == 1) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.silver_medal);
            holder.itemAmiCount.setTextColor(context.getResources().getColor(R.color.silver_diamond_color));
        } else if(position == 2) {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.bronze_medal);
            holder.itemAmiCount.setTextColor(context.getResources().getColor(R.color.brozne_diamond_color));
        } else {
            holder.itemAmiIndex.setBackgroundResource(R.drawable.normal_medal);
            holder.itemAmiCount.setTextColor(context.getResources().getColor(R.color.play_color2));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView itemAmiIndex;
        ImageView itemAmiImg;
        TextView itemAmiText;
        TextView itemAmiCount;
    }
}