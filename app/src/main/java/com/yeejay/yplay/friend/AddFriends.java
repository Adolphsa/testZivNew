package com.yeejay.yplay.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.ContactsAdapter;
import com.yeejay.yplay.adapter.SchoolmateAdapter;
import com.yeejay.yplay.adapter.WaitInviteAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CardBigDialog;
import com.yeejay.yplay.customview.LoadMoreView;
import com.yeejay.yplay.customview.SideView;
import com.yeejay.yplay.customview.SpinerPopWindow;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.model.AddFriendRespond;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.GetRecommendsRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.Arrays;
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

public class AddFriends extends BaseActivity implements AdapterView.OnItemClickListener,
        WaitInviteAdapter.OnGetAlphaIndexerAndSectionsListener {

    private static final String TAG = "AddFriends";

    private static final int CLASSMATE_TYPE_ALL = 0;
    private static final int CLASSMATE_MALE = 1;
    private static final int CLASSMATE_FEMALE = 2;
    private static final int CLASSMATE_SAME_GRADE = 3;

    @BindView(R.id.af_btn_add_contacts)
    ImageButton btnAddContacts;
    @BindView(R.id.af_btn_add_schoolmate)
    ImageButton btnAddSchollMate;
    @BindView(R.id.af_btn_wait_invite)
    ImageButton btnAddWaitInvite;
    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;
    @BindView(R.id.searchView)
    TextView searchView;
//    @BindView(R.id.aafd_scroll_view)
//    ScrollView scrollView;


    //    @BindView(R.id.friend_pll_refresh)
//    PullToRefreshLayout pullToRefreshLayout;
    @BindView(R.id.filter_text)
    TextView filterText;

    @OnClick(R.id.filter_text)
    public void filterClick() {
        Drawable drawable = getResources().getDrawable(R.drawable.spinner_down);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        filterText.setCompoundDrawables(null, null, drawable, null);

        mSpinerPopWindow.setWidth(filterText.getWidth());
        mSpinerPopWindow.showAsDropDown(filterText);
    }

    @OnClick(R.id.layout_title_back2)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.searchView)
    public void clickSearch(View view) {
        startActivity(new Intent(AddFriends.this, ActivitySearchFriends.class));
    }

    @OnClick(R.id.af_btn_add_contacts)
    public void btnAddContacts() {
        btnAddContacts.setImageResource(R.drawable.add_friends_contacts_icon_selected);
        btnAddSchollMate.setImageResource(R.drawable.add_friends_classmates_icon_unselected);
        btnAddWaitInvite.setImageResource(R.drawable.add_friends__wait_invite_icon_unselected);

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

//        positionList.clear();
//        contactDredgeList.clear();
//
//        if (contactRoot != null && contactRoot.isShown()) {
//            getRecommends(1, mPageNum);
//        }

    }

    @OnClick(R.id.af_btn_add_schoolmate)
    public void btnAddSchool() {
        btnAddContacts.setImageResource(R.drawable.add_friends_contacts_icon_unselected);
        btnAddSchollMate.setImageResource(R.drawable.add_friends_classmates_icon_selected);
        btnAddWaitInvite.setImageResource(R.drawable.add_friends__wait_invite_icon_unselected);

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

        positionList.clear();
        allSchoolMateList.clear();

        if (schoolRoot != null && schoolRoot.isShown()) {
            getRecommends(3, mPageNum);
        }

    }

    @OnClick(R.id.af_btn_wait_invite)
    public void btnWaitInvite() {
        btnAddContacts.setImageResource(R.drawable.add_friends_contacts_icon_unselected);
        btnAddSchollMate.setImageResource(R.drawable.add_friends_classmates_icon_unselected);
        btnAddWaitInvite.setImageResource(R.drawable.add_friends__wait_invite_icon_selected);

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

        positionList.clear();
        maybeKnowList.clear();

        if (maybeRoot != null && maybeRoot.isShown()) {
            getRecommends(7, mPageNum);
        }
    }

    private LoadMoreView loadMoreView;
    private RelativeLayout contactRoot; //通讯录好友
    private LinearLayout nullLl;
