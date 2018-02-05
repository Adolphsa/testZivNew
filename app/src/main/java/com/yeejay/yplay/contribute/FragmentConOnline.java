package com.yeejay.yplay.contribute;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ContributeOnlineAdapter;
import com.yeejay.yplay.base.BaseFragment;
import com.yeejay.yplay.customview.HeadRefreshView;
import com.yeejay.yplay.customview.MyLinearLayoutManager;
import com.yeejay.yplay.model.SubmitQueryDetailRespond;
import com.yeejay.yplay.model.SubmitQueryListRespond;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 已上线的投稿情况
 * Created by xjg on 2018/01/15
 */

public class FragmentConOnline extends BaseFragment implements
                                        ContributeOnlineAdapter.OnRecycleImageListener{
    @BindView(R.id.message_null)
    ImageView nullView;
    @BindView(R.id.message_refresh_view)
    PullToRefreshLayout refreshLayout;
    @BindView(R.id.message_recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private static final String TAG = "FragmentConOnline";

    private static final int STATUS_REVIEWED = 1;

    private int mPageNum = 1;
    private int mPageSize = 10;
    //已上线的投稿列表
    public List<SubmitQueryListRespond.PayloadBean.ContributesBean> mReviewedList =  new ArrayList<>();
    //已上线的投稿列表item中扩展部分展示的详情列表;
    private List<SubmitQueryDetailRespond.PayloadBean.InfosBean> mQueryDetailList =  new ArrayList<>();
    public ContributeOnlineAdapter reviewedAdapter;
    private MyLinearLayoutManager reviewedLtManager;
    private HeadRefreshView headRefreshView;
    private Context mContext;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_contribute_online;
    }


    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        LogUtils.getInstance().debug("initAllMembersView: ");
        mContext = getActivity();
        initControlsAndAdapter();
    }

    private void initControlsAndAdapter() {
        reviewedLtManager = new MyLinearLayoutManager(YplayApplication.getContext());

        //设置recyclerView的分割线；
        DividerItemDecoration divider = new DividerItemDecoration(YplayApplication.getContext(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getDrawable(R.drawable.shape_divider_item_contribute_list));

        //审核中的adapter初始化;
        reviewedAdapter = new ContributeOnlineAdapter(YplayApplication.getContext(),this, mReviewedList);
        recyclerView.setLayoutManager(reviewedLtManager);
        recyclerView.setAdapter(reviewedAdapter);
        recyclerView.addItemDecoration(divider);

        //顶部刷新
        headRefreshView = new HeadRefreshView(mContext);
        refreshLayout.setHeaderView(headRefreshView);
        refreshLayout.setCanLoadMore(false);
        refreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                //拉取已上线投稿信息时,采用顶部刷新的方式，因为未读信息在顶部显示;
                getContributeList(STATUS_REVIEWED, ++mPageNum, mPageSize);
            }

            @Override
            public void loadMore() {
            }
        });

        getContributeList(STATUS_REVIEWED, mPageNum, mPageSize);
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        super.onVisibilityChangedToUser(isVisibleToUser, isHappenedInSetUserVisibleHintMethod);

    }

    @Override
    public void OnRecycleImageClick(View v, Object o) {
        switch (v.getId()){
            case R.id.cora_item_rl:
                //更改箭头图标，扩展时箭头向下，收起时箭头向上；
                View parentView = (View) v.getParent();
                ImageView arrowView = (ImageView) v.findViewById(R.id.arrow);
                if (parentView != null) {
                    LinearLayout ll = (LinearLayout)parentView.findViewById(R.id.expand_ll);
                    if(ll != null) {
                        if(ll.isShown()) {
                            arrowView.setImageResource(R.drawable.contribute_down_arrow);
                            ll.setVisibility(View.GONE);
                        } else if (!ll.isShown()){
                            arrowView.setImageResource(R.drawable.contribute_up_arrow);
                            ll.setVisibility(View.VISIBLE);
                            //扩展布局展开后，向里面加入拉取的数据进行显示；
                            handleLoadQueryDetailList(ll, (int)o);
                        }
                    }
                }
                break;

            default:
        }
    }

    private void handleLoadQueryDetailList(LinearLayout ll, final int position) {
        //查询已经上线的题目投票详情;
        mQueryDetailList.clear();
        getSubmitQueryDetail(mReviewedList.get(position).getQid(), ll);
    }

    private void initQueryDetailList(final List<SubmitQueryDetailRespond.PayloadBean.InfosBean> tempList,
                                    final ListView listView){
        listView.setAdapter(new BaseAdapter() {
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
                FragmentConOnline.ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_contribute_online_subitem, null);
                    holder = new FragmentConOnline.ViewHolder();
                    holder.itemHeadImg = (ImageView) convertView.findViewById(R.id.contribute_item_header_img);
                    holder.itemTitle = (TextView) convertView.findViewById(R.id.af_item_name);
                    holder.itemMsg = (TextView) convertView.findViewById(R.id.af_item_tv_shares_friends);
                    holder.itemDiamondNum = (TextView) convertView.findViewById(R.id.diamon_num);

                    convertView.setTag(holder);
                } else {
                    holder = (FragmentConOnline.ViewHolder) convertView.getTag();
                }
                SubmitQueryDetailRespond.PayloadBean.InfosBean statsBean = tempList.get(position);

                if (!TextUtils.isEmpty(statsBean.getHeadImgUrl())){
                    Picasso.with(context).load(statsBean.getHeadImgUrl()).resizeDimen(R.dimen.item_my_friends_width,
                            R.dimen.item_my_friends_height).into(holder.itemHeadImg);
                }else {
                    holder.itemHeadImg.setImageResource(R.drawable.header_deafult);
                }

                String genderStr = (statsBean.getGender() == 1 ? "男生" : "女生");
                holder.itemTitle.setText(statsBean.getNickName());
                holder.itemMsg.setText(FriendFeedsUtil.schoolType(statsBean.getSchoolType(), statsBean.getGrade())
                        + genderStr);
                holder.itemDiamondNum.setText(String.valueOf(statsBean.getVotedCnt()));

                return convertView;
            }
        });
    }

    //查询已经上线的题目投票详情;
    private void getSubmitQueryDetail(int qid, final LinearLayout ll) {
        Map<String, Object> contributesMap = new HashMap<>();
        contributesMap.put("qid", qid);
        contributesMap.put("uin", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_UIN, 0));
        contributesMap.put("token", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_TOKEN, "yplay"));
        contributesMap.put("ver", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_QUERYDETAIL, contributesMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetSubmitQueryDetailResponse(result, ll);
                        LogUtils.getInstance().debug("已经上线的题目投票详情---onComplete, mQueryDetailList.size() = {}",
                                mQueryDetailList.size());
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("已经上线的题目投票详情查询异常");
                    }
                });
    }

    private void handleGetSubmitQueryDetailResponse(String result, final LinearLayout ll) {
        final ListView listView = (ListView) ll.findViewById(R.id.expand_list);
        final TextView totalView = (TextView) ll.findViewById(R.id.expand_note);

        SubmitQueryDetailRespond submitQueryDetailRespond = GsonUtil.GsonToBean(result, SubmitQueryDetailRespond.class);
        if (submitQueryDetailRespond.getCode() == 0) {

            List<SubmitQueryDetailRespond.PayloadBean.InfosBean> contributesBeanList =
                    submitQueryDetailRespond.getPayload().getInfos();
            if(contributesBeanList != null && contributesBeanList.size() > 0) {
                LogUtils.getInstance().debug("已经上线的题目投票详情, {}",
                        contributesBeanList.toString());
                totalView.setText(String.valueOf(submitQueryDetailRespond.getPayload().getTotal()) +
                        mContext.getResources().getString(R.string.contribute_answer_num_note_msg1));
                listView.setVisibility(View.VISIBLE);

                mQueryDetailList.addAll(contributesBeanList);
                initQueryDetailList(mQueryDetailList, listView);

            } else {
                //没有拉取到数据
                totalView.setText(R.string.same_school_grade_no_submit);
            }
        } else {
            //todo失败的处理
        }
    }

    //查询所有类型的投稿列表
    public void getContributeList(int type, int pageNum, int pageSize) {
        LogUtils.getInstance().debug("getContributeList(), type = {}, pageNum = {}, pageSize = {}",
                type, pageNum, pageSize);
        Map<String, Object> contributesMap = new HashMap<>();
        contributesMap.put("type", type);
        contributesMap.put("pageNum", pageNum);
        contributesMap.put("pageSize", pageSize);
        contributesMap.put("uin", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_UIN, 0));
        contributesMap.put("token", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_TOKEN, "yplay"));
        contributesMap.put("ver", SharePreferenceUtil.get(mContext, YPlayConstant.YPLAY_VER, 0));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_QUERYLIST, contributesMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        handleGetContributeListResponse(result);
                        LogUtils.getInstance().debug("查询投稿列表---onComplete, mReviewedList.size() = {}",
                                mReviewedList.size());
                    }

                    @Override
                    public void onTimeOut() {
                    }

                    @Override
                    public void onError() {
                        LogUtils.getInstance().debug("查询投稿列表异常");
                        refreshLayout.finishRefresh();
                    }
                });
    }

    private void handleGetContributeListResponse(String result) {
        SubmitQueryListRespond contributesListRespond = GsonUtil.GsonToBean(result, SubmitQueryListRespond.class);
        LogUtils.getInstance().debug("查询投稿列表, {}", contributesListRespond.toString());
        if (contributesListRespond.getCode() == 0) {

            List<SubmitQueryListRespond.PayloadBean.ContributesBean> contributesBeanList =
                    contributesListRespond.getPayload().getContributesInfo();
            if (contributesBeanList != null && contributesBeanList.size() > 0) {
                //顶部刷新，因此是将数据添加到list的头部
                mReviewedList.addAll(0, contributesBeanList);
                LogUtils.getInstance().debug("查询投稿列表, contributesBeanList.size() = {}, mReviewedList.size() = {}"
                        , contributesBeanList.size(), mReviewedList.size());

                //拉取审核通过的投稿存入list后更新UI;
                if (nullView.getVisibility() == View.VISIBLE) {
                    nullView.setVisibility(View.GONE);
                }
                //refreshLayout.setVisibility(View.VISIBLE);

                reviewedAdapter.notifyDataSetChanged();
            } else {
                //拉取不到数据了
                headRefreshView.noData();

                LogUtils.getInstance().debug("查询投稿列表---contributesBeanList.size() = 0");
                if (mReviewedList.size() <= 0) {
                    //refreshLayout.setVisibility(View.GONE);
                    nullView.setVisibility(View.VISIBLE);
                }
            }

            refreshLayout.finishRefresh();
        }else{
            //todo失败的处理
        }
    }

    private static class ViewHolder {
        ImageView itemHeadImg;
        TextView itemTitle;
        TextView itemMsg;
        TextView itemDiamondNum;
    }
}