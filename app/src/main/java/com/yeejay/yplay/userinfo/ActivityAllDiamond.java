package com.yeejay.yplay.userinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityAllDiamond extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.aad_list_view)
    ListView aadListView;
    @BindView(R.id.aad_ptf_refresh)
    PullToRefreshLayout aadPtfRefresh;

    @OnClick(R.id.layout_title_back)
    public void back(View view) {
        finish();
    }

    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> mDataList;

    int mPageNum = 1;
    int uin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diamond);
        ButterKnife.bind(this);

        layoutTitle.setText("所有钻石");
        mDataList = new ArrayList<>();
        uin = (int)SharePreferenceUtil.get(ActivityAllDiamond.this, YPlayConstant.YPLAY_UIN, 0);
        getUserDiamondInfo(uin,mPageNum,20);

    }

    private void initDiamondList(final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList){

        aadListView.setAdapter(new BaseAdapter() {
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
                ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(ActivityAllDiamond.this, R.layout.item_afi, null);
                    holder = new ViewHolder();
                    holder.itemAmiImg = (ImageView) convertView.findViewById(R.id.afi_item_img);
                    holder.itemAmiText = (TextView) convertView.findViewById(R.id.afi_item_text);
                    holder.itemAmiText2 = (TextView) convertView.findViewById(R.id.afi_item_text2);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                UsersDiamondInfoRespond.PayloadBean.StatsBean statsBean = tempList.get(position);
                String url = statsBean.getQiconUrl();
                if (!TextUtils.isEmpty(url)){
                    Picasso.with(ActivityAllDiamond.this).load(url).resize(30,30).into(holder.itemAmiImg);
                }
                holder.itemAmiText.setText(statsBean.getQtext());
                holder.itemAmiText2.setVisibility(View.VISIBLE);
                holder.itemAmiText2.setText("x" + statsBean.getGemCnt());
                return convertView;
            }
        });
        loadMore();
    }

    private void loadMore(){
        aadPtfRefresh.setCanRefresh(false);
        aadPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                mPageNum++;
                System.out.println("mPageNum---" + mPageNum);
                getUserDiamondInfo(uin,mPageNum,20);
            }
        });
    }

    //获取钻石信息
    private void getUserDiamondInfo(int userUin,int pageNum, int pageSize){
        Map<String, Object> diamondInfoMap = new HashMap<>();
        diamondInfoMap.put("userUin",userUin);
        diamondInfoMap.put("pageNum",pageNum);
        diamondInfoMap.put("pageSize",pageSize);
        diamondInfoMap.put("uin", SharePreferenceUtil.get(ActivityAllDiamond.this, YPlayConstant.YPLAY_UIN, 0));
        diamondInfoMap.put("token", SharePreferenceUtil.get(ActivityAllDiamond.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        diamondInfoMap.put("ver", SharePreferenceUtil.get(ActivityAllDiamond.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUsersDamonInfo(diamondInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UsersDiamondInfoRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UsersDiamondInfoRespond usersDiamondInfoRespond) {
                        System.out.println("获取用户钻石信息---" + usersDiamondInfoRespond.toString());
                        if (usersDiamondInfoRespond.getCode() == 0){
                            mDataList.addAll(usersDiamondInfoRespond.getPayload().getStats());
                            if (mDataList.size() > 0){
                                int total = usersDiamondInfoRespond.getPayload().getTotal();
                                layoutTitle.setText("所有钻石" + total);
                                initDiamondList(mDataList);
                            }

                        }
                        aadPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取用户钻石信息异常---" + e.getMessage());
                        aadPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class ViewHolder {
        ImageView itemAmiImg;
        TextView itemAmiText;
        TextView itemAmiText2;
    }
}
