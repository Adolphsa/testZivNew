package com.yeejay.yplay.friend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.MainActivity;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ContactsAdapter;
import com.yeejay.yplay.adapter.SchoolmateAdapter;
import com.yeejay.yplay.answer.ActivityInviteFriend;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CardDialog;
import com.yeejay.yplay.customview.MesureListView;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
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

public class AddFriends extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "AddFriends";
    private static final int REQUEST_CODE_PERMISSION_SINGLE_CONTACTS = 101;

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.searchView)
    TextView searchView;

    @BindView(R.id.friend_pll_refresh)
    PullToRefreshLayout pullToRefreshLayout;

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {

        if (isFromAddFriend){
            startActivity(new Intent(AddFriends.this, MainActivity.class));
        }

        finish();
    }

    @OnClick(R.id.searchView)
    public void clickSearch(View view) {
        startActivity(new Intent(AddFriends.this, ActivitySearchFriends.class));
    }

    @OnClick(R.id.af_btn_add_contacts)
    public void btnAddContacts() {
        //通讯录好友
        System.out.println("通讯录好友");

        if (contactRoot.isShown()) {
            contactRoot.setEnabled(false);
            schoolRoot.setEnabled(true);
            maybeRoot.setEnabled(true);
            return;
        }

        mPageNum = 1;
        mType = 1;

        contactRoot.setVisibility(View.VISIBLE);
        schoolRoot.setVisibility(View.GONE);
        maybeRoot.setVisibility(View.GONE);

        contactDredgeList.clear();

        if (contactRoot != null && contactRoot.isShown()) {
            getRecommends(1, mPageNum);
        }

    }

    @OnClick(R.id.af_btn_add_schoolmate)
    public void btnAddSchool() {

        //同校好友
        System.out.println("同校好友");

        if (schoolRoot.isShown()) {
            contactRoot.setEnabled(true);
            schoolRoot.setEnabled(false);
            maybeRoot.setEnabled(false);
            return;
        }

        mPageNum = 1;
        mType = 3;

        contactRoot.setVisibility(View.GONE);
        schoolRoot.setVisibility(View.VISIBLE);
        maybeRoot.setVisibility(View.GONE);

        allSchoolMateList.clear();

        if (schoolRoot != null && schoolRoot.isShown()) {
            getRecommends(3, mPageNum);
        }

    }

    @OnClick(R.id.af_btn_wait_invite)
    public void btnWaitInvite() {
        //可能认识的人
        System.out.println("可能认识的人");

        if (maybeRoot.isShown()) {
            contactRoot.setEnabled(true);
            schoolRoot.setEnabled(true);
            maybeRoot.setEnabled(false);
            return;
        }

        mPageNum = 1;
        mType = 7;

        contactRoot.setVisibility(View.GONE);
        schoolRoot.setVisibility(View.GONE);
        maybeRoot.setVisibility(View.VISIBLE);

        maybeKnowList.clear();

        if (maybeRoot != null && maybeRoot.isShown()) {
            getRecommends(7, mPageNum);
        }
    }

    LinearLayout contactRoot; //通讯录好友
    LinearLayout schoolRoot;    //同校同学
    LinearLayout maybeRoot;     //可能认识的人

    List<GetRecommendsRespond.PayloadBean.FriendsBean> contactDredgeList;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> allSchoolMateList;   //全部
    List<GetRecommendsRespond.PayloadBean.FriendsBean> sameGradeList;       //同年级
    List<GetRecommendsRespond.PayloadBean.FriendsBean> boyList;             //男
    List<GetRecommendsRespond.PayloadBean.FriendsBean> girlList;            //女
    List<GetRecommendsRespond.PayloadBean.FriendsBean> maybeKnowList;       //可能认识的人

    int mPageNum = 1;

    int mType = 1; //好友类型
    int buttonDirt = 1; //学校按钮朝向
    boolean isFromAddFriend;

    boolean numberBookAuthoritySuccess = false;
    ContactsInfoDao contactsInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(AddFriends.this, true);
        layoutTitle.setText("添加好友");

        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            isFromAddFriend = bundle.getBoolean("from_add_friend_guide");
        }


        contactRoot = (LinearLayout) findViewById(R.id.layout_contact);
        schoolRoot = (LinearLayout) findViewById(R.id.layout_school_mate);
        maybeRoot = (LinearLayout) findViewById(R.id.layout_maybe_know);


        contactDredgeList = new ArrayList<>();
        allSchoolMateList = new ArrayList<>();
        sameGradeList = new ArrayList<>();
        boyList = new ArrayList<>();
        girlList = new ArrayList<>();
        maybeKnowList = new ArrayList<>();

        initPullRefresh();

        getRecommends(1, 1);
    }

    private void initPullRefresh() {

        pullToRefreshLayout.setCanRefresh(false);
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                //刷新
            }

            @Override
            public void loadMore() {
                //加载更多
                if (mType == 1) {    //通讯录已开通
                    getRecommends(1, mPageNum);
                } else if (mType == 3) {
                    getRecommends(3, mPageNum);
                } else if (mType == 4) {
                    getRecommends(4, mPageNum);
                } else if (mType == 5) {
                    getRecommends(5, mPageNum);
                } else if (mType == 6) {
                    getRecommends(6, mPageNum);
                } else if (mType == 7) {
                    getRecommends(7, mPageNum);
                }
            }
        });

    }

    //接受好友请求
    private void accepeAddFreind(int msgId) {
        Map<String, Object> accepeAddFreindMap = new HashMap<>();
        accepeAddFreindMap.put("msgId", msgId);
        accepeAddFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        accepeAddFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        accepeAddFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //发送加好友的请求
    private void addFriend(int toUin) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        addFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        addFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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
                        System.out.println("发送加好友请求---" + addFriendRespond.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("发送加好友请求异常---" + e.getMessage());
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
        removeFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {
        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("friends", friends);
        removeFreindMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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

    //拉取同校/通讯录好友
    private void getRecommends(int type, int pageNum) {

        System.out.println("type---" + type);
        Map<String, Object> recommendsMap = new HashMap<>();
        recommendsMap.put("type", type);
        recommendsMap.put("pageNum", pageNum);
        recommendsMap.put("pageSize", 5);
        recommendsMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        recommendsMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        recommendsMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
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
                        if (getRecommendsRespond.getCode() == 0) {
                            System.out.println("好友列表---" + getRecommendsRespond.toString());
                            List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList =
                                    getRecommendsRespond.getPayload().getFriends();
                            if (friendsBeanList != null) {

                                if (mType == 1) { //通讯录已开通
                                    contactDredgeList.addAll(friendsBeanList);
                                    handleContactDredge(contactDredgeList);
                                } else if (mType == 3) {  //全部
                                    allSchoolMateList.addAll(friendsBeanList);
                                    handleSchoolMate(allSchoolMateList);
                                } else if (mType == 4) {  //同年级
                                    sameGradeList.addAll(friendsBeanList);
                                    handleSchoolMate(sameGradeList);
                                } else if (mType == 5) {      //男
                                    boyList.addAll(friendsBeanList);
                                    handleSchoolMate(boyList);
                                } else if (mType == 6) {      //女
                                    girlList.addAll(friendsBeanList);
                                    handleSchoolMate(girlList);
                                } else if (mType == 7) {      //可能认识的人
                                    maybeKnowList.addAll(friendsBeanList);
                                    handleMaybeKnowFriend(maybeKnowList);
                                }

                            }

                        }

                        pullToRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("拉取好友异常---" + e.getMessage());
                        Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        pullToRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("position---" + position);
        if (position == 4) {
            System.out.println("啦啦啦");
            startActivity(new Intent(AddFriends.this, ActivityAddFiendsDetail.class));
        }
    }

    //处理通讯录已开通
    private void handleContactDredge(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {

        LinearLayout nullLl = (LinearLayout) contactRoot.findViewById(R.id.lcn_ll_null);
        MesureListView dredgeListView = (MesureListView) contactRoot.findViewById(R.id.lcn_dredge_list);
        RelativeLayout dredgeNoRl = (RelativeLayout) contactRoot.findViewById(R.id.lcn_rl);
        dredgeNoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到邀请好友界面 判断权限
                getNumberBookAuthority();
            }
        });

        if (friendsBeanList.size() > 0) {
            mPageNum++;
            nullLl.setVisibility(View.GONE);
            dredgeListView.setAdapter(new ContactsAdapter(AddFriends.this,
                    new ContactsAdapter.hideCallback() {
                        @Override
                        public void hideClick(View v) {

                        }
                    },
                    new ContactsAdapter.acceptCallback() {
                        @Override
                        public void acceptClick(View v) {
                            if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                                Button button = (Button) v;
                                button.setBackgroundResource(R.drawable.already_apply);
                                button.setEnabled(false);

                                int position = (int) button.getTag();
                                addFriend(friendsBeanList.get(position).getUin());
                            } else {
                                Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                            }


                        }
                    },
                    friendsBeanList));

            dredgeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int uin = friendsBeanList.get(position).getUin();
                    if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                        getFriendInfo(uin);
                    } else {
                        Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }
    }

    //处理同校同学
    private void handleSchoolMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {

        final ImageButton arrowButton = (ImageButton) schoolRoot.findViewById(R.id.lsm_arrow);
        final LinearLayout llButton = (LinearLayout) schoolRoot.findViewById(R.id.lsm_ll_button);
        LinearLayout llNullView = (LinearLayout) schoolRoot.findViewById(R.id.lsm_ll_null);
        final ImageButton allImgButton = (ImageButton) schoolRoot.findViewById(R.id.lsm_all);
        final ImageButton sameGradeButton = (ImageButton) schoolRoot.findViewById(R.id.lsm_class);
        final ImageButton boyButton = (ImageButton) schoolRoot.findViewById(R.id.lsm_boy);
        final ImageButton girlButton = (ImageButton) schoolRoot.findViewById(R.id.lsm_girl);
        MesureListView allSchoolmateListView = (MesureListView) schoolRoot.findViewById(R.id.lsm_list);

        if (friendsBeanList.size() > 0) {
            mPageNum++;
            arrowButton.setVisibility(View.VISIBLE);
            llNullView.setVisibility(View.GONE);
        }

        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("箭头被点击");
                if (buttonDirt == 1) {
                    arrowButton.setImageResource(R.drawable.up_arrow);
                    buttonDirt = 2;
                    llButton.setVisibility(View.VISIBLE);
                } else if (buttonDirt == 2) {
                    arrowButton.setImageResource(R.drawable.down_arrow);
                    buttonDirt = 1;
                    llButton.setVisibility(View.GONE);
                }
            }
        });

        //全部
        allImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allImgButton.setImageResource(R.drawable.school_all_select);
                sameGradeButton.setImageResource(R.drawable.school_same_grade_unselect);
                boyButton.setImageResource(R.drawable.school_boy_unselect);
                girlButton.setImageResource(R.drawable.school_girl_unselect);

                mType = 3;
                mPageNum = 1;

                allSchoolMateList.clear();
                getRecommends(3, mPageNum);
            }
        });

        //同年级
        sameGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allImgButton.setImageResource(R.drawable.school_all_unselect);
                sameGradeButton.setImageResource(R.drawable.school_same_grade_select);
                boyButton.setImageResource(R.drawable.school_boy_unselect);
                girlButton.setImageResource(R.drawable.school_girl_unselect);


                mType = 4;
                mPageNum = 1;

                sameGradeList.clear();
                getRecommends(4, mPageNum);
            }
        });

        //男
        boyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allImgButton.setImageResource(R.drawable.school_all_unselect);
                sameGradeButton.setImageResource(R.drawable.school_same_grade_unselect);
                boyButton.setImageResource(R.drawable.school_boy_select);
                girlButton.setImageResource(R.drawable.school_girl_unselect);

                mType = 5;
                mPageNum = 1;
                boyList.clear();
                getRecommends(5, mPageNum);
            }
        });

        //女
        girlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allImgButton.setImageResource(R.drawable.school_all_unselect);
                sameGradeButton.setImageResource(R.drawable.school_same_grade_unselect);
                boyButton.setImageResource(R.drawable.school_boy_unselect);
                girlButton.setImageResource(R.drawable.school_gril_select);

                mType = 6;
                mPageNum = 1;
                girlList.clear();
                getRecommends(6, mPageNum);
            }
        });

        allSchoolmateListView.setAdapter(new SchoolmateAdapter(AddFriends.this,
                new SchoolmateAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {

                    }
                },
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.already_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();
                            addFriend(friendsBeanList.get(position).getUin());
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                friendsBeanList));

        allSchoolmateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int uin = friendsBeanList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    //处理可能认识的人
    private void handleMaybeKnowFriend(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        maybeRoot = (LinearLayout) findViewById(R.id.layout_maybe_know);
        LinearLayout lmklLlNull = (LinearLayout) maybeRoot.findViewById(R.id.lmk_ll_null);
        MesureListView lmkListView = (MesureListView) maybeRoot.findViewById(R.id.lmk_list);

        if (friendsBeanList.size() > 0) {
            mPageNum++;
            lmklLlNull.setVisibility(View.GONE);
        }

        lmkListView.setAdapter(new SchoolmateAdapter(AddFriends.this,
                new SchoolmateAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {

                    }
                },
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.already_apply);
                            button.setEnabled(false);

                            int position = (int) button.getTag();
                            addFriend(friendsBeanList.get(position).getUin());
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                friendsBeanList));

        lmkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("点击事件");
                int uin = friendsBeanList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //查询朋友的信息
    private void getFriendInfo(int friendUin) {
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("userUin", friendUin);
        friendMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        friendMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        friendMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUserInfo(friendMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserInfoResponde userInfoResponde) {
                        System.out.println("获取朋友资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0) {
                            UserInfoResponde.PayloadBean.InfoBean infoBean =
                                    userInfoResponde.getPayload().getInfo();
                            int status = userInfoResponde.getPayload().getStatus();
                            if (status == 1) {
                                Intent intent = new Intent(AddFriends.this, ActivityFriendsInfo.class);
                                intent.putExtra("yplay_friend_name", infoBean.getNickName());
                                intent.putExtra("yplay_friend_uin", infoBean.getUin());
                                System.out.println("朋友的uin---" + infoBean.getUin());
                                startActivity(intent);
                            } else {
                                showCardDialog(userInfoResponde.getPayload());
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("获取朋友资料异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //显示名片
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean) {

        final UserInfoResponde.PayloadBean.InfoBean infoBean = payloadBean.getInfo();

        //状态
        int status = payloadBean.getStatus();

        final CardDialog cardDialog = new CardDialog(AddFriends.this, R.style.CustomDialog);
        cardDialog.setCardImgStr(infoBean.getHeadImgUrl());
        cardDialog.setCardDiamondCountStr("钻石 " + String.valueOf(infoBean.getGemCnt()));
        cardDialog.setCardNameStr(infoBean.getNickName());
        cardDialog.setCardSchoolNameStr(infoBean.getSchoolName());
        cardDialog.setCardGradeStr(FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade()));

        if (status == 0) {
            cardDialog.setButtonImg(R.drawable.green_add_friend);

        } else if (status == 2) {
            cardDialog.setButtonImg(R.drawable.already_apply);
        }

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    button.setImageResource(R.drawable.already_apply);
                    addFriend(infoBean.getUin());
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardDialog.setCarDialogRlListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("哈哈哈");
                cardDialog.dismiss();
            }
        });
        cardDialog.show();
    }

    //获取通讯录权限
    private void getNumberBookAuthority() {

        AndPermission.with(AddFriends.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_CONTACTS)
                .permission(Permission.CONTACTS)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(AddFriends.this, rationale).show();
                    }
                })
                .start();
    }

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    Log.i(TAG, "onSucceed: 通讯录权限成功");
                    getContacts();
                    break;
            }

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    Log.i(TAG, "onFailed: 通讯录权限失败");
                    getContacts();
                    break;
            }

            if (numberBookAuthoritySuccess){
                Log.i(TAG, "onFailed: 读到通讯录权限了numberBookAuthoritySuccess---" + numberBookAuthoritySuccess);
            }else {
                if (AndPermission.hasAlwaysDeniedPermission(AddFriends.this, deniedPermissions)) {
                    if (requestCode == REQUEST_CODE_PERMISSION_SINGLE_CONTACTS) {
                        AndPermission.defaultSettingDialog(AddFriends.this, 400).show();
                    }

                }
            }
        }
    };

    private void getContacts() {

        if (Build.VERSION.SDK_INT >= 23
                && AddFriends.this.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && AddFriends.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            System.out.println("contactUri---" + contactUri);
            if (contactUri != null) {
                numberBookAuthoritySuccess = true;
                Log.i(TAG, "getContacts: 通讯录权限申请成功");

            }
            Cursor cursor = getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            //String contactSortKey;
            //int contactId;
            while (cursor != null && cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                com.yeejay.yplay.greendao.ContactsInfo contactsInfo = new com.yeejay.yplay.greendao.ContactsInfo(null,contactName,contactNumber);
                contactsInfoDao.insert(contactsInfo);
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

            //开启服务上传通讯录
//            startService(new Intent(AddFriends.this, ContactsService.class));

            //跳转到邀请好友界面
            Intent intent = new Intent(AddFriends.this, ActivityInviteFriend.class);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFromAddFriend){

            startActivity(new Intent(AddFriends.this, MainActivity.class));
            return true;//不执行父类点击事件
        }

        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
