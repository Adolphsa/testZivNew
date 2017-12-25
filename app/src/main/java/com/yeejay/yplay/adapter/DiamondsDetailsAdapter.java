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
import com.yeejay.yplay.utils.FriendFeedsUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 添加钻石榜单金银铜排行榜的适配器
 * Created by xjg on 2017/12/22.
 */

public class DiamondsDetailsAdapter extends BaseAdapter{

    private Context context;

    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> diamondsInfoList;

    public DiamondsDetailsAdapter(Context context,
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
            convertView = View.inflate(context, R.layout.item_diamond_details, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String qiconUrl = diamondsInfoList.get(position).getQiconUrl();
        if (!TextUtils.isEmpty(qiconUrl)){
            Picasso.with(context).load(qiconUrl).into(holder.topListImg);
        }else {
            holder.topListImg.setImageResource(R.drawable.diamond_null);
        }

        String qtext = diamondsInfoList.get(position).getQtext();
        holder.topListMsg.setText(qtext);


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.item_diamond_sort)
        EffectiveShapeView topListIcon;
        @BindView(R.id.item_diamond_msg)
        TextView topListMsg;
        @BindView(R.id.item_diamond_lable)
        ImageView topListImg;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}