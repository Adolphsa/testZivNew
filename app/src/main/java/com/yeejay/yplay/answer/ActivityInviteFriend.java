package com.yeejay.yplay.answer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.adapter.WaitInviteAdapter;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.SideView;
import com.yeejay.yplay.greendao.ContactInvite;
import com.yeejay.yplay.greendao.ContactInviteDao;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.greendao.MyInfo;
import com.yeejay.yplay.greendao.MyInfoDao;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.GsonUtil;
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

public class ActivityInviteFriend extends BaseActivity implements WaitInviteAdapter.OnGetAlphaIndexerAndSectionsListener {

    @BindView(R.id.aif_back)
    ImageButton aifBack;
    //    @BindView(R.id.aif_tv_search_view)
//    TextView aifTvSearchView;
    @BindView(R.id.aif_list_view)
    RecyclerView aifListView;
    @BindView(R.id.aif_tip_close)
    ImageButton aifTipClose;
    @BindView(R.id.aif_tip_ll)
    LinearLayout aifTipLl;
    @BindView(R.id.emptyview)
    View emptyView;
    @BindView(R.id.aif_side_veiw)
    SideView aifSideView;   //字母表

    private static final String TAG = "ActivityInviteFriend";

    WaitInviteAdapter waitInviteAdapter;
    List<ContactsInfo> mDataList;   //所有联系人的集合
    List<ContactInvite> contactAlreadyInviteList;
    private Map<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private List<String> sections;// 存放存在的汉语拼音首字母

    private int uin;
    private MyInfoDao myInfoDao;
    private MyInfo myInfo;
    private ContactsInfoDao contactsInfoDao;
    private ContactInviteDao contactInviteDao;

    @OnClick(R.id.aif_tip_close)
    public void tipClose() {
        aifTipLl.setVisibility(View.GONE);
        myInfo.setIsInviteTipShow(1);
        myInfoDao.update(myInfo);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivityInviteFriend.this, true);
        myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();
        contactInviteDao = YplayApplication.getInstance().getDaoSession().getContactInviteDao();