//    private RecyclerView dredgeListView;
//    private RelativeLayout dredgeNoRl;
    private RecyclerView notOpenListView; //未开通好友
    private SideView sideView;              //字母表


    WaitInviteAdapter waitInviteAdapter;
    List<ContactsInfo> allContactsList; //所有联系人的集合

    List<ContactsInfo> notOpenList;   //未开通的联系人
    private Map<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private List<String> sections;// 存放存在的汉语拼音首字母
    private ContactsInfoDao contactsInfoDao;
    int moffset = 0;


    private LinearLayout schoolRoot;    //同校同学
    private LinearLayout llNullView;
    private RecyclerView allSchoolmateListView;

    private LinearLayout maybeRoot;     //可能认识的人
    private LinearLayout lmklLlNull;
    private RecyclerView lmkListView;

    List<ContactsInfo> contactDredgeList;
    List<GetRecommendsRespond.PayloadBean.FriendsBean> allSchoolMateList;   //全部
    List<GetRecommendsRespond.PayloadBean.FriendsBean> sameGradeList;       //同年级
    List<GetRecommendsRespond.PayloadBean.FriendsBean> boyList;             //男
    List<GetRecommendsRespond.PayloadBean.FriendsBean> girlList;            //女
    List<GetRecommendsRespond.PayloadBean.FriendsBean> maybeKnowList;       //可能认识的人

    int mPageNum = 1;

    int mType = 1; //好友类型
    int buttonDirt = 1; //学校按钮朝向

    ContactsAdapter contactsAdapter;
    SchoolmateAdapter schoolmateAdapter;//全部同学
    SchoolmateAdapter sameGradeAdapter;//同年级
    SchoolmateAdapter boyAdapter;//男同学
    SchoolmateAdapter girlAdapter;//女同学
    SchoolmateAdapter maybeKnownAdapter;
    List<Integer> positionList;

    private SpinerPopWindow<String> mSpinerPopWindow;
    private List<String> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(AddFriends.this, true);
        layoutTitle.setText("添加好友");
        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();

        initViewControls();
        initListAndAdapter();

        initPullRefresh();
        initClassmatesTypePop();

        getRecommends(1, 1);

    }

    private void initViewControls() {

        loadMoreView = new LoadMoreView(this);
        //通讯录联系人相关的UI控件
        contactRoot = (RelativeLayout) findViewById(R.id.layout_contact);
        nullLl = (LinearLayout) contactRoot.findViewById(R.id.lcn_ll_null);
//        dredgeListView = (RecyclerView) contactRoot.findViewById(R.id.lcn_dredge_list);
//        dredgeNoRl = (RelativeLayout) contactRoot.findViewById(R.id.lcn_rl);
        notOpenListView = (RecyclerView) findViewById(R.id.lcn_not_open_list);
        sideView = (SideView) findViewById(R.id.lcn_side_view);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        dredgeListView.setNestedScrollingEnabled(false);
//        dredgeListView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        notOpenListView.setNestedScrollingEnabled(false);
        notOpenListView.setLayoutManager(linearLayoutManager2);

        /*dredgeNoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //邀请好友开通
                //跳转到邀请好友界面
                Intent intent = new Intent(AddFriends.this, ActivityInviteFriend.class);
                startActivity(intent);

            }
        });*/

        //同校同学相关的UI控件
        schoolRoot = (LinearLayout) findViewById(R.id.layout_school_mate);
        llNullView = (LinearLayout) schoolRoot.findViewById(R.id.lsm_ll_null);
        allSchoolmateListView = (RecyclerView) schoolRoot.findViewById(R.id.lsm_list);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        allSchoolmateListView.setNestedScrollingEnabled(false);
        allSchoolmateListView.setLayoutManager(linearLayoutManager3);

        allSchoolmateListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isbootom = allSchoolmateListView.canScrollVertically(1); //false表示已经到底部
                if (!isbootom) {
                    Log.i(TAG, "onScrolled: 滚动到底部了");
                    mPageNum++;
                    if (mType == 3) {    //同校所有
                        getRecommends(3, mPageNum);
                    } else if (mType == 4) {    //同校同年级
                        getRecommends(4, mPageNum);
                    } else if (mType == 5) {    //同校男生
                        getRecommends(5, mPageNum);
                    } else if (mType == 6) {    //同校女生
                        getRecommends(6, mPageNum);
                    }
                }
            }
        });

        //可能认识的人相关的UI控件
        maybeRoot = (LinearLayout) findViewById(R.id.layout_maybe_know);
        lmklLlNull = (LinearLayout) maybeRoot.findViewById(R.id.lmk_ll_null);
        lmkListView = (RecyclerView) maybeRoot.findViewById(R.id.lmk_list);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(this);
        lmkListView.setNestedScrollingEnabled(false);
        lmkListView.setLayoutManager(linearLayoutManager4);

        lmkListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isbootom = lmkListView.canScrollVertically(1); //false表示已经到底部
                if (!isbootom) {
                    Log.i(TAG, "onScrolled: 滚动到底部了");
                    mPageNum++;
                    if (mType == 7) {
                        getRecommends(7, mPageNum);
                    }
                }
            }
        });

    }

    private void initListAndAdapter() {
        contactDredgeList = new ArrayList<>();
        notOpenList = new ArrayList<>();
        allContactsList = new ArrayList<>();
        allSchoolMateList = new ArrayList<>();
        sameGradeList = new ArrayList<>();
        boyList = new ArrayList<>();
        girlList = new ArrayList<>();
        maybeKnowList = new ArrayList<>();
        positionList = new ArrayList();
        
        initContactsAdapter();
        initAllSchoolMateAdapter();
        initSameGradeAdapter();
        initBoyAdapter();
        initGirlAdapter();
        initMaybeKnownAdapter();

    }

    //通讯录
    private void initContactsAdapter() {

        //查询通讯录已开通
        String phoneNumber = (String) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_PHONE_NUMBER, "");
        contactDredgeList = contactsInfoDao.queryBuilder()
                .where(ContactsInfoDao.Properties.Uin.gt(1000))
                .where(ContactsInfoDao.Properties.Phone.notEq(phoneNumber))
                .list();
        if (contactDredgeList != null && contactDredgeList.size() > 0){
            nullLl.setVisibility(View.GONE);
            allContactsList.add(new ContactsInfo(null,"已开通好友",null,null,2,null,null,null));
            allContactsList.addAll(contactDredgeList);
        }

        //查询通讯录未开通
        notOpenList = queryNotOpenContactsData();

        if (notOpenList == null || notOpenList.size() == 0) {
            Log.i(TAG, "initNotOpenContactsAdapter: null");
            sideView.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "initNotOpenContactsAdapter: not null");

            allContactsList.add(new ContactsInfo(null,"未开通好友",null,null,3,null,null,null));
            allContactsList.addAll(notOpenList);

            sideView.setVisibility(View.VISIBLE);
            sideView.setOnTouchingLetterChangedListener(new notOpenSideViewListener());
        }

        waitInviteAdapter = new WaitInviteAdapter(AddFriends.this, new WaitInviteAdapter.hideCallback() {
            @Override
            public void hideClick(View v) {

            }
        }, new WaitInviteAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    Button button = (Button) v;
                    button.setBackgroundResource(R.drawable.friend_invitation_done);
                    button.setEnabled(false);

                    String phone = GsonUtil.GsonString(notOpenList.get((int) v.getTag()).getPhone());
                    System.out.println("邀请的电话---" + phone);
                    String phoneStr = "[" + phone + "]";
                    String base64phone = Base64.encodeToString(phoneStr.getBytes(), Base64.DEFAULT);
                    Log.i(TAG, "acceptClick: base64phone---" + base64phone);
                    invitefriendsbysms(base64phone);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

            }
        }, allContactsList);

        waitInviteAdapter.setOnGetAlphaIndeserAndSectionListener(this);
        waitInviteAdapter.addRecycleItemListener(new WaitInviteAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = allContactsList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        notOpenListView.setAdapter(waitInviteAdapter);
    }


    private void initAllSchoolMateAdapter() {
        Log.d(TAG, "initAllSchoolMateAdapter(), allSchoolMateList = " + allSchoolMateList.toString());
        //全部同学
        schoolmateAdapter = new SchoolmateAdapter(AddFriends.this,
                null,
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();
                            Log.i(TAG, "acceptClick: mType---" + mType);
                            positionList.add(position);
                            addFriend(allSchoolMateList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                allSchoolMateList, positionList);
    }

    private void initSameGradeAdapter() {
        Log.d(TAG, "initSameGradeAdapter(), sameGradeList = " + sameGradeList.toString());
        //同年级
        sameGradeAdapter = new SchoolmateAdapter(AddFriends.this,
                null,
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();
                            Log.i(TAG, "acceptClick: mType---" + mType);
                            positionList.add(position);
                            addFriend(sameGradeList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                sameGradeList, positionList);
    }

    private void initBoyAdapter() {
        Log.d(TAG, "initBoyAdapter(), boyList = " + boyList.toString());
        //男同学
        boyAdapter = new SchoolmateAdapter(AddFriends.this,
                null,
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();
                            Log.i(TAG, "acceptClick: mType---" + mType);
                            positionList.add(position);
                            addFriend(boyList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                boyList, positionList);
    }

    private void initGirlAdapter() {
        Log.d(TAG, "initGirlAdapter(), girlList = " + girlList.toString());
        //女同学
        girlAdapter = new SchoolmateAdapter(AddFriends.this,
                null,
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);
                            int position = (int) button.getTag();
                            Log.i(TAG, "acceptClick: mType---" + mType);
                            positionList.add(position);
                            addFriend(girlList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                girlList, positionList);
    }

    private void initMaybeKnownAdapter() {
        //可能认识的人；
        maybeKnownAdapter = new SchoolmateAdapter(AddFriends.this,
                null,
                new SchoolmateAdapter.acceptCallback() {
                    @Override
                    public void acceptClick(View v) {
                        if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                            Button button = (Button) v;
                            button.setBackgroundResource(R.drawable.add_friend_apply);
                            button.setEnabled(false);

                            int position = (int) button.getTag();
                            positionList.add(position);
                            addFriend(maybeKnowList.get(position).getUin(), mType);
                        } else {
                            Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                maybeKnowList, positionList);

        maybeKnownAdapter.addRecycleItemListener(new SchoolmateAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = maybeKnowList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lmkListView.setAdapter(maybeKnownAdapter);
    }

    private void initClassmatesTypePop() {
        final List<String> typeList = Arrays.asList(getResources().getStringArray(R.array.classmates_type));
        SharedPreferences sharedFilter = getSharedPreferences("preferences_class_filter",
                Context.MODE_PRIVATE);
        int selectOpn = sharedFilter.getInt("position", 0);
        filterText.setText(typeList.get(selectOpn));
        mSpinerPopWindow = new SpinerPopWindow<String>(this, this, typeList,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences sharedFilterItem = getSharedPreferences("preferences_class_filter",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorsettings = sharedFilterItem.edit();
                        editorsettings.putInt("position", position);
                        editorsettings.commit();

                        filterText.setText(typeList.get(position));
                        switch (position) {
                            case CLASSMATE_TYPE_ALL://全部
                                mType = 3;
                                mPageNum = 1;
                                positionList.clear();
                                allSchoolMateList.clear();
                                getRecommends(3, mPageNum);
                                break;
                            case CLASSMATE_MALE://男同学
                                mType = 5;
                                mPageNum = 1;
                                positionList.clear();
                                boyList.clear();
                                getRecommends(5, mPageNum);
                                break;
                            case CLASSMATE_FEMALE://女同学
                                mType = 6;
                                mPageNum = 1;
                                positionList.clear();
                                girlList.clear();
                                getRecommends(6, mPageNum);
                                break;
                            case CLASSMATE_SAME_GRADE://同年级
                                mType = 4;
                                mPageNum = 1;
                                positionList.clear();
                                sameGradeList.clear();
                                getRecommends(4, mPageNum);
                                break;
                            default:
                        }

                        mSpinerPopWindow.dismiss();
                    }
                });
        mSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable drawable = getResources().getDrawable(R.drawable.spinner_normal);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
                filterText.setCompoundDrawables(null, null, drawable, null);
            }
        });
    }

    private void initPullRefresh() {

//        pullToRefreshLayout.setCanRefresh(false);
//        pullToRefreshLayout.setFooterView(loadMoreView);
//        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
//            @Override
//            public void refresh() {
//                //刷新
//            }
//
//            @Override
//            public void loadMore() {
//                //加载更多
//                mPageNum++;
//                Log.i(TAG, "loadMore: pageNum---" + mPageNum);
//
//                if (mType == 1) {    //通讯录已开通
//                    getRecommends(1, mPageNum);
//                } else if (mType == 3) {    //同校所有
//                    getRecommends(3, mPageNum);
//                } else if (mType == 4) {    //同校同年级
//                    getRecommends(4, mPageNum);
//                } else if (mType == 5) {    //同校男生
//                    getRecommends(5, mPageNum);
//                } else if (mType == 6) {    //同校女生
//                    getRecommends(6, mPageNum);
//                } else if (mType == 7) {    //可能认识的人
//                    getRecommends(7, mPageNum);
//                }
//            }
//        });

    }

    //发送加好友的请求
    private void addFriend(int toUin, int srcType) {
        Map<String, Object> addFreindMap = new HashMap<>();
        addFreindMap.put("toUin", toUin);
        addFreindMap.put("srcType", srcType);
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

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {

        Log.i(TAG, "invitefriendsbysms: friends---" + friends);
        Map<String, Object> invitefriendsMap = new HashMap<>();
        invitefriendsMap.put("friends", friends);
        invitefriendsMap.put("uin", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_UIN, 0));
        invitefriendsMap.put("token", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        invitefriendsMap.put("ver", SharePreferenceUtil.get(AddFriends.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .smsInviteFriends(invitefriendsMap)
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
        recommendsMap.put("pageSize", 20);
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
                            if (friendsBeanList != null && friendsBeanList.size() > 0) {

//                                if (mType == 1) { //通讯录已开通
//                                    contactDredgeList.addAll(friendsBeanList);
//                                    handleContactDredge(friendsBeanList);
//                                } else

                                if (mType == 3) {  //全部
                                    allSchoolMateList.addAll(friendsBeanList);
                                    handleSchoolMate(friendsBeanList);
                                } else if (mType == 4) {  //同年级
                                    sameGradeList.addAll(friendsBeanList);
                                    handleSameGradeMate(friendsBeanList);
                                } else if (mType == 5) {      //男
                                    boyList.addAll(friendsBeanList);
                                    handleBoyMate(friendsBeanList);
                                } else if (mType == 6) {      //女
                                    girlList.addAll(friendsBeanList);
                                    handleGirlMate(friendsBeanList);
                                } else if (mType == 7) {      //可能认识的人
                                    maybeKnowList.addAll(friendsBeanList);
                                    handleMaybeKnowFriend(friendsBeanList);
                                }

                            } else {
                                //针对同校同学的处理（拉第一把数据就没有时，要如此处理是因为四个同学类型公用一个ListView）
                                if (mType == 3) {//全部同学
                                    if (allSchoolMateList.size() == 0) {
                                        allSchoolmateListView.setAdapter(null);
                                        schoolmateAdapter.notifyDataSetChanged();

                                        llNullView.setVisibility(View.VISIBLE);
                                    }
                                } else if (mType == 4) {//同年级
                                    if (sameGradeList.size() == 0) {
                                        allSchoolmateListView.setAdapter(null);
                                        sameGradeAdapter.notifyDataSetChanged();

                                        llNullView.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (mType == 5) {//男生
                                    if (boyList.size() == 0) {
                                        allSchoolmateListView.setAdapter(null);
                                        boyAdapter.notifyDataSetChanged();

                                        llNullView.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (mType == 6) {//女生
                                    if (girlList.size() == 0) {
                                        allSchoolmateListView.setAdapter(null);
                                        girlAdapter.notifyDataSetChanged();

                                        llNullView.setVisibility(View.VISIBLE);
                                    }
                                }
                                //网络获取不到数据了；
                                loadMoreView.noData();
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("拉取好友异常---" + e.getMessage());
                        Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
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
        if (friendsBeanList.size() > 0) {
            nullLl.setVisibility(View.GONE);
        }
        contactsAdapter.notifyDataSetChanged();
    }

    //处理同校同学
    private void handleSchoolMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleSchoolMate(), allSchoolMateList.size() = " + allSchoolMateList.size()
                + allSchoolMateList.toString());
        if (friendsBeanList.size() > 0) {
            filterText.setVisibility(View.VISIBLE);
            llNullView.setVisibility(View.GONE);
        }
        schoolmateAdapter.addRecycleItemListener(new SchoolmateAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = allSchoolMateList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        allSchoolmateListView.setAdapter(schoolmateAdapter);
//        schoolmateAdapter.notifyDataSetChanged();
    }

    //处理同年级同学
    private void handleSameGradeMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleSameGradeMate(), sameGradeList.size() = " + sameGradeList.size()
                + sameGradeList.toString());
        if (friendsBeanList.size() > 0) {
            filterText.setVisibility(View.VISIBLE);
            llNullView.setVisibility(View.GONE);
        }

        sameGradeAdapter.addRecycleItemListener(new SchoolmateAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = sameGradeList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        allSchoolmateListView.setAdapter(sameGradeAdapter);
//        sameGradeAdapter.notifyDataSetChanged();
    }

    //处理男同学
    private void handleBoyMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleBoyMate(), boyList.size() = " + boyList.size()
                + boyList.toString());
        if (friendsBeanList.size() > 0) {
            filterText.setVisibility(View.VISIBLE);
            llNullView.setVisibility(View.GONE);
        }

        boyAdapter.addRecycleItemListener(new SchoolmateAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = boyList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        allSchoolmateListView.setAdapter(boyAdapter);
//        boyAdapter.notifyDataSetChanged();
    }

    //处理女同学
    private void handleGirlMate(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleGirlMate(), girlList.size() = " + girlList.size()
                + girlList.toString());
        if (friendsBeanList.size() > 0) {
            filterText.setVisibility(View.VISIBLE);
            llNullView.setVisibility(View.GONE);
        }

        girlAdapter.addRecycleItemListener(new SchoolmateAdapter.OnRecycleItemListener() {
            @Override
            public void onRecycleItemClick(View v, Object o) {
                int position = (int) o;
                int uin = girlList.get(position).getUin();
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    getFriendInfo(uin, v);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        allSchoolmateListView.setAdapter(girlAdapter);

//        girlAdapter.notifyDataSetChanged();
    }


    //处理可能认识的人
    private void handleMaybeKnowFriend(final List<GetRecommendsRespond.PayloadBean.FriendsBean> friendsBeanList) {
        Log.d(TAG, ", handleMaybeKnowFriend(), maybeKnowList.size() = " + maybeKnowList.size()
                + " , friendsBeanList.size() = " + friendsBeanList.size() + " , mPageNum = " + mPageNum);
        if (friendsBeanList.size() > 0) {
            lmklLlNull.setVisibility(View.GONE);
        }

        maybeKnownAdapter.notifyDataSetChanged();
    }

    //查询朋友的信息
    private void getFriendInfo(int friendUin, View view) {
        final View friendItemView = view;
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
                                showCardDialog(userInfoResponde.getPayload(), friendItemView);
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
    private void showCardDialog(UserInfoResponde.PayloadBean payloadBean, View view) {

        final View friendItemView = view;
        final UserInfoResponde.PayloadBean.InfoBean infoBean = payloadBean.getInfo();

        //状态
        int status = payloadBean.getStatus();

        final CardBigDialog cardDialog = new CardBigDialog(AddFriends.this, R.style.CustomDialog,
                payloadBean);

        cardDialog.setAddFriendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView button = (ImageView) v;
                if (NetWorkUtil.isNetWorkAvailable(AddFriends.this)) {
                    button.setImageResource(R.drawable.peer_friend_requested);
                    //除了更新朋友选项卡信息中的按钮状态外，还要更新外部对应的朋友列表item的按钮状态；
                    if (friendItemView != null) {
                        Button freiendIcon = (Button) friendItemView.findViewById(R.id.af_btn_accept2);
                        if (freiendIcon != null) {
                            freiendIcon.setBackgroundResource(R.drawable.add_friend_apply);
                        }
                    }
                    addFriend(infoBean.getUin(), mType);
                } else {
                    Toast.makeText(AddFriends.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardDialog.show();
    }

    @Override
    public void getAlphaIndexerAndSectionsListner(Map<String, Integer> alphaIndexer, List<String> sections) {
        this.alphaIndexer = alphaIndexer;
        this.sections = sections;
    }

    private class notOpenSideViewListener implements SideView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            if (alphaIndexer.get(s) != null) {//判断当前选中的字母是否存在集合中
                int position = alphaIndexer.get(s);//如果存在集合中则取出集合中该字母对应所在的位置,再利用对应的setSelection，就可以实现点击选中相应字母，然后联系人就会定位到相应的位置
//                notOpenListView.scrollToPosition(position);
                LinearLayoutManager llm = (LinearLayoutManager) notOpenListView.getLayoutManager();
                llm.scrollToPositionWithOffset(position, 0);
            }
        }
    }


    private List<ContactsInfo> queryNotOpenContactsData() {
        return contactsInfoDao.queryBuilder()
                .where(ContactsInfoDao.Properties.Uin.eq(0))
                .orderAsc(ContactsInfoDao.Properties.SortKey)
                .list();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && isFromAddFriend) {
//
//            startActivity(new Intent(AddFriends.this, MainActivity.class));
//            return true;//不执行父类点击事件
//        }
//        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
//    }
}
