package com.yeejay.yplay.contribute;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.ContributeOfflineRefusedAdapter;
import com.yeejay.yplay.adapter.ContributeOfflineReviewingAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.customview.MyLinearLayoutManager;
import com.yeejay.yplay.message.ActivityChatWindow;
import com.yeejay.yplay.model.SubmitQueryListRespond;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 已上线的投稿情况
 * Created by xjg on 2018/01/15
 */

public class FragmentConOffline extends BaseFragment implements
                                            ContributeOfflineReviewingAdapter.OnRecycleImageListener{
    @BindView(R.id.message_null)
    ImageView nullView;
    @BindView(R.id.message_refresh_view)
    PullToRefreshLayout refreshLayout;
    @BindView(R.id.recycle_view_reviewing)
    SwipeMenuRecyclerView recyclerViewReviewing;
    @BindView(R.id.recycle_view_refused)
    SwipeMenuRecyclerView recyclerViewRefused;
    @BindView(R.id.reviewing_title)
    TextView reviewingTitle;
    @BindView(R.id.refused_title)
    TextView refusedTitle;

    private static final String TAG = "FragmentConOffline";
    private static final int STATUS_REVIEWING = 0;
    private static final int STATUS_REFUSE = 2;

    private int mPageNum = 1;
    private int mPageSize = 10;
    private List<SubmitQueryListRespond.PayloadBean.ContributesBean> mReviewingList =  new ArrayList<>();
    private List<SubmitQueryListRespond.PayloadBean.ContributesBean> mRefusedList =  new ArrayList<>();
    private ContributeOfflineReviewingAdapter reviewingAdapter;
    private ContributeOfflineRefusedAdapter refusedAdapter;
    private MyLinearLayoutManager reviewingLtManager;
    private MyLinearLayoutManager refusedLtManager;
    private LoadMoreView loadMoreView;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_contribute_offline;
    }


    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        Log.i(TAG, "initAllMembersView: ");
        initControlsAndAdapter();
    }

    private void initControlsAndAdapter() {
        reviewingLtManager = new MyLinearLayoutManager(getActivity());
        refusedLtManager = new MyLinearLayoutManager(getActivity());

        //设置recyclerView的分割线；
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getDrawable(R.drawable.shape_divider_item_contribute_list));

        //审核中的adapter初始化;
        reviewingAdapter = new ContributeOfflineReviewingAdapter(getActivity(),
                this, mReviewingList);
        recyclerViewReviewing.setLayoutManager(reviewingLtManager);
        recyclerViewReviewing.setAdapter(reviewingAdapter);
        recyclerViewReviewing.addItemDecoration(divider);

        //审核未通过的adapter初始化;
        refusedAdapter = new ContributeOfflineRefusedAdapter(getActivity(), this, mRefusedList);
        recyclerViewRefused.setLayoutManager(refusedLtManager);
        recyclerViewRefused.setAdapter(refusedAdapter);
        recyclerViewRefused.addItemDecoration(divider);

        //底部刷新
        loadMoreView = new LoadMoreView(getActivity());
        refreshLayout.setFooterView(loadMoreView);
        refreshLayout.setCanRefresh(false);
        refreshLayout.setCanLoadMore(false);
        /*refreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
            }

            @Override
            public void loadMore() {
                //因为未上线接口一次性就将所有数据返回，因此实际底部刷新时不需要重新拉取数据；
                refreshLayout.finishLoadMore();
            }
        });*/

        //因为未上线投稿数据时一次性拉取完毕，只需在这里拉取一次即可；
        getContributeOfflineList();
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);
        //nothing to do;
    }

    @Override
    public void OnRecycleImageClick(View v, Object o) {
        switch (v.getId()){
            case R.id.cora_item_end_img :
                Toast.makeText(getActivity(), R.string.contribute_offline_reviewing_warning,
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.contri_refused_item_end_img :
                //处理未通过审核item中点击三个点，显示更多操作的逻辑
                handleClickMoreBtn(v, (int)o);
                break;

            default:
        }
    }

    private void handleClickMoreBtn(View v, final int position) {
        View parentView = (View)v.getParent().getParent();
        if (parentView != null) {
            final LinearLayout ll = (LinearLayout)parentView.findViewById(R.id.ll_refused_more);
            if (ll == null) {
                return;
            }

            if (ll.isShown()) {
                ll.setVisibility(View.GONE);
            } else if (!ll.isShown()) {
                ll.setVisibility(View.VISIBLE);
                TextView deleteView = (TextView)ll.findViewById(R.id.refused_delete);
                deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除当前未通过审核的item；
                        showImageBottomDialog(position, ll);
                    }
                });

                TextView editView = (TextView)ll.findViewById(R.id.refused_edit);
                editView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重新编辑当前未通过审核的item；
                        Intent intent = new Intent(getActivity(), ActivityContributeReedit.class);
                        intent.putExtra("selected_con_qtext", mRefusedList.get(position).getQtext());
                        intent.putExtra("selected_con_qiconurl", mRefusedList.get(position).getQiconUrl());
                        intent.putExtra("selected_con_position", position);
                        startActivityForResult(intent, 6);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.getInstance().debug("requestCode = " + String.valueOf(requestCode) +
                " , resultCode = " + String.valueOf(resultCode));
        if (requestCode == 6 && resultCode == 6) {
            //因为重新提交了未通过审核的项，因此这里需要重新拉一遍数据；
            //先删除该项，再重新拉数据;
            deleteContributeItem(data.getIntExtra("position", 0));
        }
    }

    //查询未上线的投稿列表,一次性将全部数据拉取完毕
    public void getContributeOfflineList() {
        Log.d(TAG, "getContributeOfflineList(), uin = " +
                SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        Map<String, Object> contributesMap = new HashMap<>();
        contributesMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        contributesMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contributesMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .getContributeOfflineList(contributesMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitQueryListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull SubmitQueryListRespond contributesListRespond) {
                        System.out.println("查询未上线投稿列表---" + contributesListRespond.toString());
                        if (contributesListRespond.getCode() == 0) {

                            List<SubmitQueryListRespond.PayloadBean.ContributesBean> contributesBeanList =
                                    contributesListRespond.getPayload().getContributesInfo();
                            if (contributesBeanList != null && contributesBeanList.size() > 0) {
                                //有数据时，隐藏空图片：
                                nullView.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);

                                for(int i = 0; i < contributesBeanList.size(); i++) {
                                    if (contributesBeanList.get(i).getStatus() == STATUS_REVIEWING) {
                                        mReviewingList.add(contributesBeanList.get(i));
                                    } else if (contributesBeanList.get(i).getStatus() == STATUS_REFUSE) {
                                        mRefusedList.add(contributesBeanList.get(i));
                                    }
                                }

                                //如果有正在审核中的数据，则显示，否则隐藏审核中的部分;
                                if(mReviewingList.size() > 0) {
                                    Log.d(TAG, "getContributeOfflineList(), mReviewingList.size()"
                                            + mReviewingList.size());
                                    reviewingTitle.setVisibility(View.VISIBLE);
                                    recyclerViewReviewing.setVisibility(View.VISIBLE);

                                    //通知审核中的adapter刷新UI；
                                    reviewingAdapter.notifyDataSetChanged();
                                } else {
                                    reviewingTitle.setVisibility(View.GONE);
                                    recyclerViewReviewing.setVisibility(View.GONE);
                                }

                                //如果有未通过审核的数据，则显示，否则隐藏未通过审核的部分；
                                if (mRefusedList.size() > 0) {
                                    Log.d(TAG, "getContributeOfflineList(), mRefusedList.size()"
                                            + mRefusedList.size());
                                    refusedTitle.setVisibility(View.VISIBLE);
                                    recyclerViewRefused.setVisibility(View.VISIBLE);

                                    //通知未通过审核的adapter刷新UI；
                                    refusedAdapter.notifyDataSetChanged();
                                } else {
                                    refusedTitle.setVisibility(View.GONE);
                                    recyclerViewRefused.setVisibility(View.GONE);
                                }

                                refreshLayout.finishLoadMore();
                            } else {
                                //如果没有任何数据，则显示一个空图片
                                refreshLayout.setVisibility(View.GONE);
                                nullView.setVisibility(View.VISIBLE);
                            }
                        }else{
                            //todo失败的处理
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("查询未上线投稿列表---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("查询未上线投稿列表---onComplete, mReviewingList.size() = " +
                                mReviewingList.size() + " , mRefusedList.size() = " + mRefusedList.size());
                    }
                });
    }

    //删除重新编辑的审核未通过投稿;
    private void deleteContributeItem(final int postition) {
        Log.d(TAG, "deleteContributeItem(), 删除重新编辑的未通过item的postition = " + postition
                + " submitId = " + mRefusedList.get(postition).getSubmitId());
        Map<String, Object> contributesMap = new HashMap<>();
        contributesMap.put("submitId", mRefusedList.get(postition).getSubmitId());
        contributesMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        contributesMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contributesMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .deleteContributeItem(contributesMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitQueryListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull SubmitQueryListRespond contributesListRespond) {
                        System.out.println("删除重新编辑的未通过项---" + contributesListRespond.toString());
                        if (contributesListRespond.getCode() == 0) {

                            //删除item中的扩展项;
                            View itemView = recyclerViewRefused.getChildAt(postition);
                            if (itemView != null) {
                                LinearLayout ll = (LinearLayout) itemView.findViewById(R.id.ll_refused_more);
                                if (ll != null && ll.isShown()) {
                                    ll.setVisibility(View.GONE);
                                }
                            }

                            mRefusedList.remove(postition);
                            refusedAdapter.notifyDataSetChanged();

                            //清空数据后，重新拉取一次数据；
                            mReviewingList.clear();
                            mRefusedList.clear();
                            getContributeOfflineList();
                        }else{
                            //todo失败的处理
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("删除重新编辑的未通过项---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("删除重新编辑的未通过项---onComplete, mReviewingList.size() = " +
                                mReviewingList.size() + " , mRefusedList.size() = " + mRefusedList.size());
                    }
                });
    }

    //删除所选择的审核未通过投稿;
    private void deleteContributeItem(final int postition, final LinearLayout ll) {
        Log.d(TAG, "deleteContributeItem(), 删除未审核通过item的submitId = " +
                mRefusedList.get(postition).getSubmitId());
        Map<String, Object> contributesMap = new HashMap<>();
        contributesMap.put("submitId", mRefusedList.get(postition).getSubmitId());
        contributesMap.put("uin", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_UIN, 0));
        contributesMap.put("token", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        contributesMap.put("ver", SharePreferenceUtil.get(getActivity(), YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .deleteContributeItem(contributesMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitQueryListRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull SubmitQueryListRespond contributesListRespond) {
                        System.out.println("删除指定投稿列表项---" + contributesListRespond.toString());
                        if (contributesListRespond.getCode() == 0) {
                            //删除指定item，更新UI;
                            ll.setVisibility(View.GONE);
                            mRefusedList.remove(postition);
                            refusedAdapter.notifyDataSetChanged();
                        }else{
                            //todo失败的处理
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("删除指定投稿列表项---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("删除指定投稿列表项---onComplete, mReviewingList.size() = " +
                                mReviewingList.size() + " , mRefusedList.size() = " + mRefusedList.size());
                    }
                });
    }

    //显示底部对话框
    private void showImageBottomDialog(final int position, final LinearLayout ll) {
        Log.d(TAG, "showImageBottomDialog(), position = " + position);
        final Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_contribute_offline_refused, null);
        bottomDialog.setContentView(contentView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_delete:
                        deleteContributeItem(position, ll);
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_cancel:
                        bottomDialog.dismiss();
                        break;
                }
            }
        };

        Button deleteBt = (Button) contentView.findViewById(R.id.message_delete);
        Button cancelBt = (Button) contentView.findViewById(R.id.message_cancel);
        deleteBt.setOnClickListener(onClickListener);
        cancelBt.setOnClickListener(onClickListener);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(getActivity(), 16f);
        params.bottomMargin = DensityUtil.dp2px(getActivity(), 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }
}