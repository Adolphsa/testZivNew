package com.yeejay.yplay.friend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.WaitInviteAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.utils.GsonUtil;
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

public class ActivityWaitInvite extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.aafd_searchView)
    SearchView aafdSearchView;
    @BindView(R.id.aafd_list_view)
    ListView aafdListView;
    @BindView(R.id.aafd_ptf_refresh)
    PullToRefreshLayout aafdPtfRefresh;

    @OnClick(R.id.layout_title_back)
    public void back(View view) {
        finish();
    }

    WaitInviteAdapter waitInviteAdapter;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> mDataList;
    int mPageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activty_add_fiends_detail);
        ButterKnife.bind(this);

        layoutTitle.setText("等待邀请");
        mDataList = new ArrayList<>();
        getRecommends(2,mPageNum);
        loadMore();
    }

    private void initWaiteInviteListView(final List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList) {
        waitInviteAdapter = new WaitInviteAdapter(ActivityWaitInvite.this,
                new WaitInviteAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {
                        System.out.println("隐藏按钮被点击");
                        Button button = (Button) v;
                        removeFriend(tempList.get((int) button.getTag()).getUin());
                        button.setVisibility(View.INVISIBLE);
                        if (tempList.size() > 0) {
                            System.out.println("tempList---" + tempList.size() + "----" + (int) v.getTag());
                            tempList.remove((int) v.getTag());
                            waitInviteAdapter.notifyDataSetChanged();
                        }

                    }
                }, new WaitInviteAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("邀请按钮被点击");
                Button button = (Button) v;
                button.setText("已邀请");
                button.setEnabled(false);
                //邀请好友的请求

                String phone = GsonUtil.GsonString(tempList.get((int) v.getTag()).getPhone());
                System.out.println("邀请的电话---" + phone);
                String base64phone = Base64.encodeToString(phone.getBytes(), Base64.DEFAULT);
                invitefriendsbysms(base64phone);
            }
        }, tempList);
        aafdListView.setAdapter(waitInviteAdapter);
    }

    //加载更多
    private void loadMore(){
        aafdPtfRefresh.setCanRefresh(false);
        aafdPtfRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {

            }

            @Override
            public void loadMore() {
                mPageNum++;
                System.out.println("pageNum---" + mPageNum);
                getRecommends(2,mPageNum);
            }
        });
    }

    //拉取等待邀请
    private void getRecommends(final int type,int pageNum) {

        Map<String, Object> recommendsMap = new HashMap<>();
        recommendsMap.put("type", type);
        recommendsMap.put("pageNum", pageNum);
        recommendsMap.put("uin", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_UIN, 0));
        recommendsMap.put("token", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        recommendsMap.put("ver", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getSchoolmates(recommendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
                        System.out.println("等待邀请---" + getRecommendsRespond.toString());
                        if (getRecommendsRespond.getCode() == 0) {
                            List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList = getRecommendsRespond.getPayload().getFriends();
                            mDataList.addAll(tempList);
                            initWaiteInviteListView(mDataList);
                        }
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("等待邀请异常---" + e.getMessage());
                        aafdPtfRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {
        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("friends", friends);
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .removeFriend(removeFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("短信邀请好友---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("短信邀请好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //删除好友
    private void removeFriend(int toUin) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("toUin", toUin);
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityWaitInvite.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .removeFriend(removeFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("删除好友---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("删除好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