        uin = (int) SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_UIN, (int) 0);
        myInfo = myInfoDao.queryBuilder()
                .where(MyInfoDao.Properties.Uin.eq(uin))
                .build().unique();
        if (myInfo != null){
            int isInviteTipShow = myInfo.getIsInviteTipShow();
            if (0 == isInviteTipShow) {
                aifTipLl.setVisibility(View.VISIBLE);
            }
        }

        mDataList = new ArrayList<>();
        contactAlreadyInviteList = new ArrayList<>();

        init();

        aifSideView.setOnTouchingLetterChangedListener(new SideListViewListener());
    }

    @OnClick(R.id.aif_back)
    public void back(View view) {
        finish();
    }

    private void init() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityInviteFriend.this);
        aifListView.setLayoutManager(linearLayoutManager);


        mDataList = contactsInfoDao.queryBuilder()
                .where(ContactsInfoDao.Properties.Uin.eq(0))
                .orderAsc(ContactsInfoDao.Properties.SortKey)
                .list();
        contactAlreadyInviteList = contactInviteDao.queryBuilder()
                .where(ContactInviteDao.Properties.Uin.eq(String.valueOf(uin)))
                .list();

        if (mDataList == null || mDataList.size() == 0){
            Log.i(TAG, "init: mDataList为空");
            aifSideView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else {
            Log.i(TAG, "init: mDataList不为空---" + mDataList.size());
            aifSideView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        waitInviteAdapter = new WaitInviteAdapter(ActivityInviteFriend.this,
                new WaitInviteAdapter.hideCallback() {
                    @Override
                    public void hideClick(View v) {
                        System.out.println("隐藏按钮被点击");
                        Button button = (Button) v;
                        removeFriend(mDataList.get((int) button.getTag()).getUin());
                        button.setVisibility(View.INVISIBLE);
                        if (mDataList.size() > 0) {
                            System.out.println("tempList---" + mDataList.size() + "----" + (int) v.getTag());
                            mDataList.remove((int) v.getTag());
                            waitInviteAdapter.notifyDataSetChanged();
                        }

                    }
                }, new WaitInviteAdapter.acceptCallback() {
            @Override
            public void acceptClick(View v) {
                System.out.println("邀请按钮被点击");
                if (NetWorkUtil.isNetWorkAvailable(ActivityInviteFriend.this)){
                    Log.i(TAG, "acceptClick: 11111");
                    Button button = (Button) v;
                    button.setBackgroundResource(R.drawable.friend_invitation_done);
                    button.setEnabled(false);

                    MyInfoDao myInfoDao = YplayApplication.getInstance().getDaoSession().getMyInfoDao();
                    int uin = (int) SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_UIN, (int) 0);
                    MyInfo myInfo = myInfoDao.queryBuilder().where(MyInfoDao.Properties.Uin.eq(uin))
                            .build().unique();

                    if (myInfo != null){
                        int IsShowInviteDialogInfo = myInfo.getIsShowInviteDialogInfo();
                        if (IsShowInviteDialogInfo == 0){
                            myInfo.setIsShowInviteDialogInfo(1);
                            myInfoDao.update(myInfo);
                            DialogUtils.showInviteDialogInfo(ActivityInviteFriend.this,
                                    getString(R.string.aif_message_invite_text));
                        }

                    }else {
                        Log.i(TAG, "acceptClick: myinfo is null");
                    }

                    if (myInfo != null){
                        //邀请好友的请求

                        String phone = GsonUtil.GsonString(mDataList.get((int) v.getTag()).getPhone());
                        System.out.println("邀请的电话---" + phone);
                        if (!TextUtils.isEmpty(phone)){
                            contactInviteDao.insert(new ContactInvite(null,String.valueOf(uin),mDataList.get((int) v.getTag()).getPhone()));
                        }
                        String phoneStr = "[" + phone + "]";
                        String base64phone = Base64.encodeToString(phoneStr.getBytes(), Base64.DEFAULT);
                        Log.i(TAG, "acceptClick: base64phone---" + base64phone);
                        invitefriendsbysms(base64phone);
                    }
                }else {
                    Toast.makeText(ActivityInviteFriend.this,"网络异常",Toast.LENGTH_SHORT).show();
                }

            }
        }, mDataList,contactAlreadyInviteList,null);

        waitInviteAdapter.setOnGetAlphaIndeserAndSectionListener(this);

//        aifListView.setEmptyView(emptyView);
        aifListView.setAdapter(waitInviteAdapter);
    }

    //通过短信邀请好友
    private void invitefriendsbysms(String friends) {

        Log.i(TAG, "invitefriendsbysms: friends---" + friends);
        Map<String, Object> invitefriendsMap = new HashMap<>();
        invitefriendsMap.put("friends", friends);
        invitefriendsMap.put("uin", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_UIN, 0));
        invitefriendsMap.put("token", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        invitefriendsMap.put("ver", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_VER, 0));
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

    //删除好友
    private void removeFriend(int toUin) {

        Map<String, Object> removeFreindMap = new HashMap<>();
        removeFreindMap.put("toUin", toUin);
        removeFreindMap.put("uin", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_UIN, 0));
        removeFreindMap.put("token", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        removeFreindMap.put("ver", SharePreferenceUtil.get(ActivityInviteFriend.this, YPlayConstant.YPLAY_VER, 0));
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

    /**
     * 字母列表点击滑动监听器事件
     */
    private class SideListViewListener implements SideView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            if (alphaIndexer.get(s) != null) {//判断当前选中的字母是否存在集合中
                int position = alphaIndexer.get(s);//如果存在集合中则取出集合中该字母对应所在的位置,再利用对应的setSelection，就可以实现点击选中相应字母，然后联系人就会定位到相应的位置
                LinearLayoutManager llm = (LinearLayoutManager) aifListView.getLayoutManager();
                llm.scrollToPositionWithOffset(position, 0);
            }
        }

    }

    @Override
    public void getAlphaIndexerAndSectionsListner(Map<String, Integer> alphaIndexer, List<String> sections) {
        this.alphaIndexer = alphaIndexer;
        this.sections = sections;
    }
}
