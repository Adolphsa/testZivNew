package com.yeejay.yplay.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tangxiaolv.com.library.EffectiveShapeView;

/**
 * 钻石榜单dialog
 * Created by xjg on 2017/12/23.
 */

public class DiamondsListDialog extends Dialog {

    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> mDataList;

    private final static int PAGE_NUM = 1;
    private final static int PAGE_SIZE = 3;
    private int mUin;

    ImageView backView;
    ListView aadListView;

    private Context context;

    public DiamondsListDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public DiamondsListDialog(@NonNull Context context, int themeResId,
                         int uin) {
        super(context, themeResId);
        this.context = context;
        this.mUin = uin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_layout_diamonds_list);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        backView = (ImageView) findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        aadListView = (ListView) findViewById(R.id.aad_list_view);
        mDataList = new ArrayList<>();
        getUserDiamondInfo(mUin, PAGE_NUM, PAGE_SIZE);
    }

    private void initDiamondList(final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList){

        aadListView.setAdapter(new BaseAdapter() {
            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public int getCount() {
                return tempList.size();
            }

            @Override
            public Object getItem(int position) {
                return tempList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                DiamondsListDialog.ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_diamond_details, null);
                    holder = new DiamondsListDialog.ViewHolder();
                    holder.itemAmiIndex = (TextView) convertView.findViewById(R.id.item_diamond_sort);
                    holder.itemAmiMsg = (TextView) convertView.findViewById(R.id.item_diamond_msg);
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.item_diamond_lable);

                    convertView.setTag(holder);
                } else {
                    holder = (DiamondsListDialog.ViewHolder) convertView.getTag();
                }
                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean = tempList.get(position);

                holder.itemAmiIndex.setText(String.valueOf(position+1));

                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(context).load(url).resizeDimen(R.dimen.card_info_diamond_width,
                            R.dimen.card_info_diamond_height).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(holder.itemAmiImg);
                }else {
                    holder.itemAmiImg.setImageResource(R.drawable.diamond_null);
                }
                holder.itemAmiMsg.setText(statsBean.getQtext());

                if(position == 0) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.gold_medal);
                } else if (position == 1) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.silver_medal);
                } else if (position == 2) {
                    holder.itemAmiIndex.setBackgroundResource(R.drawable.bronze_medal);
                }

                return convertView;
            }
        });
    }

    //获取钻石信息
    private void getUserDiamondInfo(int userUin,int pageNum, int pageSize){
        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin",userUin);
        diamondInfoMap.put("pageNum",pageNum);
        diamondInfoMap.put("pageSize",pageSize);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(context, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUsersDamonInfo(diamondInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UsersDiamondInfoRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull UsersDiamondInfoRespond usersDiamondInfoRespond) {
                        System.out.println("get diamonds' info---" + usersDiamondInfoRespond.toString());
                        if (usersDiamondInfoRespond.getCode() == 0){
                            List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = usersDiamondInfoRespond.getPayload().getStats();
                            System.out.println("List<>---" + tempList.size());
                            if (tempList.size() > 0 && tempList.size() < 4){
                                mDataList.addAll(tempList);
                                int total = usersDiamondInfoRespond.getPayload().getTotal();
                                initDiamondList(mDataList);
                            }else {
                                System.out.println("data load completely!");
                            }

                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("error while getting diamonds' info ---" + e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class ViewHolder {
        TextView itemAmiIndex;
        TextView itemAmiMsg;
        ImageView itemAmiImg;
    }

}