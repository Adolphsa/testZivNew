package com.yeejay.yplay.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.AllDiamondsAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.model.UsersDiamondInfoRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
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

public class ActivityAllDiamond extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.aad_list_view)
    ListView aadListView;
    @BindView(R.id.aad_ptf_refresh)
    PullToRefreshLayout aadPtfRefresh;
    private LoadMoreView loadMoreView;
    @BindView(R.id.emptyview)
    View emptyView;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    List<UsersDiamondInfoRespond.PayloadBean.StatsBean> mDataList;

    int mPageNum = 1;
    int mPageSize = 15;
    int uin;
    private AllDiamondsAdapter diamondsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diamond);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityAllDiamond.this, true);

        layoutTitle.setText(R.string.diamonds_list);
        mDataList = new ArrayList<>();

        initAdapter();

        uin = (int)SharePreferenceUtil.get(ActivityAllDiamond.this, YPlayConstant.YPLAY_UIN, 0);
        getUserDiamondInfo(uin,mPageNum,mPageSize);

        loadMore();


    }

    private void initAdapter() {
        diamondsAdapter = new AllDiamondsAdapter(this, mDataList);
        aadListView.setEmptyView(emptyView);
        aadListView.setAdapter(diamondsAdapter);
    }

    private void initDiamondList(final List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList){
        if (tempList.size() > 0) {
            diamondsAdapter.notifyDataSetChanged();
            //拉到第二页数据时，自动向上滚动两个item高度（如果第二页只有一个数据的话，则只滚动一个item高度）
            //ListView需要先调用notifyDataSetChanged()再滚动
            if (tempList.size() >= 2) {
                aadListView.smoothScrollToPosition(mDataList.size() - tempList.size() + 1);
            } else if (tempList.size() == 1) {
                aadListView.smoothScrollToPosition(mDataList.size() - tempList.size());
            }
        }
    }

    private void loadMore(){

        aadPtfRefresh.setCanRefresh(false);
        loadMoreView = new LoadMoreView(ActivityAllDiamond.this);
        aadPtfRefresh.setFooterView(loadMoreView);
        aadPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {}

            @Override
            public void loadMore() {

                System.out.println("mPageNum---" + mPageNum);
                getUserDiamondInfo(uin,mPageNum,mPageSize);
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
                            List<UsersDiamondInfoRespond.PayloadBean.StatsBean> tempList = usersDiamondInfoRespond.getPayload().getStats();
                            System.out.println("List<>---" + tempList.size());
                            if (tempList.size() > 0){
                                mPageNum++;
                                mDataList.addAll(tempList);
                                int total = usersDiamondInfoRespond.getPayload().getTotal();
                                initDiamondList(tempList);
                            }else {
                                System.out.println("数据加载完毕");
                                loadMoreView.noData();
                            }

                        } else {
                            //服务器获取信息失败
                            aadListView.setAdapter(null);
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
        TextView itemAmiIndex;
        ImageView itemAmiImg;
        TextView itemAmiText;
        TextView itemAmiCount;
    }
}
