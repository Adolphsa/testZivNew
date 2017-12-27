package com.yeejay.yplay.friend;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.adapter.SearchFriendsAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

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

public class ActivitySearchFriends extends BaseActivity {

    @BindView(R.id.asf_search_view)
    SearchView asfSearchView;
    @BindView(R.id.asf_list_view)
    ListView asfListView;
    @BindView(R.id.asf_btn_cancel)
    ImageButton asfBtnCancel;
    @BindView(R.id.asf_rl)
    RelativeLayout asfRl;
    @BindView(R.id.asf_tv_result)
    TextView asfTvResult;
    @BindView(R.id.asf_no_found_img)
    ImageView asfNoFoundImg;
    @BindView(R.id.asf_no_found)
    ImageView asfNoFound;

    @OnClick(R.id.asf_btn_cancel)
    public void back() {
        finish();
    }

    SearchFriendsAdapter searchFriendsAdapter;
    int myUin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivitySearchFriends.this, true);
        myUin = (int) SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_UIN, 0);

        initSearchView();
    }

    private void initSearchView() {
        asfSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("提交---" + query);

                String username = (String) SharePreferenceUtil.get(ActivitySearchFriends.this,
                        YPlayConstant.YPLAY_USER_NAME,
                        "");

                if (!TextUtils.isEmpty(username) && query.equals(username)){
                    DialogUtils.showInviteDialogInfo(ActivitySearchFriends.this,"不能搜索自己哦");
                }else {
                    searchFriends(query);
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initListView(final List<GetRecommendsRespond.PayloadBean.FriendsBean> tempList,String username) {

        if (tempList.size() == 0) {
            asfNoFound.setVisibility(View.VISIBLE);
            asfNoFoundImg.setVisibility(View.INVISIBLE);
            asfListView.setVisibility(View.INVISIBLE);
            return;
        }
        asfNoFound.setVisibility(View.INVISIBLE);
        asfNoFoundImg.setVisibility(View.INVISIBLE);
        asfListView.setVisibility(View.VISIBLE);
        searchFriendsAdapter = new SearchFriendsAdapter(ActivitySearchFriends.this,
                new SearchFriendsAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {
                        System.out.println("隐藏按钮被点击");
                        Button button = (Button) v;
                        //忽略好友请求，而不是删除
                        //removeFriend(tempList.get((int) button.getTag()).getUin());
                        button.setVisibility(View.INVISIBLE);
                        if (tempList.size() > 0) {
                            System.out.println("tempList---" + tempList.size() + "----" + (int) v.getTag());
                            tempList.remove((int) v.getTag());
                            searchFriendsAdapter.notifyDataSetChanged();
                        }

                    }
                }, new SearchFriendsAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("接受按钮被点击");
                Button button = (Button) v;
                int status = tempList.get((int) button.getTag()).getStatus();
                if (NetWorkUtil.isNetWorkAvailable(ActivitySearchFriends.this)){
                    if (status == 0){
                        button.setBackgroundResource(R.drawable.add_friend_apply);
                        //加好友
                        addFriend(tempList.get((int) button.getTag()).getUin());
                    }else if (status == 3){
                        button.setBackgroundResource(R.drawable.be_as_friends);
                        //接受
                        accepeAddFreind(tempList.get((int) button.getTag()).getMsgId(),0);
                    }
                    button.setEnabled(false);
                }else {
                    Toast.makeText(ActivitySearchFriends.this,"网络异常",Toast.LENGTH_SHORT).show();
                }



            }
        }, tempList, myUin,username);
        asfListView.setAdapter(searchFriendsAdapter);
    }

    //搜索好友
    private void searchFriends(final String username) {

        Map<String, Object> searchFriendsMap = new HashMap<>();
        searchFriendsMap.put("username", username);
        searchFriendsMap.put("uin", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_UIN, 0));
        searchFriendsMap.put("token", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        searchFriendsMap.put("ver", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .searchFriends(searchFriendsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetRecommendsRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GetRecommendsRespond getRecommendsRespond) {
                        System.out.println("搜索好友---" + getRecommendsRespond.toString());
                        if (getRecommendsRespond.getCode() == 0) {
                            initListView(getRecommendsRespond.getPayload().getFriends(),username);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("搜索好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //添加好友
    private void addFriend(int toUin) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("srcType",8);
        addFreindMap.put("uin", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .addFriend(addFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddFriendRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull AddFriendRespond addFriendRespond) {
                        System.out.println("搜索加好友请求---" + addFriendRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("搜索加好友请求异常---" + e.getMessage());
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
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_VER, 0));
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
                        System.out.println("搜索删除好友---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("搜索删除好友异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //接受好友请求
    private void accepeAddFreind(int msgId, int act) {
        Map<String, Object> accepeAddFreindMap = new HashMap<>();
        accepeAddFreindMap.put("msgId", msgId);
        accepeAddFreindMap.put("act",act);
        accepeAddFreindMap.put("uin", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_UIN, 0));
        accepeAddFreindMap.put("token", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        accepeAddFreindMap.put("ver", SharePreferenceUtil.get(ActivitySearchFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .acceptAddFriend(accepeAddFreindMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        System.out.println("接受好友请求---" + baseRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("接受好友请求异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白处隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (ActivitySearchFriends.this.getCurrentFocus() != null) {
                if (ActivitySearchFriends.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(ActivitySearchFriends.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
